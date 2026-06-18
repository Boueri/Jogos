import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*; 

public class SnakeGame extends JPanel implements ActionListener, KeyListener {

    // Sprites da Cabeça
    private BufferedImage cabecaCima;
    private BufferedImage cabecaBaixo;
    private BufferedImage cabecaEsquerda;
    private BufferedImage cabecaDireita;
    
    // Sprites da Cauda
    private BufferedImage caudaCima;
    private BufferedImage caudaBaixo;
    private BufferedImage caudaEsquerda;
    private BufferedImage caudaDireita;

    // Sprites do Corpo (Retas e Curvas)
    private BufferedImage corpoHorizontal;
    private BufferedImage corpoVertical;
    private BufferedImage curvaSupEsq;
    private BufferedImage curvaSupDir;
    private BufferedImage curvaInfEsq;
    private BufferedImage curvaInfDir;

    // Outros Elementos e Imagens de Fundo
    private BufferedImage maca;
    private BufferedImage fundoMenu; 
    private BufferedImage fundoJogo; // Nova imagem para o fundo da grama durante o jogo

    // Controlador do Áudio
    private Clip musicaFundo;

    // Configurações do Tabuleiro
    private final int TAMANHO = 20;
    private final int LARGURA = 600;
    private final int ALTURA = 600;

    private ArrayList<Point> cobra;
    private Point comida;

    private char direcao = 'R';
    
    // Controle de Estados do Jogo
    private boolean noMenu = true;       
    private boolean jogoAtivo = false;   
    private int maiorPontuacao = 0;      

    private Timer timer;
    private Random random = new Random();

    public SnakeGame() {
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        try {
            // Carregando Cabeça
            cabecaCima = ImageIO.read(new File("sprites/cabeca_cima.png"));
            cabecaBaixo = ImageIO.read(new File("sprites/cabeca_baixo.png"));
            cabecaEsquerda = ImageIO.read(new File("sprites/cabeca_esquerda.png"));
            cabecaDireita = ImageIO.read(new File("sprites/cabeca_direita.png"));
            
            // Carregando Cauda
            caudaCima = ImageIO.read(new File("sprites/cauda_cima.png"));
            caudaBaixo = ImageIO.read(new File("sprites/cauda_baixo.png"));
            caudaEsquerda = ImageIO.read(new File("sprites/cauda_esquerda.png"));
            caudaDireita = ImageIO.read(new File("sprites/cauda_direita.png"));
            
            // Carregando Variantes do Corpo
            corpoHorizontal = ImageIO.read(new File("sprites/corpo_horizontal.png"));
            corpoVertical = ImageIO.read(new File("sprites/corpo_vertical.png"));
            curvaSupEsq = ImageIO.read(new File("sprites/curva_sup_esq.png"));
            curvaSupDir = ImageIO.read(new File("sprites/curva_sup_dir.png"));
            curvaInfEsq = ImageIO.read(new File("sprites/curva_inf_esq.png"));
            curvaInfDir = ImageIO.read(new File("sprites/curva_inf_dir.png"));

            // Carregando Imagens extras e Fundos
            maca = ImageIO.read(new File("sprites/maca.png"));
            fundoMenu = ImageIO.read(new File("sprites/fundo_menu.png")); 
            fundoJogo = ImageIO.read(new File("sprites/fundo_jogo.png")); // Carrega a imagem da grama
            
        } catch (IOException e) {
            System.err.println("Erro ao carregar as imagens! Verifique a pasta 'sprites/'.");
            e.printStackTrace();
        }

        // Inicializa o áudio se o arquivo existir
        tocarMusica("sprites/musica_fundo.wav");

        timer = new Timer(120, this);
        timer.start();
    }

    private void tocarMusica(String caminho) {
        try {
            File arquivoMusica = new File(caminho);
            if (arquivoMusica.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(arquivoMusica);
                musicaFundo = AudioSystem.getClip();
                musicaFundo.open(audioStream);
                musicaFundo.loop(Clip.LOOP_CONTINUOUSLY); 
                musicaFundo.start();
            }
        } catch (Exception e) {
            // Mantém o jogo rodando mesmo se a música falhar
        }
    }

    private void iniciarJogo() {
        cobra = new ArrayList<>();
        cobra.add(new Point(5, 5));
        cobra.add(new Point(4, 5));
        cobra.add(new Point(3, 5));
        direcao = 'R'; 
        gerarComida();
    }

