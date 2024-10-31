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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PostAdapter {
    public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.PostViewHolder> {
        private List<Post> posts;
        private Context context;

        public FeedAdapter(List<Post> posts) {
            this.posts = posts;
        }

        @NonNull
        @Override
        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
            return new PostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
            Post post = posts.get(position);

            holder.textNome.setText(post.getNome());
            holder.textDescricao.setText(post.getDescricao());
            holder.textNota.setText(post.getNota());

            // Carregar imagem se existir
            String imagemUrl = post.getImagem();
            if (!TextUtils.isEmpty(imagemUrl)) {
                Glide.with(context)
                        .load("https://ludis.onrender.com/api/image/" + imagemUrl)
                        .into(holder.imageView);
            } else {
                holder.imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // Configurar botão de comentário
            holder.btnComentar.setOnClickListener(v -> {
                Intent intent = new Intent(context, ComentariosActivity.class);
                intent.putExtra("post_id", position);
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }

        public class PostViewHolder extends RecyclerView.ViewHolder {
            TextView textNome, textDescricao, textNota;
            ImageView imageView;
            Button btnComentar;

            public PostViewHolder(@NonNull View itemView) {
                super(itemView);
                textNome = itemView.findViewById(R.id.textNome);
                textDescricao = itemView.findViewById(R.id.textDescricao);
                textNota = itemView.findViewById(R.id.textNota);
                imageView = itemView.findViewById(R.id.imageView);
                btnComentar = itemView.findViewById(R.id.btnComentar);
            }
        }
    }
}