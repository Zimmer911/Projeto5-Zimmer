package br.fecap.pi.ludis;

public class Post {
    private int id;
    private String nome;
    private String descricao;
    private String nota;
    private String imagem; // Mudando para String para armazenar a URL da imagem

    public Post(int id, String nome, String descricao, String nota, String imagem) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.nota = nota;
        this.imagem = imagem;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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