    private void gerarComida() {
        boolean noCorpo;
        do {
            noCorpo = false;
            comida = new Point(
                    random.nextInt(LARGURA / TAMANHO),
                    random.nextInt(ALTURA / TAMANHO)
            );
            for (Point p : cobra) {
                if (comida.equals(p)) {
                    noCorpo = true;
                    break;
                }
            }
        } while (noCorpo);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. TELA DE MENU INICIAL
        if (noMenu) {
            if (fundoMenu != null) {
                g.drawImage(fundoMenu, 0, 0, LARGURA, ALTURA, null);
            }

            g.setColor(new Color(0, 0, 0, 100));
            g.fillRect(0, 0, LARGURA, ALTURA);

            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("SNAKE GAME", 140, 200);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 22));
            g.drawString("Maior Pontuação: " + maiorPontuacao, 190, 300);

            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Pressione ESPAÇO para começar", 145, 400);
            return;
        }

        // 2. TELA DE GAME OVER
        if (!jogoAtivo) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("GAME OVER", 180, 250);
            
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 22));
            g.drawString("Pontuação final: " + (cobra.size() - 3), 200, 310);
            
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Pressione ESPAÇO para voltar ao menu", 130, 390);
            return;
        }

        // 3. TELA DO JOGO EM ANDAMENTO
        // Desenha a imagem de grama texturizada ao fundo se ela existir
        if (fundoJogo != null) {
            g.drawImage(fundoJogo, 0, 0, LARGURA, ALTURA, null);
        } else {
            // Sistema reserva: se a imagem sumir, desenha o xadrez básico em cores
            for (int x = 0; x < LARGURA / TAMANHO; x++) {
                for (int y = 0; y < ALTURA / TAMANHO; y++) {
                    if ((x + y) % 2 == 0) g.setColor(new Color(14, 74, 19));
                    else g.setColor(new Color(19, 87, 25));
                    g.fillRect(x * TAMANHO, y * TAMANHO, TAMANHO, TAMANHO);
                }
            }
        }

        // Desenhar Maçã
        g.drawImage(maca, comida.x * TAMANHO, comida.y * TAMANHO, TAMANHO, TAMANHO, null);

        // Desenhar Cobra
        for (int i = 0; i < cobra.size(); i++) {
            Point p = cobra.get(i);

            // Cabeça
            if (i == 0) {
                BufferedImage cabecaAtual = cabecaDireita;
                switch (direcao) {
                    case 'U' -> cabecaAtual = cabecaCima;
                    case 'D' -> cabecaAtual = cabecaBaixo;
                    case 'L' -> cabecaAtual = cabecaEsquerda;
                    case 'R' -> cabecaAtual = cabecaDireita;
                }
                g.drawImage(cabecaAtual, p.x * TAMANHO, p.y * TAMANHO, TAMANHO, TAMANHO, null);
            } 
            // Cauda
            else if (i == cobra.size() - 1) {
                BufferedImage caudaAtual = caudaDireita;
                Point penultimo = cobra.get(i - 1); 

                if (p.x < penultimo.x) caudaAtual = caudaDireita;
                else if (p.x > penultimo.x) caudaAtual = caudaEsquerda;
                else if (p.y < penultimo.y) caudaAtual = caudaBaixo;
                else if (p.y > penultimo.y) caudaAtual = caudaCima;

                g.drawImage(caudaAtual, p.x * TAMANHO, p.y * TAMANHO, TAMANHO, TAMANHO, null);
            } 
            // Corpo Inteligente
            else {
                Point anterior = cobra.get(i - 1);  
                Point posterior = cobra.get(i + 1); 
                BufferedImage corpoAtual = corpoHorizontal; 

                if (anterior.x == posterior.x) {
                    corpoAtual = corpoVertical;
                } else if (anterior.y == posterior.y) {
                    corpoAtual = corpoHorizontal;
                } 
                else {
                    boolean temEsquerda = (anterior.x < p.x || posterior.x < p.x);
                    boolean temDireita  = (anterior.x > p.x || posterior.x > p.x);
                    boolean temCima     = (anterior.y < p.y || posterior.y < p.y);
                    boolean temBaixo    = (anterior.y > p.y || posterior.y > p.y);

                    if (temCima && temEsquerda)       corpoAtual = curvaSupEsq;
                    else if (temCima && temDireita)   corpoAtual = curvaSupDir;
                    else if (temBaixo && temEsquerda) corpoAtual = curvaInfEsq;
                    else if (temBaixo && temDireita)  corpoAtual = curvaInfDir;
                }

                g.drawImage(corpoAtual, p.x * TAMANHO, p.y * TAMANHO, TAMANHO, TAMANHO, null);
            }
        }

        // Desenhar Pontuação
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Pontos: " + (cobra.size() - 3), 10, 25);
    }

    private void finalizarJogo() {
        jogoAtivo = false;
        int pontuacaoAtual = cobra.size() - 3;
        if (pontuacaoAtual > maiorPontuacao) {
            maiorPontuacao = pontuacaoAtual; 
        }
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!jogoAtivo || noMenu) return;

        Point cabeca = new Point(cobra.get(0));

        switch (direcao) {
            case 'U' -> cabeca.y--;
            case 'D' -> cabeca.y++;
            case 'L' -> cabeca.x--;
            case 'R' -> cabeca.x++;
        }

        // Colisão com a Parede
        if (cabeca.x < 0 || cabeca.y < 0 ||
            cabeca.x >= LARGURA / TAMANHO ||
            cabeca.y >= ALTURA / TAMANHO) {
            finalizarJogo();
            return;
        }

        // Colisão com o próprio corpo
        boolean vaiComer = cabeca.equals(comida);
        int limiteValidacao = vaiComer ? cobra.size() : cobra.size() - 1;

        for (int i = 0; i < limiteValidacao; i++) {
            if (cabeca.equals(cobra.get(i))) {
                finalizarJogo();
                return;
            }
        }

        cobra.add(0, cabeca);

        if (vaiComer) {
            gerarComida();
        } else {
            cobra.remove(cobra.size() - 1);
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (noMenu) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                iniciarJogo();
                noMenu = false;
                jogoAtivo = true;
                repaint();
            }
            return;
        }

        if (!jogoAtivo) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                noMenu = true;
                repaint();
            }
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP    -> { if (direcao != 'D') direcao = 'U'; }
            case KeyEvent.VK_DOWN  -> { if (direcao != 'U') direcao = 'D'; }
            case KeyEvent.VK_LEFT  -> { if (direcao != 'R') direcao = 'L'; }
            case KeyEvent.VK_RIGHT -> { if (direcao != 'L') direcao = 'R'; }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame janela = new JFrame("Snake");
        SnakeGame jogo = new SnakeGame();

        janela.add(jogo);
        janela.pack();
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setResizable(false);
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
    }
}