package br.com.aula.text;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginClube extends AppCompatActivity {

    private ImageView btnVoltar;
    private TextInputEditText inputNomeClube;
    private TextInputEditText inputEmail;
    private TextInputEditText inputCnpj;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_clube);


        btnVoltar = findViewById(R.id.btnVoltar);

        btnVoltar.setOnClickListener(view -> {
            Intent voltarIntent = new Intent(LoginClube.this, Principal.class);
            startActivity(voltarIntent);
            finish();
        });

        Log.i("Ciclo de Vida", "Tela 1 - onCreate");

        TextInputLayout nomeClubeLayout = findViewById(R.id.textInputNomeClube);
        TextInputLayout emailLayout = findViewById(R.id.textInputEmail);
        TextInputLayout cnpjLayout = findViewById(R.id.textInputCnpj);

        inputNomeClube = (TextInputEditText) nomeClubeLayout.getEditText();
        inputEmail = (TextInputEditText) emailLayout.getEditText();
        inputCnpj = (TextInputEditText) cnpjLayout.getEditText();

        btnLogin = findViewById(R.id.buttonLogin);

        btnLogin.setOnClickListener(view -> {

            String nome = inputNomeClube.getText().toString();
            String email = inputEmail.getText().toString();
            String Cnpj = inputCnpj.getText().toString();


            Intent intent = new Intent(LoginClube.this, FeedSimulator.class);

            intent.putExtra("nome do clube", nome);
            intent.putExtra("email", email);
            intent.putExtra("CNPJ", Cnpj);

            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }
}