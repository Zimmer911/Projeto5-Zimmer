package br.com.aula.text;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
    private int postId; // ID do post para o qual os comentários estão sendo feitos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        postId = getIntent().getIntExtra("post_id", -1); // Recebe o ID do post

        editTextComentario = findViewById(R.id.editTextComentario);
        recyclerViewComentarios = findViewById(R.id.recyclerViewComentarios);

        comentarios = new ArrayList<>();
        adapter = new ComentariosAdapter(comentarios);

        recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewComentarios.setAdapter(adapter);

        Button btnEnviar = findViewById(R.id.btnEnviarComentario);
        btnEnviar.setOnClickListener(v -> enviarComentario());

        carregarComentarios(); // Carrega os comentários ao iniciar a activity
    }

    private void enviarComentario() {
        String comentario = editTextComentario.getText().toString().trim();
        if (comentario.isEmpty()) {
            Toast.makeText(this, "Por favor, escreva um comentário", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cria um JSON para enviar o comentário
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nome", "Usuário"); // Nome fictício, pode ser alterado
            jsonObject.put("descricao", comentario);
            jsonObject.put("postId", postId); // ID do post

            // Envia o comentário para o servidor
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url("https://ludis.onrender.com/api/comentario")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> Toast.makeText(ComentariosActivity.this, "Erro ao enviar comentário", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            editTextComentario.setText(""); // Limpa o campo de texto
                            carregarComentarios(); // Recarrega os comentários
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(ComentariosActivity.this, "Erro ao enviar comentário", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void carregarComentarios() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://ludis.onrender.com/api/comentario/post/" + postId)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(ComentariosActivity.this, "Erro ao carregar comentários", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        comentarios.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject comentarioJson = jsonArray.getJSONObject(i);
                            String nome = comentarioJson.getString("nome");
                            String descricao = comentarioJson.getString("descricao");
                            comentarios.add(new Comentario(nome, descricao)); // Adiciona o comentário à lista
                        }

                        runOnUiThread(() -> adapter.notifyDataSetChanged()); // Notifica o adapter para atualizar a RecyclerView
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}