package com.biblioteca.controllers;

import com.biblioteca.models.*;
import com.biblioteca.utils.FileManager;
import com.biblioteca.utils.MenuUtils;

import java.util.List;

public class MenuController {
    private final SistemaBibliotecas sistema;
    private Biblioteca bibliotecaAtual;
    private LivroController livroController;
    private CategoriaController categoriaController;
    private EmprestimoController emprestimoController;
    private String usuarioLogado;
    private Leitor leitorLogado;

    public MenuController(SistemaBibliotecas sistema) {
        this.sistema = sistema;
        // Se há apenas uma biblioteca, seleciona ela automaticamente
        if (!sistema.getBibliotecas().isEmpty()) {
            this.bibliotecaAtual = sistema.getBibliotecas().get(0);
            atualizarControllers();
        }

        this.livroController = new LivroController(bibliotecaAtual);
        this.categoriaController = new CategoriaController(bibliotecaAtual);
        this.emprestimoController = new EmprestimoController(bibliotecaAtual);
    }

    private void cadastrarNovaBiblioteca() {
        String nome = MenuUtils.lerString("Nome da biblioteca: ");
        if (nome == null) return;

        String endereco = MenuUtils.lerString("Endereço da biblioteca: ");
        if (endereco == null) return;

        sistema.adicionarBiblioteca(nome, endereco);
        FileManager.salvarDados(sistema);
        System.out.println("Biblioteca cadastrada com sucesso!");
    }

    private void menuGerenciarBibliotecas() {
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 4,
                    "=== Gerenciar Bibliotecas ===\n" +
                            "1. Cadastrar Nova Biblioteca\n" +
                            "2. Listar Bibliotecas\n" +
                            "3. Editar Biblioteca\n" +
                            "4. Remover Biblioteca");

