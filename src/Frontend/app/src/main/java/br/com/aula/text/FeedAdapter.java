package br.com.aula.text;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

            // Configurar clique na imagem
            holder.imageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra("imageUrl", imageUrl);
                context.startActivity(intent);
            });
        } else {
            Log.d("FeedAdapter", "Nenhuma imagem para carregar, usando imagem padrão");
            holder.imageView.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.btnComentar.setOnClickListener(v -> {
            Intent intent = new Intent(context, ComentariosActivity.class);
            intent.putExtra("post_id", position);
            context.startActivity(intent);
        });

        holder.btnExcluir.setOnClickListener(v -> {
            excluirPost(post.getId(), position);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }




    private void excluirPost(int postId, int position) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String nomeUsuarioCifrado = prefs.getString("userName", ""); // Nome do usuário logado (cifrado)

        if (nomeUsuarioCifrado.isEmpty()) {
            Toast.makeText(context, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Descriptografar o nome do usuário
        String nomeUsuario = decodificarCesar(nomeUsuarioCifrado, 3); // Exemplo com deslocamento 3

        // Obter o nome da postagem (cifrado)
        String nomePostagemCifrado = posts.get(position).getNome(); // Supondo que está cifrado

        // Logs para depuração
        Log.d("ExcluirPost", "Nome do usuário (cifrado): " + nomeUsuarioCifrado);
        Log.d("ExcluirPost", "Nome do usuário (decifrado): " + nomeUsuario);
        Log.d("ExcluirPost", "Nome da postagem (cifrado): " + nomePostagemCifrado);

        // Comparação entre o nome do usuário decifrado e o nome da postagem cifrado
        if (!nomeUsuario.equals(nomePostagemCifrado)) {
            Toast.makeText(context, "Você não tem permissão para excluir esta postagem", Toast.LENGTH_SHORT).show();
            Log.e("ExcluirPost", "Os nomes não coincidem: " + nomeUsuario + " vs " + nomePostagemCifrado);
            return;
        }

        // Configuração da requisição HTTP para exclusão
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://ludis.onrender.com/api/publicacao/" + postId)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ((Activity) context).runOnUiThread(() -> {
                    Toast.makeText(context, "Erro ao excluir post: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    ((Activity) context).runOnUiThread(() -> {
                        posts.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, posts.size());
                        Toast.makeText(context, "Post excluído com sucesso", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    ((Activity) context).runOnUiThread(() -> {
                        Toast.makeText(context, "Erro ao excluir post: " + response.message(),
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }


    // Método para decodificar usando a cifra de César
    private String decodificarCesar(String texto, int deslocamento) {
        StringBuilder textoDecodificado = new StringBuilder();
        for (char caractere : texto.toCharArray()) {
            textoDecodificado.append((char) (caractere - deslocamento)); // Subtração para reverter
        }
        return textoDecodificado.toString();
    }


    public class FeedViewHolder extends RecyclerView.ViewHolder {
        TextView textNome;
        TextView textDescricao;
        TextView textNota;
        ImageView imageView;
        Button btnComentar;
        Button btnExcluir;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textNome);
            textDescricao = itemView.findViewById(R.id.textDescricao);
            textNota = itemView.findViewById(R.id.textNota);
            imageView = itemView.findViewById(R.id.imageView);
            btnComentar = itemView.findViewById(R.id.btnComentar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }
}