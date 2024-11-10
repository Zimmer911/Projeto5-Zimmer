package br.com.aula.text;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    public class ComentarioViewHolder extends RecyclerView.ViewHolder {
        TextView textNome;
        TextView textDescricao;

        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textComentarioNome);
            textDescricao = itemView.findViewById(R.id.textComentarioDescricao);
        }
    }
}