            switch (opcao) {
                case 1:
                    cadastrarNovaBiblioteca();
                    break;
                case 2:
                    listarBibliotecas();
                    break;
                case 3:
                    editarBiblioteca();
                    break;
                case 4:
                    removerBiblioteca();
                    break;
                case -1:
                    return;
            }
        }
    }

    private void atualizarControllers() {
        this.livroController = new LivroController(bibliotecaAtual);
        this.categoriaController = new CategoriaController(bibliotecaAtual);
        this.emprestimoController = new EmprestimoController(bibliotecaAtual);
    }

    private void menuSelecionarBiblioteca() {
        while (true) {
            System.out.println("\n=== Bibliotecas Disponíveis ===");
            List<Biblioteca> bibliotecas = sistema.getBibliotecas();

            if (bibliotecas.isEmpty()) {
                System.out.println("Nenhuma biblioteca cadastrada.");
                if (Usuario.isAdmin(usuarioLogado)) {
                    if (MenuUtils.lerSimNao("Deseja cadastrar uma nova biblioteca?")) {
                        cadastrarNovaBiblioteca();
                    }
                }
                return;
            }

            for (int i = 0; i < bibliotecas.size(); i++) {
                Biblioteca b = bibliotecas.get(i);
                System.out.println((i + 1) + ". " + b.getNome() + " - " + b.getEndereco());
            }

            Integer opcao = MenuUtils.lerInteiro("Selecione uma biblioteca (0 para voltar): ");
            if (opcao == null || opcao == 0) return;

            if (opcao > 0 && opcao <= bibliotecas.size()) {
                bibliotecaAtual = bibliotecas.get(opcao - 1);
                atualizarControllers(); // Atualiza os controllers ao trocar de biblioteca
                break;
            }
        }
    }

    private void listarBibliotecas() {
        System.out.println("\n=== Bibliotecas Disponíveis ===");
        List<Biblioteca> bibliotecas = sistema.getBibliotecas();

        if (bibliotecas.isEmpty()) {
            System.out.println("Nenhuma biblioteca cadastrada.");
            return;
        }

        for (Biblioteca b : bibliotecas) {
            System.out.println("Nome: " + b.getNome());
            System.out.println("Endereço: " + b.getEndereco());
            System.out.println("----------------------------------------");
        }
    }

    private void editarBiblioteca() {
        listarBibliotecas();

        String nome = MenuUtils.lerString("\nDigite o nome da biblioteca que deseja editar: ");
        if (nome == null) return;

        Biblioteca biblioteca = sistema.buscarBiblioteca(nome);
        if (biblioteca == null) {
            System.out.println("Biblioteca não encontrada!");
            return;
        }

        String novoNome = MenuUtils.lerString("Novo nome (ou vazio para manter): ");
        if (novoNome == null) return;

        String novoEndereco = MenuUtils.lerString("Novo endereço (ou vazio para manter): ");
        if (novoEndereco == null) return;

        if (!novoNome.isEmpty()) biblioteca.setNome(novoNome);
        if (!novoEndereco.isEmpty()) biblioteca.setEndereco(novoEndereco);

        FileManager.salvarDados(sistema);
        System.out.println("Biblioteca editada com sucesso!");
    }

    private void removerBiblioteca() {
        listarBibliotecas();

        String nome = MenuUtils.lerString("\nDigite o nome da biblioteca que deseja remover: ");
        if (nome == null) return;

        Boolean confirmar = MenuUtils.lerSimNao("Tem certeza que deseja remover esta biblioteca?");
        if (confirmar == null || !confirmar) {
            System.out.println("Operação cancelada.");
            return;
        }

        sistema.removerBiblioteca(nome);
        FileManager.salvarDados(sistema);
        System.out.println("Biblioteca removida com sucesso!");
    }
    public void menuLogin() {
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 3,
                    "=== Sistema de Biblioteca ===\n" +
                            "1. Login\n" +
                            "2. Realizar Cadastro\n" +
                            "3. Sair");

            switch (opcao) {
                case 1:
                    realizarLogin();
                    break;
                case 2:
                    realizarCadastro();
                    break;
                case 3:
                    System.out.println("Sistema encerrado.");
                    System.exit(0);
                    break;
            }
        }
    }

    private void realizarLogin() {
        String login = MenuUtils.lerString("Login: ");
        if (login == null) return;

        String senha = MenuUtils.lerString("Senha: ");
        if (senha == null) return;

        if (Usuario.autenticar(login, senha)) {
            Usuario usuarioAtual = null;
            for (Usuario usuario : Usuario.getUsuarios()) {
                if (usuario.getLogin().equals(login)) {
                    usuarioAtual = usuario;
                    break;
                }
            }

            if (usuarioAtual != null) {
                usuarioLogado = login;

                if (usuarioAtual.isAdmin()) {
                    System.out.println("Bem-vindo, Administrador!");
                    leitorLogado = null;
                    menuAdmin();
                } else {
                    leitorLogado = usuarioAtual.getLeitor();
                    if (leitorLogado != null) {
                        System.out.println("Bem-vindo, " + leitorLogado.getNome() + "!");
                        menuLeitor();
                    } else {
                        System.out.println("Erro: Dados do leitor não encontrados!");
                    }
                }
            }
        } else {
            System.out.println("Login ou senha inválidos!");
        }
    }

    private void realizarCadastro() {
        String login = MenuUtils.lerString("Digite seu login: ");
        if (login == null) return;

        // Verificar se login já existe
        for (Usuario u : Usuario.getUsuarios()) {
            if (u.getLogin().equals(login)) {
                System.out.println("Este login já está em uso!");
                return;
            }
        }

        String senha = MenuUtils.lerString("Digite sua senha: ");
        if (senha == null) return;

        Boolean isAdmin = MenuUtils.lerSimNao("Você é um administrador?");
        if (isAdmin == null) return;

        if (isAdmin) {
            // Verificar se já existe outro admin
            if (!Usuario.getUsuarios().isEmpty() && !Usuario.isAdmin(usuarioLogado)) {
                System.out.println("Apenas administradores podem criar novos administradores!");
                return;
            }

            Usuario novoUsuario = new Usuario(login, senha, true);
            Usuario.getUsuarios().add(novoUsuario);
            System.out.println("Administrador cadastrado com sucesso!");
            usuarioLogado = login;
            leitorLogado = null;
            menuAdmin();
        } else {
            String nome = MenuUtils.lerString("Nome completo: ");
            if (nome == null) return;

            String email = MenuUtils.lerString("E-mail: ");
            if (email == null) return;

            // Validar email
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                System.out.println("E-mail inválido!");
                return;
            }

            // Verificar se email já está cadastrado
            for (Usuario u : Usuario.getUsuarios()) {
                if (u.getLeitor() != null && u.getLeitor().getEmail().equals(email)) {
                    System.out.println("Este e-mail já está cadastrado!");
                    return;
                }
            }

            Leitor novoLeitor = new Leitor(nome, email);
            Usuario novoUsuario = new Usuario(login, senha, false, novoLeitor);
            Usuario.getUsuarios().add(novoUsuario);
            System.out.println("Leitor cadastrado com sucesso!");
            usuarioLogado = login;
            leitorLogado = novoLeitor;
            menuLeitor();
        }

        FileManager.salvarDados(sistema);
    }

    private void menuAdmin() {
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 8,
                    "=== Menu Administrador ===\n" +
                            "1. Gerenciar Categorias\n" +
                            "2. Gerenciar Livros\n" +
                            "3. Gerenciar Empréstimos\n" +
                            "4. Listar TOndeodos Empréstimos\n" +
                            "5. Mostrar Banco de Dados\n" +
                            "6. Carregar Dados Exemplo\n" +
                            "7. Limpar Banco de Dados\n" +
                            "8. Gerenciar Bibliotecas\n" +
                            "9. Logout");

            switch (opcao) {
                case 1:
                    categoriaController.menuCategorias();
                    break;
                case 2:
                    livroController.menuLivros(usuarioLogado);
                    break;
                case 3:
                    emprestimoController.menuGerenciarEmprestimos();
                    break;
                case 4:
                    bibliotecaAtual .listarEmprestimos();
                    break;
                case 5:
                    FileManager.mostrarConteudoBanco();
                    break;
                case 6:
                    carregarDadosExemplo();
                    break;
                case 7:
                    if (confirmarLimparBanco()) {
                        FileManager.limparBanco();
                        bibliotecaAtual.getLivros().clear();
                        bibliotecaAtual.getCategorias().clear();
                        bibliotecaAtual.getEmprestimos().clear();
                        bibliotecaAtual.getLeitores().clear();
                    }
                    break;
                case 8:
                    menuGerenciarBibliotecas();
                    break;
                case 9:
                    menuSelecionarBiblioteca();
                    break;
                case 10:
                case -1:
                    logout();
                    return;
            }
        }
    }

    private void verificarBibliotecaSelecionada() {
        if (bibliotecaAtual == null) {
            System.out.println("\nNenhuma biblioteca selecionada!");
            menuSelecionarBiblioteca();
            if (bibliotecaAtual == null) { // Se ainda não selecionou
                throw new RuntimeException("É necessário selecionar uma biblioteca para continuar.");
            }
        }
    }

    private void menuLeitor() {
        verificarBibliotecaSelecionada();
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 4,
                    "=== Menu do Leitor ===\n" +
                            "1. Consultar Livros\n" +
                            "2. Meus Empréstimos\n" +
                            "3. Realizar Empréstimo/Devolução\n" +
                            "4. Logout");

            switch (opcao) {
                case 1:
                    livroController.menuLivros(usuarioLogado);
                    break;
                case 2:
                    emprestimoController.menuEmprestimosLeitor(leitorLogado);
                    break;
                case 3:
                    menuEmprestimosDevolucoes();
                    break;
                case 4:
                case -1:
                    logout();
                    return;
            }
        }
    }

    private void menuEmprestimosDevolucoes() {
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 3,
                    "=== Empréstimos e Devoluções ===\n" +
                            "1. Realizar Novo Empréstimo\n" +
                            "2. Devolver Livro\n" +
                            "3. Voltar");

            switch (opcao) {
                case 1:
                    emprestimoController.realizarEmprestimo(leitorLogado);
                    break;
                case 2:
                    emprestimoController.realizarDevolucao(leitorLogado);
                    break;
                case 3:
                case -1:
                    return;
            }
        }
    }

    private void carregarDadosExemplo() {
        verificarBibliotecaSelecionada();
        Boolean confirmar = MenuUtils.lerSimNao(
                "ATENÇÃO: Isso irá substituir todos os dados existentes! Deseja continuar?");

        if (confirmar == null || !confirmar) {
            System.out.println("Operação cancelada.");
            return;
        }

        System.out.println("\n=== Carregando Dados Exemplo ===");

        // Criar categorias exemplo
        Categoria literatura = new Categoria("Literatura", "LIT");
        Categoria ficcaoCientifica = new Categoria("Ficção Científica", "FIC");
        Categoria romance = new Categoria("Romance", "ROM");
        Categoria tecnico = new Categoria("Técnico", "TEC");

        bibliotecaAtual.adicionarCategorias(literatura);
        bibliotecaAtual.adicionarCategorias(ficcaoCientifica);
        bibliotecaAtual.adicionarCategorias(romance);
        bibliotecaAtual.adicionarCategorias(tecnico);

        // Criar livros exemplo
        Livro livro1 = new Livro("1984", "George Orwell", "123456", 5, ficcaoCientifica);
        Livro livro2 = new Livro("O Senhor dos Anéis", "J.R.R. Tolkien", "789012", 3, ficcaoCientifica);
        Livro livro3 = new Livro("Dom Casmurro", "Machado de Assis", "345678", 4, literatura);
        Livro livro4 = new Livro("Clean Code", "Robert C. Martin", "901234", 2, tecnico);
        Livro livro5 = new Livro("Orgulho e Preconceito", "Jane Austen", "567890", 3, romance);

        bibliotecaAtual.adicionarLivro(livro1);
        bibliotecaAtual.adicionarLivro(livro2);
        bibliotecaAtual.adicionarLivro(livro3);
        bibliotecaAtual.adicionarLivro(livro4);
        bibliotecaAtual.adicionarLivro(livro5);

        FileManager.salvarDados(sistema);
        System.out.println("Dados exemplo carregados com sucesso!");
    }

    private boolean confirmarLimparBanco() {
        System.out.println("\nATENÇÃO: Esta operação irá remover todos os dados do sistema!");
        System.out.println("Isso inclui:");
        System.out.println("- Todos os usuários (exceto o administrador principal)");
        System.out.println("- Todas as categorias");
        System.out.println("- Todos os livros");
        System.out.println("- Todos os empréstimos");

        Boolean confirmar = MenuUtils.lerSimNao("Tem certeza que deseja continuar?");
        return confirmar != null && confirmar;
    }

    private void logout() {
        usuarioLogado = null;
        leitorLogado = null;
        System.out.println("Logout realizado com sucesso!");
    }
}