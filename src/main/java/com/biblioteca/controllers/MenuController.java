package com.biblioteca.controllers;

import com.biblioteca.models.Biblioteca;
import com.biblioteca.models.Leitor;
import com.biblioteca.models.Usuario;
import com.biblioteca.models.Categoria;
import com.biblioteca.models.Livro;
import com.biblioteca.utils.FileManager;
import com.biblioteca.utils.MenuUtils;
import com.biblioteca.utils.ValidationUtils;

public class MenuController {
    private final Biblioteca biblioteca;
    private final LivroController livroController;
    private final CategoriaController categoriaController;
    private final EmprestimoController emprestimoController;
    private String usuarioLogado;
    private Leitor leitorLogado;

    public MenuController(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        this.livroController = new LivroController(biblioteca);
        this.categoriaController = new CategoriaController(biblioteca);
        this.emprestimoController = new EmprestimoController(biblioteca);
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

        String senha = MenuUtils.lerString("Digite sua senha: ");
        if (senha == null) return;

        // Validate credentials using ValidationUtils
        if (!ValidationUtils.isValidCredentials(login, senha)) {
            System.out.println("Credenciais inválidas! A senha deve ter pelo menos 6 caracteres.");
            return;
        }

        Boolean isAdmin = MenuUtils.lerSimNao("Você é um administrador?");
        if (isAdmin == null) return;

        if (isAdmin) {
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

            Leitor novoLeitor = new Leitor(nome, email);
            String validationError = ValidationUtils.validarLeitor(novoLeitor, biblioteca.getLeitores());

            if (validationError != null) {
                System.out.println("Erro de validação: " + validationError);
                return;
            }

            Usuario novoUsuario = new Usuario(login, senha, false, novoLeitor);
            Usuario.getUsuarios().add(novoUsuario);
            System.out.println("Leitor cadastrado com sucesso!");
            usuarioLogado = login;
            leitorLogado = novoLeitor;
            menuLeitor();
        }

        FileManager.salvarDados(biblioteca);
    }
    
    private void menuAdmin() {
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 8,
                    "=== Menu Administrador ===\n" +
                            "1. Gerenciar Categorias\n" +
                            "2. Gerenciar Livros\n" +
                            "3. Gerenciar Empréstimos\n" +
                            "4. Listar Todos Empréstimos\n" +
                            "5. Mostrar Banco de Dados\n" +
                            "6. Carregar Dados Exemplo\n" +
                            "7. Limpar Banco de Dados\n" +
                            "8. Logout");

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
                    biblioteca.listarEmprestimos();
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
                        biblioteca.getLivros().clear();
                        biblioteca.getCategorias().clear();
                        biblioteca.getEmprestimos().clear();
                        biblioteca.getLeitores().clear();
                    }
                    break;
                case 8:
                case -1:
                    logout();
                    return;
            }
        }
    }

    private void menuLeitor() {
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
        Boolean confirmar = MenuUtils.lerSimNao(
                "ATENÇÃO: Isso irá substituir todos os dados existentes! Deseja continuar?");

        if (confirmar == null || !confirmar) {
            System.out.println("Operação cancelada.");
            return;
        }

        System.out.println("\n=== Carregando Dados Exemplo ===");

        Categoria literatura = new Categoria("Literatura", "LIT");
        Categoria ficcaoCientifica = new Categoria("Ficção Científica", "FIC");
        Categoria romance = new Categoria("Romance", "ROM");
        Categoria tecnico = new Categoria("Técnico", "TEC");

        biblioteca.adicionarCategorias(literatura);
        biblioteca.adicionarCategorias(ficcaoCientifica);
        biblioteca.adicionarCategorias(romance);
        biblioteca.adicionarCategorias(tecnico);

        Livro livro1 = new Livro("1984", "George Orwell", "123456", 5, ficcaoCientifica);
        Livro livro2 = new Livro("O Senhor dos Anéis", "J.R.R. Tolkien", "789012", 3, ficcaoCientifica);
        Livro livro3 = new Livro("Dom Casmurro", "Machado de Assis", "345678", 4, literatura);
        Livro livro4 = new Livro("Clean Code", "Robert C. Martin", "901234", 2, tecnico);
        Livro livro5 = new Livro("Orgulho e Preconceito", "Jane Austen", "567890", 3, romance);

        biblioteca.adicionarLivro(livro1);
        biblioteca.adicionarLivro(livro2);
        biblioteca.adicionarLivro(livro3);
        biblioteca.adicionarLivro(livro4);
        biblioteca.adicionarLivro(livro5);

        FileManager.salvarDados(biblioteca);
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