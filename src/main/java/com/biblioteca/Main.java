package com.biblioteca;

import com.biblioteca.models.Biblioteca;
import com.biblioteca.models.SistemaBibliotecas;
import com.biblioteca.models.Usuario;
import com.biblioteca.controllers.MenuController;
import com.biblioteca.utils.FileManager;
import com.biblioteca.utils.MenuUtils;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SistemaBibliotecas sistema = SistemaBibliotecas.getInstance();

        if (sistema.getBibliotecas().isEmpty()) {
            sistema.adicionarBiblioteca("Biblioteca Central", "Rua Principal, 123");
        }

        if (precisaCriarAdmin()) {
            criarPrimeiroAdmin(sistema); // Passando o sistema como parâmetro
        }

        MenuController menuController = new MenuController(sistema);
        menuController.menuLogin();
    }

    private static boolean precisaCriarAdmin() {
        return Usuario.getUsuarios() == null || Usuario.getUsuarios().isEmpty();
    }

    private static void criarPrimeiroAdmin(SistemaBibliotecas sistema) {
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

        FileManager.salvarDados(sistema);
        System.out.println("Administrador criado com sucesso!");
    }
}