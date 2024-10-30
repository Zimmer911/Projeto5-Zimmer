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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class Telafeed extends AppCompatActivity {

    private Button btnpostagem;
    private TextView textfeednome;
    private TextView textfeedcoment;
    private TextView textfeedtitulo;
    private TextView textfeedimage;
    private RecyclerView recyclerView;
    private FeedAdapter feedAdapter;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_telafeed);

        // Inicializar views
        btnpostagem = findViewById(R.id.btnpostagem);
        textfeednome = findViewById(R.id.textfeednome);
        textfeedcoment = findViewById(R.id.textfeedcoment);
        textfeedtitulo = findViewById(R.id.textfeedtitulo);
        textfeedimage = findViewById(R.id.textfeedimage);
        recyclerView = findViewById(R.id.recyclerViewPosts);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar lista de posts
        postList = new ArrayList<>();

        // Inicializar adapter - CORRIGIDO
        feedAdapter = new FeedAdapter(postList);
        recyclerView.setAdapter(feedAdapter);

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

        // Carregar posts do servidor
        carregarPosts();
    }

    private void carregarPosts() {
        // Exemplo de posts para teste
        postList.add(new Post("Lipe", "Jogo Volei muito bem", "0", "@drawable/img.png"));
        postList.add(new Post("Nome 2", "Descrição 2", "4", ""));
        feedAdapter.notifyDataSetChanged();
    }
}