package br.com.aula.text;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import br.com.aula.text.FeedSimulator;
import br.com.aula.text.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginAtleta extends AppCompatActivity {

    private TextInputLayout emailTextInputLayout;
    private TextInputLayout senhaTextInputLayout;
    private Button LoginAtletaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginatleta);

        emailTextInputLayout = findViewById(R.id.textInputEmail);
        senhaTextInputLayout = findViewById(R.id.textInputSenha);
        LoginAtletaButton = findViewById(R.id.buttonLogin);

        LoginAtletaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTextInputLayout.getEditText().getText().toString();
                String senha = senhaTextInputLayout.getEditText().getText().toString();

                if (email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(LoginAtleta.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestBody requestBody = new okhttp3.FormBody.Builder()
                        .add("email", email)
                        .add("senha", senha)
                        .build();

                Request request = new Request.Builder()
                        .url("http://10.0.2.2:8080/api/user/LoginAtleta") // Use o endereço IP do seu computador
                        .post(requestBody)
                        .build();

                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                        System.out.println("Erro ao fazer LoginAtleta: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginAtleta.this, "Erro ao fazer LoginAtleta", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            System.out.println("Resposta do servidor: " + responseBody);

                            // Parse o token de autenticação
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(responseBody);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            String token = null;
                            try {
                                token = jsonObject.getString("token");
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            // Salve o token de autenticação em um lugar seguro
                            SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
                            sharedPreferences.edit().putString("token", token).apply();

                            // Redirecione o usuário para a tela principal
                            Intent intent = new Intent(LoginAtleta.this, FeedSimulator.class);
                            startActivity(intent);
                            finish();
                        } else {
                            System.out.println("Erro ao fazer LoginAtleta: " + response.code());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginAtleta.this, "Erro ao fazer LoginAtleta", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}