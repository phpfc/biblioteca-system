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
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public static void main(String[] args) {
        inicializarSistema();
        menuLogin();
    }

    // ==================== Inicialização e Login ====================

    private static void inicializarSistema() {
        FileManager.carregarDados(biblioteca);
        if (Usuario.getUsuarios() == null || Usuario.getUsuarios().isEmpty()) {
            System.out.println("\n=== Primeira Execução - Criação do Administrador ===");
            criarPrimeiroAdmin();
        }
    }

    private static void criarPrimeiroAdmin() {
        String login = MenuUtils.lerString("Digite o login do administrador: ");
        if (login == null) return;

        String senha = MenuUtils.lerString("Digite a senha do administrador: ");
        if (senha == null) return;

        Usuario admin = new Usuario(login, senha, true);
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(admin);
        Usuario.setUsuarios(usuarios);

        FileManager.salvarDados(biblioteca);
        System.out.println("Administrador criado com sucesso!");
    }

    private static void menuLogin() {
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 3,
                    "\n=== Sistema de Biblioteca ===\n" +
                            "1. Login\n" +
                            "2. Realizar Cadastro\n" +
                            "3. Sair");

            switch (opcao) {
                case 1: realizarLogin(); break;
                case 2: realizarCadastro(); break;
                case 3: System.out.println("Sistema encerrado."); System.exit(0); break;
            }
        }
    }

    private static void realizarLogin() {
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
                    leitorlogado = null;
                    menuAdmin();
                } else {
                    leitorlogado = usuarioAtual.getLeitor();
                    if (leitorlogado != null) {
                        System.out.println("Bem-vindo, " + leitorlogado.getNome());
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

    // ==================== Menus Principais ====================

    private static void menuAdmin() {
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 4,
                    "\n=== Menu Administrador ===\n" +
                            "1. Gerenciar Categorias\n" +
                            "2. Gerenciar Livros\n" +
                            "3. Gerenciar Empréstimos\n" +
                            "4. Logout");

            switch (opcao) {
                case 1: menuCategorias(); break;
                case 2: menuLivros(); break;
                case 3: menuGerenciarEmprestimos(); break;
                case 4: case -1:
                    usuarioLogado = null;
                    return;
            }
        }
    }

    private static void menuLeitor() {
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 6,
                    "\n=== Menu do Leitor ===\n" +
                            "1. Consultar Livros Disponíveis\n" +
                            "2. Pesquisar Livros\n" +
                            "3. Meus Empréstimos\n" +
                            "4. Realizar Empréstimo\n" +
                            "5. Devolver Livro\n" +
                            "6. Logout");

            switch (opcao) {
                case 1: biblioteca.listarLivrosDisponiveis(); break;
                case 2: pesquisarLivros(); break;
                case 3: menuConsultaEmprestimosLeitor(); break;
                case 4: realizarEmprestimo(); break;
                case 5: realizarDevolucao(); break;
                case 6: case -1: return;
            }
        }
    }

    // ==================== Menus de Gerenciamento ====================

    private static void menuGerenciarEmprestimos() {
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 7,
                    "\n=== Gerenciar Empréstimos ===\n" +
                            "1. Listar todos os empréstimos\n" +
                            "2. Consultar empréstimos por livro\n" +
                            "3. Consultar empréstimos por leitor\n" +
                            "4. Consultar empréstimos por data\n" +
                            "5. Alterar data de devolução\n" +
                            "6. Marcar empréstimo como devolvido\n" +
                            "7. Voltar");

            switch (opcao) {
                case 1: listarTodosEmprestimos(); break;
                case 2: consultarEmprestimosPorLivro(); break;
                case 3: consultarEmprestimosPorLeitor(); break;
                case 4: consultarEmprestimosPorData(); break;
                case 5: alterarDataDevolucao(); break;
                case 6: marcarEmprestimoComoDevolvido(); break;
                case 7: case -1: return;
            }
        }
    }

    private static void menuCategorias() {
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 5,
                    "\n=== Gerenciar Categorias ===\n" +
                            "1. Adicionar Categoria\n" +
                            "2. Listar Categorias\n" +
                            "3. Buscar Categoria\n" +
                            "4. Editar Categoria\n" +
                            "5. Remover Categoria");

            switch (opcao) {
                case 1: adicionarCategoria(); break;
                case 2: biblioteca.listarCategorias(); break;
                case 3: buscarCategoria(); break;
                case 4: editarCategoria(); break;
                case 5: removerCategoria(); break;
                case -1: return;
            }
        }
    }

    private static void menuLivros() {
        while (true) {
            boolean isAdmin = Usuario.isAdmin(usuarioLogado);
            String menuOptions = "\n=== Menu Livros ===\n" +
                    "1. Pesquisar Livros\n" +
                    "2. Listar Todos os Livros\n";
            int maxOption = 2;

            if (isAdmin) {
                menuOptions += "3. Adicionar Livro\n" +
                        "4. Editar Livro\n" +
                        "5. Remover Livro\n";
                maxOption = 5;
            }

            int opcao = MenuUtils.lerOpcaoMenu(1, maxOption, menuOptions);

            switch (opcao) {
                case 1: pesquisarLivros(); break;
                case 2: biblioteca.listarLivros(); break;
                case 3: if (isAdmin) adicionarLivro(); break;
                case 4: if (isAdmin) editarLivro(); break;
                case 5: if (isAdmin) removerLivro(); break;
                case -1: return;
            }
        }
    }

    private static void adicionarLivro() {
        String titulo = MenuUtils.lerString("Título do livro: ");
        if (titulo == null) return;

        String autor = MenuUtils.lerString("Autor do livro: ");
        if (autor == null) return;

        String isbn = MenuUtils.lerString("ISBN do livro: ");
        if (isbn == null) return;

        Integer copias = MenuUtils.lerInteiro("Número de cópias: ", 1, Integer.MAX_VALUE);
        if (copias == null) return;

        System.out.println("\nCategorias disponíveis:");
        List<Categoria> categoriasDisponiveis = biblioteca.getCategorias();
        if (categoriasDisponiveis.isEmpty()) {
            System.out.println("Não há categorias cadastradas. Por favor, cadastre uma categoria primeiro.");
            return;
        }

        for (Categoria cat : categoriasDisponiveis) {
            System.out.println("Código: " + cat.getCodigo() + " - Nome: " + cat.getNome());
        }

        String codCategoria = MenuUtils.lerString("\nDigite o código da categoria desejada: ");
        if (codCategoria == null) return;

        Categoria categoria = biblioteca.buscarPorCodigo(codCategoria);
        if (categoria == null) {
            System.out.println("Categoria não encontrada!");
            return;
        }

        Livro novoLivro = new Livro(titulo, autor, isbn, copias, categoria);
        biblioteca.adicionarLivro(novoLivro);
        FileManager.salvarDados(biblioteca);
        System.out.println("Livro adicionado com sucesso!");
    }

    private static void editarLivro() {
        String isbn = MenuUtils.lerString("Digite o ISBN do livro que deseja editar: ");
        if (isbn == null) return;

        Livro livro = biblioteca.buscarPorIsbn(isbn);
        if (livro != null) {
            String novoTitulo = MenuUtils.lerString("Digite o novo título (ou deixe vazio para manter o atual): ");
            String novoAutor = MenuUtils.lerString("Digite o novo autor (ou deixe vazio para manter o atual): ");

            System.out.println("Número atual de cópias: " + livro.getCopiasTotal());
            Integer novasCopias = MenuUtils.lerInteiro("Digite o novo número de cópias (ou 0 para manter o atual): ", 0, Integer.MAX_VALUE);
            if (novasCopias != null && novasCopias == 0) novasCopias = -1;  // -1 indica que não deve ser alterado

            System.out.println("Código da categoria atual: " + (livro.getCategoria() != null ? livro.getCategoria().getCodigo() : "Nenhuma"));
            String codCategoria = MenuUtils.lerString("Digite o novo código da categoria (ou deixe vazio para manter a atual): ");

            Categoria novaCategoria = null;
            if (codCategoria != null && !codCategoria.trim().isEmpty()) {
                novaCategoria = biblioteca.buscarPorCodigo(codCategoria);
                if (novaCategoria == null) {
                    System.out.println("Categoria não encontrada!");
                    return;
                }
            }

            biblioteca.editarLivro(isbn, novoTitulo, novoAutor, novasCopias, novaCategoria);
            System.out.println("Livro editado com sucesso!");
            FileManager.salvarDados(biblioteca);
        } else {
            System.out.println("Livro não encontrado!");
        }
    }

    private static void removerLivro() {
        String isbn = MenuUtils.lerString("Digite o ISBN do livro a ser removido: ");
        if (isbn == null) return;

        if (biblioteca.removerLivro(isbn)) {
            System.out.println("Livro removido com sucesso!");
            FileManager.salvarDados(biblioteca);
        } else {
            System.out.println("Livro não encontrado!");
        }
    }
    private static void menuConsultaEmprestimosLeitor() {
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 5,
                    "\n=== Consulta de Empréstimos ===\n" +
                            "1. Todos os Empréstimos\n" +
                            "2. Buscar por Período\n" +
                            "3. Buscar por Termo (Título/Autor)\n" +
                            "4. Buscar por Código ISBN\n" +
                            "5. Voltar");

            switch (opcao) {
                case 1: biblioteca.listarEmprestimosLeitor(leitorlogado); break;
                case 2: consultarEmprestimosPorPeriodo(); break;
                case 3: consultarEmprestimosPorTermo(); break;
                case 4: consultarEmprestimosPorCodigo(); break;
                case 5: case -1: return;
            }
        }
    }

    // ==================== Operações de Empréstimo ====================

    private static void listarTodosEmprestimos() {
        List<Emprestimo> emprestimos = biblioteca.getEmprestimos();
        if (emprestimos.isEmpty()) {
            System.out.println("Não há empréstimos registrados.");
            return;
        }
        exibirEmprestimos(emprestimos);
    }

    private static void consultarEmprestimosPorLivro() {
        String isbn = MenuUtils.lerString("Digite o código ISBN do livro: ");
        if (isbn == null) return;

        List<Emprestimo> emprestimos = biblioteca.consultarEmprestimosPorLivro(isbn);
        if (emprestimos.isEmpty()) {
            System.out.println("Não há empréstimos registrados para este livro.");
            return;
        }
        exibirEmprestimos(emprestimos);
    }

    private static void consultarEmprestimosPorLeitor() {
        String termo = MenuUtils.lerString("Digite o código ou nome do leitor: ");
        if (termo == null) return;

        List<Emprestimo> emprestimos = biblioteca.consultarEmprestimosPorLeitor(termo);
        if (emprestimos.isEmpty()) {
            System.out.println("Não há empréstimos registrados para este leitor.");
            return;
        }
        exibirEmprestimos(emprestimos);
    }

    private static void consultarEmprestimosPorData() {
        try {
            String dataInicialStr = MenuUtils.lerString("Digite a data inicial (dd/MM/yyyy): ");
            if (dataInicialStr == null) return;

            String dataFinalStr = MenuUtils.lerString("Digite a data final (dd/MM/yyyy): ");
            if (dataFinalStr == null) return;

            Date dataInicial = sdf.parse(dataInicialStr);
            Date dataFinal = sdf.parse(dataFinalStr);

            List<Emprestimo> emprestimos = biblioteca.consultarEmprestimosPorData(dataInicial, dataFinal);
            if (emprestimos.isEmpty()) {
                System.out.println("Não há empréstimos registrados neste período.");
                return;
            }
            exibirEmprestimos(emprestimos);
        } catch (ParseException e) {
            System.out.println("Formato de data inválido. Use dd/MM/yyyy");
        }
    }

    private static void consultarEmprestimosPorPeriodo() {
        try {
            String dataInicialStr = MenuUtils.lerString("Digite a data inicial (dd/MM/yyyy): ");
            if (dataInicialStr == null) return;

            String dataFinalStr = MenuUtils.lerString("Digite a data final (dd/MM/yyyy): ");
            if (dataFinalStr == null) return;

            Date dataInicial = sdf.parse(dataInicialStr);
            Date dataFinal = sdf.parse(dataFinalStr);

            List<Emprestimo> emprestimos = biblioteca.consultarEmprestimosLeitorPorPeriodo(leitorlogado, dataInicial, dataFinal);
            exibirEmprestimos(emprestimos);
        } catch (ParseException e) {
            System.out.println("Formato de data inválido! Use dd/MM/yyyy");
        }
    }

    private static void consultarEmprestimosPorTermo() {
        String termo = MenuUtils.lerString("Digite o termo de busca (título ou autor): ");
        if (termo == null) return;

        List<Emprestimo> emprestimos = biblioteca.consultarEmprestimosLeitorPorTermo(leitorlogado, termo);
        exibirEmprestimos(emprestimos);
    }

    private static void consultarEmprestimosPorCodigo() {
        String codigo = MenuUtils.lerString("Digite o código ISBN do livro: ");
        if (codigo == null) return;

        List<Emprestimo> emprestimos = biblioteca.consultarEmprestimosLeitorPorCodigo(leitorlogado, codigo);
        exibirEmprestimos(emprestimos);
    }

    private static void alterarDataDevolucao() {
        // Lista os empréstimos primeiro
        listarTodosEmprestimos();

        // Carrega a lista de empréstimos
        List<Emprestimo> emprestimos = biblioteca.getEmprestimos();
        if (emprestimos.isEmpty()) {
            System.out.println("Não há empréstimos registrados.");
            return;
        }

        // Agora podemos usar emprestimos.size() como limite
        Integer codigoEmprestimo = MenuUtils.lerInteiro(
                "Digite o código do empréstimo que deseja alterar: ",
                1,
                emprestimos.size()
        );
        if (codigoEmprestimo == null) return;

        if (codigoEmprestimo <= 0 || codigoEmprestimo > emprestimos.size()) {
            System.out.println("Código de empréstimo inválido.");
            return;
        }

        String dataStr = MenuUtils.lerString("Digite a nova data de devolução (dd/MM/yyyy): ");
        if (dataStr == null) return;

        try {
            Date novaData = sdf.parse(dataStr);
            Emprestimo emprestimo = emprestimos.get(codigoEmprestimo - 1);
            emprestimo.setDataDevolucao(novaData);
            System.out.println("Data de devolução alterada com sucesso!");
            FileManager.salvarDados(biblioteca);
        } catch (ParseException e) {
            System.out.println("Formato de data inválido. Use dd/MM/yyyy");
        }
    }

    private static void marcarEmprestimoComoDevolvido() {
        // Lista os empréstimos primeiro
        listarTodosEmprestimos();

        // Carrega a lista de empréstimos
        List<Emprestimo> emprestimos = biblioteca.getEmprestimos();
        if (emprestimos.isEmpty()) {
            System.out.println("Não há empréstimos registrados.");
            return;
        }

        // Agora podemos usar emprestimos.size() como limite
        Integer codigoEmprestimo = MenuUtils.lerInteiro(
                "Digite o código do empréstimo que deseja marcar como devolvido: ",
                1,
                emprestimos.size()
        );
        if (codigoEmprestimo == null) return;

        if (codigoEmprestimo <= 0 || codigoEmprestimo > emprestimos.size()) {
            System.out.println("Código de empréstimo inválido.");
            return;
        }

        Emprestimo emprestimo = emprestimos.get(codigoEmprestimo - 1);
        if (emprestimo.getDataDevolucao() != null) {
            System.out.println("Este empréstimo já foi devolvido.");
            return;
        }

        emprestimo.setDataDevolucao(new Date());
        emprestimo.getLivro().devolverCopia();

        System.out.println("Empréstimo marcado como devolvido com sucesso!");
        FileManager.salvarDados(biblioteca);
    }
    // ==================== Operações de Livros ====================

    private static void pesquisarLivros() {
        String termo = MenuUtils.lerString("\nDigite o termo de pesquisa (ISBN, título ou autor): ");
        if (termo == null) return;

        List<Livro> resultados = biblioteca.pesquisarLivros(termo);
        biblioteca.exibirResultadosPesquisa(resultados);

        if (!resultados.isEmpty() && leitorlogado != null) {
            Boolean realizarEmprestimo = MenuUtils.lerSimNao("\nDeseja realizar um empréstimo?");
            if (realizarEmprestimo != null && realizarEmprestimo) {
                realizarEmprestimoPorIndice(resultados);
            }
        }
    }

    private static void realizarEmprestimo() {
        int opcao = MenuUtils.lerOpcaoMenu(1, 2,
                "\n=== Realizar Empréstimo ===\n" +
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
        }
    }

    private static void adicionarCategoria() {
        String nome = MenuUtils.lerString("Nome da categoria: ");
        if (nome == null) return;

        String codigo = MenuUtils.lerString("Código da categoria: ");
        if (codigo == null) return;

        Categoria novaCategoria = new Categoria(nome, codigo);
        biblioteca.adicionarCategorias(novaCategoria);
        System.out.println("Categoria adicionada com sucesso!");
        FileManager.salvarDados(biblioteca);
    }

    private static void editarCategoria() {
        String codigo = MenuUtils.lerString("Digite o código da categoria que deseja editar: ");
        if (codigo == null) return;

        Categoria categoria = biblioteca.buscarPorCodigo(codigo);
        if (categoria != null) {
            String novoNome = MenuUtils.lerString("Digite o novo nome (ou deixe vazio para manter o atual): ");
            String novoCodigo = MenuUtils.lerString("Digite o novo código (ou deixe vazio para manter o atual): ");

            biblioteca.editarCategoria(codigo, novoNome, novoCodigo);
            System.out.println("Categoria editada com sucesso!");
            FileManager.salvarDados(biblioteca);
        } else {
            System.out.println("Categoria não encontrada!");
        }
    }

    private static void removerCategoria() {
        String codigo = MenuUtils.lerString("Digite o código da categoria a ser removida: ");
        if (codigo == null) return;

        if (biblioteca.removerCategoria(codigo)) {
            System.out.println("Categoria removida com sucesso!");
            FileManager.salvarDados(biblioteca);
        } else {
            System.out.println("Categoria não encontrada!");
        }
    }

    private static void buscarCategoria() {
        int opcao = MenuUtils.lerOpcaoMenu(1, 2,
                "1. Buscar por código\n" +
                        "2. Buscar por nome");

        switch (opcao) {
            case 1:
                String codigo = MenuUtils.lerString("Digite o código: ");
                if (codigo != null) {
                    Categoria catCodigo = biblioteca.buscarPorCodigo(codigo);
                    if (catCodigo != null) {
                        System.out.println("Categoria encontrada: " + catCodigo.getNome());
                    } else {
                        System.out.println("Categoria não encontrada!");
                    }
                }
                break;
            case 2:
                String nome = MenuUtils.lerString("Digite o nome: ");
                if (nome != null) {
                    Categoria catNome = biblioteca.buscarPorNome(nome);
                    if (catNome != null) {
                        System.out.println("Categoria encontrada: " + catNome.getNome());
                    } else {
                        System.out.println("Categoria não encontrada!");
                    }
                }
                break;
        }
    }

    private static void realizarEmprestimoPorIndice(List<Livro> livros) {
        Integer indice = MenuUtils.lerInteiro("Digite o número (#) do livro que deseja emprestar: ", 1, livros.size());
        if (indice == null) return;

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
    }
    private static void realizarDevolucao() {
        biblioteca.listarEmprestimosLeitor(leitorlogado);
        String codigoLivro = MenuUtils.lerString("\nDigite o código do livro que deseja devolver: ");
        if (codigoLivro != null) {
            biblioteca.devolverLivro(leitorlogado, codigoLivro);
        }
    }

    // ==================== Exibição de Dados ====================

    private static void exibirEmprestimos(List<Emprestimo> emprestimos) {
        if (emprestimos.isEmpty()) {
            System.out.println("Nenhum empréstimo encontrado!");
            return;
        }

        System.out.println("\nEmpréstimos encontrados:");
        for (int i = 0; i < emprestimos.size(); i++) {
            Emprestimo emp = emprestimos.get(i);
            System.out.println("\nCódigo do Empréstimo: " + (i + 1));

            System.out.println("=== Dados do Livro ===");
            System.out.println("Título: " + emp.getLivro().getTitulo());
            System.out.println("Autor: " + emp.getLivro().getAutor());
            System.out.println("ISBN: " + emp.getLivro().getCodigoIsbn());
            System.out.println("Categoria: " + (emp.getLivro().getCategoria() != null ?
                    emp.getLivro().getCategoria().getNome() : "Sem categoria"));

            System.out.println("\n=== Dados do Leitor ===");
            System.out.println("Nome: " + emp.getLeitor().getNome());
            System.out.println("Email: " + emp.getLeitor().getEmail());
            System.out.println("Código: " + emp.getLeitor().getCodigo());

            System.out.println("\n=== Dados do Empréstimo ===");
            System.out.println("Data de empréstimo: " + sdf.format(emp.getDataEmprestimo()));
            System.out.println("Data de devolução: " +
                    (emp.getDataDevolucao() != null ? sdf.format(emp.getDataDevolucao()) : "Ainda não devolvido"));
            System.out.println("------------------------");
        }
    }
}