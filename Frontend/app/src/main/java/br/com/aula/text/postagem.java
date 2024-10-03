package br.com.aula.text;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;

public class postagem extends AppCompatActivity {

    private EditText inputNome;
    private EditText inputDescricao;
    private EditText inputNota;
    private Button buttonPublicar;
    private Button buttonSelecionarImagem;
    private ImageView imageViewImagem;

    private static final String URL = "http://10.0.2.2:8080/api/publicacao";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_postagem);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextInputLayout inputLayoutNome = findViewById(R.id.inputNome);
        TextInputLayout inputLayoutDescricao = findViewById(R.id.inputDescricao);
        TextInputLayout inputLayoutNota = findViewById(R.id.inputNota);

        inputNome = inputLayoutNome.getEditText();
        inputDescricao = inputLayoutDescricao.getEditText();
        inputNota = inputLayoutNota.getEditText();
        buttonPublicar = findViewById(R.id.buttonpublicar);
        buttonSelecionarImagem = findViewById(R.id.buttonSelecionarImagem);
        imageViewImagem = findViewById(R.id.imageViewImagem);

        buttonSelecionarImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        buttonPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = inputNome.getText().toString();
                String descricao = inputDescricao.getText().toString();
                String nota = inputNota.getText().toString();

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("nome", nome);
                    jsonObject.put("descricao", descricao);
                    jsonObject.put("nota", nota);
                } catch (Exception e) {
                    Log.e("Erro", e.getMessage());
                }

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(URL)
                        .post(RequestBody.create(MediaType.get("application/json"), jsonObject.toString()))
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("Erro", e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(postagem.this, "Erro ao criar publicação: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(postagem.this, "Publicação criada com sucesso!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            String responseBody = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(postagem.this, "Erro ao criar publicação: " + responseBody, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageViewImagem.setImageURI(selectedImage);
        }
    }
}