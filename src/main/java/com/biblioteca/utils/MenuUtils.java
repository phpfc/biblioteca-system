package com.biblioteca.utils;

import java.util.Scanner;

public class MenuUtils {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Lê uma opção do menu com validação de intervalo
     * @param min Valor mínimo aceito
     * @param max Valor máximo aceito
     * @param menuText Texto do menu a ser exibido
     * @return Opção escolhida ou -1 para voltar
     */
    public static int lerOpcaoMenu(int min, int max, String menuText) {
        while (true) {
            System.out.println("\n" + menuText);
            System.out.println("0. Voltar");
            System.out.print("\nEscolha uma opção: ");

            try {
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    System.out.println("Por favor, digite uma opção.");
                    continue;
                }

                int opcao = Integer.parseInt(input);

                if (opcao == 0) {
                    return -1;
                }

                if (opcao >= min && opcao <= max) {
                    return opcao;
                } else {
                    System.out.println("Por favor, escolha uma opção entre " + min + " e " + max);
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido.");
            }
        }
    }

    /**
     * Lê uma string com prompt
     * @param prompt Mensagem a ser exibida
     * @return String lida ou null se vazia
     */
    public static String lerString(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? null : input;
    }

    /**
     * Lê um inteiro com prompt e validação
     * @param prompt Mensagem a ser exibida
     * @return Integer lido ou null se inválido
     */
    public static Integer lerInteiro(String prompt) {
        String input = lerString(prompt);
        if (input == null) return null;

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um número válido.");
            return null;
        }
    }

    /**
     * Lê uma resposta sim/não
     * @param prompt Mensagem a ser exibida
     * @return Boolean (true para sim, false para não) ou null se inválido
     */
    public static Boolean lerSimNao(String prompt) {
        while (true) {
            String input = lerString(prompt + " (S/N): ");
            if (input == null) return null;

            if (input.equalsIgnoreCase("S")) return true;
            if (input.equalsIgnoreCase("N")) return false;

            System.out.println("Por favor, responda com S ou N.");
        }
    }

    /**
     * Lê uma data no formato dd/MM/yyyy
     * @param prompt Mensagem a ser exibida
     * @return String da data ou null se inválida
     */
    public static String lerData(String prompt) {
        while (true) {
            String input = lerString(prompt + " (dd/MM/yyyy): ");
            if (input == null) return null;

            if (input.matches("\\d{2}/\\d{2}/\\d{4}")) {
                return input;
            }

            System.out.println("Por favor, use o formato dd/MM/yyyy");
        }
    }

    /**
     * Aguarda o usuário pressionar ENTER para continuar
     */
    public static void aguardarEnter() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }

    /**
     * Limpa a tela do console
     */
    public static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Formata um texto como título
     * @param texto Texto a ser formatado
     * @return Texto formatado como título
     */
    public static String formatarTitulo(String texto) {
        String linha = "=".repeat(texto.length() + 4);
        return "\n" + linha + "\n  " + texto + "  \n" + linha;
    }

    /**
     * Formata uma mensagem de erro
     * @param mensagem Mensagem de erro
     * @return Mensagem formatada
     */
    public static String formatarErro(String mensagem) {
        return "\nERRO: " + mensagem;
    }

    /**
     * Formata uma mensagem de sucesso
     * @param mensagem Mensagem de sucesso
     * @return Mensagem formatada
     */
    public static String formatarSucesso(String mensagem) {
        return "\nSUCESSO: " + mensagem;
    }

    /**
     * Exibe uma lista numerada de itens
     * @param itens Array de strings a serem exibidos
     */
    public static void exibirListaNumerada(String[] itens) {
        for (int i = 0; i < itens.length; i++) {
            System.out.println((i + 1) + ". " + itens[i]);
        }
    }

    /**
     * Confirma uma ação com o usuário
     * @param mensagem Mensagem de confirmação
     * @return true se confirmado, false caso contrário
     */
    public static boolean confirmarAcao(String mensagem) {
        Boolean resposta = lerSimNao(mensagem + "\nConfirma a operação?");
        return resposta != null && resposta;
    }
}