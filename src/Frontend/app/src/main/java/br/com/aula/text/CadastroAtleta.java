package br.com.aula.text;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Base64;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CadastroAtleta extends AppCompatActivity {

    private EditText nomeEditText;
    private EditText emailEditText;
    private EditText senhaEditText;
    private Button cadastrarButton;

    // Adiciona constantes para criptografia
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastroatleta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialização dos campos
        nomeEditText = ((TextInputLayout) findViewById(R.id.textInputNome)).getEditText();
        emailEditText = ((TextInputLayout) findViewById(R.id.textInputEmail)).getEditText();
        senhaEditText = ((TextInputLayout) findViewById(R.id.textInputSenha)).getEditText();
        cadastrarButton = findViewById(R.id.buttonCadastrar);

        // Configuração do botão de cadastro
        cadastrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtém os valores dos campos
                String nome = nomeEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String senha = senhaEditText.getText().toString();

                // Validação dos campos
                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(CadastroAtleta.this,
                            "Por favor, preencha todos os campos",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validação do email
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(CadastroAtleta.this,
                            "Por favor, insira um email válido",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validação da senha
                if (senha.length() < 6) {
                    Toast.makeText(CadastroAtleta.this,
                            "A senha deve ter pelo menos 6 caracteres",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Realiza o cadastro
                cadastrar(nome, email, senha);
            }
        });
    }

    // Método para gerar salt aleatório
    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    // Método para criptografar senha
    private String hashPassword(String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.reset();
            digest.update(salt);
            byte[] hash = digest.digest(password.getBytes());

            // Combina salt e hash
            byte[] saltedHash = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, saltedHash, 0, salt.length);
            System.arraycopy(hash, 0, saltedHash, salt.length, hash.length);

            return Base64.encodeToString(saltedHash, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void cadastrar(String nome, String email, String senha) {
        CustomTrustManager customTrustManager = new CustomTrustManager();
        OkHttpClient client = customTrustManager.getOkHttpClient();

        // Gera salt e criptografa a senha
        byte[] salt = generateSalt();
        String senhaHash = hashPassword(senha, salt);

        if (senhaHash == null) {
            Toast.makeText(CadastroAtleta.this,
                    "Erro ao processar senha",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Cria o corpo da requisição
        RequestBody requestBody = new okhttp3.FormBody.Builder()
                .add("nome", nome)
                .add("email", email)
                .add("senha", senhaHash)
                .build();

        // Cria a requisição
        Request request = new Request.Builder()
                .url("https://ludis.onrender.com/api/user")
                .post(requestBody)
                .build();

        // Logs seguros
        System.out.println("URL: " + request.url());
        System.out.println("Método: " + request.method());
        System.out.println("Cabeçalhos: " + request.headers());

        // Executa a requisição
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(CadastroAtleta.this,
                            "Erro ao cadastrar usuário: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response)
                    throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(CadastroAtleta.this,
                                "Cadastro realizado com sucesso!",
                                Toast.LENGTH_SHORT).show();

                        // Navega para a tela de feed
                        Intent intent = new Intent(CadastroAtleta.this, Telafeed.class);
                        startActivity(intent);
                        finish(); // Fecha a tela de cadastro
                    });
                } else {
                    final String errorBody = response.body().string();
                    runOnUiThread(() -> {
                        Toast.makeText(CadastroAtleta.this,
                                "Erro ao cadastrar: " + errorBody,
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}