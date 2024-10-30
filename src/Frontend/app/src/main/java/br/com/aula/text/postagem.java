package br.com.aula.text;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class postagem extends AppCompatActivity {

    private EditText nomeEditText;
    private EditText descricaoEditText;
    private EditText notaEditText;
    private Button postarButton;
    private static final int SHIFT = 3; // Deslocamento para a cifra de César

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_postagem);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        nomeEditText = ((TextInputLayout) findViewById(R.id.inputNome)).getEditText();
        descricaoEditText = ((TextInputLayout) findViewById(R.id.inputDescricao)).getEditText();
        notaEditText = ((TextInputLayout) findViewById(R.id.inputNota)).getEditText();
        postarButton = findViewById(R.id.buttonpublicar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupListeners() {
        postarButton.setOnClickListener(v -> validateAndPost());
    }

    private void validateAndPost() {
        String nome = nomeEditText.getText().toString().trim();
        String descricao = descricaoEditText.getText().toString().trim();
        String nota = notaEditText.getText().toString().trim();

        if (!validateFields(nome, descricao, nota)) {
            return;
        }

        // Criptografar os dados antes de enviar
        String nomeCriptografado = cifraCesar(nome, SHIFT);
        String descricaoCriptografada = cifraCesar(descricao, SHIFT);

        // Log para verificar a criptografia
        System.out.println("Nome original: " + nome);
        System.out.println("Nome criptografado: " + nomeCriptografado);
        System.out.println("Descrição original: " + descricao);
        System.out.println("Descrição criptografada: " + descricaoCriptografada);

        criarPostagem(nomeCriptografado, descricaoCriptografada, nota);
    }

    private boolean validateFields(String nome, String descricao, String nota) {
        if (nome.isEmpty() || descricao.isEmpty() || nota.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            int notaValue = Integer.parseInt(nota);
            if (notaValue < 0 || notaValue > 10) {
                Toast.makeText(this, "A nota deve estar entre 0 e 10", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Por favor, insira uma nota válida", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void criarPostagem(String nome, String descricao, String nota) {
        CustomTrustManager customTrustManager = new CustomTrustManager();
        OkHttpClient client = customTrustManager.getOkHttpClient();

        RequestBody requestBody = new okhttp3.FormBody.Builder()
                .add("nome", nome)
                .add("descricao", descricao)
                .add("nota", nota)
                .build();

        Request request = new Request.Builder()
                .url("https://ludis.onrender.com/api/publicacao")
                .post(requestBody)
                .build();

        // Log para verificar os dados enviados
        System.out.println("Enviando para o servidor:");
        System.out.println("Nome: " + nome);
        System.out.println("Descrição: " + descricao);
        System.out.println("Nota: " + nota);

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(postagem.this,
                            "Erro ao criar postagem: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response)
                    throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(postagem.this,
                                "Postagem criada com sucesso!",
                                Toast.LENGTH_SHORT).show();
                        navigateToFeed();
                    });
                } else {
                    handleErrorResponse(response);
                }
            }
        });
    }

    private void navigateToFeed() {
        Intent intent = new Intent(postagem.this, Telafeed.class);
        startActivity(intent);
        finish();
    }

    private void handleErrorResponse(Response response) throws IOException {
        final String errorBody = response.body().string();
        runOnUiThread(() -> {
            Toast.makeText(postagem.this,
                    "Erro ao criar postagem: " + errorBody,
                    Toast.LENGTH_SHORT).show();
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

    // Método para descriptografar (pode ser útil para testes)
    private String decifrarCesar(String textoCifrado, int deslocamento) {
        return cifraCesar(textoCifrado, 26 - deslocamento); // 26 - deslocamento é o deslocamento inverso
    }
}