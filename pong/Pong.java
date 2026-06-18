import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;

public class Pong extends JPanel implements ActionListener, KeyListener {

    Timer timer;

    int bolaX = 300;
    int bolaY = 200;
    int velX = 5;
    int velY = 5;

    int jogadorY = 150;
    int cpuY = 150;

    int pontosJogador = 0;
    int pontosCPU = 0;

    boolean fimDeJogo = false;
    String vencedor = "";

    // Estados do jogo: 0 = Menu Principal, 1 = Jogando
    int estadoJogo = 0; 

    // --- CLIPS SEPARADOS PARA AS MÚSICAS ---
    Clip musicaMenu;
    Clip musicaJogo;

    // Movimento suave
    boolean cima = false;
    boolean baixo = false;

    int velocidadeRaquete = 8;
    int velocidadeCPU = 7;

    public Pong() {
        setPreferredSize(new Dimension(800, 500));
        setBackground(Color.BLACK);

        addKeyListener(this);
        setFocusable(true);

        // Inicia a música do menu ao abrir o jogo
        iniciarMusicaMenu("sons/musica_menu.wav");

        timer = new Timer(16, this); 
        timer.start();
    }

    // --- MÉTODOS DA MÚSICA DO MENU ---
    private void iniciarMusicaMenu(String caminho) {
        try {
            File arquivo = new File(caminho);
            if (arquivo.exists()) {
                AudioInputStream audio = AudioSystem.getAudioInputStream(arquivo);
                musicaMenu = AudioSystem.getClip();
                musicaMenu.open(audio);
                musicaMenu.loop(Clip.LOOP_CONTINUOUSLY); // Loop infinito
                musicaMenu.start();
            } else {
                System.out.println("Música do menu não encontrada em: " + arquivo.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("ERRO AO CARREGAR MÚSICA DO MENU: " + e.getMessage());
        }
    }

    private void pararMusicaMenu() {
        if (musicaMenu != null && musicaMenu.isRunning()) {
            musicaMenu.stop();
            musicaMenu.close();
        }
    }

    // --- MÉTODOS DA MÚSICA DA PARTIDA ---
    private void iniciarMusicaJogo(String caminho) {
        try {
            File arquivo = new File(caminho);
            if (arquivo.exists()) {
                AudioInputStream audio = AudioSystem.getAudioInputStream(arquivo);
                musicaJogo = AudioSystem.getClip();
                musicaJogo.open(audio);
                musicaJogo.loop(Clip.LOOP_CONTINUOUSLY); // Loop infinito na partida
                musicaJogo.start();
            } else {
                System.out.println("Música do jogo não encontrada em: " + arquivo.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("ERRO AO CARREGAR MÚSICA DO JOGO: " + e.getMessage());
        }
    }

    private void pararMusicaJogo() {
        if (musicaJogo != null && musicaJogo.isRunning()) {
            musicaJogo.stop();
            musicaJogo.close();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (estadoJogo == 0) {
            desenharMenu(g);
            return; 
        }

        g.setColor(Color.WHITE);
        g.drawLine(400, 0, 400, 500);
        g.fillRect(20, jogadorY, 15, 100);
        g.fillRect(765, cpuY, 15, 100);
        g.fillOval(bolaX, bolaY, 20, 20);

        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString(String.valueOf(pontosJogador), 350, 50);
        g.drawString(String.valueOf(pontosCPU), 430, 50);

        if (fimDeJogo) {
            g.setFont(new Font("Arial", Font.BOLD, 50));
            FontMetrics fm = g.getFontMetrics();
            int largura = fm.stringWidth(vencedor);
            g.drawString(vencedor, (800 - largura) / 2, 220);

            g.setFont(new Font("Arial", Font.PLAIN, 25));
            String texto = "Pressione R para ir ao Menu";
            largura = g.getFontMetrics().stringWidth(texto);
            g.drawString(texto, (800 - largura) / 2, 280);
        }
    }

    private void desenharMenu(Graphics g) {
        g.setColor(Color.WHITE);
        
        g.setFont(new Font("Arial", Font.BOLD, 70));
        FontMetrics fmTitle = g.getFontMetrics();
        String titulo = "PONG";
        int larguraTitulo = fmTitle.stringWidth(titulo);
        g.drawString(titulo, (800 - larguraTitulo) / 2, 180);

        g.setFont(new Font("Arial", Font.PLAIN, 25));
        FontMetrics fmSub = g.getFontMetrics();
        String instrucao = "Pressione ESPAÇO para Iniciar";
        int larguraInstrucao = fmSub.stringWidth(instrucao);
        g.drawString(instrucao, (800 - larguraInstrucao) / 2, 300);

        g.setFont(new Font("Arial", Font.ITALIC, 18));
        g.setColor(Color.GRAY);
        String controles = "Controles: W (Cima) | S (Baixo)";
        int larguraControles = g.getFontMetrics().stringWidth(controles);
        g.drawString(controles, (800 - larguraControles) / 2, 400);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (estadoJogo == 0 || fimDeJogo) {
            repaint();
            return;
        }

        if (cima) jogadorY -= velocidadeRaquete;
        if (baixo) jogadorY += velocidadeRaquete;

        bolaX += velX;
        bolaY += velY;

        if (bolaY <= 0 || bolaY >= 480) {
            velY *= -1;
            tocarSom("sons/hit.wav");
        }

        Rectangle jogador = new Rectangle(20, jogadorY, 15, 100);
        Rectangle bola = new Rectangle(bolaX, bolaY, 20, 20);

        if (bola.intersects(jogador)) {
            tocarSom("sons/hit.wav");
            velX = Math.abs(velX) + 1;
            int centroRaquete = jogadorY + 50;
            int centroBola = bolaY + 10;
            velY = (centroBola - centroRaquete) / 4;
            if (velY == 0) velY = (Math.random() > 0.5) ? 2 : -2;
        }

        Rectangle cpu = new Rectangle(765, cpuY, 15, 100);

        if (bola.intersects(cpu)) {
            tocarSom("sons/hit.wav");
            velX = -(Math.abs(velX) + 1);
            int centroRaquete = cpuY + 50;
            int centroBola = bolaY + 10;
            velY = (centroBola - centroRaquete) / 4;
            if (velY == 0) velY = (Math.random() > 0.5) ? 2 : -2;
        }

        if (bolaX > 400) {
            int centroCPU = cpuY + 50;
            int centroBola = bolaY + 10;
            if (centroBola > centroCPU) cpuY += velocidadeCPU;
            if (centroBola < centroCPU) cpuY -= velocidadeCPU;
        }

        jogadorY = Math.max(0, Math.min(jogadorY, 400));
        cpuY = Math.max(0, Math.min(cpuY, 400));

        if (bolaX < 0) {
            tocarSom("sons/ponto.wav");
            pontosCPU++;
            resetarBola();
        }

        if (bolaX > 800) {
            tocarSom("sons/ponto.wav");
            pontosJogador++;
            resetarBola();
        }

        // FIM DE JOGO: Para a música da partida
        if (pontosJogador >= 10) {
            pararMusicaJogo();
            tocarSom("sons/vitoria.wav");
            vencedor = "VOCE VENCEU!";
            fimDeJogo = true;
            timer.stop();
        }

        if (pontosCPU >= 10) {
            pararMusicaJogo();
            tocarSom("sons/perdeu.wav");
            vencedor = "CPU VENCEU!";
            fimDeJogo = true;
            timer.stop();
        }

        repaint();
    }

    private void resetarBola() {
        bolaX = 400;
        bolaY = 250;
        velX = (velX > 0) ? -5 : 5;
        velY = (int) (Math.random() * 5) - 2;
        if (velY == 0) velY = 2;
    }

    private void reiniciarJogo() {
        pontosJogador = 0;
        pontosCPU = 0;
        jogadorY = 150;
        cpuY = 150;
        fimDeJogo = false;
        vencedor = "";
        bolaX = 400;
        bolaY = 250;
        velX = 5;
        velY = 5;

        estadoJogo = 0; 
        
        // Garante que para a música do jogo e reinicia a do menu
        pararMusicaJogo();
        iniciarMusicaMenu("sons/musica_menu.wav");
        
        timer.start();
        requestFocusInWindow();
    }

    private void tocarSom(String caminho) {
        try {
            File arquivo = new File(caminho);
            if (arquivo.exists()) {
                AudioInputStream audio = AudioSystem.getAudioInputStream(arquivo);
                Clip clip = AudioSystem.getClip();
                clip.open(audio);
                clip.start();
            }
        } catch (Exception e) {
            System.out.println("ERRO AO TOCAR EFEITO: " + e.getMessage());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (estadoJogo == 0 && e.getKeyCode() == KeyEvent.VK_SPACE) {
            pararMusicaMenu();           // Para a música do menu
            iniciarMusicaJogo("sons/musica_jogo.wav"); // Transiciona para a música da partida!
            estadoJogo = 1; 
        }

        if (e.getKeyCode() == KeyEvent.VK_W) cima = true;
        if (e.getKeyCode() == KeyEvent.VK_S) baixo = true;
        
        if (e.getKeyCode() == KeyEvent.VK_R && fimDeJogo) {
            reiniciarJogo();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) cima = false;
        if (e.getKeyCode() == KeyEvent.VK_S) baixo = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pong");
        Pong jogo = new Pong();

        frame.add(jogo);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}