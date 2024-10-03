package br.com.aula.text;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Telafeed extends AppCompatActivity {

    private Button btnpostagem;
    private TextView textfeednome;
    private TextView textfeedcoment;
    private TextView textfeedtitulo;
    private TextView textfeedimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_telafeed);

        btnpostagem = findViewById(R.id.btnpostagem);
        textfeednome = findViewById(R.id.textfeednome);
        textfeedcoment = findViewById(R.id.textfeedcoment);
        textfeedtitulo = findViewById(R.id.textfeedtitulo);
        textfeedimage = findViewById(R.id.textfeedimage);

        btnpostagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Telafeed.this, postagem.class);
                startActivity(intent);
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
