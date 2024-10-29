package br.com.aula.text;

public class Post {
    private String nome;
    private String descricao;
    private String nota;
    private String imagem;

    public Post(String nome, String descricao, String nota, String imagem) {
        this.nome = nome;
        this.descricao = descricao;
        this.nota = nota;
        this.imagem = imagem;
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

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
}