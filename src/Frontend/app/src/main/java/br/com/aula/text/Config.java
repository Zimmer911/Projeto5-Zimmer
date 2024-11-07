package br.com.aula.text;

import static br.com.aula.text.CadastroAtleta.SHIFT;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Config extends AppCompatActivity {

    private EditText editTextNewPassword;
    private Button buttonChangePassword, buttonDeleteAccount, buttonBack;
    private OkHttpClient client;
    private static final String BASE_URL = "https://ludis.onrender.com/api";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private int userId;
    private boolean isClube;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        // Recuperar ID do usuário e tipo do SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        isClube = prefs.getBoolean("isClube", false);

        // Log para debug
        System.out.println("Config iniciada - UserId: " + userId + ", isClube: " + isClube);

        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        buttonDeleteAccount = findViewById(R.id.buttonDeleteAccount);
        buttonBack = findViewById(R.id.buttonBack);

        client = new OkHttpClient();

        buttonChangePassword.setOnClickListener(v -> changePassword());
        buttonDeleteAccount.setOnClickListener(v -> deleteAccount());
        buttonBack.setOnClickListener(v -> finish());
    }

    private void changePassword() {
        String newPassword = editTextNewPassword.getText().toString();
        if (newPassword.isEmpty()) {
            Toast.makeText(this, "Digite a nova senha", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criptografar a senha usando cifra de César
        String senhaCriptografada = cifraCesar(newPassword, SHIFT);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("senha", senhaCriptografada);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        String endpoint = isClube ? "/user2/" : "/user/";

        Request request = new Request.Builder()
                .url(BASE_URL + endpoint + userId)
                .put(body)
                .build();

        // Log para debug
        System.out.println("Alterando senha - URL: " + BASE_URL + endpoint + userId);
        System.out.println("Senha original: " + newPassword);
        System.out.println("Senha criptografada: " + senhaCriptografada);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(Config.this,
                        "Erro na conexão", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(Config.this,
                                "Senha alterada com sucesso", Toast.LENGTH_SHORT).show();
                        editTextNewPassword.setText("");
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(Config.this,
                            "Erro ao alterar senha", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private String cifraCesar(String texto, int deslocamento) {
        StringBuilder resultado = new StringBuilder();
        for (char caractere : texto.toCharArray()) {
            if (Character.isLetter(caractere)) {
                char base = Character.isUpperCase(caractere) ? 'A' : 'a';
                resultado.append((char) (((caractere - base + deslocamento) % 26) + base));
            } else {
                resultado.append(caractere);
            }
        }
        return resultado.toString();
    }


    private void deleteAccount() {
        // Log para debug
        System.out.println("Excluindo conta - UserId: " + userId + ", isClube: " + isClube);

        String endpoint = isClube ? "/user2/" : "/user/";
        String deleteUrl = BASE_URL + endpoint + userId;

        // Log para debug
        System.out.println("URL de exclusão: " + deleteUrl);

        Request request = new Request.Builder()
                .url(deleteUrl)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    System.out.println("Erro na exclusão: " + e.getMessage());
                    Toast.makeText(Config.this,
                            "Erro na conexão", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        // Limpar as preferências ao excluir a conta
                        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        prefs.edit().clear().apply();

                        Toast.makeText(Config.this,
                                "Conta excluída com sucesso", Toast.LENGTH_SHORT).show();

                        // Redirecionar para a tela principal e limpar a pilha de activities
                        Intent intent = new Intent(Config.this, Principal.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        System.out.println("Erro na resposta: " + response.message());
                        Toast.makeText(Config.this,
                                "Erro ao excluir conta", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}