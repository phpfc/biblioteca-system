package com.biblioteca.controllers;

import com.biblioteca.models.Biblioteca;
import com.biblioteca.models.Emprestimo;
import com.biblioteca.models.Leitor;
import com.biblioteca.models.Livro;
import com.biblioteca.utils.FileManager;
import com.biblioteca.utils.MenuUtils;
import com.biblioteca.utils.ValidationUtils;

import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;

public class EmprestimoController {
    private final Biblioteca biblioteca;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public EmprestimoController(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
    }

    public void menuGerenciarEmprestimos() {
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 5,
                    "=== Gerenciar Empréstimos ===\n" +
                            "1. Consultar Empréstimos por Livro\n" +
                            "2. Consultar Empréstimos por Leitor\n" +  // New option
                            "3. Marcar Livro como Devolvido\n" +
                            "4. Alterar Data de Devolução\n" +
                            "5. Voltar");

            switch (opcao) {
                case 1:
                    consultarEmprestimosPorLivro();
                    break;
                case 2:
                    consultarEmprestimosPorLeitor();  // New method
                    break;
                case 3:
                    marcarLivroDevolvido();
                    break;
                case 4:
                    alterarDataDevolucao();
                    break;
                case 5:
                case -1:
                    return;
            }
        }
    }

    public void menuEmprestimosLeitor(Leitor leitor) {
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 5,
                    "=== Consulta de Empréstimos ===\n" +
                            "1. Meus Empréstimos Ativos\n" +
                            "2. Histórico de Empréstimos\n" +
                            "3. Realizar Novo Empréstimo\n" +
                            "4. Devolver Livro\n" +
                            "5. Voltar");

            switch (opcao) {
                case 1:
                    listarEmprestimosAtivos(leitor);
                    break;
                case 2:
                    menuHistoricoEmprestimos(leitor);
                    break;
                case 3:
                    realizarEmprestimo(leitor);
                    break;
                case 4:
                    realizarDevolucao(leitor);
                    break;
                case 5:
                case -1:
                    return;
            }
        }
    }

    private void menuHistoricoEmprestimos(Leitor leitor) {
        while (true) {
            int opcao = MenuUtils.lerOpcaoMenu(1, 4,
                    "=== Histórico de Empréstimos ===\n" +
                            "1. Por Período\n" +
                            "2. Por Título/Autor\n" +
                            "3. Por ISBN\n" +
                            "4. Voltar");

            switch (opcao) {
                case 1:
                    consultarEmprestimosPorPeriodo(leitor);
                    break;
                case 2:
                    consultarEmprestimosPorTermo(leitor);
                    break;
                case 3:
                    consultarEmprestimosPorCodigo(leitor);
                    break;
                case 4:
                case -1:
                    return;
            }
        }
    }

    private void consultarEmprestimosPorLivro() {
        String isbn = MenuUtils.lerString("Digite o ISBN do livro: ");
        if (isbn == null) return;

        List<Emprestimo> emprestimos = biblioteca.consultarEmprestimosPorLivro(isbn);
        exibirResultadosEmprestimos(emprestimos);
    }

    private void consultarEmprestimosPorLeitor() {
        String emailLeitor = MenuUtils.lerString("Digite o email do leitor: ");
        if (emailLeitor == null) return;

        List<Emprestimo> emprestimos = biblioteca.consultarEmprestimosPorLeitor(emailLeitor);
        if (emprestimos.isEmpty()) {
            System.out.println("Nenhum empréstimo encontrado para este leitor.");
            return;
        }

        System.out.println("\n=== Empréstimos do Leitor ===");
        for (Emprestimo emp : emprestimos) {
            System.out.println("\nLivro: " + emp.getLivro().getTitulo());
            System.out.println("ISBN: " + emp.getLivro().getCodigoIsbn());
            System.out.println("Data do empréstimo: " + sdf.format(emp.getDataEmprestimo()));
            System.out.println("Data prevista devolução: " + sdf.format(emp.getDataPrevistaDevolucao()));
            if (emp.getDataDevolucao() != null) {
                System.out.println("Devolvido em: " + sdf.format(emp.getDataDevolucao()));
            } else {
                System.out.println("Status: Em andamento");
            }
            System.out.println("----------------------------------------");
        }
    }

    private void marcarLivroDevolvido() {
        String isbn = MenuUtils.lerString("Digite o ISBN do livro: ");
        if (isbn == null) return;

        String emailLeitor = MenuUtils.lerString("Digite o email do leitor: ");
        if (emailLeitor == null) return;

        try {
            biblioteca.marcarComoDevolvidoAdmin(isbn, emailLeitor);
            FileManager.salvarDados(biblioteca);
            System.out.println("Livro marcado como devolvido com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void alterarDataDevolucao() {
        String isbn = MenuUtils.lerString("Digite o ISBN do livro: ");
        if (isbn == null) return;

        String emailLeitor = MenuUtils.lerString("Digite o email do leitor: ");
        if (emailLeitor == null) return;

        String dataStr = MenuUtils.lerString("Digite a nova data de devolução (dd/MM/yyyy): ");
        if (dataStr == null) return;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date novaData = sdf.parse(dataStr);

            // Find the active loan
            Emprestimo emprestimo = biblioteca.getEmprestimos().stream()
                    .filter(e -> e.getLivro().getCodigoIsbn().equals(isbn) &&
                            e.getLeitor().getEmail().equals(emailLeitor) &&
                            e.getDataDevolucao() == null)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado"));

            // Validate the date change
            String validationError = ValidationUtils.validarAlteracaoDataDevolucao(novaData, emprestimo);
            if (validationError != null) {
                System.out.println("Erro: " + validationError);
                return;
            }

            biblioteca.alterarDataDevolucaoAdmin(isbn, emailLeitor, novaData);
            FileManager.salvarDados(biblioteca);
            System.out.println("Data de devolução alterada com sucesso!");
        } catch (ParseException e) {
            System.out.println("Formato de data inválido! Use dd/MM/yyyy");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void listarEmprestimosAtivos(Leitor leitor) {
        List<Emprestimo> emprestimos = biblioteca.consultarEmprestimosAtivos(leitor);
        if (emprestimos.isEmpty()) {
            System.out.println("Você não possui empréstimos ativos.");
            return;
        }

        System.out.println("\n=== Seus Empréstimos Ativos ===");
        for (Emprestimo emp : emprestimos) {
            exibirDetalhesEmprestimo(emp);
        }
    }

    private void consultarEmprestimosPorPeriodo(Leitor leitor) {
        String dataInicialStr = MenuUtils.lerString("Digite a data inicial (dd/MM/yyyy): ");
        if (dataInicialStr == null) return;

        String dataFinalStr = MenuUtils.lerString("Digite a data final (dd/MM/yyyy): ");
        if (dataFinalStr == null) return;

        try {
            Date dataInicial = sdf.parse(dataInicialStr);
            Date dataFinal = sdf.parse(dataFinalStr);

            if (dataFinal.before(dataInicial)) {
                System.out.println("A data final não pode ser anterior à data inicial!");
                return;
            }

            List<Emprestimo> emprestimos = biblioteca.consultarEmprestimosLeitorPorPeriodo(leitor, dataInicial, dataFinal);
            exibirResultadosEmprestimos(emprestimos);
        } catch (ParseException e) {
            System.out.println("Formato de data inválido! Use dd/MM/yyyy");
        }
    }

    private void consultarEmprestimosPorTermo(Leitor leitor) {
        String termo = MenuUtils.lerString("Digite o termo de busca (título ou autor): ");
        if (termo == null) return;

        List<Emprestimo> emprestimos = biblioteca.consultarEmprestimosLeitorPorTermo(leitor, termo);
        exibirResultadosEmprestimos(emprestimos);
    }

    private void consultarEmprestimosPorCodigo(Leitor leitor) {
        String isbn = MenuUtils.lerString("Digite o ISBN do livro: ");
        if (isbn == null) return;

        List<Emprestimo> emprestimos = biblioteca.consultarEmprestimosLeitorPorCodigo(leitor, isbn);
        exibirResultadosEmprestimos(emprestimos);
    }

    public void realizarEmprestimo(Leitor leitor) {
        List<Emprestimo> emprestimosAtivos = biblioteca.consultarEmprestimosAtivos(leitor);
        if (emprestimosAtivos.size() >= biblioteca.getLimiteEmprestimosPorLeitor()) {
            System.out.println("Você atingiu o limite de empréstimos simultâneos!");
            return;
        }

        int opcao = MenuUtils.lerOpcaoMenu(1, 2,
                "=== Realizar Empréstimo ===\n" +
                        "1. Ver todos os livros disponíveis\n" +
                        "2. Pesquisar livro específico");

        switch (opcao) {
            case 1:
                realizarEmprestimoPorLista(leitor);
                break;
            case 2:
                realizarEmprestimoPorPesquisa(leitor);
                break;
        }
    }

    private void realizarEmprestimoPorLista(Leitor leitor) {
        biblioteca.listarLivrosDisponiveis();
        String isbn = MenuUtils.lerString("\nDigite o ISBN do livro que deseja emprestar: ");
        if (isbn == null) return;

        try {
            realizarEmprestimoLivro(leitor, isbn);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void realizarEmprestimoPorPesquisa(Leitor leitor) {
        String termo = MenuUtils.lerString("Digite o termo para pesquisa (título, autor ou ISBN): ");
        if (termo == null) return;

        List<Livro> resultados = biblioteca.pesquisarLivros(termo);
        if (resultados.isEmpty()) {
            System.out.println("Nenhum livro encontrado com esse termo.");
            return;
        }

        biblioteca.exibirResultadosPesquisa(resultados);
        String isbn = MenuUtils.lerString("\nDigite o ISBN do livro que deseja emprestar: ");
        if (isbn == null) return;

        try {
            realizarEmprestimoLivro(leitor, isbn);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void realizarEmprestimoLivro(Leitor leitor, String isbn) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7);
        Date dataDevolucao = cal.getTime();

        try {
            biblioteca.realizarEmprestimo(leitor, isbn, dataDevolucao); // Add this call
            FileManager.salvarDados(biblioteca);
            System.out.println("Empréstimo realizado com sucesso!");
            System.out.println("Data de devolução prevista: " + sdf.format(dataDevolucao));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Não foi possível realizar o empréstimo: " + e.getMessage());
        }
    }

    public void realizarDevolucao(Leitor leitor) {
        List<Emprestimo> emprestimosAtivos = biblioteca.consultarEmprestimosAtivos(leitor);
        if (emprestimosAtivos.isEmpty()) {
            System.out.println("Você não possui empréstimos ativos para devolver.");
            return;
        }

        System.out.println("\n=== Seus Empréstimos Ativos ===");
        for (Emprestimo emp : emprestimosAtivos) {
            exibirDetalhesEmprestimo(emp);
        }

        String isbn = MenuUtils.lerString("\nDigite o ISBN do livro que deseja devolver: ");
        if (isbn == null) return;

        try {
            // Find the active loan
            Emprestimo emprestimo = emprestimosAtivos.stream()
                    .filter(e -> e.getLivro().getCodigoIsbn().equals(isbn))
                    .findFirst()
                    .orElse(null);

            // Validate the return
            String validationError = ValidationUtils.validarDevolucao(emprestimo);
            if (validationError != null) {
                System.out.println("Erro: " + validationError);
                return;
            }

            biblioteca.devolverLivro(leitor, isbn);
            FileManager.salvarDados(biblioteca);
            System.out.println("Livro devolvido com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao devolver livro: " + e.getMessage());
        }
    }

    private void exibirDetalhesEmprestimo(Emprestimo emp) {
        System.out.println("\nLivro: " + emp.getLivro().getTitulo());
        System.out.println("ISBN: " + emp.getLivro().getCodigoIsbn());
        System.out.println("Data do empréstimo: " + sdf.format(emp.getDataEmprestimo()));
        System.out.println("Data prevista para devolução: " + sdf.format(emp.getDataPrevistaDevolucao()));
        if (emp.getDataDevolucao() != null) {
            System.out.println("Devolvido em: " + sdf.format(emp.getDataDevolucao()));
        }
        System.out.println("----------------------------------------");
    }

    private void exibirResultadosEmprestimos(List<Emprestimo> emprestimos) {
        if (emprestimos.isEmpty()) {
            System.out.println("Nenhum empréstimo encontrado!");
            return;
        }

        System.out.println("\n=== Empréstimos Encontrados ===");
        for (Emprestimo emp : emprestimos) {
            exibirDetalhesEmprestimo(emp);
        }
    }
}