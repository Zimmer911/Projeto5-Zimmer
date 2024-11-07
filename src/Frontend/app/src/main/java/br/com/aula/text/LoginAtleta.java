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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText = ((TextInputLayout) findViewById(R.id.textInputEmail)).getEditText();
        senhaEditText = ((TextInputLayout) findViewById(R.id.textInputSenha)).getEditText();
        loginButton = findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener(v -> fazerLogin());
    }

    private void fazerLogin() {
        String email = emailEditText.getText().toString().trim();
        String senha = senhaEditText.getText().toString().trim();

        // Validação dos campos
        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criar cliente HTTP e requisição
        CustomTrustManager customTrustManager = new CustomTrustManager();
        OkHttpClient client = customTrustManager.getOkHttpClient();

        // Criar URL com email e senha como parâmetros (não é seguro, mas para fins de simplicidade)
        String url = "https://ludis.onrender.com/api/user/login?email=" + email + "&senha=" + senha;

        // Fazer a requisição GET
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        // Fazer a requisição assíncrona
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginAtleta.this, "Erro de conexão: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Login bem-sucedido
                    runOnUiThread(() -> {
                        Toast.makeText(LoginAtleta.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginAtleta.this, Telafeed.class);
                        startActivity(intent);
                        finish(); // Fecha a tela de login
                    });
                } else {
                    // Login falhou
                    runOnUiThread(() -> {
                        Toast.makeText(LoginAtleta.this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}