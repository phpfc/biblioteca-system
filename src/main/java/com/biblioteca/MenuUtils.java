package com.biblioteca;

import java.util.Scanner;

public class MenuUtils {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Lê e valida uma opção de menu dentro de um intervalo especificado
     * @param min Valor mínimo válido
     * @param max Valor máximo válido
     * @param prompt Mensagem a ser exibida ao pedir input
     * @return Opção válida do menu ou -1 para voltar
     */
    public static int lerOpcaoMenu(int min, int max, String prompt) {
        while (true) {
            System.out.println(prompt);
            System.out.println("0. Voltar ao menu anterior");
            System.out.print("Digite sua opção: ");

            try {
                String input = scanner.nextLine().trim();

                // Verifica se o input está vazio
                if (input.isEmpty()) {
                    System.out.println("Por favor, digite uma opção válida.");
                    continue;
                }

                // Converte input para inteiro
                int opcao = Integer.parseInt(input);

                // Verifica se a opção está dentro do intervalo válido ou é 0 (voltar)
                if (opcao == 0) {
                    return -1; // Sinaliza para voltar
                } else if (opcao >= min && opcao <= max) {
                    return opcao;
                } else {
                    System.out.printf("Por favor, digite uma opção entre %d e %d, ou 0 para voltar.\n", min, max);
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite apenas números.");
            }
        }
    }

    /**
     * Lê uma string não vazia
     * @param prompt Mensagem a ser exibida ao pedir input
     * @return String válida ou null para voltar
     */
    public static String lerString(String prompt) {
        while (true) {
            System.out.println(prompt);
            System.out.println("Digite 0 para voltar ao menu anterior");
            System.out.print("Digite sua resposta: ");

            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                return null;
            } else if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println("Por favor, digite um valor válido.");
            }
        }
    }

    /**
     * Lê uma resposta sim/não
     * @param prompt Mensagem a ser exibida ao pedir input
     * @return true para sim, false para não, null para voltar
     */
    public static Boolean lerSimNao(String prompt) {
        while (true) {
            System.out.println(prompt + " (sim/nao)");
            System.out.println("Digite 0 para voltar ao menu anterior");
            System.out.print("Digite sua resposta: ");

            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("0")) {
                return null;
            } else if (input.equals("sim") || input.equals("s")) {
                return true;
            } else if (input.equals("nao") || input.equals("n")) {
                return false;
            } else {
                System.out.println("Por favor, digite 'sim' ou 'nao'.");
            }
        }
    }

    /**
     * Lê uma data no formato dd/MM/yyyy
     * @param prompt Mensagem a ser exibida ao pedir input
     * @return String da data ou null para voltar
     */
    public static String lerData(String prompt) {
        while (true) {
            System.out.println(prompt + " (formato: dd/MM/yyyy)");
            System.out.println("Digite 0 para voltar ao menu anterior");
            System.out.print("Digite a data: ");

            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                return null;
            }

            // Verifica formato básico da data (dd/MM/yyyy)
            if (input.matches("\\d{2}/\\d{2}/\\d{4}")) {
                return input;
            } else {
                System.out.println("Formato de data inválido. Use dd/MM/yyyy");
            }
        }
    }

    /**
     * Lê um número inteiro
     * @param prompt Mensagem a ser exibida ao pedir input
     * @param min Valor mínimo permitido
     * @param max Valor máximo permitido
     * @return Integer válido ou null para voltar
     */
    public static Integer lerInteiro(String prompt, int min, int max) {
        while (true) {
            String input = lerString(prompt);
            if (input == null) return null;

            try {
                int valor = Integer.parseInt(input);
                if (valor >= min && valor <= max) {
                    return valor;
                } else {
                    System.out.println("Digite um número entre " + min + " e " + max);
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido.");
            }
        }
    }
}