package br.com.aula.text;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Telafeed extends AppCompatActivity {

    private Button btnpostagem;
    private ImageView imageEngrenagem;
    private RecyclerView recyclerView;
    private FeedAdapter feedAdapter;
    private List<Post> postList;
    private static final int SHIFT = 3;
    public static final String CHAVE_SECRETA = "MinhaChaveSecreta123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_telafeed);

        imageEngrenagem = findViewById(R.id.imageEngrenagem);
        btnpostagem = findViewById(R.id.btnpostagem);
        recyclerView = findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        feedAdapter = new FeedAdapter(postList);
        recyclerView.setAdapter(feedAdapter);

        btnpostagem.setOnClickListener(view -> {
            Intent intent = new Intent(Telafeed.this, postagem.class);
            startActivity(intent);
            finish();
        });

        // Configurar clique na engrenagem
        imageEngrenagem.setOnClickListener(view -> {
            Intent intent = new Intent(Telafeed.this, Config.class);
            startActivity(intent);
            // Não finalizamos esta activity para poder voltar para ela
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        carregarPosts();
    }

    private byte[] descriptografarImagem(String imagemBase64) {
        try {
            byte[] imagemCriptografada = Base64.decode(imagemBase64, Base64.DEFAULT);
            byte[] chave = CHAVE_SECRETA.getBytes();
            byte[] salt = new byte[16];
            System.arraycopy(imagemCriptografada, 0, salt, 0, 16);

            byte[] imagemDescriptografada = new byte[imagemCriptografada.length - 16];

            for (int i = 0; i < imagemDescriptografada.length; i++) {
                imagemDescriptografada[i] = (byte) (imagemCriptografada[i + 16] ^
                        chave[i % chave.length] ^
                        salt[i % salt.length]);
            }

            return imagemDescriptografada;
        } catch (Exception e) {
            Log.e("Telafeed", "Erro ao descriptografar imagem: " + e.getMessage());
            return null;
        }
    }

    private void carregarPosts() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://ludis.onrender.com/api/publicacao")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(Telafeed.this,
                        "Erro ao carregar posts: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("Telafeed", "Resposta do servidor: " + responseData);

                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        if (jsonResponse.has("publicacoes")) {
                            JSONArray jsonArray = jsonResponse.getJSONArray("publicacoes");
                            postList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                int id = jsonObject.getInt("id");
                                String nomeCriptografado = jsonObject.getString("nome");
                                String descricaoCriptografada = jsonObject.getString("descricao");
                                String nota = jsonObject.getString("nota");
                                String imagem = jsonObject.optString("imagem", null);

                                String nomeDecifrado = decifrarCesar(nomeCriptografado, SHIFT);
                                String descricaoDecifrada = decifrarCesar(descricaoCriptografada, SHIFT);

                                Log.d("Telafeed", "ID: " + id);
                                Log.d("Telafeed", "Nome criptografado: " + nomeCriptografado);
                                Log.d("Telafeed", "Nome decifrado: " + nomeDecifrado);
                                Log.d("Telafeed", "Descrição criptografada: " + descricaoCriptografada);
                                Log.d("Telafeed", "Descrição decifrada: " + descricaoDecifrada);
                                Log.d("Telafeed", "Imagem: " + imagem);

                                Post novoPost = new Post(id, nomeDecifrado, descricaoDecifrada, nota, imagem);
                                postList.add(novoPost);
                            }
                            runOnUiThread(() -> {
                                feedAdapter.notifyDataSetChanged();
                                Toast.makeText(Telafeed.this,
                                        "Posts carregados com sucesso",
                                        Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(Telafeed.this,
                                    "Formato de resposta inválido",
                                    Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        Log.e("Telafeed", "Erro ao processar JSON: " + e.getMessage());
                        runOnUiThread(() -> Toast.makeText(Telafeed.this,
                                "Erro ao processar dados: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(Telafeed.this,
                            "Erro ao carregar posts: " + response.message(),
                            Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private String decifrarCesar(String textoCifrado, int deslocamento) {
        StringBuilder resultado = new StringBuilder();
        for (char caractere : textoCifrado.toCharArray()) {
            if (Character.isLetter(caractere)) {
                char base = Character.isUpperCase(caractere) ? 'A' : 'a';
                resultado.append((char) (((caractere - base - deslocamento + 26) % 26) + base));
            } else {
                resultado.append(caractere);
            }
        }
        return resultado.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarPosts(); // Recarrega os posts quando a activity voltar ao primeiro plano
    }

    // Método para atualizar a lista após exclusão
    public void atualizarListaAposExclusao(int position) {
        postList.remove(position);
        feedAdapter.notifyItemRemoved(position);
        feedAdapter.notifyItemRangeChanged(position, postList.size());
    }

    // Método para recarregar todos os posts
    public void recarregarPosts() {
        carregarPosts();
    }
}