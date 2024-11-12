package br.com.aula.text;

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

    private EditText editTextNewEmail;
    private Button buttonChangePassword, buttonDeleteAccount, buttonBack;
    private OkHttpClient client;
    private static final String BASE_URL = "https://ludis.onrender.com/api";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private int userId;
    private boolean isClube;
    private EditText editTextNewName;
    private EditText editTextCurrentPassword;

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

        editTextNewEmail = findViewById(R.id.editTextNewEmail);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        buttonDeleteAccount = findViewById(R.id.buttonDeleteAccount);
        buttonBack = findViewById(R.id.buttonBack);
        editTextNewName = findViewById(R.id.editTextNewName);
        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword);

        client = new OkHttpClient();

        buttonChangePassword.setOnClickListener(v -> changeEmail());
        buttonDeleteAccount.setOnClickListener(v -> deleteAccount());
        buttonBack.setOnClickListener(v -> finish());
    }

    private void changeEmail() {
        String newEmail = editTextNewEmail.getText().toString();
        String nome = editTextNewName.getText().toString();
        String currentPassword = editTextCurrentPassword.getText().toString(); // Obtenha a senha atual

        if (newEmail.isEmpty()) {
            Toast.makeText(this, "Digite o novo e-mail", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nome.isEmpty()) {
            Toast.makeText(this, "Digite seu nome", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentPassword.isEmpty()) {
            Toast.makeText(this, "Digite sua senha atual", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(newEmail)) {
            Toast.makeText(this, "E-mail inválido. Tente novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", newEmail);
            jsonBody.put("nome", nome);
            jsonBody.put("senha", currentPassword); // Inclua a senha no JSON
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        String endpoint = isClube ? "/user2/" : "/user/";

        Request request = new Request.Builder()
                .url(BASE_URL + endpoint + userId)
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(Config.this,
                        "Erro na conexão: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(Config.this,
                                "E-mail alterado com sucesso", Toast.LENGTH_SHORT).show();
                        editTextNewEmail.setText(""); // Limpa o campo de entrada
                        editTextNewName.setText(""); // Limpa o campo de nome
                        editTextCurrentPassword.setText(""); // Limpa o campo de senha
                    });
                } else {
                    String errorResponse = response.body().string(); // Captura a resposta de erro
                    runOnUiThread(() -> {
                        Toast.makeText(Config.this,
                                "Erro ao alterar e-mail: " + errorResponse, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }



    // Método para validar o e-mail
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
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