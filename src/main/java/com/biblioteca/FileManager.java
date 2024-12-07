package com.biblioteca;

import java.io.*;
import java.util.*;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class FileManager {
    private static final String DATA_DIR = "data";
    private static final String LIVROS_FILE = DATA_DIR + "/livros.ser";
    private static final String USUARIOS_FILE = DATA_DIR + "/usuarios.ser";
    private static final String CATEGORIAS_FILE = DATA_DIR + "/categorias.ser";
    private static final String EMPRESTIMOS_FILE = DATA_DIR + "/emprestimos.ser";
    private static final String LEITORES_FILE = DATA_DIR + "/leitores.ser";
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();

    public static void inicializarDiretorio() {
        File directory = new File(DATA_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static void salvarDados(Biblioteca biblioteca) {
        inicializarDiretorio();
        salvarLivros(biblioteca.getLivros());
        salvarUsuarios(Usuario.getUsuarios());
        salvarCategorias(biblioteca.getCategorias());
        salvarEmprestimos(biblioteca.getEmprestimos());
        salvarLeitores(biblioteca.getLeitores());
    }

    public static void carregarDados(Biblioteca biblioteca) {
        inicializarDiretorio();
        List<Livro> livros = carregarLivros();
        List<Usuario> usuarios = carregarUsuarios();
        List<Categoria> categorias = carregarCategorias();
        List<Emprestimo> emprestimos = carregarEmprestimos();
        List<Leitor> leitores = carregarLeitores();

        if (livros != null) biblioteca.setLivros(livros);
        if (usuarios != null) Usuario.setUsuarios(usuarios);
        if (categorias != null) biblioteca.setCategorias(categorias);
        if (emprestimos != null) biblioteca.setEmprestimos(emprestimos);
        if (leitores != null) biblioteca.setLeitores(leitores);
        biblioteca.limparEmprestimosInvalidos();
    }

    private static void salvarLivros(List<Livro> livros) {
        salvarParaArquivo(LIVROS_FILE, livros);
    }

    private static void salvarUsuarios(List<Usuario> usuarios) {
        salvarParaArquivo(USUARIOS_FILE, usuarios);
    }

    private static void salvarCategorias(List<Categoria> categorias) {
        salvarParaArquivo(CATEGORIAS_FILE, categorias);
    }

    private static void salvarEmprestimos(List<Emprestimo> emprestimos) {
        salvarParaArquivo(EMPRESTIMOS_FILE, emprestimos);
    }

    private static void salvarLeitores(List<Leitor> leitores) {
        salvarParaArquivo(LEITORES_FILE, leitores);
    }

    private static List<Livro> carregarLivros() {
        return carregarDeArquivo(LIVROS_FILE, new TypeToken<List<Livro>>(){}.getType());
    }

    private static List<Usuario> carregarUsuarios() {
        return carregarDeArquivo(USUARIOS_FILE, new TypeToken<List<Usuario>>(){}.getType());
    }

    private static List<Categoria> carregarCategorias() {
        return carregarDeArquivo(CATEGORIAS_FILE, new TypeToken<List<Categoria>>(){}.getType());
    }

    private static List<Emprestimo> carregarEmprestimos() {
        return carregarDeArquivo(EMPRESTIMOS_FILE, new TypeToken<List<Emprestimo>>(){}.getType());
    }

    private static List<Leitor> carregarLeitores() {
        return carregarDeArquivo(LEITORES_FILE, new TypeToken<List<Leitor>>(){}.getType());
    }

    private static <T> void salvarParaArquivo(String nomeArquivo, T dados) {
        try (Writer writer = new FileWriter(nomeArquivo)) {
            gson.toJson(dados, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static <T> T carregarDeArquivo(String nomeArquivo, Type tipo) {
        try (Reader reader = new FileReader(nomeArquivo)) {
            return gson.fromJson(reader, tipo);
        } catch (IOException e) {
            return null;
        }
    }
}