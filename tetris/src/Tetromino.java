import java.util.Random;

public class Tetromino {

    public int[][] forma;
    public int x = 3;
    public int y = 0;
    public int idCor; // Guarda o número correspondente à cor da peça

    public Tetromino() {
        Random random = new Random();
        // Sorteia de 1 a 7 para definir o tipo e a cor da peça
        idCor = random.nextInt(7) + 1; 

        switch (idCor) {
            case 1: // Linha (I)
                forma = new int[][]{
                    {1, 1, 1, 1}
                };
                break;

            case 2: // Quadrado (O)
                forma = new int[][]{
                    {2, 2},
                    {2, 2}
                };
                break;

            case 3: // Peça T
                forma = new int[][]{
                    {3, 3, 3},
                    {0, 3, 0}
                };
                break;

            case 4: // L
                forma = new int[][]{
                    {4, 4, 4},
                    {4, 0, 0}
                };
                break;

            case 5: // J
                forma = new int[][]{
                    {5, 5, 5},
                    {0, 0, 5}
                };
                break;

            case 6: // Z
                forma = new int[][]{
                    {6, 6, 0},
                    {0, 6, 6}
                };
                break;

            case 7: // S
                forma = new int[][]{
                    {0, 7, 7},
                    {7, 7, 0}
                };
                break;
        }
    }

    // Rotaciona a matriz da peça em 90 graus no sentido horário
    public void rotacionar() {
        int linhas = forma.length;
        int colunas = forma[0].length;
        int[][] novaForma = new int[colunas][linhas];

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                novaForma[j][linhas - 1 - i] = forma[i][j];
            }
        }
        forma = novaForma;
    }

    // Desfaz a rotação caso a peça tente girar sem espaço na parede ou blocos fixos
    public void desfazerRotacao() {
        rotacionar();
        rotacionar();
        rotacionar();
    }
}