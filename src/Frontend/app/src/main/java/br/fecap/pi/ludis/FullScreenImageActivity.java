package br.fecap.pi.ludis;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class FullScreenImageActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        imageView = findViewById(R.id.imageViewFullScreen);
        btnVoltar = findViewById(R.id.btnVoltar);

        // Obter a URL da imagem da Intent
        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("imageUrl");

        // Carregar a imagem usando Glide
        Glide.with(this)
                .load(imageUrl)
                .into(imageView);

        // Configurar o botão de voltar
        btnVoltar.setOnClickListener(v -> finish());
    }
}