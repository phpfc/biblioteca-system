package com.biblioteca;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Biblioteca {
    private List<Livro> livros;
    private List<Categoria> categorias;
    private List<Emprestimo> emprestimos;
    private List<Leitor> leitores;

    public Biblioteca() {
        this.livros = new ArrayList<>();
        this.categorias = new ArrayList<>();
        this.emprestimos = new ArrayList<>();
        this.leitores = new ArrayList<>();
    }

    public List<Leitor> getLeitores() {
        return leitores;
    }

    public void listarLivros() {
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
            return;
        }

        System.out.println("\n=== Livros Cadastrados ===");
        for (Livro livro : livros) {
            System.out.println(livro.informaLivro());
            System.out.println("-----------------------------");
        }
    }

    public Livro buscarPorIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return null;
        }

        for (Livro livro : livros) {
            if (livro.getCodigoIsbn().equals(isbn)) {
                return livro;
            }
        }
        return null;
    }

    public boolean removerLivro(String isbn) {
        Livro livro = buscarPorIsbn(isbn);
        if (livro != null) {
            livros.remove(livro);
            return true;
        }
        return false;
    }

    public Categoria buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }

        for (Categoria categoria : categorias) {
            if (categoria.getCodigo().equals(codigo)) {
                return categoria;
            }
        }
        return null;
    }

    public Categoria buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return null;
        }

        for (Categoria categoria : categorias) {
            if (categoria.getNome().toLowerCase().contains(nome.toLowerCase())) {
                return categoria;
            }
        }
        return null;
    }

    public boolean removerCategoria(String codigo) {
        Categoria categoria = buscarPorCodigo(codigo);
        if (categoria != null) {
            for (Livro livro : livros) {
                if (livro.getCategoria() != null && livro.getCategoria().equals(categoria)) {
                    livro.setCategoria(null);
                }
            }
            categorias.remove(categoria);
            return true;
        }
        return false;
    }

    public boolean editarCategoria(String codigo, String novoNome, String novoCodigo) {
        Categoria categoria = buscarPorCodigo(codigo);
        if (categoria != null) {
            if (novoNome != null && !novoNome.isEmpty()) {
                categoria.setNome(novoNome);
            }
            if (novoCodigo != null && !novoCodigo.isEmpty()) {
                categoria.setCodigo(novoCodigo);
            }
            return true;
        }
        return false;
    }

    public void editarLivro(String isbn, String novoTitulo, String novoAutor, int novasCopias, Categoria novaCategoria) {
        Livro livro = buscarPorIsbn(isbn);
        if (livro != null) {
            if (novoTitulo != null && !novoTitulo.trim().isEmpty()) {
                livro.setTitulo(novoTitulo);
            }
            if (novoAutor != null && !novoAutor.trim().isEmpty()) {
                livro.setAutor(novoAutor);
            }
            if (novasCopias >= 0) {
                int copiasEmprestadas = livro.getCopiasTotal() - livro.getCopiasDisponiveis();
                int novasCopiasDisponiveis = Math.max(0, novasCopias - copiasEmprestadas);
                livro.setCopiasTotal(novasCopias);
                livro.setCopiasDisponiveis(novasCopiasDisponiveis);
            }
            if (novaCategoria != null) {
                livro.setCategoria(novaCategoria);
            }
            FileManager.salvarDados(this);
        }
    }

    public void listarEmprestimos() {
        System.out.println("=== Lista de Empréstimos ===");
        if (emprestimos.isEmpty()) {
            System.out.println("Não há empréstimos registrados.");
            return;
        }

        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo != null) {  // Verifica se o empréstimo não é nulo
                System.out.print(emprestimo.informaEmprestimo());
            }
        }
    }

    public void limparEmprestimosInvalidos() {
        emprestimos.removeIf(emp ->
                emp == null ||
                        emp.getLivro() == null ||
                        emp.getDataEmprestimo() == null
        );
        FileManager.salvarDados(this);
    }

    // Para o menu do leitor, podemos ter um método específico
    public void listarEmprestimosLeitor(Leitor leitor) {
        if (leitor == null) {
            System.out.println("Erro: Leitor não identificado!");
            return;
        }

        System.out.println("=== Seus Empréstimos ===");
        boolean temEmprestimo = false;

        for (Emprestimo emprestimo : emprestimos) {
            // Verifica se o empréstimo é válido e pertence ao leitor correto
            // Compara tanto o código quanto o email para garantir que é o leitor correto
            if (emprestimo != null &&
                    emprestimo.getLeitor() != null &&
                    emprestimo.getLeitor().getCodigo().equals(leitor.getCodigo()) &&
                    emprestimo.getLeitor().getEmail().equals(leitor.getEmail())) {

                System.out.println("Código do Livro: " + emprestimo.getLivro().getCodigoIsbn());
                System.out.println("Título: " + emprestimo.getLivro().getTitulo());
                System.out.println("Data do empréstimo: " + emprestimo.getDataEmprestimo());

                if (emprestimo.getDataDevolucao() != null) {
                    System.out.println("Status: Devolvido");
                    System.out.println("Data de devolução: " + emprestimo.getDataDevolucao());
                } else {
                    System.out.println("Status: Em andamento");
                }
                System.out.println("-----------------------------");
                temEmprestimo = true;
            }
        }

        if (!temEmprestimo) {
            System.out.println("Você não possui empréstimos.");
        }
    }

    // Atualiza também os métodos de consulta para usar a mesma lógica de filtragem
    public List<Emprestimo> consultarEmprestimosLeitorPorPeriodo(Leitor leitor, Date inicio, Date fim) {
        if (leitor == null) return new ArrayList<>();

        return emprestimos.stream()
                .filter(emp -> emp.getLeitor() != null &&
                        emp.getLeitor().getCodigo().equals(leitor.getCodigo()) &&
                        emp.getLeitor().getEmail().equals(leitor.getEmail()) &&
                        emp.getDataEmprestimo().after(inicio) &&
                        emp.getDataEmprestimo().before(fim))
                .collect(Collectors.toList());
    }

    public List<Emprestimo> consultarEmprestimosLeitorPorTermo(Leitor leitor, String termoBusca) {
        if (leitor == null || termoBusca == null) return new ArrayList<>();

        final String termoFinal = termoBusca.toLowerCase();
        return emprestimos.stream()
                .filter(emp -> emp.getLeitor() != null &&
                        emp.getLeitor().getCodigo().equals(leitor.getCodigo()) &&
                        emp.getLeitor().getEmail().equals(leitor.getEmail()) &&
                        (emp.getLivro().getTitulo().toLowerCase().contains(termoFinal) ||
                                emp.getLivro().getAutor().toLowerCase().contains(termoFinal)))
                .collect(Collectors.toList());
    }

    public List<Emprestimo> consultarEmprestimosLeitorPorCodigo(Leitor leitor, String codigo) {
        if (leitor == null || codigo == null) return new ArrayList<>();

        return emprestimos.stream()
                .filter(emp -> emp.getLeitor() != null &&
                        emp.getLeitor().getCodigo().equals(leitor.getCodigo()) &&
                        emp.getLeitor().getEmail().equals(leitor.getEmail()) &&
                        emp.getLivro().getCodigoIsbn().equals(codigo))
                .collect(Collectors.toList());
    }

    public void listarLivrosDisponiveis() {
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
            return;
        }

        System.out.println("\n=== Livros Disponíveis ===");
        for (Livro livro : livros) {
            if (livro.temCopiaDisponivel()) {
                System.out.println(livro.informaLivro());
                System.out.println("-----------------------------");
            }
        }
    }

    public void emprestarLivro(Leitor leitor, String codigoLivro) {
        if (leitor == null) {
            System.out.println("Erro: Leitor não identificado!");
            return;
        }

        Livro livro = buscarPorIsbn(codigoLivro);
        if (livro == null) {
            System.out.println("Livro não encontrado!");
            return;
        }

        if (!livro.temCopiaDisponivel()) {
            System.out.println("Não há cópias disponíveis deste livro!");
            return;
        }

        // Verifica se o leitor já tem um empréstimo ativo deste livro
        for (Emprestimo emp : emprestimos) {
            if (emp.getLeitor() != null &&
                    emp.getLeitor().getCodigo().equals(leitor.getCodigo()) &&
                    emp.getLivro().getCodigoIsbn().equals(codigoLivro) &&
                    emp.getDataDevolucao() == null) {
                System.out.println("Você já possui um empréstimo ativo deste livro!");
                return;
            }
        }

        livro.emprestarCopia();
        Emprestimo emprestimo = new Emprestimo(leitor, livro, new Date(), null);
        emprestimos.add(emprestimo);
        FileManager.salvarDados(this);
        System.out.println("Empréstimo realizado com sucesso!");
    }


    public void devolverLivro(Leitor leitor, String codigoLivro) {
        Emprestimo emprestimoAtivo = null;
        for (Emprestimo emp : emprestimos) {
            if (emp.getLeitor().getCodigo().equals(leitor.getCodigo()) &&
                    emp.getLivro().getCodigoIsbn().equals(codigoLivro) &&
                    emp.getDataDevolucao() == null) {
                emprestimoAtivo = emp;
                break;
            }
        }

        if (emprestimoAtivo == null) {
            System.out.println("Não foi encontrado empréstimo ativo deste livro para você!");
            return;
        }

        emprestimoAtivo.setDataDevolucao(new Date());
        emprestimoAtivo.getLivro().devolverCopia();
        FileManager.salvarDados(this);
        System.out.println("Livro devolvido com sucesso!");
    }

    // Adicione estes métodos na classe Biblioteca

    public List<Livro> pesquisarLivros(String termo) {
        List<Livro> resultados = new ArrayList<>();

        // Se o termo estiver vazio, retorna lista vazia
        if (termo == null || termo.trim().isEmpty()) {
            return resultados;
        }

        termo = termo.toLowerCase().trim();

        // Primeiro tenta buscar por código ISBN exato
        for (Livro livro : livros) {
            if (livro.getCodigoIsbn().toLowerCase().equals(termo)) {
                resultados.add(livro);
                return resultados; // Retorna imediatamente se encontrar por ISBN
            }
        }

        // Se não encontrou por ISBN, busca por título ou autor
        for (Livro livro : livros) {
            if (livro.getTitulo().toLowerCase().contains(termo) ||
                    livro.getAutor().toLowerCase().contains(termo)) {
                resultados.add(livro);
            }
        }

        return resultados;
    }

    public void exibirResultadosPesquisa(List<Livro> resultados) {
        if (resultados.isEmpty()) {
            System.out.println("Nenhum livro encontrado.");
            return;
        }

        System.out.println("\n=== Resultados da Pesquisa ===");
        for (int i = 0; i < resultados.size(); i++) {
            System.out.println("\nLivro #" + (i + 1));
            System.out.println(resultados.get(i).informaLivro());
        }

        System.out.println("\nTotal de livros encontrados: " + resultados.size());
    }

    public void adicionarLivro(Livro livro) {
        livros.add(livro);
        FileManager.salvarDados(this);
    }

    public void adicionarCategorias(Categoria categoria) {
        categorias.add(categoria);
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void listarCategorias() {
        if (categorias.isEmpty()) {
            System.out.println("Nenhuma categoria cadastrada.");
            return;
        }

        System.out.println("\n=== Categorias Cadastradas ===");
        for (Categoria categoria : categorias) {
            System.out.println("Nome: " + categoria.getNome());
            System.out.println("Código: " + categoria.getCodigo());
            System.out.println("-----------------------------");
        }
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public void setLivros(List<Livro> livros) {
        this.livros = livros;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    public List<Emprestimo> getEmprestimos() {
        return emprestimos;
    }

    public void setEmprestimos(List<Emprestimo> emprestimos) {
        this.emprestimos = emprestimos;
    }

    public void setLeitores(List<Leitor> leitores) {
        this.leitores = leitores;
    }

    public List<Livro> buscarLivros(String termo) {
        List<Livro> resultados = new ArrayList<>();
        termo = termo.toLowerCase();

        for (Livro livro : livros) {
            if (livro.getTitulo().toLowerCase().contains(termo) ||
                    livro.getAutor().toLowerCase().contains(termo)) {
                resultados.add(livro);
            }
        }
        return resultados;
    }

    public List<Emprestimo> consultarEmprestimosPorData(Date inicio, Date fim) {
        List<Emprestimo> resultados = new ArrayList<>();
        for (Emprestimo emprestimo : emprestimos) {
            Date dataEmprestimo = emprestimo.getDataEmprestimo();
            if (dataEmprestimo.after(inicio) && dataEmprestimo.before(fim)) {
                resultados.add(emprestimo);
            }
        }
        return resultados;
    }

    public List<Emprestimo> consultarEmprestimosPorLivro(String codigoLivro) {
        List<Emprestimo> resultados = new ArrayList<>();
        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.getLivro().getCodigoIsbn().equals(codigoLivro)) {
                resultados.add(emprestimo);
            }
        }
        return resultados;
    }


        // Novo método para listar empréstimos de um livro específico
    public void listarEmprestimosLivro(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            System.out.println("ISBN inválido!");
            return;
        }

        Livro livro = buscarPorIsbn(isbn);
        if (livro == null) {
            System.out.println("Livro não encontrado!");
            return;
        }

        System.out.println("\n=== Empréstimos do Livro ===");
        System.out.println("Título: " + livro.getTitulo());
        System.out.println("ISBN: " + livro.getCodigoIsbn());
        System.out.println("Total de cópias: " + livro.getCopiasTotal());
        System.out.println("Cópias disponíveis: " + livro.getCopiasDisponiveis());
        System.out.println("\nEmpréstimos Ativos:");

        boolean temEmprestimo = false;
        for (Emprestimo emp : emprestimos) {
            if (emp != null && emp.getLivro().getCodigoIsbn().equals(isbn)) {
                System.out.println("\nLeitor: " + emp.getLeitor().getNome());
                System.out.println("Email: " + emp.getLeitor().getEmail());
                System.out.println("Data do empréstimo: " + emp.getDataEmprestimo());
                if (emp.getDataDevolucao() != null) {
                    System.out.println("Status: Devolvido em " + emp.getDataDevolucao());
                } else {
                    System.out.println("Status: Em andamento");
                }
                System.out.println("-----------------------------");
                temEmprestimo = true;
            }
        }

        if (!temEmprestimo) {
            System.out.println("Nenhum empréstimo registrado para este livro.");
        }
    }

    // Método para marcar um livro como devolvido
    public void marcarComoDevolvidoAdmin(String isbn, String emailLeitor) {
        for (Emprestimo emp : emprestimos) {
            if (emp != null &&
                    emp.getLivro().getCodigoIsbn().equals(isbn) &&
                    emp.getLeitor().getEmail().equals(emailLeitor) &&
                    emp.getDataDevolucao() == null) {

                emp.setDataDevolucao(new Date());
                emp.getLivro().devolverCopia();
                FileManager.salvarDados(this);
                System.out.println("Livro marcado como devolvido com sucesso!");
                return;
            }
        }
        System.out.println("Empréstimo não encontrado ou já devolvido!");
    }

    // Método para alterar a data de devolução de um empréstimo
    public void alterarDataDevolucaoAdmin(String isbn, String emailLeitor, Date novaData) {
        for (Emprestimo emp : emprestimos) {
            if (emp != null &&
                    emp.getLivro().getCodigoIsbn().equals(isbn) &&
                    emp.getLeitor().getEmail().equals(emailLeitor)) {

                emp.setDataDevolucao(novaData);
                FileManager.salvarDados(this);
                System.out.println("Data de devolução alterada com sucesso!");
                return;
            }
        }
        System.out.println("Empréstimo não encontrado!");
    }
}
