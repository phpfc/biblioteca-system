package com.biblioteca;

import com.biblioteca.models.Biblioteca;
import com.biblioteca.models.Usuario;
import com.biblioteca.controllers.MenuController;
import com.biblioteca.utils.FileManager;
import com.biblioteca.utils.MenuUtils;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();
        FileManager.carregarDados(biblioteca);

        if (precisaCriarAdmin()) {
            criarPrimeiroAdmin(biblioteca);
        }

        MenuController menuController = new MenuController(biblioteca);
        menuController.menuLogin();
    }

    private static boolean precisaCriarAdmin() {
        return Usuario.getUsuarios() == null || Usuario.getUsuarios().isEmpty();
    }

    private static void criarPrimeiroAdmin(Biblioteca biblioteca) {
        System.out.println("\n=== Primeira Execução - Criação do Administrador ===");
        System.out.println("Para começar, vamos criar o usuário administrador.");

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
}