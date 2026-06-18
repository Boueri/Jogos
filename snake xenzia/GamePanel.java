import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private final int TAM_BLOCO = 20; 
    private final int COLUNAS = 20;   
    private final int LINHAS = 20;    

    private ArrayList<Ponto> cobra;
    private Ponto comida;
    private char direcao = 'R'; 
    
    private Timer timer;
    private Random random = new Random();
    private int pontuacao = 0;

    private boolean emMenu = true;
    private boolean pausado = false;

    public GamePanel() {
        setPreferredSize(new Dimension(COLUNAS * TAM_BLOCO, LINHAS * TAM_BLOCO));
        
        // COR DE FUNDO: Tom verde-cinza nostálgico dos visores da Nokia
        setBackground(new Color(165, 180, 155)); 

        // Define a velocidade (120 milissegundos por atualização)
        timer = new Timer(120, this);
        timer.start();

        inicializarJogo();
    }

    private void inicializarJogo() {
        cobra = new ArrayList<>();
        cobra.add(new Ponto(10, 10)); // Cabeça
        cobra.add(new Ponto(9, 10));  // Corpo
        cobra.add(new Ponto(8, 10));  // Cauda
        
        direcao = 'R';
        pontuacao = 0;
        gerarComida();
    }

    private void gerarComida() {
        int x, y;
        boolean sobreCobra;
        
        do {
            x = random.nextInt(COLUNAS);
            y = random.nextInt(LINHAS);
            sobreCobra = false;
            for (Ponto p : cobra) {
                if (p.x == x && p.y == y) {
                    sobreCobra = true;
                    break;
                }
            }
        } while (sobreCobra);

        comida = new Ponto(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (emMenu) {
            desenharMenu(g);
            return;
        }

        // 1. Linhas de grade discretas do visor LCD do celular
        g.setColor(new Color(155, 170, 145));
        for (int x = 0; x <= COLUNAS; x++) g.drawLine(x * TAM_BLOCO, 0, x * TAM_BLOCO, LINHAS * TAM_BLOCO);
        for (int y = 0; y <= LINHAS; y++) g.drawLine(0, y * TAM_BLOCO, COLUNAS * TAM_BLOCO, y * TAM_BLOCO);

        // 2. Sprite da Comida (Preta com o miolo vazado)
        g.setColor(Color.BLACK);
        g.fillRect(comida.x * TAM_BLOCO + 2, comida.y * TAM_BLOCO + 2, TAM_BLOCO - 4, TAM_BLOCO - 4);
        g.setColor(new Color(165, 180, 155)); // Usa a própria cor de fundo para esvaziar o centro
        g.fillRect(comida.x * TAM_BLOCO + 5, comida.y * TAM_BLOCO + 5, TAM_BLOCO - 10, TAM_BLOCO - 10);

        // 3. SPRITE DA COBRA ANIMADA (Ondulação característica do Xenzia)
        for (int i = 0; i < cobra.size(); i++) {
            Ponto p = cobra.get(i);

            if (i == 0) {
                // Cabeça: Bloco preto regular com olho pixelado
                g.setColor(Color.BLACK);
                g.fillRect(p.x * TAM_BLOCO + 1, p.y * TAM_BLOCO + 1, TAM_BLOCO - 2, TAM_BLOCO - 2);
                g.setColor(new Color(165, 180, 155));
                g.fillRect(p.x * TAM_BLOCO + 4, p.y * TAM_BLOCO + 4, 3, 3);
            } else {
                // CORPO COM ANIMAÇÃO:
                // Alterna o tamanho de forma dinâmica baseado no índice e movimento
                int modificadorDeTamanho = (i % 2 == 0) ? 0 : 2; 
                
                // Desenha a base externa do bloco
                g.setColor(Color.BLACK);
                g.fillRect(
                    p.x * TAM_BLOCO + 1 + (modificadorDeTamanho / 2), 
                    p.y * TAM_BLOCO + 1 + (modificadorDeTamanho / 2), 
                    TAM_BLOCO - 2 - modificadorDeTamanho, 
                    TAM_BLOCO - 2 - modificadorDeTamanho
                );
                
                // Desenha o preenchimento interno para dar o efeito de contorno
                g.setColor(new Color(50, 60, 50)); 
                g.fillRect(
                    p.x * TAM_BLOCO + 3 + (modificadorDeTamanho / 2), 
                    p.y * TAM_BLOCO + 3 + (modificadorDeTamanho / 2), 
                    TAM_BLOCO - 6 - modificadorDeTamanho, 
                    TAM_BLOCO - 6 - modificadorDeTamanho
                );
            }
        }

        // 4. Pontuação na tela (Fonte monoespaçada pixelada)
        g.setColor(Color.BLACK);
        g.setFont(new Font("Courier New", Font.BOLD, 16)); 
        g.drawString("Score: " + pontuacao, 10, 25);

        // 5. Tela de Pause escura por cima
        if (pausado) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("PAUSADO", 130, 200);
        }
    }

    private void desenharMenu(Graphics g) {
        g.setColor(new Color(40, 50, 40));
        g.setFont(new Font("Courier New", Font.BOLD, 32));
        g.drawString("SNAKE XENZIA", 80, 150);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Courier New", Font.PLAIN, 14));
        g.drawString("Pressione ENTER para Jogar", 85, 250);

        g.setFont(new Font("Courier New", Font.ITALIC, 12));
        g.drawString("Controles: Setas | P para Pausar", 75, 350);
    }

    private void mover() {
        Ponto cabecaAtual = cobra.get(0);
        int novoX = cabecaAtual.x;
        int novoY = cabecaAtual.y;

        switch (direcao) {
            case 'U': novoY--; break;
            case 'D': novoY++; break;
            case 'L': novoX--; break;
            case 'R': novoX++; break;
        }

        // Verifica colisão com as extremidades da tela
        if (novoX < 0 || novoX >= COLUNAS || novoY < 0 || novoY >= LINHAS) {
            gameOver();
            return;
        }

        // Verifica colisão contra o próprio corpo
        for (int i = 0; i < cobra.size() - 1; i++) {
            if (cobra.get(i).x == novoX && cobra.get(i).y == novoY) {
                gameOver();
                return;
            }
        }

        Ponto novaCabeca = new Ponto(novoX, novoY);
        cobra.add(0, novaCabeca);

        // Verifica se alcançou o bloco da comida
        if (novoX == comida.x && novoY == comida.y) {
            pontuacao += 10;
            gerarComida(); 
        } else {
            cobra.remove(cobra.size() - 1);
        }
    }

    private void gameOver() {
        timer.stop();
        JOptionPane.showMessageDialog(this, "Game Over! Pontuação: " + pontuacao);
        inicializarJogo();
        emMenu = true;
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!emMenu && !pausado) {
            mover();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int codigo = e.getKeyCode();

        if (emMenu) {
            if (codigo == KeyEvent.VK_ENTER) {
                emMenu = false;
                pausado = false;
                repaint();
            }
            return;
        }

        if (codigo == KeyEvent.VK_P) {
            pausado = !pausado;
            repaint();
            return;
        }

        if (pausado) return;

        // Limita a direção para evitar curvas impossíveis de 180 graus imediatas
        if (codigo == KeyEvent.VK_LEFT && direcao != 'R') {
            direcao = 'L';
        } else if (codigo == KeyEvent.VK_RIGHT && direcao != 'L') {
            direcao = 'R';
        } else if (codigo == KeyEvent.VK_UP && direcao != 'D') {
            direcao = 'U';
        } else if (codigo == KeyEvent.VK_DOWN && direcao != 'U') {
            direcao = 'D';
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}