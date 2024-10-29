package br.com.aula.text;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class Telafeed extends AppCompatActivity {

    private Button btnpostagem;
    private RecyclerView recyclerView;
    private FeedAdapter feedAdapter;
    private List<Post> postList;
    private OkHttpClient client;

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

        // Inicializar OkHttpClient
        client = new OkHttpClient();

        btnpostagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Telafeed.this, postagem.class);
                startActivity(intent);
            }
        });

        // Carregar posts do servidor
        carregarPosts();
    }

    private void carregarPosts() {
        Request request = new Request.Builder()
                .url("https://ludis.onrender.com/api/publicacao")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(Telafeed.this, "Erro ao carregar publicações", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseData);
                        JSONArray publicacoes = json.getJSONArray("publicacoes");
                        List<Post> novosPosts = new ArrayList<>();

                        for (int i = 0; i < publicacoes.length(); i++) {
                            JSONObject publicacao = publicacoes.getJSONObject(i);
                            String nome = publicacao.getString("nome");
                            String descricao = publicacao.getString("descricao");
                            String nota = publicacao.getString("nota");
                            String imagem = publicacao.optString("imagem", "");
                            novosPosts.add(new Post(nome, descricao, nota, imagem));
                        }

                        runOnUiThread(() -> {
                            postList.clear();
                            postList.addAll(novosPosts);
                            feedAdapter.notifyDataSetChanged();
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(Telafeed.this, "Erro ao processar dados", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(Telafeed.this, "Erro ao carregar publicações", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarPosts(); // Recarrega os posts quando a atividade volta ao primeiro plano
    }
}