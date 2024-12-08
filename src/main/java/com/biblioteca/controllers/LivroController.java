package com.biblioteca.controllers;

import com.biblioteca.models.Biblioteca;
import com.biblioteca.models.Livro;
import com.biblioteca.models.Categoria;
import com.biblioteca.utils.FileManager;
import com.biblioteca.models.Usuario;
import com.biblioteca.utils.MenuUtils;
import java.util.List;

public class LivroController {
    private final Biblioteca biblioteca;

    public LivroController(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
    }

    public void menuLivros(String usuarioLogado) {
        while (true) {
            String menuOptions = "=== Menu Livros ===\n" +
                    "1. Pesquisar Livros\n" +
                    "2. Listar Todos os Livros\n";

            boolean isAdmin = Usuario.isAdmin(usuarioLogado);
            int maxOption = 2;

            if (isAdmin) {
                menuOptions += "3. Adicionar Livro\n" +
                        "4. Editar Livro\n" +
                        "5. Remover Livro\n";
                maxOption = 5;
            }

            int opcao = MenuUtils.lerOpcaoMenu(1, maxOption, menuOptions);

            switch (opcao) {
                case 1:
                    pesquisarLivros();
                    break;
                case 2:
                    biblioteca.listarLivros();
                    break;
                case 3:
                    if (isAdmin) adicionarLivro();
                    break;
                case 4:
                    if (isAdmin) editarLivro();
                    break;
                case 5:
                    if (isAdmin) removerLivro();
                    break;
                case -1:
                    return;
            }
        }
    }

    private void pesquisarLivros() {
        String termo = MenuUtils.lerString("\nDigite o termo de pesquisa (ISBN, título ou autor): ");
        if (termo == null) return;

        List<Livro> resultados = biblioteca.pesquisarLivros(termo);
        exibirResultadosPesquisa(resultados);
    }

    private void exibirResultadosPesquisa(List<Livro> livros) {
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro encontrado!");
            return;
        }

