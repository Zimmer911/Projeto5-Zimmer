package br.com.aula.text;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

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
        holder.textNome.setText(post.getNome());
        holder.textDescricao.setText(post.getDescricao());
        holder.textNota.setText(post.getNota());

        // Carregar imagem usando Glide
        if (!TextUtils.isEmpty(post.getImagem())) {
            String imageUrl = "https://ludis.onrender.com/api/image/" + post.getImagem();
            Log.d("FeedAdapter", "Tentando carregar imagem: " + imageUrl);

            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("FeedAdapter", "Erro ao carregar imagem: " + (e != null ? e.getMessage() : "Erro desconhecido"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("FeedAdapter", "Imagem carregada com sucesso");
                            return false;
                        }
                    })
                    .into(holder.imageView);
        } else {
            Log.d("FeedAdapter", "Nenhuma imagem para carregar, usando imagem padrão");
            holder.imageView.setImageResource(R.drawable.ic_launcher_background);
        }

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
        }
    }
}