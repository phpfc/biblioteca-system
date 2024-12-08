package com.biblioteca.models;

public class Livro {
    private String titulo;
    private String autor;
    private String codigoIsbn;
    private int copiasTotal;
    private int copiasDisponiveis;
    private Categoria categoria;

    public Livro(String titulo, String autor, String codigoIsbn, int copias, Categoria categoria) {
        this.titulo = titulo;
        this.autor = autor;
        this.codigoIsbn = codigoIsbn;
        this.copiasTotal = copias;
        this.copiasDisponiveis = copias;
        this.categoria = categoria;
    }

    // Getters e Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getCodigoIsbn() { return codigoIsbn; }
    public int getCopiasTotal() { return copiasTotal; }
    public void setCopiasTotal(int copiasTotal) { this.copiasTotal = copiasTotal; }
    public int getCopiasDisponiveis() { return copiasDisponiveis; }
    public void setCopiasDisponiveis(int copiasDisponiveis) { this.copiasDisponiveis = copiasDisponiveis; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public boolean temCopiaDisponivel() {
        return copiasDisponiveis > 0;
    }

    public String informaLivro() {
        return "Título: " + titulo +
                "\nAutor: " + autor +
                "\nISBN: " + codigoIsbn +
                "\nCópias Disponíveis: " + copiasDisponiveis +
                "\nTotal de Cópias: " + copiasTotal +
                "\nCategoria: " + (categoria != null ? categoria.getNome() : "Não categorizado");
    }
}