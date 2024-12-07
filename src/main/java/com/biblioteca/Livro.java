package com.biblioteca;

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

    public String informaLivro() {
        return "Código: " + codigoIsbn +
                "\nTítulo: " + titulo +
                "\nAutor: " + autor +
                "\nCópias disponíveis: " + copiasDisponiveis + "/" + copiasTotal +
                "\nCategoria: " + (categoria != null ? categoria.getNome() : "Nenhuma")
                + "\n";
    }

    public boolean temCopiaDisponivel() {
        return copiasDisponiveis > 0;
    }

    public void emprestarCopia() {
        if (copiasDisponiveis > 0) {
            copiasDisponiveis--;
        }
    }

    public void devolverCopia() {
        if (copiasDisponiveis < copiasTotal) {
            copiasDisponiveis++;
        }
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getCodigoIsbn() { return codigoIsbn; }
    public int getCopiasTotal() { return copiasTotal; }
    public void setCopiasTotal(int copias) { this.copiasTotal = copias; }
    public int getCopiasDisponiveis() { return copiasDisponiveis; }
    public void setCopiasDisponiveis(int copias) { this.copiasDisponiveis = copias; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}