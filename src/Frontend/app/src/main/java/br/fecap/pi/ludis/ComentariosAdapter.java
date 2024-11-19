package br.fecap.pi.ludis;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ComentariosAdapter extends RecyclerView.Adapter<ComentariosAdapter.ComentarioViewHolder> {
    private List<Comentario> comentarios;

    public ComentariosAdapter(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    @NonNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comentario, parent, false);
        return new ComentarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
        Comentario comentario = comentarios.get(position);
        holder.textNome.setText(comentario.getNome());
        holder.textDescricao.setText(comentario.getDescricao());

        // Configurando o botão de excluir
        holder.btnExcluir.setOnClickListener(v -> excluirComentario(comentario.getId(), position, holder.itemView.getContext()));
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    private void excluirComentario(int comentarioId, int position, Context context) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://ludis.onrender.com/api/comentario/" + comentarioId)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ((Activity) context).runOnUiThread(() -> {
                    Toast.makeText(context, "Erro ao excluir comentário: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    ((Activity) context).runOnUiThread(() -> {
                        comentarios.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, comentarios.size());
                        Toast.makeText(context, "Comentário excluído com sucesso", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    ((Activity) context).runOnUiThread(() -> {
                        Toast.makeText(context, "Erro ao excluir comentário: " + response.message(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    public class ComentarioViewHolder extends RecyclerView.ViewHolder {
        TextView textNome;
        TextView textDescricao;
        Button btnExcluir; // Adicionando botão de excluir

        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textComentarioNome);
            textDescricao = itemView.findViewById(R.id.textComentarioDescricao);
            btnExcluir = itemView.findViewById(R.id.btnExcluirComentario); // Inicializando botão de excluir
        }
    }
}