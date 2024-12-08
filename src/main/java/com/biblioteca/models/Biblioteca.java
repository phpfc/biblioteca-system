package com.biblioteca.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Biblioteca {
    private String nome;
    private String endereco;
    private List<Livro> livros;
    private List<Categoria> categorias;
    private List<Emprestimo> emprestimos;
    private List<Leitor> leitores;
    private static final int LIMITE_EMPRESTIMOS = 3;

    public Biblioteca() {
        this.livros = new ArrayList<>();
        this.categorias = new ArrayList<>();
        this.emprestimos = new ArrayList<>();
        this.leitores = new ArrayList<>();
    }

    // Getters e Setters básicos
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public List<Livro> getLivros() { return livros; }
    public void setLivros(List<Livro> livros) { this.livros = livros; }
    public List<Categoria> getCategorias() { return categorias; }
    public void setCategorias(List<Categoria> categorias) { this.categorias = categorias; }
    public List<Emprestimo> getEmprestimos() { return emprestimos; }
    public void setEmprestimos(List<Emprestimo> emprestimos) { this.emprestimos = emprestimos; }
    public List<Leitor> getLeitores() { return leitores; }
    public void setLeitores(List<Leitor> leitores) { this.leitores = leitores; }
    public int getLimiteEmprestimosPorLeitor() { return LIMITE_EMPRESTIMOS; }

    // Métodos de negócio
    public List<Livro> buscarLivros(String termo) {
        List<Livro> resultados = new ArrayList<>();
        termo = termo.toLowerCase();
        for (Livro livro : livros) {
            if (livro.getTitulo().toLowerCase().contains(termo) ||
                    livro.getAutor().toLowerCase().contains(termo) ||
                    livro.getCodigoIsbn().equals(termo)) {
                resultados.add(livro);
            }
        }
        return resultados;
    }

    public Livro buscarPorIsbn(String isbn) {
        return livros.stream()
                .filter(l -> l.getCodigoIsbn().equals(isbn))
                .findFirst()
                .orElse(null);
    }

    public Categoria buscarPorCodigo(String codigo) {
        return categorias.stream()
                .filter(c -> c.getCodigo().equals(codigo))
                .findFirst()
                .orElse(null);
    }

    // ... outros métodos da classe Biblioteca
}