package com.biblioteca;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static Biblioteca biblioteca = new Biblioteca();
    private static String usuarioLogado = null;
    private static Leitor leitorlogado = null;

    public static void main(String[] args) {
        inicializarSistema();
        menuLogin();
    }

    private static void inicializarSistema() {
        FileManager.carregarDados(biblioteca);

        if (Usuario.getUsuarios() == null || Usuario.getUsuarios().isEmpty()) {
            System.out.println("\n=== Primeira Execução - Criação do Administrador ===");
            criarPrimeiroAdmin();
        }
    }


    private static void criarPrimeiroAdmin() {
        System.out.println("Para começar, vamos criar o usuário administrador.");

        System.out.println("Digite o login do administrador: ");
        String login = scanner.nextLine();

        System.out.println("Digite a senha do administrador: ");
        String senha = scanner.nextLine();

        Usuario admin = new Usuario(login, senha, true);
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(admin);
        Usuario.setUsuarios(usuarios);

        FileManager.salvarDados(biblioteca);
        System.out.println("Administrador criado com sucesso!");
    }

    private static void menuLogin() {
        while (true) {
            System.out.println("\n=== Sistema de Biblioteca ===");
            System.out.println("1. Login");
            System.out.println("2. Realizar Cadastro");
            System.out.println("3. Sair");

            int opcao = scanner.nextInt();
            scanner.nextLine();

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
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static void realizarCadastro() {
        System.out.println("Digite seu login: ");
        String login = scanner.nextLine();
        System.out.println("Digite sua senha: ");
        String senha = scanner.nextLine();

        System.out.println("É administrador? (s/n): ");
        String resposta = scanner.nextLine().toLowerCase();
        boolean isAdmin = resposta.equals("s");

        if (isAdmin) {
            System.out.println("Digite a senha de administrador: ");
            String senhaAdmin = scanner.nextLine();
            if (!senhaAdmin.equals("admin123")) {
                System.out.println("Senha de administrador inválida!");
                return;
            }

            Usuario novoUsuario = new Usuario(login, senha, true);
            Usuario.getUsuarios().add(novoUsuario);
            System.out.println("Administrador cadastrado com sucesso!");
            FileManager.salvarDados(biblioteca);
            menuAdmin();
        } else {
            System.out.println("Cadastro de Leitor: ");
            System.out.println("Nome: ");
            String nome = scanner.nextLine();
            System.out.println("E-mail: ");
            String email = scanner.nextLine();

            Leitor novoLeitor = new Leitor(nome, email);
            biblioteca.getLeitores().add(novoLeitor);

            Usuario novoUsuario = new Usuario(login, senha, false, novoLeitor);
            Usuario.getUsuarios().add(novoUsuario);

            System.out.println("Leitor cadastrado com sucesso!");
            FileManager.salvarDados(biblioteca);
            leitorlogado = novoLeitor;
            menuLeitor();
        }
    }

    private static void realizarLogin() {
        System.out.println("Login: ");
        String login = scanner.nextLine();
        System.out.println("Senha: ");
        String senha = scanner.nextLine();

        if (Usuario.autenticar(login, senha)) {
            Usuario usuarioAtual = null;

            for (Usuario usuario : Usuario.getUsuarios()) {
                if (usuario.getLogin().equals(login)) {
                    usuarioAtual = usuario;
                    break;
                }
            }

            if (usuarioAtual != null) {
                if (usuarioAtual.isAdmin()) {
                    System.out.println("Bem-vindo, Administrador!");
                    menuAdmin();
                } else {
                    leitorlogado = usuarioAtual.getLeitor();
                    System.out.println("Bem-vindo, " + leitorlogado.getNome());
                    menuLeitor();
                }
            }
        } else {
            System.out.println("Login ou senha inválidos!");
        }
    }

    private static void menuAdmin() {
        while (true) {
            System.out.println("\n=== Menu Administrador ===");
            System.out.println("1. Gerenciar Categorias");
            System.out.println("2. Gerenciar Livros");
            System.out.println("3. Listar Empréstimos");
            System.out.println("4. Mostrar Banco de Dados");
            System.out.println("5. Carregar Dados Exemplo");
            System.out.println("6. Limpar Banco de Dados");
            System.out.println("7. Logout");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    menuCategorias();
                    break;
                case 2:
                    menuLivros();
                    break;
                case 3:
                    biblioteca.listarEmprestimos();
                    break;
                case 4:
                    mostrarConteudoBanco();
                    break;
                case 5:
                    carregarDadosExemplo();
                    break;
                case 6:
                    limparBanco();
                    biblioteca = new Biblioteca();
                    break;
                case 7:
                    usuarioLogado = null;
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static void carregarDadosExemplo() {
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
    private static void menuLeitor() {
        while (true) {
            System.out.println("\n=== Menu do Leitor ===");
            System.out.println("1. Consultar Livros Disponíveis");
            System.out.println("2. Meus Empréstimos");
            System.out.println("3. Realizar Empréstimo");
            System.out.println("4. Devolver Livro");
            System.out.println("5. Logout");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    biblioteca.listarLivrosDisponiveis();
                    break;
                case 2:
                    biblioteca.listarEmprestimosLeitor(leitorlogado);
                    break;
                case 3:
                    realizarEmprestimo();
                    break;
                case 4:
                    realizarDevolucao();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static void menuCategorias() {
        while (true) {
            System.out.println("\n=== Gerenciar Categorias ===");
            System.out.println("1. Adicionar Categoria");
            System.out.println("2. Listar Categorias");
            System.out.println("3. Buscar Categoria");
            System.out.println("4. Editar Categoria");
            System.out.println("5. Remover Categoria");
            System.out.println("6. Voltar");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    adicionarCategoria();
                    break;
                case 2:
                    biblioteca.listarCategorias();
                    break;
                case 3:
                    buscarCategoria();
                    break;
                case 4:
                    editarCategoria();
                    break;
                case 5:
                    removerCategoria();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static void menuLivros() {
        while (true) {
            System.out.println("\n=== Gerenciar Livros ===");
            System.out.println("1. Adicionar Livro");
            System.out.println("2. Listar Livros");
            System.out.println("3. Buscar Livro");
            System.out.println("4. Editar Livro");
            System.out.println("5. Remover Livro");
            System.out.println("6. Voltar");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    adicionarLivro();
                    break;
                case 2:
                    biblioteca.listarLivros();
                    break;
                case 3:
                    buscarLivro();
                    break;
                case 4:
                    editarLivro();
                    break;
                case 5:
                    removerLivro();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static void adicionarCategoria() {
        System.out.println("Nome da categoria: ");
        String nome = scanner.nextLine();
        System.out.println("Código da categoria: ");
        String codigo = scanner.nextLine();
        Categoria novaCategoria = new Categoria(nome, codigo);
        biblioteca.adicionarCategorias(novaCategoria);
        FileManager.salvarDados(biblioteca);
        System.out.println("Categoria adicionada com sucesso!");
    }

    private static void editarCategoria() {
        System.out.println("Digite o código da categoria que deseja editar: ");
        String codigo = scanner.nextLine();
        Categoria categoria = biblioteca.buscarPorCodigo(codigo);

        if (categoria != null) {
            System.out.println("Nome atual: " + categoria.getNome());
            System.out.println("Digite o novo nome (ou deixe vazio para manter o atual): ");
            String novoNome = scanner.nextLine();

            System.out.println("Código atual: " + categoria.getCodigo());
            System.out.println("Digite o novo código (ou deixe vazio para manter o atual): ");
            String novoCodigo = scanner.nextLine();

            biblioteca.editarCategoria(codigo, novoNome, novoCodigo);
            FileManager.salvarDados(biblioteca);
            System.out.println("Categoria editada com sucesso!");
        } else {
            System.out.println("Categoria não encontrada!");
        }
    }

    private static void buscarCategoria() {
        System.out.println("1. Buscar por código");
        System.out.println("2. Buscar por nome");
        int opcao = scanner.nextInt();
        scanner.nextLine();

        switch (opcao) {
            case 1:
                System.out.println("Digite o código: ");
                String codigo = scanner.nextLine();
                Categoria catCodigo = biblioteca.buscarPorCodigo(codigo);
                if (catCodigo != null) {
                    System.out.println("Categoria encontrada: " + catCodigo.getNome());
                } else {
                    System.out.println("Categoria não encontrada!");
                }
                break;
            case 2:
                System.out.println("Digite o nome: ");
                String nome = scanner.nextLine();
                Categoria catNome = biblioteca.buscarPorNome(nome);
                if (catNome != null) {
                    System.out.println("Categoria encontrada: " + catNome.getNome());
                } else {
                    System.out.println("Categoria não encontrada!");
                }
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }

    private static void removerCategoria() {
        System.out.println("Digite o código da categoria a ser removida: ");
        String codigo = scanner.nextLine();
        if (biblioteca.removerCategoria(codigo)) {
            FileManager.salvarDados(biblioteca);
            System.out.println("Categoria removida com sucesso!");
        } else {
            System.out.println("Categoria não encontrada!");
        }
    }

    private static void adicionarLivro() {
        System.out.println("Título do livro: ");
        String titulo = scanner.nextLine();

        System.out.println("Autor do livro: ");
        String autor = scanner.nextLine();

        System.out.println("ISBN do livro: ");
        String isbn = scanner.nextLine();

        System.out.println("Número de cópias: ");
        int copias = 0;
        try {
            copias = Integer.parseInt(scanner.nextLine());
            if (copias < 1) {
                System.out.println("O número de cópias deve ser maior que zero!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Número de cópias inválido!");
            return;
        }

        System.out.println("\nCategorias disponíveis:");
        List<Categoria> categoriasDisponiveis = biblioteca.getCategorias();
        if (categoriasDisponiveis.isEmpty()) {
            System.out.println("Não há categorias cadastradas. Por favor, cadastre uma categoria primeiro.");
            return;
        }

        for (Categoria cat : categoriasDisponiveis) {
            System.out.println("Código: " + cat.getCodigo() + " - Nome: " + cat.getNome());
        }

        System.out.println("\nDigite o código da categoria desejada: ");
        String codCategoria = scanner.nextLine();

        Categoria categoria = biblioteca.buscarPorCodigo(codCategoria);
        if (categoria == null) {
            System.out.println("Categoria não encontrada!");
            return;
        }

        Livro novoLivro = new Livro(titulo, autor, isbn, copias, categoria);
        biblioteca.adicionarLivro(novoLivro);
        System.out.println("Livro adicionado com sucesso!");
    }

    private static void editarLivro() {
        System.out.println("Digite o ISBN do livro que deseja editar: ");
        String isbn = scanner.nextLine();
        Livro livro = biblioteca.buscarPorIsbn(isbn);

        if (livro != null) {
            System.out.println("Título atual: " + livro.getTitulo());
            System.out.println("Digite o novo título (ou deixe vazio para manter o atual): ");
            String novoTitulo = scanner.nextLine();

            System.out.println("Autor atual: " + livro.getAutor());
            System.out.println("Digite o novo autor (ou deixe vazio para manter o atual): ");
            String novoAutor = scanner.nextLine();

            System.out.println("Número atual de cópias: " + livro.getCopiasTotal());
            System.out.println("Digite o novo número de cópias (ou deixe vazio para manter o atual): ");
            String copiasInput = scanner.nextLine();

            int novasCopias = -1;
            if (!copiasInput.trim().isEmpty()) {
                try {
                    novasCopias = Integer.parseInt(copiasInput);
                    if (novasCopias < 0) {
                        System.out.println("O número de cópias não pode ser negativo!");
                        return;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Número de cópias inválido!");
                    return;
                }
            }

            System.out.println("Código da categoria atual: " + (livro.getCategoria() != null ? livro.getCategoria().getCodigo() : "Nenhuma"));
            System.out.println("Digite o novo código da categoria (ou deixe vazio para manter a atual): ");
            String codCategoria = scanner.nextLine();

            Categoria novaCategoria = null;
            if (!codCategoria.trim().isEmpty()) {
                novaCategoria = biblioteca.buscarPorCodigo(codCategoria);
                if (novaCategoria == null) {
                    System.out.println("Categoria não encontrada!");
                    return;
                }
            }

            biblioteca.editarLivro(isbn, novoTitulo, novoAutor, novasCopias, novaCategoria);
            System.out.println("Livro editado com sucesso!");
        } else {
            System.out.println("Livro não encontrado!");
        }
    }

    private static void buscarLivro() {
        System.out.println("Digite o ISBN do livro: ");
        String isbn = scanner.nextLine();

        Livro livro = biblioteca.buscarPorIsbn(isbn);
        if (livro != null) {
            System.out.println(livro.informaLivro());
        } else {
            System.out.println("Livro não encontrado!");
        }
    }

    private static void removerLivro() {
        System.out.println("Digite o ISBN do livro a ser removido: ");
        String isbn = scanner.nextLine();

        if (biblioteca.removerLivro(isbn)) {
            FileManager.salvarDados(biblioteca);
            System.out.println("Livro removido com sucesso!");
        } else {
            System.out.println("Livro não encontrado!");
        }
    }

    private static void realizarEmprestimo() {
        biblioteca.listarLivrosDisponiveis();
        System.out.println("\nDigite o código do livro que deseja emprestar: ");
        String codigoLivro = scanner.nextLine();
        biblioteca.emprestarLivro(leitorlogado, codigoLivro);
    }

    private static void realizarDevolucao() {
        biblioteca.listarEmprestimosLeitor(leitorlogado);
        System.out.println("\nDigite o código do livro que deseja devolver: ");
        String codigoLivro = scanner.nextLine();
        biblioteca.devolverLivro(leitorlogado, codigoLivro);
    }

    private static void verificarDiretorio() {
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    private static void mostrarConteudoBanco() {
        verificarDiretorio();
        String DATA_DIR = "data";
        System.out.println("\n=== Conteúdo do Banco de Dados ===");

        System.out.println("\nUsuários:");
        mostrarArquivo(DATA_DIR + "/usuarios.ser");

        System.out.println("\nLivros:");
        mostrarArquivo(DATA_DIR + "/livros.ser");

        System.out.println("\nCategorias:");
        mostrarArquivo(DATA_DIR + "/categorias.ser");

        System.out.println("\nEmpréstimos:");
        mostrarArquivo(DATA_DIR + "/emprestimos.ser");

        System.out.println("\nLeitores:");
        mostrarArquivo(DATA_DIR + "/leitores.ser");
    }

    private static void limparBanco() {
        verificarDiretorio();
        String DATA_DIR = "data";
        System.out.println("\n=== Limpando Banco de Dados ===");
        limparArquivo(DATA_DIR + "/usuarios.ser");
        limparArquivo(DATA_DIR + "/livros.ser");
        limparArquivo(DATA_DIR + "/categorias.ser");
        limparArquivo(DATA_DIR + "/emprestimos.ser");
        limparArquivo(DATA_DIR + "/leitores.ser");
        System.out.println("Banco de dados limpo com sucesso!");
    }

    private static void limparArquivo(String nomeArquivo) {
        try {
            File file = new File(nomeArquivo);
            if (file.exists()) {
                FileWriter writer = new FileWriter(file);
                writer.write("[]");
                writer.close();
                System.out.println("Arquivo " + nomeArquivo + " limpo.");
            } else {
                System.out.println("Arquivo " + nomeArquivo + " não existe.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao limpar " + nomeArquivo + ": " + e.getMessage());
        }
    }

    private static void mostrarArquivo(String nomeArquivo) {
        try {
            File file = new File(nomeArquivo);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String linha;
                while ((linha = reader.readLine()) != null) {
                    System.out.println(linha);
                }
                reader.close();
            } else {
                System.out.println("Arquivo " + nomeArquivo + " não existe.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao ler " + nomeArquivo + ": " + e.getMessage());
        }
    }
}