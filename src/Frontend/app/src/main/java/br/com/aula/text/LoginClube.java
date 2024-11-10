package br.com.aula.text;

import android.content.Intent;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginClube extends AppCompatActivity {

    private EditText emailEditText;
    private EditText senhaEditText;
    private Button loginButton;
    private static final int SHIFT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_clube);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        emailEditText = ((TextInputLayout) findViewById(R.id.textInputEmailClube)).getEditText();
        senhaEditText = ((TextInputLayout) findViewById(R.id.textInputSenhaClube)).getEditText();
        loginButton = findViewById(R.id.buttonLoginClube);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> fazerLogin());
    }

    private String cifraCesar(String texto, int deslocamento) {
        StringBuilder resultado = new StringBuilder();
        for (char caractere : texto.toCharArray()) {
            resultado.append((char) (caractere + deslocamento));
        }
        return resultado.toString();
    }

    private void fazerLogin() {
        String email = emailEditText.getText().toString().trim();
        String senha = senhaEditText.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criptografar email e senha para comparação
        String emailCriptografado = cifraCesar(email, SHIFT);
        String senhaCriptografada = cifraCesar(senha, SHIFT);

        // Log para debug
        System.out.println("Email inserido: " + email);
        System.out.println("Email criptografado: " + emailCriptografado);
        System.out.println("Senha inserida: " + senha);
        System.out.println("Senha criptografada: " + senhaCriptografada);

        // Criar cliente HTTP e requisição
        CustomTrustManager customTrustManager = new CustomTrustManager();
        OkHttpClient client = customTrustManager.getOkHttpClient();

        Request request = new Request.Builder()
                .url("https://ludis.onrender.com/api/user2")  // Note o "user2" para clubes
                .get()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginClube.this,
                            "Erro de conexão: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response)
                    throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONArray usuarios = jsonResponse.getJSONArray("usuarios");

                        // Verificar se existe um clube com o email e senha fornecidos
                        boolean clubeEncontrado = false;
                        for (int i = 0; i < usuarios.length(); i++) {
                            JSONObject usuario = usuarios.getJSONObject(i);
                            String emailUsuario = usuario.getString("email");
                            String senhaUsuario = usuario.getString("senha");

                            System.out.println("Email do banco: " + emailUsuario);
                            System.out.println("Senha do banco: " + senhaUsuario);

                            if (emailUsuario.equals(emailCriptografado) &&
                                    senhaUsuario.equals(senhaCriptografada)) {
                                clubeEncontrado = true;
                                break;
                            }
                        }

                        final boolean encontrado = clubeEncontrado;
                        runOnUiThread(() -> {
                            if (encontrado) {
                                Toast.makeText(LoginClube.this,
                                        "Login realizado com sucesso!",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginClube.this, Telafeed.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginClube.this,
                                        "Email ou senha incorretos",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            Toast.makeText(LoginClube.this,
                                    "Erro ao processar dados do servidor",
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginClube.this,
                                "Erro ao verificar credenciais",
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}