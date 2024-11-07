package br.com.aula.text;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginAtleta extends AppCompatActivity {

    private EditText emailEditText;
    private EditText senhaEditText;
    private Button loginButton;
    private static final int SHIFT = 3;
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

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

    private String hashPassword(String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.reset();
            digest.update(salt);
            byte[] hash = digest.digest(password.getBytes());

            byte[] saltedHash = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, saltedHash, 0, salt.length);
            System.arraycopy(hash, 0, saltedHash, salt.length, hash.length);

            return Base64.encodeToString(saltedHash, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void fazerLogin() {
        String email = emailEditText.getText().toString().trim();
        String senha = senhaEditText.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criptografar email para comparação
        String emailCriptografado = cifraCesar(email, SHIFT);

        // Criar cliente HTTP e requisição
        CustomTrustManager customTrustManager = new CustomTrustManager();
        OkHttpClient client = customTrustManager.getOkHttpClient();

        // Buscar todos os usuários
        Request request = new Request.Builder()
                .url("https://ludis.onrender.com/api/user")
                .get()
                .build();

        // Log para debug
        System.out.println("Email inserido (criptografado): " + emailCriptografado);

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
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response)
                    throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONArray usuarios = jsonResponse.getJSONArray("usuarios");

                        // Verificar se existe um usuário com o email e senha fornecidos
                        boolean usuarioEncontrado = false;
                        for (int i = 0; i < usuarios.length(); i++) {
                            JSONObject usuario = usuarios.getJSONObject(i);
                            String emailUsuario = usuario.getString("email");
                            String senhaUsuario = usuario.getString("senha");

                            System.out.println("Email do banco: " + emailUsuario);

                            if (emailUsuario.equals(emailCriptografado) && verificarSenha(senha, senhaUsuario)) {
                                usuarioEncontrado = true;
                                break;
                            }
                        }

                        final boolean encontrado = usuarioEncontrado;
                        runOnUiThread(() -> {
                            if (encontrado) {
                                Toast.makeText(LoginAtleta.this,
                                        "Login realizado com sucesso!",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginAtleta.this, Telafeed.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginAtleta.this,
                                        "Email ou senha incorretos",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            Toast.makeText(LoginAtleta.this,
                                    "Erro ao processar dados do servidor",
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginAtleta.this,
                                "Erro ao verificar credenciais",
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private boolean verificarSenha(String senhaInserida, String senhaArmazenada) {
        try {
            // Decodificar a senha armazenada de Base64
            byte[] saltedHash = Base64.decode(senhaArmazenada, Base64.DEFAULT);

            // Extrair o salt (primeiros SALT_LENGTH bytes)
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(saltedHash, 0, salt, 0, SALT_LENGTH);

            // Gerar hash da senha inserida com o mesmo salt
            String hashTeste = hashPassword(senhaInserida, salt);

            // Comparar os hashes
            return senhaArmazenada.equals(hashTeste);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}