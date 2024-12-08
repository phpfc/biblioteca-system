package com.biblioteca.utils;

import com.biblioteca.models.*;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern ISBN_PATTERN = Pattern.compile("^[0-9]{6}$");
    private static final Pattern NOME_PATTERN = Pattern.compile("^[\\p{L}\\s.',-]{2,100}$");
    private static final Pattern CODIGO_CATEGORIA_PATTERN = Pattern.compile("^[A-Z]{2,5}$");

    /**
     * Valida um endereço de email
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Valida um ISBN (6 dígitos)
     */
    public static boolean isValidISBN(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }
        return ISBN_PATTERN.matcher(isbn).matches();
    }

    /**
     * Valida um nome (2-100 caracteres, letras e pontuação básica)
     */
    public static boolean isValidNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        return NOME_PATTERN.matcher(nome).matches();
    }

    /**
     * Valida um código de categoria (2-5 letras maiúsculas)
     */
    public static boolean isValidCodigoCategoria(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return false;
        }
        return CODIGO_CATEGORIA_PATTERN.matcher(codigo).matches();
    }

    /**
     * Valida quantidade de cópias
     */
    public static boolean isValidQuantidade(int quantidade) {
        return quantidade > 0;
    }

    /**
     * Valida se uma data é futura
     */
    public static boolean isDataFutura(Date data) {
        return data != null && data.after(new Date());
    }

    /**
     * Valida credenciais de login
     */
    public static boolean isValidCredentials(String login, String senha) {
        return login != null && !login.trim().isEmpty() &&
                senha != null && senha.length() >= 6;
    }

    /**
     * Valida um novo livro
     */
    public static String validarLivro(Livro livro, List<Livro> livrosExistentes) {
        if (!isValidNome(livro.getTitulo())) {
            return "Título inválido";
        }
        if (!isValidNome(livro.getAutor())) {
            return "Autor inválido";
        }
        if (!isValidISBN(livro.getCodigoIsbn())) {
            return "ISBN inválido";
        }
        if (!isValidQuantidade(livro.getCopiasTotal())) {
            return "Quantidade de cópias inválida";
        }
        if (livro.getCategoria() == null) {
            return "Categoria é obrigatória";
        }

        // Verificar ISBN duplicado
        for (Livro livroExistente : livrosExistentes) {
            if (livroExistente.getCodigoIsbn().equals(livro.getCodigoIsbn())) {
                return "ISBN já cadastrado";
            }
        }

        return null; // null significa que não há erros
    }

    /**
     * Valida uma nova categoria
     */
    public static String validarCategoria(Categoria categoria, List<Categoria> categoriasExistentes) {
        if (!isValidNome(categoria.getNome())) {
            return "Nome inválido";
        }
        if (!isValidCodigoCategoria(categoria.getCodigo())) {
            return "Código inválido";
        }

        // Verificar código duplicado
        for (Categoria categoriaExistente : categoriasExistentes) {
            if (categoriaExistente.getCodigo().equals(categoria.getCodigo())) {
                return "Código já cadastrado";
            }
        }

        return null; // null significa que não há erros
    }

    /**
     * Valida um novo leitor
     */
    public static String validarLeitor(Leitor leitor, List<Leitor> leitoresExistentes) {
        if (!isValidNome(leitor.getNome())) {
            return "Nome inválido";
        }
        if (!isValidEmail(leitor.getEmail())) {
            return "Email inválido";
        }

        // Verificar email duplicado
        for (Leitor leitorExistente : leitoresExistentes) {
            if (leitorExistente.getEmail().equals(leitor.getEmail())) {
                return "Email já cadastrado";
            }
        }

        return null; // null significa que não há erros
    }

    /**
     * Valida um novo empréstimo
     */
    public static String validarEmprestimo(Emprestimo emprestimo, Biblioteca biblioteca) {
        if (emprestimo.getLeitor() == null) {
            return "Leitor é obrigatório";
        }
        if (emprestimo.getLivro() == null) {
            return "Livro é obrigatório";
        }
        if (emprestimo.getDataEmprestimo() == null) {
            return "Data de empréstimo é obrigatória";
        }
        if (!emprestimo.getLivro().temCopiaDisponivel()) {
            return "Livro não possui cópias disponíveis";
        }

        // Verificar limite de empréstimos do leitor
        int emprestimosAtivos = 0;
        for (Emprestimo emp : biblioteca.getEmprestimos()) {
            if (emp.getLeitor().equals(emprestimo.getLeitor()) && emp.getDataDevolucao() == null) {
                emprestimosAtivos++;
            }
        }
        if (emprestimosAtivos >= biblioteca.getLimiteEmprestimosPorLeitor()) {
            return "Leitor atingiu o limite de empréstimos";
        }

        return null; // null significa que não há erros
    }

    /**
     * Valida devolução de livro
     */
    public static String validarDevolucao(Emprestimo emprestimo) {
        if (emprestimo == null) {
            return "Empréstimo não encontrado";
        }
        if (emprestimo.getDataDevolucao() != null) {
            return "Livro já foi devolvido";
        }
        return null; // null significa que não há erros
    }

    /**
     * Valida alteração de data de devolução
     */
    public static String validarAlteracaoDataDevolucao(Date novaData, Emprestimo emprestimo) {
        if (novaData == null) {
            return "Data inválida";
        }
        if (novaData.before(emprestimo.getDataEmprestimo())) {
            return "Data de devolução não pode ser anterior à data do empréstimo";
        }
        if (emprestimo.getDataDevolucao() != null) {
            return "Livro já foi devolvido";
        }
        return null; // null significa que não há erros
    }
}