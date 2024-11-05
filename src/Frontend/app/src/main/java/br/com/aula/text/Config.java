package br.com.aula.text;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

    private static final String TAG = "Config";
    private EditText editTextNewPassword;
    private Button buttonChangePassword, buttonDeleteAccount, buttonBack;
    private OkHttpClient client;
    private static final String BASE_URL = "https://ludis.onrender.com/api";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private int userId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        // Recuperar ID do usuário e token do SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        token = prefs.getString("token", "");

        Log.d(TAG, "ID do usuário recuperado: " + userId);
        Log.d(TAG, "Token recuperado: " + (token.isEmpty() ? "Vazio" : "Não vazio"));

        if (userId == -1 || token.isEmpty()) {
            Log.e(TAG, "Erro: Usuário não identificado ou token vazio");
            Toast.makeText(this, "Erro: Usuário não identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupListeners();
        client = new OkHttpClient();
    }

    private void initializeViews() {
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        buttonDeleteAccount = findViewById(R.id.buttonDeleteAccount);
        buttonBack = findViewById(R.id.buttonBack);
    }

    private void setupListeners() {
        buttonChangePassword.setOnClickListener(v -> changePassword());
        buttonDeleteAccount.setOnClickListener(v -> confirmDeleteAccount());
        buttonBack.setOnClickListener(v -> finish());
    }

    private void changePassword() {
        String newPassword = editTextNewPassword.getText().toString();
        if (newPassword.isEmpty()) {
            Toast.makeText(this, "Digite a nova senha", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Tentando alterar a senha para o usuário ID: " + userId);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("senha", newPassword);
        } catch (JSONException e) {
            Log.e(TAG, "Erro ao criar JSON para alteração de senha", e);
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/user/" + userId)
                .put(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Log.d(TAG, "Enviando requisição para: " + request.url());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Falha na conexão", e);
                runOnUiThread(() -> Toast.makeText(Config.this, "Erro na conexão", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                Log.d(TAG, "Resposta do servidor: " + responseBody);
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(Config.this, "Senha alterada com sucesso", Toast.LENGTH_SHORT).show();
                        editTextNewPassword.setText("");
                    });
                } else {
                    Log.e(TAG, "Erro ao alterar senha: " + responseBody);
                    runOnUiThread(() -> Toast.makeText(Config.this, "Erro ao alterar senha: " + responseBody, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Conta")
                .setMessage("Tem certeza que deseja excluir sua conta? Esta ação não pode ser desfeita.")
                .setPositiveButton("Sim", (dialog, which) -> deleteAccount())
                .setNegativeButton("Não", null)
                .show();
    }

    private void deleteAccount() {
        Log.d(TAG, "Tentando excluir conta para o usuário ID: " + userId);

        Request request = new Request.Builder()
                .url(BASE_URL + "/user/" + userId)
                .delete()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Log.d(TAG, "Enviando requisição para: " + request.url());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Falha na conexão ao tentar excluir conta", e);
                runOnUiThread(() -> Toast.makeText(Config.this, "Erro na conexão", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                Log.d(TAG, "Resposta do servidor para exclusão: " + responseBody);
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        // Limpar dados do usuário
                        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        prefs.edit().clear().apply();

                        Toast.makeText(Config.this, "Conta excluída com sucesso", Toast.LENGTH_SHORT).show();

                        // Voltar para tela de login
                        Intent intent = new Intent(Config.this, LoginAtleta.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    Log.e(TAG, "Erro ao excluir conta: " + responseBody);
                    runOnUiThread(() -> Toast.makeText(Config.this, "Erro ao excluir conta: " + responseBody, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}