        System.out.println("\n=== Resultados da Pesquisa ===");
        int i = 1;
        for (Livro livro : livros) {
            System.out.println("\n#" + i++);
            System.out.println(livro.informaLivro());
            System.out.println("Cópias disponíveis: " + livro.getCopiasDisponiveis());
            System.out.println("Total de cópias: " + livro.getCopiasTotal());
            System.out.println("----------------------------------------");
        }
    }

    private void adicionarLivro() {
        // Verificar se existem categorias cadastradas
        List<Categoria> categorias = biblioteca.getCategorias();
        if (categorias.isEmpty()) {
            System.out.println("Não há categorias cadastradas. Por favor, cadastre uma categoria primeiro.");
            return;
        }

        String titulo = MenuUtils.lerString("Título do livro: ");
        if (titulo == null) return;

        String autor = MenuUtils.lerString("Autor do livro: ");
        if (autor == null) return;

        String isbn = MenuUtils.lerString("ISBN do livro: ");
        if (isbn == null) return;

        // Verificar se já existe livro com este ISBN
        if (biblioteca.buscarPorIsbn(isbn) != null) {
            System.out.println("Já existe um livro cadastrado com este ISBN!");
            return;
        }

        Integer copias = MenuUtils.lerInteiro("Número de cópias: ");
        if (copias == null || copias < 1) {
            System.out.println("O número de cópias deve ser maior que zero!");
            return;
        }

        // Mostrar categorias disponíveis
        System.out.println("\nCategorias disponíveis:");
        for (Categoria cat : categorias) {
            System.out.println("Código: " + cat.getCodigo() + " - Nome: " + cat.getNome());
        }

        String codCategoria = MenuUtils.lerString("\nDigite o código da categoria desejada: ");
        if (codCategoria == null) return;

        Categoria categoria = biblioteca.buscarPorCodigo(codCategoria);
        if (categoria == null) {
            System.out.println("Categoria não encontrada!");
            return;
        }

        try {
            Livro novoLivro = new Livro(titulo, autor, isbn, copias, categoria);
            biblioteca.adicionarLivro(novoLivro);
            FileManager.salvarDados(biblioteca);
            System.out.println("Livro adicionado com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao adicionar livro: " + e.getMessage());
        }
    }

    private void editarLivro() {
        String isbn = MenuUtils.lerString("Digite o ISBN do livro que deseja editar: ");
        if (isbn == null) return;

        Livro livro = biblioteca.buscarPorIsbn(isbn);
        if (livro == null) {
            System.out.println("Livro não encontrado!");
            return;
        }

        System.out.println("\nDados atuais:");
        System.out.println(livro.informaLivro());
        System.out.println("Cópias totais: " + livro.getCopiasTotal());
        System.out.println("Cópias disponíveis: " + livro.getCopiasDisponiveis());

        String novoTitulo = MenuUtils.lerString("\nNovo título (ou vazio para manter): ");
        if (novoTitulo == null) return;

        String novoAutor = MenuUtils.lerString("Novo autor (ou vazio para manter): ");
        if (novoAutor == null) return;

        String novasCopiasStr = MenuUtils.lerString("Novo número de cópias (ou vazio para manter): ");
        Integer novasCopias = null;
        if (novasCopiasStr != null && !novasCopiasStr.isEmpty()) {
            try {
                novasCopias = Integer.parseInt(novasCopiasStr);
                if (novasCopias < livro.getCopiasTotal() - livro.getCopiasDisponiveis()) {
                    System.out.println("Não é possível reduzir para um número menor que as cópias emprestadas!");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Número de cópias inválido!");
                return;
            }
        }

        // Mostrar categorias disponíveis
        System.out.println("\nCategorias disponíveis:");
        for (Categoria cat : biblioteca.getCategorias()) {
            System.out.println("Código: " + cat.getCodigo() + " - Nome: " + cat.getNome());
        }

        String novaCategoriaStr = MenuUtils.lerString("\nNovo código da categoria (ou vazio para manter): ");
        Categoria novaCategoria = null;
        if (novaCategoriaStr != null && !novaCategoriaStr.isEmpty()) {
            novaCategoria = biblioteca.buscarPorCodigo(novaCategoriaStr);
            if (novaCategoria == null) {
                System.out.println("Categoria não encontrada!");
                return;
            }
        }

        try {
            biblioteca.editarLivro(isbn,
                    novoTitulo.isEmpty() ? livro.getTitulo() : novoTitulo,
                    novoAutor.isEmpty() ? livro.getAutor() : novoAutor,
                    novasCopias,
                    novaCategoria);
            FileManager.salvarDados(biblioteca);
            System.out.println("Livro editado com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao editar livro: " + e.getMessage());
        }
    }

    private void removerLivro() {
        String isbn = MenuUtils.lerString("Digite o ISBN do livro a ser removido: ");
        if (isbn == null) return;

        Livro livro = biblioteca.buscarPorIsbn(isbn);
        if (livro == null) {
            System.out.println("Livro não encontrado!");
            return;
        }

        // Verificar se há empréstimos ativos
        if (livro.getCopiasTotal() > livro.getCopiasDisponiveis()) {
            System.out.println("\nAtenção: Este livro possui cópias emprestadas!");
            System.out.println("Cópias emprestadas: " + (livro.getCopiasTotal() - livro.getCopiasDisponiveis()));

            Boolean confirmar = MenuUtils.lerSimNao("Deseja realmente remover o livro? Isso afetará os registros de empréstimo.");
            if (confirmar == null || !confirmar) {
                System.out.println("Operação cancelada.");
                return;
            }
        }

        try {
            if (biblioteca.removerLivro(isbn)) {
                FileManager.salvarDados(biblioteca);
                System.out.println("Livro removido com sucesso!");
            }
        } catch (Exception e) {
            System.out.println("Erro ao remover livro: " + e.getMessage());
        }
    }
}