package com.biblioteca.models;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private String login;
    private String senha;
    private boolean isAdmin;
    private Leitor leitor;
    private static List<Usuario> usuarios = new ArrayList<>();

    public Usuario(String login, String senha, boolean isAdmin) {
        this.login = login;
        this.senha = senha;
        this.isAdmin = isAdmin;
    }

    public Usuario(String login, String senha, boolean isAdmin, Leitor leitor) {
        this(login, senha, isAdmin);
        this.leitor = leitor;
    }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public boolean isAdmin() { return isAdmin; }
    public Leitor getLeitor() { return leitor; }
    public void setLeitor(Leitor leitor) { this.leitor = leitor; }
    public static List<Usuario> getUsuarios() { return usuarios; }
    public static void setUsuarios(List<Usuario> usuarios) { Usuario.usuarios = usuarios; }

    public static boolean autenticar(String login, String senha) {
        return usuarios.stream()
                .anyMatch(u -> u.login.equals(login) && u.senha.equals(senha));
    }

    public static boolean isAdmin(String login) {
        return usuarios.stream()
                .filter(u -> u.login.equals(login))
                .findFirst()
                .map(u -> u.isAdmin)
                .orElse(false);
    }
}