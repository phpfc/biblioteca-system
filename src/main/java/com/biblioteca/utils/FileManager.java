package com.biblioteca.utils;

import java.io.*;
import java.util.*;

import com.biblioteca.models.*;
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
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .setPrettyPrinting()
            .create();

    public static void verificarDiretorio() {
        File directory = new File(DATA_DIR);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Diretório de dados criado com sucesso.");
            } else {
                System.out.println("Falha ao criar diretório de dados.");
            }
        }
    }

    public static void salvarDados(Biblioteca biblioteca) {
        verificarDiretorio();

        try {
            salvarLivros(biblioteca.getLivros());
            salvarUsuarios(Usuario.getUsuarios());
            salvarCategorias(biblioteca.getCategorias());
            salvarEmprestimos(biblioteca.getEmprestimos());
            salvarLeitores(biblioteca.getLeitores());
        } catch (Exception e) {
            System.out.println("Erro ao salvar dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void carregarDados(Biblioteca biblioteca) {
        verificarDiretorio();

        try {
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
        } catch (Exception e) {
            System.out.println("Erro ao carregar dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void salvarLivros(List<Livro> livros) throws IOException {
        salvarParaArquivo(LIVROS_FILE, livros);
    }

    private static void salvarUsuarios(List<Usuario> usuarios) throws IOException {
        salvarParaArquivo(USUARIOS_FILE, usuarios);
    }

    private static void salvarCategorias(List<Categoria> categorias) throws IOException {
        salvarParaArquivo(CATEGORIAS_FILE, categorias);
    }

    private static void salvarEmprestimos(List<Emprestimo> emprestimos) throws IOException {
        salvarParaArquivo(EMPRESTIMOS_FILE, emprestimos);
    }

    private static void salvarLeitores(List<Leitor> leitores) throws IOException {
        salvarParaArquivo(LEITORES_FILE, leitores);
    }

    private static List<Livro> carregarLivros() throws IOException {
        return carregarDeArquivo(LIVROS_FILE, new TypeToken<List<Livro>>(){}.getType());
    }

    private static List<Usuario> carregarUsuarios() throws IOException {
        return carregarDeArquivo(USUARIOS_FILE, new TypeToken<List<Usuario>>(){}.getType());
    }

    private static List<Categoria> carregarCategorias() throws IOException {
        return carregarDeArquivo(CATEGORIAS_FILE, new TypeToken<List<Categoria>>(){}.getType());
    }

    private static List<Emprestimo> carregarEmprestimos() throws IOException {
        return carregarDeArquivo(EMPRESTIMOS_FILE, new TypeToken<List<Emprestimo>>(){}.getType());
    }

    private static List<Leitor> carregarLeitores() throws IOException {
        return carregarDeArquivo(LEITORES_FILE, new TypeToken<List<Leitor>>(){}.getType());
    }

    private static <T> void salvarParaArquivo(String nomeArquivo, T dados) throws IOException {
        try (Writer writer = new FileWriter(nomeArquivo)) {
            gson.toJson(dados, writer);
        }
    }

    private static <T> T carregarDeArquivo(String nomeArquivo, java.lang.reflect.Type tipo) throws IOException {
        File arquivo = new File(nomeArquivo);
        if (!arquivo.exists()) {
            return null;
        }
        try (Reader reader = new FileReader(arquivo)) {
            return gson.fromJson(reader, tipo);
        }
    }

    public static void mostrarConteudoBanco() {
        verificarDiretorio();
        System.out.println("\n=== Conteúdo do Banco de Dados ===");

        System.out.println("\nUsuários:");
        mostrarArquivo(USUARIOS_FILE);

        System.out.println("\nLivros:");
        mostrarArquivo(LIVROS_FILE);

        System.out.println("\nCategorias:");
        mostrarArquivo(CATEGORIAS_FILE);

        System.out.println("\nEmpréstimos:");
        mostrarArquivo(EMPRESTIMOS_FILE);

        System.out.println("\nLeitores:");
        mostrarArquivo(LEITORES_FILE);
    }

    public static void limparBanco() {
        verificarDiretorio();
        System.out.println("\n=== Limpando Banco de Dados ===");

        limparArquivo(USUARIOS_FILE);
        limparArquivo(LIVROS_FILE);
        limparArquivo(CATEGORIAS_FILE);
        limparArquivo(EMPRESTIMOS_FILE);
        limparArquivo(LEITORES_FILE);

        System.out.println("Banco de dados limpo com sucesso!");
    }

    private static void limparArquivo(String nomeArquivo) {
        try {
            File arquivo = new File(nomeArquivo);
            if (arquivo.exists()) {
                try (FileWriter writer = new FileWriter(arquivo)) {
                    writer.write("[]");
                }
                System.out.println("Arquivo " + nomeArquivo + " limpo.");
            } else {
                System.out.println("Arquivo " + nomeArquivo + " não existe.");
            }
        } catch (IOException e) {
            System.out.println("Erro ao limpar " + nomeArquivo + ": " + e.getMessage());
        }
    }

    public static void mostrarArquivo(String nomeArquivo) {
        try {
            File arquivo = new File(nomeArquivo);
            if (arquivo.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
                    String linha;
                    while ((linha = reader.readLine()) != null) {
                        System.out.println(linha);
                    }
                }
            } else {
                System.out.println("Arquivo " + nomeArquivo + " não existe.");
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler " + nomeArquivo + ": " + e.getMessage());
        }
    }

    public static void fazerBackup() {
        verificarDiretorio();
        String backupDir = DATA_DIR + "/backup_" + new Date().getTime();
        File diretorioBackup = new File(backupDir);

        if (!diretorioBackup.mkdirs()) {
            System.out.println("Erro ao criar diretório de backup");
            return;
        }

        copiarArquivo(USUARIOS_FILE, backupDir + "/usuarios.ser");
        copiarArquivo(LIVROS_FILE, backupDir + "/livros.ser");
        copiarArquivo(CATEGORIAS_FILE, backupDir + "/categorias.ser");
        copiarArquivo(EMPRESTIMOS_FILE, backupDir + "/emprestimos.ser");
        copiarArquivo(LEITORES_FILE, backupDir + "/leitores.ser");

        System.out.println("Backup realizado com sucesso em: " + backupDir);
    }

    private static void copiarArquivo(String origem, String destino) {
        try {
            File arquivoOrigem = new File(origem);
            if (arquivoOrigem.exists()) {
                try (FileInputStream fis = new FileInputStream(arquivoOrigem);
                     FileOutputStream fos = new FileOutputStream(destino)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                }
                System.out.println("Arquivo " + origem + " copiado com sucesso.");
            }
        } catch (IOException e) {
            System.out.println("Erro ao copiar arquivo " + origem + ": " + e.getMessage());
        }
    }
}