package com.biblioteca;

import java.util.regex.Pattern;

public class ValidationUtils {
    private static final int MIN_LENGTH_NOME = 3;
    private static final int MIN_LENGTH_SENHA = 6;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );
    private static final Pattern LOGIN_PATTERN = Pattern.compile(
            "^[A-Za-z0-9_-]{3,20}$"
    );

    public static boolean validarNome(String nome) {
        return nome != null &&
                !nome.trim().isEmpty() &&
                nome.length() >= MIN_LENGTH_NOME &&
                nome.matches("^[A-Za-zÀ-ÿ\\s]{3,50}$");
    }

    public static boolean validarEmail(String email) {
        return email != null &&
                EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean validarLogin(String login) {
        return login != null &&
                LOGIN_PATTERN.matcher(login).matches();
    }

    public static boolean validarSenha(String senha) {
        return senha != null &&
                senha.length() >= MIN_LENGTH_SENHA &&
                senha.matches(".*[A-Z].*") && // pelo menos uma letra maiúscula
                senha.matches(".*[a-z].*") && // pelo menos uma letra minúscula
                senha.matches(".*\\d.*");     // pelo menos um número
    }

    public static String getLoginErrorMessage() {
        return "Login inválido. Deve conter entre 3 e 20 caracteres, apenas letras, números, _ ou -";
    }

    public static String getSenhaErrorMessage() {
        return "Senha inválida. Deve ter pelo menos 6 caracteres, uma letra maiúscula, uma minúscula e um número";
    }

    public static String getNomeErrorMessage() {
        return "Nome inválido. Deve conter pelo menos 3 caracteres e apenas letras";
    }

    public static String getEmailErrorMessage() {
        return "Email inválido. Deve estar em um formato válido (exemplo@dominio.com)";
    }
}