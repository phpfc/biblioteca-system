package com.biblioteca;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private String login;
    private String senha;
    private boolean isAdmin;
    private static List<Usuario> usuarios = new ArrayList<>();
    private Leitor leitor;

    public Usuario(String login, String senha, boolean isAdmin) {
        this.login = login;
        this.senha = senha;
        this.isAdmin = isAdmin;
        this.leitor = null;
    }

    public Usuario(String login, String senha, boolean isAdmin, Leitor leitor) {
        this.login = login;
        this.senha = senha;
        this.isAdmin = isAdmin;
        this.leitor = leitor;
    }

    public static boolean autenticar(String login, String senha) {
        for (Usuario usuario : usuarios) {
            if (usuario.login.equals(login) && usuario.senha.equals(senha)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAdmin(String login) {
        if (login == null) return false;

        for (Usuario usuario : usuarios) {
            if (usuario.login.equals(login)) {
                return usuario.isAdmin;
            }
        }
        return false;
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }

    public static boolean isAdminByLogin(String login) {
        for (Usuario usuario : usuarios) {
            if (usuario.login.equals(login)) {
                return usuario.isAdmin;
            }
        }
        return false;
    }

    public static List<Usuario> getUsuarios() {
        return usuarios;
    }

    public static void setUsuarios(List<Usuario> novaLista) {
        usuarios = novaLista;
    }

    public String getLogin() {
        return this.login;
    }

    public Leitor getLeitor() {
        return leitor;
    }

    public void setLeitor(Leitor leitor) {
        this.leitor = leitor;
    }
}