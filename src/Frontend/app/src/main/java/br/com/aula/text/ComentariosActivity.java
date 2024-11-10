package br.com.aula.text;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ComentariosActivity extends AppCompatActivity {
    private EditText editTextComentario;
    private RecyclerView recyclerViewComentarios;
    private ComentariosAdapter adapter;
    private List<Comentario> comentarios;
    private int postId;
    private static final int SHIFT = 3; // Adicionado para criptografia César

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        postId = getIntent().getIntExtra("post_id", -1);

        editTextComentario = findViewById(R.id.editTextComentario);
        recyclerViewComentarios = findViewById(R.id.recyclerViewComentarios);

        comentarios = new ArrayList<>();
        adapter = new ComentariosAdapter(comentarios);

        recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewComentarios.setAdapter(adapter);

        Button btnEnviar = findViewById(R.id.btnEnviarComentario);
        btnEnviar.setOnClickListener(v -> enviarComentario());

        carregarComentarios();
    }

    private void enviarComentario() {
        String comentario = editTextComentario.getText().toString();
        if (comentario.isEmpty()) return;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nome", cifraCesar("user", SHIFT)); // Criptografa o nome
            jsonObject.put("descricao", cifraCesar(comentario, SHIFT)); // Criptografa o comentário
            jsonObject.put("nota", 5); // Opcional
            jsonObject.put("postId", postId); // Adicionando postId
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                jsonObject.toString()
        );

        Request request = new Request.Builder()
                .url("https://ludis.onrender.com/api/comentario")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(ComentariosActivity.this,
                                "Erro ao enviar comentário",
                                Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        editTextComentario.setText("");
                        carregarComentarios();
                    });
                }
            }
        });
    }

    private void carregarComentarios() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://ludis.onrender.com/api/comentario/post/" + postId) // Atualizando a URL para buscar comentários por postId
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(ComentariosActivity.this,
                                "Erro ao carregar comentários",
                                Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("ComentariosActivity", "Resposta da API: " + responseData);

                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONArray comentariosArray = jsonResponse.getJSONArray("comentarios");

                        List<Comentario> novosComentarios = new ArrayList<>();
                        for (int i = 0; i < comentariosArray.length(); i++) {
                            JSONObject comentarioJson = comentariosArray.getJSONObject(i);
                            String nome = comentarioJson.getString("nome");
                            String descricao = comentarioJson.getString("descricao");
                            novosComentarios.add(new Comentario(nome, descricao));
                        }

                        runOnUiThread(() -> {
                            comentarios.clear();
                            comentarios.addAll(novosComentarios);
                            adapter.notifyDataSetChanged();
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(ComentariosActivity.this,
                                    "Erro ao carregar comentários: " + response.message(),
                                    Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    // Método para criptografar usando a cifra de César
    private String cifraCesar(String texto, int deslocamento) {
        StringBuilder resultado = new StringBuilder();
        for (char caractere : texto.toCharArray()) {
            if (Character.isLetter(caractere)) {
                int base = Character.isUpperCase(caractere) ? 'A' : 'a';
                resultado.append((char) (((caractere - base + deslocamento) % 26) + base));
            } else {
                resultado.append(caractere);
            }
        }
        return resultado.toString();
    }

    // Método para descriptografar usando a cifra de César
    private String decifrarCesar(String textoCifrado, int deslocamento) {
        return cifraCesar(textoCifrado, 26 - deslocamento);
    }
}