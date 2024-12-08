package com.biblioteca;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

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
            int opcao = MenuUtils.lerOpcaoMenu(1, 3,
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
                case -1: // This case won't be reached in the main menu, but keeping for consistency
                    System.out.println("Você está no menu principal.");
                    break;
            }
        }
    }

    // Em Main.java, atualize o método realizarCadastro:
    private static void realizarCadastro() {
        String login = MenuUtils.lerString("Digite seu login: ");
        if (login == null) return;

        String senha = MenuUtils.lerString("Digite sua senha: ");
        if (senha == null) return;

        Boolean isAdmin = MenuUtils.lerSimNao("Você é um administrador?");
        if (isAdmin == null) return;

        if (isAdmin) {
            Usuario novoUsuario = new Usuario(login, senha, true);
            Usuario.getUsuarios().add(novoUsuario);
            System.out.println("Administrador cadastrado com sucesso!");
            menuAdmin();
        } else {
            String nome = MenuUtils.lerString("Nome: ");
            if (nome == null) return;

            String email = MenuUtils.lerString("E-mail: ");
            if (email == null) return;

            Leitor novoLeitor = new Leitor(nome, email);
            Usuario novoUsuario = new Usuario(login, senha, false, novoLeitor);
            Usuario.getUsuarios().add(novoUsuario);
            System.out.println("Leitor cadastrado com sucesso!");
            menuLeitor();
        }

        FileManager.salvarDados(biblioteca);
    }
    // Em Main.java, atualize o método realizarLogin:
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
                usuarioLogado = login;

                if (usuarioAtual.isAdmin()) {
                    System.out.println("Bem-vindo, Administrador!");
                    leitorlogado = null; // Garante que não há leitor logado quando é admin
                    menuAdmin();
                } else {
                    leitorlogado = usuarioAtual.getLeitor();
                    if (leitorlogado != null) {
                        System.out.println("Bem-vindo, " + leitorlogado.getNome());
                        menuLeitor();
                    } else {
                        System.out.println("Erro: Dados do leitor não encontrados!");
                        return;
                    }
                }
            }
        } else {
            System.out.println("Login ou senha inválidos!");
        }
    }

    private static void menuAdmin() {
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 8,
                    "=== Menu Administrador ===\n" +
                            "1. Gerenciar Categorias\n" +
                            "2. Gerenciar Livros\n" +
                            "3. Gerenciar Empréstimos\n" +
                            "4. Listar Empréstimos\n" +
                            "5. Mostrar Banco de Dados\n" +
                            "6. Carregar Dados Exemplo\n" +
                            "7. Limpar Banco de Dados\n" +
                            "8. Logout");

            switch (opcao) {
                case 1:
                    menuCategorias();
                    break;
                case 2:
                    menuLivros();
                    break;
                case 3:
                    menuGerenciarEmprestimos();
                    break;
                case 4:
                    biblioteca.listarEmprestimos();
                    break;
                case 5:
                    mostrarConteudoBanco();
                    break;
                case 6:
                    carregarDadosExemplo();
                    break;
                case 7:
                    limparBanco();
                    biblioteca = new Biblioteca();
                    break;
                case 8:
                case -1:
                    usuarioLogado = null;
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }
    private static void menuGerenciarEmprestimos() {
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 4,
                    "=== Gerenciar Empréstimos ===\n" +
                            "1. Consultar Empréstimos por Livro\n" +
                            "2. Marcar Livro como Devolvido\n" +
                            "3. Alterar Data de Devolução\n" +
                            "4. Voltar");

            switch (opcao) {
                case 1:
                    consultarEmprestimosPorLivro();
                    break;
                case 2:
                    marcarLivroDevolvido();
                    break;
                case 3:
                    alterarDataDevolucao();
                    break;
                case 4:
                case -1:
                    return;
            }
        }
    }

    private static void consultarEmprestimosPorLivro() {
        System.out.println("\n=== Consultar Empréstimos por Livro ===");
        System.out.println("Digite o ISBN do livro: ");
        String isbn = scanner.nextLine();
        biblioteca.listarEmprestimosLivro(isbn);
    }

    private static void marcarLivroDevolvido() {
        System.out.println("\n=== Marcar Livro como Devolvido ===");
        System.out.println("Digite o ISBN do livro: ");
        String isbn = scanner.nextLine();

        System.out.println("Digite o email do leitor: ");
        String emailLeitor = scanner.nextLine();

        biblioteca.marcarComoDevolvidoAdmin(isbn, emailLeitor);
    }

    private static void alterarDataDevolucao() {
        System.out.println("\n=== Alterar Data de Devolução ===");
        System.out.println("Digite o ISBN do livro: ");
        String isbn = scanner.nextLine();

        System.out.println("Digite o email do leitor: ");
        String emailLeitor = scanner.nextLine();

        System.out.println("Digite a nova data de devolução (dd/MM/yyyy): ");
        String dataStr = scanner.nextLine();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date novaData = sdf.parse(dataStr);
            biblioteca.alterarDataDevolucaoAdmin(isbn, emailLeitor, novaData);
        } catch (ParseException e) {
            System.out.println("Formato de data inválido! Use dd/MM/yyyy");
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
            int opcao = MenuUtils.lerOpcaoMenu(1, 6,
                    "=== Menu do Leitor ===\n" +
                            "1. Consultar Livros Disponíveis\n" +
                            "2. Pesquisar Livros\n" +
                            "3. Meus Empréstimos\n" +
                            "4. Realizar Empréstimo\n" +
                            "5. Devolver Livro\n" +
                            "6. Logout");

            switch (opcao) {
                case 1:
                    biblioteca.listarLivrosDisponiveis();
                    break;
                case 2:
                    pesquisarLivros();
                    break;
                case 3:
                    menuConsultaEmprestimosLeitor();
                    break;
                case 4:
                    realizarEmprestimo();
                    break;
                case 5:
                    realizarDevolucao();
                    break;
                case 6:
                case -1:
                    return;
            }
        }
    }

    private static void menuCategorias() {
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
                    buscarCategoria();
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

   private static void menuConsultaEmprestimosLeitor() {
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 5,
                    "=== Consulta de Empréstimos ===\n" +
                            "1. Todos os Empréstimos\n" +
                            "2. Buscar por Período\n" +
                            "3. Buscar por Termo (Título/Autor)\n" +
                            "4. Buscar por Código ISBN\n" +
                            "5. Voltar");

            switch (opcao) {
                case 1:
                    biblioteca.listarEmprestimosLeitor(leitorlogado);
                    break;
                case 2:
                    consultarEmprestimosPorPeriodo();
                    break;
                case 3:
                    consultarEmprestimosPorTermo();
                    break;
                case 4:
                    consultarEmprestimosPorCodigo();
                    break;
                case 5:
                case -1:
                    return;
            }
        }
    }

    private static void consultarEmprestimosPorPeriodo() {
        try {
            System.out.println("\nDigite a data inicial (dd/MM/yyyy): ");
            String dataInicialStr = scanner.nextLine();
            System.out.println("Digite a data final (dd/MM/yyyy): ");
            String dataFinalStr = scanner.nextLine();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dataInicial = sdf.parse(dataInicialStr);
            Date dataFinal = sdf.parse(dataFinalStr);

            List<Emprestimo> emprestimos = biblioteca.consultarEmprestimosLeitorPorPeriodo(leitorlogado, dataInicial, dataFinal);
            exibirResultadosEmprestimos(emprestimos);
        } catch (ParseException e) {
            System.out.println("Formato de data inválido! Use dd/MM/yyyy");
        }
    }

    private static void consultarEmprestimosPorTermo() {
        System.out.println("\nDigite o termo de busca (título ou autor): ");
        String termo = scanner.nextLine();
        List<Emprestimo> emprestimos = biblioteca.consultarEmprestimosLeitorPorTermo(leitorlogado, termo);
        exibirResultadosEmprestimos(emprestimos);
    }

    private static void consultarEmprestimosPorCodigo() {
        System.out.println("\nDigite o código ISBN do livro: ");
        String codigo = scanner.nextLine();
        List<Emprestimo> emprestimos = biblioteca.consultarEmprestimosLeitorPorCodigo(leitorlogado, codigo);
        exibirResultadosEmprestimos(emprestimos);
    }

    private static void consultarPorCodigoLivro() {
        System.out.println("Digite o código ISBN do livro: ");
        String codigoLivro = scanner.nextLine();

        List<Emprestimo> emprestimos = biblioteca.consultarEmprestimosPorLivro(codigoLivro);
        exibirResultadosEmprestimos(emprestimos);
    }

    private static void consultarPorNomeLivro() {
        System.out.println("Digite o nome do livro: ");
        String nomeLivro = scanner.nextLine();

        List<Livro> livros = biblioteca.buscarLivros(nomeLivro);
        List<Emprestimo> todosEmprestimos = new ArrayList<>();

        for (Livro livro : livros) {
            List<Emprestimo> emprestimosDoLivro = biblioteca.consultarEmprestimosPorLivro(livro.getCodigoIsbn());
            todosEmprestimos.addAll(emprestimosDoLivro);
        }

        exibirResultadosEmprestimos(todosEmprestimos);
    }

    private static void consultarPorIntervaloData() {
        try {
            System.out.println("Digite a data inicial (dd/MM/yyyy): ");
            String dataInicialStr = scanner.nextLine();
            System.out.println("Digite a data final (dd/MM/yyyy): ");
            String dataFinalStr = scanner.nextLine();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dataInicial = sdf.parse(dataInicialStr);
            Date dataFinal = sdf.parse(dataFinalStr);

            List<Emprestimo> emprestimos = biblioteca.consultarEmprestimosPorData(dataInicial, dataFinal);
            exibirResultadosEmprestimos(emprestimos);

        } catch (ParseException e) {
            System.out.println("Formato de data inválido! Use dd/MM/yyyy");
        }
    }

    private static void exibirResultadosEmprestimos(List<Emprestimo> emprestimos) {
        if (emprestimos.isEmpty()) {
            System.out.println("Nenhum empréstimo encontrado!");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("\n=== Empréstimos Encontrados ===");

        for (Emprestimo emp : emprestimos) {
            System.out.println("\nLeitor: " + emp.getLeitor().getNome());
            System.out.println("Livro: " + emp.getLivro().getTitulo());
            System.out.println("Data de Retirada: " + sdf.format(emp.getDataEmprestimo()));
            if (emp.getDataDevolucao() != null) {
                System.out.println("Data de Devolução: " + sdf.format(emp.getDataDevolucao()));
            } else {
                System.out.println("Status: Em andamento");
            }
            System.out.println("----------------------------------------");
        }
    }

    private static void menuLivros() {
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
    private static void pesquisarLivros() {
        System.out.println("\n=== Pesquisar Livros ===");
        System.out.println("Digite o termo de pesquisa (ISBN, título ou autor): ");
        String termo = scanner.nextLine();

        List<Livro> resultados = biblioteca.pesquisarLivros(termo);
        biblioteca.exibirResultadosPesquisa(resultados);

        if (!resultados.isEmpty() && leitorlogado != null) {
            System.out.println("\nDeseja realizar um empréstimo? (S/N)");
            String resposta = scanner.nextLine().trim();

            if (resposta.equalsIgnoreCase("S")) {
                realizarEmprestimoPorIndice(resultados);
            }
        }
    }

    private static void realizarEmprestimo() {
        int opcao = MenuUtils.lerOpcaoMenu(1, 2,
                "=== Realizar Empréstimo ===\n" +
                        "1. Ver todos os livros disponíveis\n" +
                        "2. Pesquisar livros específicos");

        switch (opcao) {
            case 1:
                System.out.println("\n=== Livros Disponíveis ===");
                biblioteca.listarLivrosDisponiveis();
                String codigoLivro = MenuUtils.lerString("Digite o código ISBN do livro que deseja emprestar: ");
                if (codigoLivro != null) {
                    biblioteca.emprestarLivro(leitorlogado, codigoLivro);
                }
                break;

            case 2:
                String termo = MenuUtils.lerString("Digite o termo para pesquisa (título, autor ou ISBN): ");
                if (termo != null) {
                    List<Livro> resultados = biblioteca.pesquisarLivros(termo);
                    if (!resultados.isEmpty()) {
                        biblioteca.exibirResultadosPesquisa(resultados);
                        realizarEmprestimoPorIndice(resultados);
                    } else {
                        System.out.println("Nenhum livro encontrado com esse termo.");
                    }
                }
                break;

            case -1:
                return;
        }
    }
    private static void realizarEmprestimoPorIndice(List<Livro> livros) {
        System.out.println("\nDigite o número (#) do livro que deseja emprestar: ");
        try {
            int indice = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer

            if (indice > 0 && indice <= livros.size()) {
                Livro livroSelecionado = livros.get(indice - 1);

                if (livroSelecionado.temCopiaDisponivel()) {
                    biblioteca.emprestarLivro(leitorlogado, livroSelecionado.getCodigoIsbn());
                } else {
                    System.out.println("Este livro não possui cópias disponíveis no momento.");
                    System.out.println("Total de cópias: " + livroSelecionado.getCopiasTotal());
                    System.out.println("Cópias disponíveis: " + livroSelecionado.getCopiasDisponiveis());
                }
            } else {
                System.out.println("Número de livro inválido!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um número válido.");
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