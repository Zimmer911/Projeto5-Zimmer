package br.fecap.pi.ludis;

public class Comentario {
    private int id; // Atributo para armazenar o ID
    private String nome;
    private String descricao;

    public Comentario(int id, String nome, String descricao) { // Construtor atualizado
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}