package com.biblioteca.controllers;

import com.biblioteca.models.Biblioteca;
import com.biblioteca.models.Categoria;
import com.biblioteca.models.Livro;
import com.biblioteca.utils.FileManager;
import com.biblioteca.utils.MenuUtils;
import java.util.List;

public class CategoriaController {
    private final Biblioteca biblioteca;

    public CategoriaController(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
    }

    public void menuCategorias() {
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 5,
                    "=== Gerenciar Categorias ===\n" +
                            "1. Adicionar Categoria\n" +
                            "2. Listar Categorias\n" +
                            "3. Buscar Categoria\n" +
                            "4. Editar Categoria\n" +
                            "5. Remover Categoria");

            switch (opcao) {
                case 1:
                    adicionarCategoria();
                    break;
                case 2:
                    biblioteca.listarCategorias();
                    break;
                case 3:
                    menuBuscarCategoria();
                    break;
                case 4:
                    editarCategoria();
                    break;
                case 5:
                    removerCategoria();
                    break;
                case -1:
                    return;
            }
        }
    }

    private void adicionarCategoria() {
        String nome = MenuUtils.lerString("Nome da categoria: ");
        if (nome == null) return;

        String codigo = MenuUtils.lerString("Código da categoria: ");
        if (codigo == null) return;

        if (biblioteca.buscarPorCodigo(codigo) != null) {
            System.out.println("Já existe uma categoria com este código!");
            return;
        }

        try {
            Categoria novaCategoria = new Categoria(nome, codigo);
            biblioteca.adicionarCategorias(novaCategoria);
            FileManager.salvarDados(biblioteca);
            System.out.println("Categoria adicionada com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao adicionar categoria: " + e.getMessage());
        }
    }

    private void menuBuscarCategoria() {
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 2,
                    "=== Buscar Categoria ===\n" +
                            "1. Buscar por código\n" +
                            "2. Buscar por nome");

            switch (opcao) {
                case 1:
                    buscarPorCodigo();
                    break;
                case 2:
                    buscarPorNome();
                    break;
                case -1:
                    return;
            }
        }
    }

    private void buscarPorCodigo() {
        String codigo = MenuUtils.lerString("Digite o código: ");
        if (codigo == null) return;

        Categoria categoria = biblioteca.buscarPorCodigo(codigo);
        exibirResultadoBusca(categoria);
    }

    private void buscarPorNome() {
        String nome = MenuUtils.lerString("Digite o nome: ");
        if (nome == null) return;

        Categoria categoria = biblioteca.buscarPorNome(nome);
        exibirResultadoBusca(categoria);
    }

    private void exibirResultadoBusca(Categoria categoria) {
        if (categoria != null) {
            System.out.println("\nCategoria encontrada:");
            System.out.println("Nome: " + categoria.getNome());
            System.out.println("Código: " + categoria.getCodigo());

            System.out.println("\nLivros desta categoria:");
            List<Livro> livrosDaCategoria = biblioteca.getLivrosPorCategoria(categoria);
            if (livrosDaCategoria.isEmpty()) {
                System.out.println("Nenhum livro cadastrado nesta categoria.");
            } else {
                for (Livro livro : livrosDaCategoria) {
                    System.out.println("- " + livro.getTitulo() + " (ISBN: " + livro.getCodigoIsbn() + ")");
                }
            }
        } else {
            System.out.println("Categoria não encontrada!");
        }
    }

    private void editarCategoria() {
        String codigo = MenuUtils.lerString("Digite o código da categoria que deseja editar: ");
        if (codigo == null) return;

        Categoria categoria = biblioteca.buscarPorCodigo(codigo);
        if (categoria == null) {
            System.out.println("Categoria não encontrada!");
            return;
        }

        System.out.println("\nDados atuais:");
        System.out.println("Nome: " + categoria.getNome());
        System.out.println("Código: " + categoria.getCodigo());

        String novoNome = MenuUtils.lerString("\nDigite o novo nome (ou deixe vazio para manter o atual): ");
        if (novoNome == null) return;

        String novoCodigo = MenuUtils.lerString("Digite o novo código (ou deixe vazio para manter o atual): ");
        if (novoCodigo == null) return;

        try {
            biblioteca.editarCategoria(codigo,
                    novoNome.isEmpty() ? categoria.getNome() : novoNome,
                    novoCodigo.isEmpty() ? categoria.getCodigo() : novoCodigo);
            FileManager.salvarDados(biblioteca);
            System.out.println("Categoria editada com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao editar categoria: " + e.getMessage());
        }
    }

    private void removerCategoria() {
        String codigo = MenuUtils.lerString("Digite o código da categoria a ser removida: ");
        if (codigo == null) return;

        Categoria categoria = biblioteca.buscarPorCodigo(codigo);
        if (categoria == null) {
            System.out.println("Categoria não encontrada!");
            return;
        }

        List<Livro> livrosDaCategoria = biblioteca.getLivrosPorCategoria(categoria);
        if (!livrosDaCategoria.isEmpty()) {
            System.out.println("\nAtenção: Esta categoria possui livros cadastrados!");
            System.out.println("Quantidade de livros: " + livrosDaCategoria.size());

            Boolean confirmar = MenuUtils.lerSimNao("Deseja realmente remover a categoria? Os livros ficarão sem categoria.");
            if (confirmar == null || !confirmar) {
                System.out.println("Operação cancelada.");
                return;
            }
        }

        try {
            if (biblioteca.removerCategoria(codigo)) {
                FileManager.salvarDados(biblioteca);
                System.out.println("Categoria removida com sucesso!");
            }
        } catch (Exception e) {
            System.out.println("Erro ao remover categoria: " + e.getMessage());
        }
    }
}