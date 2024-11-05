package br.com.aula.text;

import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONException;
import org.json.JSONObject;

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

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastroatleta);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        nomeEditText = ((TextInputLayout) findViewById(R.id.textInputNome)).getEditText();
        emailEditText = ((TextInputLayout) findViewById(R.id.textInputEmail)).getEditText();
        senhaEditText = ((TextInputLayout) findViewById(R.id.textInputSenha)).getEditText();
        cadastrarButton = findViewById(R.id.buttonCadastrar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupListeners() {
        cadastrarButton.setOnClickListener(v -> validateAndRegister());
    }

    private void validateAndRegister() {
        String nome = nomeEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String senha = senhaEditText.getText().toString();

        if (!validateFields(nome, email, senha)) {
            return;
        }

        cadastrar(nome, email, senha);
    }

    private boolean validateFields(String nome, String email, String senha) {
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor, insira um email válido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (senha.length() < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
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

    private void cadastrar(String nome, String email, String senha) {
        CustomTrustManager customTrustManager = new CustomTrustManager();
        OkHttpClient client = customTrustManager.getOkHttpClient();

        byte[] salt = generateSalt();
        String senhaHash = hashPassword(senha, salt);

        if (senhaHash == null) {
            Toast.makeText(CadastroAtleta.this,
                    "Erro ao processar senha",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        System.out.println("Nome original: " + nome);
        System.out.println("Email original: " + email);
        System.out.println("Nome criptografado: " + cifraCesar(nome, 3));
        System.out.println("Email criptografado: " + cifraCesar(email, 3));

        RequestBody requestBody = new okhttp3.FormBody.Builder()
                .add("nome", cifraCesar(nome, 3))
                .add("email", cifraCesar(email, 3))
                .add("senha", senhaHash)
                .build();

        Request request = new Request.Builder()
                .url("https://ludis.onrender.com/api/user")
                .post(requestBody)
                .build();

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
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONObject userObject = jsonResponse.getJSONObject("user");
                        int userId = userObject.getInt("id");

                        // Salvar o userId no SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("userId", userId);
                        editor.apply();

                        runOnUiThread(() -> {
                            Toast.makeText(CadastroAtleta.this,
                                    "Cadastro realizado com sucesso!",
                                    Toast.LENGTH_SHORT).show();
                            navigateToFeed();
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            Toast.makeText(CadastroAtleta.this,
                                    "Erro ao processar resposta do servidor",
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    handleErrorResponse(response);
                }
            }
        });
    }

    private void navigateToFeed() {
        Intent intent = new Intent(CadastroAtleta.this, Telafeed.class);
        startActivity(intent);
        finish();
    }

    private void handleErrorResponse(Response response) throws IOException {
        final String errorBody = response.body().string();
        runOnUiThread(() -> {
            Toast.makeText(CadastroAtleta.this,
                    "Erro ao cadastrar: " + errorBody,
                    Toast.LENGTH_SHORT).show();
        });
    }
}