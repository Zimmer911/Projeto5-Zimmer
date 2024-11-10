package br.com.aula.text;

public class Comentario {
    private String nome;
    private String descricao;

    public Comentario(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }
}