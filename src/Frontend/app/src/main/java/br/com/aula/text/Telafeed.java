package br.com.aula.text;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
    private RecyclerView recyclerView;
    private FeedAdapter feedAdapter;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_telafeed);

        // Inicializar views
        btnpostagem = findViewById(R.id.btnpostagem);
        recyclerView = findViewById(R.id.recyclerViewPosts);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar lista de posts
        postList = new ArrayList<>();

        // Inicializar adapter
        feedAdapter = new FeedAdapter(postList);
        recyclerView.setAdapter(feedAdapter);

        btnpostagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Telafeed.this, postagem.class);
                startActivity(intent);
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Carregar posts do servidor
        carregarPosts();
    }

    private void carregarPosts() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://ludis.onrender.com/api/publicacao")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(Telafeed.this, "Erro ao carregar posts: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
                                String nome = jsonObject.getString("nome");
                                String descricao = jsonObject.getString("descricao");
                                String nota = jsonObject.getString("nota");
                                String imagem = jsonObject.optString("imagem", "");
                                postList.add(new Post(nome, descricao, nota, imagem));
                            }
                            runOnUiThread(() -> {
                                feedAdapter.notifyDataSetChanged();
                                Toast.makeText(Telafeed.this, "Posts carregados com sucesso", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(Telafeed.this, "Formato de resposta inválido", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        Log.e("Telafeed", "Erro ao processar JSON: " + e.getMessage());
                        runOnUiThread(() -> Toast.makeText(Telafeed.this, "Erro ao processar dados: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(Telafeed.this, "Erro ao carregar posts: " + response.message(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }}