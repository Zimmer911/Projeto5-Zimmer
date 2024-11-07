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

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginAtleta extends AppCompatActivity {

    private EditText emailEditText;
    private EditText senhaEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loginatleta);

        // Configuração do layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialização das views
        TextInputLayout emailInputLayout = findViewById(R.id.textInputEmail);
        TextInputLayout senhaInputLayout = findViewById(R.id.textInputSenha);

        emailEditText = emailInputLayout.getEditText();
        senhaEditText = senhaInputLayout.getEditText();
        loginButton = findViewById(R.id.buttonLogin);

        // Configuração do botão de login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fazerLogin();
            }
        });
    }

    private void fazerLogin() {
        String email = emailEditText.getText().toString().trim();
        String senha = senhaEditText.getText().toString().trim();

        // Validação dos campos
        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criptografar email usando cifra de César
        String emailCriptografado = cifraCesar(email, 3);

        // Criar cliente HTTP e requisição
        CustomTrustManager customTrustManager = new CustomTrustManager();
        OkHttpClient client = customTrustManager.getOkHttpClient();

        // Criar corpo da requisição
        RequestBody requestBody = new okhttp3.FormBody.Builder()
                .add("email", emailCriptografado)
                .add("senha", senha)
                .build();

        // Criar requisição
        Request request = new Request.Builder()
                .url("https://ludis.onrender.com/api/user/login")
                .post(requestBody)
                .build();

        // Fazer a requisição assíncrona
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginAtleta.this,
                            "Erro de conexão: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    if (response.isSuccessful()) {
                        // Login bem-sucedido
                        runOnUiThread(() -> {
                            Toast.makeText(LoginAtleta.this,
                                    "Login realizado com sucesso!",
                                    Toast.LENGTH_SHORT).show();

                            // Navegar para o feed
                            Intent intent = new Intent(LoginAtleta.this, Telafeed.class);
                            startActivity(intent);
                            finish(); // Fecha a tela de login
                        });
                    } else {
                        // Login falhou
                        final String errorMessage = jsonResponse.optString("msg", "Erro ao fazer login");
                        runOnUiThread(() -> {
                            Toast.makeText(LoginAtleta.this,
                                    errorMessage,
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginAtleta.this,
                                "Erro ao processar resposta do servidor",
                                Toast.LENGTH_SHORT).show();
                    });
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
}