import java.util.Scanner;

public class JogoXadrez {
    // Matriz 8x8 que representa o tabuleiro
    // Peças Brancas: P (Peão), T (Torre), C (Cavalo), B (Bispo), D (Dama), R (Rei)
    // Peças Pretas:  p, t, c, b, d, r
    private static String[][] tabuleiro = new String[8][8];
    private static boolean turnoBrancas = true;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        inicializarTabuleiro();

        System.out.println("=========================================");
        System.out.println("        JOGO DE XADREZ NO TERMINAL       ");
        System.out.println("=========================================");
        System.out.println("Brancas: MAIÚSCULAS (P, T, C, B, D, R)");
        System.out.println("Pretas:  minúsculas  (p, t, c, b, d, r)");
        System.out.println("-----------------------------------------");
        System.out.println("Como jogar: Digite as coordenadas da jogada.");
        System.out.println("Exemplo: a2 a4 (move a peca de a2 para a4)");

        while (true) {
            desenharTabuleiro();
            System.out.println("\nTurno das: " + (turnoBrancas ? "BRANCAS (Maiusculas)" : "PRETAS (Minusculas)"));
            System.out.print("Digite seu movimento (ou 'sair'): ");
            String jogada = scanner.nextLine().trim();

            if (jogada.equalsIgnoreCase("sair")) {
                System.out.println("Jogo encerrado!");
                break;
            }

            // Valida o formato da entrada (ex: "a2 a4")
            if (!jogada.matches("[a-h][1-8] [a-h][1-8]")) {
                System.out.println("-> Movimento invalido! Use o formato correto (ex: e2 e4).");
                continue;
            }

            // Separa as coordenadas de origem e destino
            String[] partes = jogada.split(" ");
            
            // Converte colunas (a-h) para índices numéricos (0-7)
            int origemCol = partes[0].charAt(0) - 'a';
            int destinoCol = partes[1].charAt(0) - 'a';
            
            // Converte linhas (1-8) para índices da matriz (7-0) - Xadrez conta de baixo para cima
            int origemLin = 8 - Character.getNumericValue(partes[0].charAt(1));
            int destinoLin = 8 - Character.getNumericValue(partes[1].charAt(1));

            if (processarMovimento(origemLin, origemCol, destinoLin, destinoCol)) {
                turnoBrancas = !turnoBrancas; // Passa o turno se o movimento foi aceito
            }
        }
        scanner.close();
    }

    private static void inicializarTabuleiro() {
        // Peças Pretas (Linha 0 e 1)
        tabuleiro[0] = new String[]{"t", "c", "b", "d", "r", "b", "c", "t"};
        for (int c = 0; c < 8; c++) tabuleiro[1][c] = "p";

        // Espaços Vazios (Linhas 2 a 5)
        for (int l = 2; l <= 5; l++) {
            for (int c = 0; c < 8; c++) tabuleiro[l][c] = ".";
        }

        // Peças Brancas (Linha 6 e 7)
        for (int c = 0; c < 8; c++) tabuleiro[6][c] = "P";
        tabuleiro[7] = new String[]{"T", "C", "B", "D", "R", "B", "C", "T"};
    }

    private static void desenharTabuleiro() {
        System.out.println("\n   a  b  c  d  e  f  g  h");
        System.out.println("  -------------------------");
        for (int l = 0; l < 8; l++) {
            System.out.print((8 - l) + " |"); // Mostra o número da linha na esquerda
            for (int c = 0; c < 8; c++) {
                System.out.print(" " + tabuleiro[l][c] + " ");
            }
            System.out.println("| " + (8 - l)); // Mostra o número da linha na direita
        }
        System.out.println("  -------------------------");
        System.out.println("   a  b  c  d  e  f  g  h");
    }

    private static boolean processarMovimento(int oLin, int oCol, int dLin, int dCol) {
        String pecaOrigem = tabuleiro[oLin][oCol];
        String pecaDestino = tabuleiro[dLin][dCol];

        if (pecaOrigem.equals(".")) {
            System.out.println("-> Erro: Nao ha nenhuma peca na posicao de origem!");
            return false;
        }

        // Verifica se o jogador está mexendo na sua própria peça
        boolean ehPecaBranca = Character.isUpperCase(pecaOrigem.charAt(0));
        if ((turnoBrancas && !ehPecaBranca) || (!turnoBrancas && ehPecaBranca)) {
            System.out.println("-> Erro: Voce nao pode mexer nas pecas do adversario!");
            return false;
        }

        // Impede que capture uma peça da sua própria cor
        if (!pecaDestino.equals(".")) {
            boolean destinoBranca = Character.isUpperCase(pecaDestino.charAt(0));
            if (ehPecaBranca == destinoBranca) {
                System.out.println("-> Erro: Voce nao pode capturar sua propria peca!");
                return false;
            }
        }

        // Validação básica de Movimento (Peão e Rei para teste simplificado)
        // Se quiser desativar restrições temporariamente para testar livremente, apague as regras abaixo
        if (pecaOrigem.equalsIgnoreCase("P")) {
            int direcao = ehPecaBranca ? -1 : 1;
            // Movimento simples para frente
            if (oCol == dCol && pecaDestino.equals(".") && (dLin - oLin != direcao)) {
                // Permite andar 2 casas no primeiro movimento do peão
                int linhaInicial = ehPecaBranca ? 6 : 1;
                if (!(oLin == linhaInicial && dLin - oLin == direcao * 2 && tabuleiro[oLin + direcao][oCol].equals("."))) {
                    System.out.println("-> Erro: Movimento invalido para o Peao!");
                    return false;
                }
            }
        }

        // Executa o movimento movendo as strings na matriz
        tabuleiro[dLin][dCol] = pecaOrigem;
        tabuleiro[oLin][oCol] = ".";
        System.out.println("-> Movimento realizado com sucesso!");
        return true;
    }
}