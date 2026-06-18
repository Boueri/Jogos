import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private final int TAM_BLOCO = 30;
    private final int COLUNAS = 10;
    private final int LINHAS = 20;

    private Tetromino peca = new Tetromino();
    private Timer timer;
    private boolean emMenu = true; 
    
    // Agora essa variável vai controlar o estado de pausa do jogo!
    private boolean pecaParada = false; 
    
    private int[][] tabuleiro = new int[20][10];
    private Clip clipeMusica;

    private final Color[] CORES = {
        Color.BLACK, Color.CYAN, Color.YELLOW, new Color(128, 0, 128),
        Color.ORANGE, Color.BLUE, Color.RED, Color.GREEN
    };

    public GamePanel() {
        setPreferredSize(new Dimension(COLUNAS * TAM_BLOCO, LINHAS * TAM_BLOCO));
        setBackground(Color.BLACK);

        timer = new Timer(500, this);
        timer.start();

        tocarMusica("musica.wav");
    }

    private void tocarMusica(String caminhoArquivo) {
        try {
            File arquivoSom = new File(caminhoArquivo);
            if (arquivoSom.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(arquivoSom);
                clipeMusica = AudioSystem.getClip();
                clipeMusica.open(audioStream);
                clipeMusica.loop(Clip.LOOP_CONTINUOUSLY); 
                clipeMusica.start();
            }
        } catch (Exception e) {
            System.out.println("Erro ao som: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (emMenu) {
            desenharMenu(g);
            return;
        }

        // Desenha a grade cinza
        g.setColor(Color.DARK_GRAY);
        for (int x = 0; x <= COLUNAS; x++) {
            g.drawLine(x * TAM_BLOCO, 0, x * TAM_BLOCO, LINHAS * TAM_BLOCO);
        }
        for (int y = 0; y <= LINHAS; y++) {
            g.drawLine(0, y * TAM_BLOCO, COLUNAS * TAM_BLOCO, y * TAM_BLOCO);
        }

        // Desenha o tabuleiro
        for (int linha = 0; linha < LINHAS; linha++) {
            for (int coluna = 0; coluna < COLUNAS; coluna++) {
                int idCor = tabuleiro[linha][coluna];
                if (idCor > 0) {
                    g.setColor(CORES[idCor]);
                    g.fillRect(coluna * TAM_BLOCO, linha * TAM_BLOCO, TAM_BLOCO, TAM_BLOCO);
                }
            }
        }

        // Desenha a peça atual
        g.setColor(CORES[peca.idCor]);
        for (int linha = 0; linha < peca.forma.length; linha++) {
            for (int coluna = 0; coluna < peca.forma[linha].length; coluna++) {
                if (peca.forma[linha][coluna] > 0) {
                    g.fillRect(
                        (peca.x + coluna) * TAM_BLOCO,
                        (peca.y + linha) * TAM_BLOCO,
                        TAM_BLOCO,
                        TAM_BLOCO
                    );
                }
            }
        }

        // TELA DE PAUSA: Se o jogo estiver pausado, desenha um texto por cima de tudo
        if (pecaParada) {
            desenharTelaPausa(g);
        }
    }

    private void desenharMenu(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("TETRIS", 70, 150);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Pressione ENTER para Jogar", 45, 300);

        g.setFont(new Font("Arial", Font.ITALIC, 12));
        g.setColor(Color.GRAY);
        g.drawString("Controles: Setas, (P) para Pausar", 45, 500);
    }

    // Desenha uma sobreposição escura e o texto "PAUSADO"
    private void desenharTelaPausa(Graphics g) {
        // Cria um efeito de película preta semi-transparente sobre o jogo
        g.setColor(new Color(0, 0, 0, 150)); 
        g.fillRect(0, 0, COLUNAS * TAM_BLOCO, LINHAS * TAM_BLOCO);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 35));
        g.drawString("PAUSADO", 60, 280);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("Pressione 'P' para Voltar", 70, 320);
    }

    private boolean colide(int novoX, int novoY, int[][] novaForma) {
        for (int linha = 0; linha < novaForma.length; linha++) {
            for (int coluna = 0; coluna < novaForma[linha].length; coluna++) {
                if (novaForma[linha][coluna] > 0) {
                    int xX = novoX + coluna;
                    int yY = novoY + linha;

                    if (xX < 0 || xX >= COLUNAS || yY >= LINHAS) return true;
                    if (yY >= 0 && tabuleiro[yY][xX] > 0) return true;
                }
            }
        }
        return false;
    }

    private void verificarELimparLinhas() {
        for (int linha = LINHAS - 1; linha >= 0; linha--) {
            boolean linhaCompleta = true;
            for (int coluna = 0; coluna < COLUNAS; coluna++) {
                if (tabuleiro[linha][coluna] == 0) {
                    linhaCompleta = false;
                    break;
                }
            }
            if (linhaCompleta) {
                for (int l = linha; l > 0; l--) {
                    tabuleiro[l] = tabuleiro[l - 1].clone();
                }
                tabuleiro[0] = new int[COLUNAS];
                linha++; 
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (emMenu) return;
        
        // PAUSA: Se o jogo estiver pausado, o timer continua rodando, 
        // mas a peça para de cair automaticamente.
        if (pecaParada) return; 

        if (colide(peca.x, peca.y + 1, peca.forma)) {
            for (int linha = 0; linha < peca.forma.length; linha++) {
                for (int coluna = 0; coluna < peca.forma[linha].length; coluna++) {
                    if (peca.forma[linha][coluna] > 0) {
                        int x = peca.x + coluna;
                        int y = peca.y + linha;
                        if (y >= 0 && y < LINHAS && x >= 0 && x < COLUNAS) {
                            tabuleiro[y][x] = peca.idCor;
                        }
                    }
                }
            }
            
            verificarELimparLinhas();
            peca = new Tetromino();
            
            if (colide(peca.x, peca.y, peca.forma)) {
                timer.stop();
                if (clipeMusica != null && clipeMusica.isRunning()) {
                    clipeMusica.stop();
                }
                JOptionPane.showMessageDialog(this, "Fim de Jogo!");
                tabuleiro = new int[20][10];
                emMenu = true;
                if (clipeMusica != null) {
                    clipeMusica.setFramePosition(0);
                    clipeMusica.loop(Clip.LOOP_CONTINUOUSLY);
                    clipeMusica.start();
                }
                timer.start();
            }
        } else {
            peca.y++;
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (emMenu) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                emMenu = false;
                pecaParada = false; // Garante que o jogo inicia despausado
                repaint();
            }
            return; 
        }

        // CHECAGEM DA TECLA DE PAUSA (P)
        if (e.getKeyCode() == KeyEvent.VK_P) {
            pecaParada = !pecaParada; // Inverte o estado (se for true vira false, e vice-versa)
            
            // Opcional: Pausa ou despausa a música junto com o jogo
            if (clipeMusica != null) {
                if (pecaParada) {
                    clipeMusica.stop();
                } else {
                    clipeMusica.start();
                }
            }
            
            repaint();
            return; // Interrompe o método aqui para não mover a peça ao pausar
        }

        // PAUSA: Se o jogo estiver pausado, bloqueia os movimentos abaixo
        if (pecaParada) return; 

        // Controles normais do jogo
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (!colide(peca.x - 1, peca.y, peca.forma)) peca.x--;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (!colide(peca.x + 1, peca.y, peca.forma)) peca.x++;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            if (!colide(peca.x, peca.y + 1, peca.forma)) peca.y++;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            peca.rotacionar();
            if (colide(peca.x, peca.y, peca.forma)) {
                peca.desfazerRotacao();
            }
        }
        repaint();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}