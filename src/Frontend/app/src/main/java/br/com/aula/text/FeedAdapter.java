package br.com.aula.text;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {
    private List<Post> posts;
    private Context context;

    public FeedAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        Post post = posts.get(position);

        // Configurar texto
        holder.textNome.setText(post.getNome());
        holder.textDescricao.setText(post.getDescricao());
        holder.textNota.setText(String.format("Nota: %s", post.getNota()));

        // Configurar imagem
        if (!TextUtils.isEmpty(post.getImagem())) {
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL); // Cachear tanto a imagem original quanto as transformadas

            Glide.with(context)
                    .load("https://ludis.onrender.com/uploads/" + post.getImagem())
                    .apply(requestOptions)
                    .into(holder.imageView);

            // Tornar a imagem visível
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            // Se não houver imagem, esconder o ImageView
            holder.imageView.setVisibility(View.GONE);
        }

        // Configurar botão de comentários
        holder.btnComentar.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(context, ComentariosActivity.class);
                intent.putExtra("post_id", position);
                intent.putExtra("post_nome", post.getNome());
                intent.putExtra("post_descricao", post.getDescricao());
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Erro ao abrir comentários", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        // Configurar clique no post inteiro
        holder.itemView.setOnClickListener(v -> {
            try {
                // Aqui você pode adicionar uma ação para quando o post inteiro for clicado
                Toast.makeText(context, "Post de " + post.getNome(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts != null ? posts.size() : 0;
    }

    // Método para atualizar os dados
    public void updatePosts(List<Post> newPosts) {
        this.posts = newPosts;
        notifyDataSetChanged();
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        TextView textNome;
        TextView textDescricao;
        TextView textNota;
        ImageView imageView;
        Button btnComentar;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textNome);
            textDescricao = itemView.findViewById(R.id.textDescricao);
            textNota = itemView.findViewById(R.id.textNota);
            imageView = itemView.findViewById(R.id.imageView);
            btnComentar = itemView.findViewById(R.id.btnComentar);

            // Configurar estilos e comportamentos padrão
            configureDefaultStyles();
        }

        private void configureDefaultStyles() {
            // Configurar estilo do texto
            textNome.setTextSize(18);
            textDescricao.setTextSize(14);
            textNota.setTextSize(14);

            // Configurar comportamento da imagem
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    // Método auxiliar para formatar a nota
    private String formatarNota(String nota) {
        try {
            int notaInt = Integer.parseInt(nota);
            return String.format("%d/5", notaInt);
        } catch (NumberFormatException e) {
            return nota;
        }
    }
}