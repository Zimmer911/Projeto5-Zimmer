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

    private EditText editTextNewPassword;
    private Button buttonChangePassword, buttonDeleteAccount, buttonBack;
    private OkHttpClient client;
    private static final String BASE_URL = "https://ludis.onrender.com/api";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        // Recuperar ID do usuário do SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

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

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("senha", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/user/" + userId)
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(Config.this, "Erro na conexão", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(Config.this, "Senha alterada com sucesso", Toast.LENGTH_SHORT).show();
                        editTextNewPassword.setText("");
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(Config.this, "Erro ao alterar senha", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void deleteAccount() {
        Request request = new Request.Builder()
                .url(BASE_URL + "/user/" + userId)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(Config.this, "Erro na conexão", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(Config.this, "Conta excluída com sucesso", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Config.this, Principal.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(Config.this, "Erro ao excluir conta", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}