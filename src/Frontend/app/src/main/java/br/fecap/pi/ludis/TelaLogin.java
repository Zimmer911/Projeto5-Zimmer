package br.fecap.pi.ludis;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TelaLogin extends AppCompatActivity {

    private static final String TAG = "TelaLogin";
    private Button btnLoginAtletaTela;
    private Button btnTelaLoginClube;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_login);

        btnLoginAtletaTela = findViewById(R.id.btnTelaLoginAtleta);
        btnTelaLoginClube = findViewById(R.id.btnTelaLoginClube); // Adicionando o botão do clube

        btnLoginAtletaTela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Botão de login de atleta clicado");
                try {
                    Intent intent = new Intent(TelaLogin.this, LoginAtleta.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Erro ao iniciar LoginAtleta", e);
                    Toast.makeText(TelaLogin.this, "Erro ao abrir tela de login de atleta", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnTelaLoginClube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Botão de login de clube clicado");
                try {
                    Intent intent = new Intent(TelaLogin.this, LoginClube.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Erro ao iniciar LoginClube", e);
                    Toast.makeText(TelaLogin.this, "Erro ao abrir tela de login de clube", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}