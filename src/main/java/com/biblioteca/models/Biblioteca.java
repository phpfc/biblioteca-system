package com.biblioteca.models;

import com.biblioteca.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Livro> getLivros() { return livros; }
    public void setLivros(List<Livro> livros) { this.livros = livros; }
    public List<Categoria> getCategorias() { return categorias; }
    public void setCategorias(List<Categoria> categorias) { this.categorias = categorias; }
    public List<Emprestimo> getEmprestimos() { return emprestimos; }
    public void setEmprestimos(List<Emprestimo> emprestimos) { this.emprestimos = emprestimos; }
    public List<Leitor> getLeitores() { return leitores; }
    public void setLeitores(List<Leitor> leitores) { this.leitores = leitores; }
    public int getLimiteEmprestimosPorLeitor() { return LIMITE_EMPRESTIMOS; }

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

        public void listarCategorias() {
            System.out.println("\n=== Lista de Categorias ===");
            if (categorias.isEmpty()) {
                System.out.println("Nenhuma categoria cadastrada.");
                return;
            }

            for (Categoria categoria : categorias) {
                System.out.println("\nCódigo: " + categoria.getCodigo());
                System.out.println("Nome: " + categoria.getNome());

                long qtdLivros = livros.stream()
                        .filter(l -> l.getCategoria() != null &&
                                l.getCategoria().equals(categoria))
                        .count();
                System.out.println("Quantidade de livros: " + qtdLivros);
                System.out.println("----------------------------------------");
            }
        }

    public void adicionarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula");
        }
        if (buscarPorCodigo(categoria.getCodigo()) != null) {
            throw new IllegalArgumentException("Já existe uma categoria com este código");
        }
        categorias.add(categoria);
    }

    public Categoria buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return null;
        }
        return categorias.stream()
                .filter(c -> c.getNome().toLowerCase().contains(nome.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    public void editarCategoria(String codigoAtual, String novoNome, String novoCodigo) {
        Categoria categoria = buscarPorCodigo(codigoAtual);
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não encontrada");
        }

        if (!novoCodigo.equals(codigoAtual) && buscarPorCodigo(novoCodigo) != null) {
            throw new IllegalArgumentException("Já existe uma categoria com este código");
        }

        categoria.setNome(novoNome);
        categoria.setCodigo(novoCodigo);
    }

    public List<Emprestimo> consultarEmprestimosLeitorPorTermo(Leitor leitor, String termo) {
        return emprestimos.stream()
                .filter(e -> e.getLeitor().equals(leitor) &&
                        (e.getLivro().getTitulo().toLowerCase().contains(termo.toLowerCase()) ||
                                e.getLivro().getAutor().toLowerCase().contains(termo.toLowerCase())))
                .collect(Collectors.toList());
    }

    public List<Emprestimo> consultarEmprestimosLeitorPorCodigo(Leitor leitor, String isbn) {
        return emprestimos.stream()
                .filter(e -> e.getLeitor().equals(leitor) &&
                        e.getLivro().getCodigoIsbn().equals(isbn))
                .collect(Collectors.toList());
    }

    public void marcarComoDevolvidoAdmin(String isbn, String emailLeitor) {
        Emprestimo emprestimo = emprestimos.stream()
                .filter(e -> e.getLivro().getCodigoIsbn().equals(isbn) &&
                        e.getLeitor().getEmail().equals(emailLeitor) &&
                        e.getDataDevolucao() == null)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado"));

        emprestimo.setDataDevolucao(new Date());
        emprestimo.getLivro().setCopiasDisponiveis(
                emprestimo.getLivro().getCopiasDisponiveis() + 1
        );
    }

    public void alterarDataDevolucaoAdmin(String isbn, String emailLeitor, Date novaData) {
        Emprestimo emprestimo = emprestimos.stream()
                .filter(e -> e.getLivro().getCodigoIsbn().equals(isbn) &&
                        e.getLeitor().getEmail().equals(emailLeitor) &&
                        e.getDataDevolucao() == null)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado"));

        if (novaData.before(emprestimo.getDataEmprestimo())) {
            throw new IllegalArgumentException(
                    "Nova data não pode ser anterior à data do empréstimo"
            );
        }

        emprestimo.setDataPrevistaDevolucao(novaData);
    }

    public List<Livro> pesquisarLivros(String termo) {
        return livros.stream()
                .filter(l -> l.getTitulo().toLowerCase().contains(termo.toLowerCase()) ||
                        l.getAutor().toLowerCase().contains(termo.toLowerCase()) ||
                        l.getCodigoIsbn().equals(termo))
                .collect(Collectors.toList());
    }

    public void exibirResultadosPesquisa(List<Livro> resultados) {
        if (resultados.isEmpty()) {
            System.out.println("Nenhum livro encontrado!");
            return;
        }

        System.out.println("\n=== Resultados da Pesquisa ===");
        for (int i = 0; i < resultados.size(); i++) {
            Livro livro = resultados.get(i);
            System.out.println("\n" + (i + 1) + ". " + livro.getTitulo());
            System.out.println("   Autor: " + livro.getAutor());
            System.out.println("   ISBN: " + livro.getCodigoIsbn());
            System.out.println("   Disponíveis: " + livro.getCopiasDisponiveis());
            System.out.println("   Total: " + livro.getCopiasTotal());
            if (livro.getCategoria() != null) {
                System.out.println("   Categoria: " + livro.getCategoria().getNome());
            }
            System.out.println("----------------------------------------");
        }
    }

    public void adicionarCategorias(Categoria categoria) {
        adicionarCategoria(categoria);
    }

    public boolean removerCategoria(String codigo) {
        Categoria categoria = buscarPorCodigo(codigo);
        if (categoria == null) {
            return false;
        }

        for (Livro livro : livros) {
            if (livro.getCategoria() != null && livro.getCategoria().equals(categoria)) {
                livro.setCategoria(null);
            }
        }

        return categorias.remove(categoria);
    }

    public void adicionarLivro(Livro livro) {
        if (buscarPorIsbn(livro.getCodigoIsbn()) != null) {
            throw new IllegalArgumentException("Livro com este ISBN já existe");
        }
        livros.add(livro);
    }

    public void editarLivro(String isbn, String novoTitulo, String novoAutor, Integer novasCopias, Categoria novaCategoria) {
        Livro livro = buscarPorIsbn(isbn);
        if (livro == null) {
            throw new IllegalArgumentException("Livro não encontrado");
        }

        if (novoTitulo != null && !novoTitulo.trim().isEmpty()) {
            livro.setTitulo(novoTitulo);
        }

        if (novoAutor != null && !novoAutor.trim().isEmpty()) {
            livro.setAutor(novoAutor);
        }

        if (novasCopias != null) {
            int copiasEmprestadas = livro.getCopiasTotal() - livro.getCopiasDisponiveis();
            if (novasCopias < copiasEmprestadas) {
                throw new IllegalArgumentException(
                        "Não é possível reduzir o número de cópias abaixo do número de livros emprestados"
                );
            }

            int novasCopiasDisponiveis = novasCopias - copiasEmprestadas;
            livro.setCopiasTotal(novasCopias);
            livro.setCopiasDisponiveis(novasCopiasDisponiveis);
        }

        if (novaCategoria != null) {
            livro.setCategoria(novaCategoria);
        }
    }

    public boolean removerLivro(String isbn) {
        Livro livro = buscarPorIsbn(isbn);
        if (livro == null) {
            return false;
        }

        boolean temEmprestimosAtivos = emprestimos.stream()
                .anyMatch(e -> e.getLivro().equals(livro) && e.getDataDevolucao() == null);

        if (temEmprestimosAtivos) {
            throw new IllegalStateException("Não é possível remover um livro com empréstimos ativos");
        }

        return livros.remove(livro);
    }

    public void realizarEmprestimo(Leitor leitor, String isbn, Date dataPrevistaDevolucao) {
        if (!ValidationUtils.isDataFutura(dataPrevistaDevolucao)) {
            throw new IllegalArgumentException("Data de devolução deve ser futura");
        }

        Livro livro = buscarPorIsbn(isbn);
        if (livro == null) {
            throw new IllegalArgumentException("Livro não encontrado");
        }

        Emprestimo novoEmprestimo = new Emprestimo(leitor, livro, new Date(), dataPrevistaDevolucao);
        String validationError = ValidationUtils.validarEmprestimo(novoEmprestimo, this);

        if (validationError != null) {
            throw new IllegalArgumentException(validationError);
        }

        livro.setCopiasDisponiveis(livro.getCopiasDisponiveis() - 1);
        emprestimos.add(novoEmprestimo);
    }

    public void devolverLivro(Leitor leitor, String isbn) {
        Emprestimo emprestimo = emprestimos.stream()
                .filter(e -> e.getLivro().getCodigoIsbn().equals(isbn) &&
                        e.getLeitor().equals(leitor) &&
                        e.getDataDevolucao() == null)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado"));

        emprestimo.setDataDevolucao(new Date());
        emprestimo.getLivro().setCopiasDisponiveis(
                emprestimo.getLivro().getCopiasDisponiveis() + 1
        );
    }

    public List<Emprestimo> consultarEmprestimosAtivos(Leitor leitor) {
        return emprestimos.stream()
                .filter(e -> e.getLeitor().equals(leitor) && e.getDataDevolucao() == null)
                .collect(Collectors.toList());
    }

    public List<Emprestimo> consultarEmprestimosPorLivro(String isbn) {
        return emprestimos.stream()
                .filter(e -> e.getLivro().getCodigoIsbn().equals(isbn))
                .collect(Collectors.toList());
    }

    public List<Emprestimo> consultarEmprestimosLeitorPorPeriodo(Leitor leitor, Date inicio, Date fim) {
        return emprestimos.stream()
                .filter(e -> e.getLeitor().equals(leitor) &&
                        e.getDataEmprestimo().after(inicio) &&
                        e.getDataEmprestimo().before(fim))
                .collect(Collectors.toList());
    }

    public List<Emprestimo> consultarEmprestimosPorLeitor(String emailLeitor) {
        return emprestimos.stream()
                .filter(e -> e.getLeitor().getEmail().equals(emailLeitor))
                .collect(Collectors.toList());
    }

    public void listarLivros() {
        System.out.println("\n=== Lista de Livros ===");
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
            return;
        }

        for (Livro livro : livros) {
            System.out.println("\n" + livro.informaLivro());
            System.out.println("----------------------------------------");
        }
    }

    public void listarLivrosDisponiveis() {
        System.out.println("\n=== Livros Disponíveis ===");
        livros.stream()
                .filter(Livro::temCopiaDisponivel)
                .forEach(livro -> {
                    System.out.println("\n" + livro.informaLivro());
                    System.out.println("----------------------------------------");
                });
    }

    public void listarEmprestimos() {
        System.out.println("\n=== Lista de Empréstimos ===");
        if (emprestimos.isEmpty()) {
            System.out.println("Nenhum empréstimo registrado.");
            return;
        }

        for (Emprestimo emp : emprestimos) {
            System.out.println("\nLeitor: " + emp.getLeitor().getNome());
            System.out.println("Livro: " + emp.getLivro().getTitulo());
            System.out.println("Data do empréstimo: " + emp.getDataEmprestimo());
            System.out.println("Data prevista devolução: " + emp.getDataPrevistaDevolucao());
            if (emp.getDataDevolucao() != null) {
                System.out.println("Devolvido em: " + emp.getDataDevolucao());
            }
            System.out.println("----------------------------------------");
        }
    }

    public List<Livro> getLivrosPorCategoria(Categoria categoria) {
        return livros.stream()
                .filter(l -> l.getCategoria() != null && l.getCategoria().equals(categoria))
                .collect(Collectors.toList());
    }
}