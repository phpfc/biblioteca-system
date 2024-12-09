package com.biblioteca.models;

import java.util.ArrayList;
import java.util.List;

public class SistemaBibliotecas {
    private List<Biblioteca> bibliotecas;
    private static SistemaBibliotecas instance;

    private SistemaBibliotecas() {
        this.bibliotecas = new ArrayList<>();
    }

    public static SistemaBibliotecas getInstance() {
        if (instance == null) {
            instance = new SistemaBibliotecas();
        }
        return instance;
    }

    public void adicionarBiblioteca(String nome, String endereco) {
        Biblioteca biblioteca = new Biblioteca();
        biblioteca.setNome(nome);
        biblioteca.setEndereco(endereco);
        bibliotecas.add(biblioteca);
    }

    public List<Biblioteca> getBibliotecas() {
        return bibliotecas;
    }

    public Biblioteca buscarBiblioteca(String nome) {
        return bibliotecas.stream()
                .filter(b -> b.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);
    }

    public void removerBiblioteca(String nome) {
        bibliotecas.removeIf(b -> b.getNome().equalsIgnoreCase(nome));
    }
}