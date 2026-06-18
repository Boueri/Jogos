import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {

    javax.swing.Timer timer = new javax.swing.Timer(16, this);
    Random rand = new Random();

    String state = "MENU";
    String dificuldadeSelecionada = "NORMAL";

    int playerX = 220;
    int lives = 3;
    int score = 0;
    int wave = 1;
    int highScore = 0;

    boolean shield = false;
    boolean rapidFire = false;

    ArrayList<Rectangle> bullets = new ArrayList<>();
    ArrayList<Rectangle> enemyBullets = new ArrayList<>();
    
    // CLASSE DO ALIEN NORMAL
    class Alien {
        Rectangle rect;
        Color cor;
        int tipoSprite;

        Alien(int x, int y, Color cor, int tipoSprite) {
            this.rect = new Rectangle(x, y, 40, 30);
            this.cor = cor;
            this.tipoSprite = tipoSprite;
        }
    }
    
    // CLASSE PARA O CHEFE (BOSS)
    class Boss {
        Rectangle rect;
        int hp;
        int maxHp;
        int dir = 1;
        int speed = 3;

        Boss(int x, int y, int hp) {
            this.rect = new Rectangle(x, y, 120, 60);
            this.hp = hp;
            this.maxHp = hp;
        }
    }

    // CLASSE PARA OS ITENS (POWER-UPS)
    class PowerUp {
        Rectangle rect;
        String tipo; // "MELHORIA" ou "VIDA"

        PowerUp(int x, int y, String tipo) {
            this.rect = new Rectangle(x, y, 22, 22);
            this.tipo = tipo;
        }
    }

    ArrayList<Alien> aliens = new ArrayList<>();
    Boss boss = null; 
    ArrayList<PowerUp> powerUps = new ArrayList<>(); // Lista para suportar múltiplos itens no ecrã

    int alienDir = 1;
    int alienSpeed = 2;

    // SPRITE DA NAVE DO JOGADOR (Matriz 10x25)
    int[][] spriteNave = {
        {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0},
        {0,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,0},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,1,1},
        {1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1}
    };

    // SPRITES DOS ALIENS (8x12)
    int[][] spriteAlienCima = {
        {0,0,0,0,1,1,1,1,0,0,0,0}, {0,1,1,1,1,1,1,1,1,1,1,0}, {1,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,0,0,1,1,0,0,1,1,1}, {1,1,1,1,1,1,1,1,1,1,1,1}, {0,0,0,1,1,0,0,1,1,0,0,0},
        {0,0,1,1,0,1,1,0,1,1,0,0}, {1,1,0,0,0,0,0,0,0,0,1,1}
    };

    int[][] spriteAlienMeio = {
        {0,0,1,0,0,0,0,0,0,1,0,0}, {0,0,0,1,0,0,0,0,1,0,0,0}, {0,0,1,1,1,1,1,1,1,1,0,0},
        {0,1,1,0,1,1,1,1,0,1,1,0}, {1,1,1,1,1,1,1,1,1,1,1,1}, {1,0,1,1,1,1,1,1,1,1,0,1},
        {1,0,1,0,0,0,0,0,0,1,0,1}, {0,0,0,1,1,0,0,1,1,0,0,0}
    };

    int[][] spriteAlienBaixo = {
        {0,0,0,1,1,1,1,1,1,0,0,0}, {0,1,1,1,1,1,1,1,1,1,1,0}, {1,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,0,0,1,1,0,0,1,1,1}, {1,1,1,1,1,1,1,1,1,1,1,1}, {0,0,1,1,1,0,0,1,1,1,0,0},
        {0,1,1,0,0,1,1,0,0,1,1,0}, {0,0,1,1,0,0,0,0,1,1,0,0}
    };

    // SPRITE DO CHEFE (10x20)
    int[][] spriteBoss = {
        {0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0},
        {0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0},
        {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
        {1,1,1,0,0,0,1,1,0,0,0,0,1,1,0,0,0,1,1,1},
        {1,1,1,0,0,0,1,1,0,0,0,0,1,1,0,0,0,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {0,1,1,1,1,1,0,0,1,1,1,1,0,0,1,1,1,1,1,0},
        {0,0,1,1,0,0,0,0,0,1,1,0,0,0,0,0,1,1,0,0},
        {0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0},
        {1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1}
    };

    class Explosion {
        int x, y, life = 20;
        Explosion(int x, int y) { this.x = x; this.y = y; }
    }

    ArrayList<Explosion> explosions = new ArrayList<>();

    public SpaceInvaders() {
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        loadHighScore();
        timer.start();
    }

    void startGame() {
        bullets.clear();
        enemyBullets.clear();
        aliens.clear();
        explosions.clear();
        powerUps.clear();
        boss = null;

        playerX = 220;
        lives = 3;
        score = 0;
        wave = 1;
        alienDir = 1;

        if (dificuldadeSelecionada.equals("LENTO")) alienSpeed = 2; 
        else alienSpeed = 4; 

        shield = false;
        rapidFire = false;

        spawnWave();
        state = "PLAY";
    }

    void spawnWave() {
        aliens.clear();
        boss = null;

        if (wave == 4) {
            boss = new Boss(190, 60, 30); 
            return;
        }

        int rows = Math.min(3 + wave, 6); 
        for (int y = 0; y < rows; y++) {
            Color corDaLinha;
            int tipoSprite;
            switch (y % 5) {
                case 0: corDaLinha = Color.RED; tipoSprite = 0; break;     
                case 1: corDaLinha = Color.YELLOW; tipoSprite = 1; break;  
                case 2: corDaLinha = Color.PINK; tipoSprite = 1; break;    
                case 3: corDaLinha = Color.CYAN; tipoSprite = 2; break;    
                default: corDaLinha = Color.GREEN; tipoSprite = 2; break;  
            }
            for (int x = 0; x < 6; x++) {
                aliens.add(new Alien(60 + x * 60, 60 + y * 50, corDaLinha, tipoSprite));
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (state.equals("MENU")) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString("SPACE INVADERS", 110, 180);

            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("Velocidade Inicial (Use ◄ / ►):", 130, 240);
            
            if (dificuldadeSelecionada.equals("LENTO")) {
                g.setColor(Color.GREEN); g.drawString("◄ [ LENTO ]", 150, 280);
                g.setColor(Color.GRAY); g.drawString("  NORMAL  ►", 250, 280);
            } else {
                g.setColor(Color.GRAY); g.drawString("◄   LENTO  ", 150, 280);
                g.setColor(Color.GREEN); g.drawString("  [ NORMAL ] ►", 240, 280);
            }

            g.setColor(Color.WHITE);
            g.drawString("Pressione ENTER para jogar", 145, 350);
            g.drawString("High Score: " + highScore, 180, 400);
            return;
        }

        if (state.equals("GAMEOVER")) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GAME OVER", 140, 220);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Score Final: " + score, 175, 270);
            g.drawString("Pressione R para voltar ao Menu", 115, 320);
            return;
        }

        // DESENHO DA NAVE (BRANCA / CIANO COM ESCUDO)
        int pSizeW = 50 / spriteNave[0].length; 
        int pSizeH = 20 / spriteNave.length;    
        for (int row = 0; row < spriteNave.length; row++) {
            for (int col = 0; col < spriteNave[row].length; col++) {
                if (spriteNave[row][col] == 1) {
                    g.setColor(shield ? Color.CYAN : Color.WHITE);
                    g.fillRect(playerX + (col * pSizeW), 500 + (row * pSizeH), pSizeW, pSizeH);
                }
            }
        }

        if (shield) {
            g.setColor(new Color(0, 255, 255, 100));
            g.drawOval(playerX - 5, 495, 60, 30);
        }

        // TIROS
        g.setColor(Color.YELLOW);
        for (Rectangle b : bullets) g.fillRect(b.x, b.y, 5, 10);

        g.setColor(Color.RED);
        for (Rectangle b : enemyBullets) g.fillRect(b.x, b.y, 5, 10);

        // DESENHO DO BOSS
        if (boss != null) {
            int bPixelW = boss.rect.width / spriteBoss[0].length; 
            int bPixelH = boss.rect.height / spriteBoss.length;  

            for (int row = 0; row < spriteBoss.length; row++) {
                for (int col = 0; col < spriteBoss[row].length; col++) {
                    if (spriteBoss[row][col] == 1) {
                        g.setColor(boss.hp < 10 ? Color.RED : Color.ORANGE);
                        g.fillRect(boss.rect.x + (col * bPixelW), boss.rect.y + (row * bPixelH), bPixelW, bPixelH);
                    }
                }
            }

            // BARRA DE VIDA DO BOSS
            g.setColor(Color.GRAY);
            g.fillRect(150, 15, 200, 12);
            g.setColor(Color.RED);
            int larguraBarra = (int) (((double) boss.hp / boss.maxHp) * 200);
            g.fillRect(150, 15, larguraBarra, 12);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.drawString("CHEFE ALIEN", 215, 25);
        }

        // DESENHO DOS ALIENS COM SUAS SPRITES
        for (Alien a : aliens) {
            g.setColor(a.cor);
            int[][] matrizAtual = (a.tipoSprite == 0) ? spriteAlienCima : (a.tipoSprite == 1) ? spriteAlienMeio : spriteAlienBaixo;
            int alienPixelW = 40 / matrizAtual[0].length;
            int alienPixelH = 30 / matrizAtual.length;

            for (int row = 0; row < matrizAtual.length; row++) {
                for (int col = 0; col < matrizAtual[row].length; col++) {
                    if (matrizAtual[row][col] == 1) {
                        g.fillRect(a.rect.x + (col * alienPixelW), a.rect.y + (row * alienPixelH), alienPixelW, alienPixelH);
                    }
                }
            }
        }

        // --- RENDERIZAÇÃO VISUAL DOS ITENS (POWER-UPS) ---
        for (PowerUp p : powerUps) {
            if (p.tipo.equals("MELHORIA")) {
                g.setColor(Color.ORANGE);
                g.fillRect(p.rect.x, p.rect.y, p.rect.width, p.rect.height);
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString("M", p.rect.x + 6, p.rect.y + 16); // Letra M para Melhoria
            } else if (p.tipo.equals("VIDA")) {
                g.setColor(Color.GREEN);
                g.fillRect(p.rect.x, p.rect.y, p.rect.width, p.rect.height);
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString("V", p.rect.x + 6, p.rect.y + 16); // Letra V para Vida
            }
        }

        g.setColor(Color.WHITE);
        for (Explosion ex : explosions) g.drawOval(ex.x, ex.y, 20, 20);

        // HUD
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Lives: " + lives, 10, 40);
        g.drawString("Wave: " + (wave == 4 ? "BOSS WAVE" : wave), 10, 60);
        g.drawString("Velocidade: " + alienSpeed, 10, 80);
        g.drawString("[P] Pausar", 420, 20);

        if (state.equals("PAUSE")) {
            g.setColor(new Color(0, 0, 0, 150)); g.fillRect(0, 0, 500, 600);
            g.setColor(Color.YELLOW); g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("JOGO PAUSADO", 115, 260);
            g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("Pressione P para Continuar", 150, 310);
            g.drawString("Pressione ESC para sair para o Menu", 120, 340);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!state.equals("PLAY")) return;

        // CONTROLES DO BOSS
        if (boss != null) {
            boss.rect.x += boss.speed * boss.dir;
            if (boss.rect.x <= 10 || boss.rect.x >= 370) {
                boss.dir *= -1;
            }
            if (rand.nextInt(100) < 4) {
                enemyBullets.add(new Rectangle(boss.rect.x + 20, boss.rect.y + 55, 5, 12));
                enemyBullets.add(new Rectangle(boss.rect.x + 60, boss.rect.y + 55, 5, 12));
                enemyBullets.add(new Rectangle(boss.rect.x + 100, boss.rect.y + 55, 5, 12));
            }
        }

        // MOVIMENTO DOS INIMIGOS COMUNS
        boolean changeDir = false;
        for (int i = 0; i < aliens.size(); i++) {
            Alien a = aliens.get(i);
            a.rect.x += alienSpeed * alienDir;
            if (rand.nextInt(1000) < 3) enemyBullets.add(new Rectangle(a.rect.x + 20, a.rect.y + 20, 5, 10));
            if (a.rect.x <= 0 || a.rect.x >= 450) changeDir = true;
            if (a.rect.y > 450) { gameOver(); return; }
        }

        if (changeDir) {
            alienDir *= -1;
            for (Alien a : aliens) a.rect.y += 20;
        }

        move(bullets, -10);
        move(enemyBullets, 6);

        Rectangle player = new Rectangle(playerX, 500, 50, 20);

        // COLISÃO: DISPAROS INIMIGOS NO JOGADOR
        for (int i = enemyBullets.size() - 1; i >= 0; i--) {
            if (enemyBullets.get(i).intersects(player)) {
                enemyBullets.remove(i);
                if (!shield) {
                    lives--;
                    if (lives <= 0) { gameOver(); return; }
                } else shield = false;
            }
        }

        // COLISÃO: JOGADOR NOS INIMIGOS
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Rectangle b = bullets.get(i);

            if (boss != null && b.intersects(boss.rect)) {
                bullets.remove(i);
                boss.hp--;
                explosions.add(new Explosion(b.x - 8, b.y - 5));

                if (boss.hp <= 0) {
                    for(int k=0; k<5; k++) {
                        explosions.add(new Explosion(boss.rect.x + rand.nextInt(100), boss.rect.y + rand.nextInt(40)));
                    }
                    score += 500; 
                    boss = null;
                    wave++;
                    
                    if (alienSpeed > 1) alienSpeed--;
                    spawnWave();
                }
                break;
            }

            for (int j = aliens.size() - 1; j >= 0; j--) {
                if (b.intersects(aliens.get(j).rect)) {
                    explosions.add(new Explosion(aliens.get(j).rect.x, aliens.get(j).rect.y));
                    aliens.remove(j);
                    bullets.remove(i);
                    score += 10;
                    break; 
                }
            }
        }

        // --- SISTEMA ALEATÓRIO DE SPAWN DE ITENS ---
        // Sorteia uma probabilidade. Se passar, escolhe aleatoriamente entre lançar Melhoria ou Vida
        if (rand.nextInt(1000) < 3) { 
            String tipoSorteado = (rand.nextBoolean()) ? "MELHORIA" : "VIDA";
            powerUps.add(new PowerUp(rand.nextInt(440), 0, tipoSorteado));
        }

        // MOVIMENTO E COLISÃO DOS ITENS COM O JOGADOR
        for (int i = powerUps.size() - 1; i >= 0; i--) {
            PowerUp p = powerUps.get(i);
            p.rect.y += 3; // Velocidade de queda do item

            if (p.rect.intersects(player)) {
                if (p.tipo.equals("MELHORIA")) {
                    rapidFire = true;
                    shield = true;
                } else if (p.tipo.equals("VIDA")) {
                    if (lives < 5) { // Limite máximo para equilíbrio do gameplay
                        lives++;
                    }
                }
                powerUps.remove(i);
            } else if (p.rect.y > 600) {
                powerUps.remove(i); // Remove se sair do ecrã
            }
        }

        for (int i = explosions.size() - 1; i >= 0; i--) {
            explosions.get(i).life--;
            if (explosions.get(i).life <= 0) explosions.remove(i);
        }

        // FIM DA WAVE
        if (aliens.isEmpty() && boss == null) {
            wave++;
            if (alienSpeed > 1) {
                alienSpeed--; 
            }
            spawnWave();
        }

        repaint();
    }

    void move(ArrayList<Rectangle> list, int speed) {
        for (int i = list.size() - 1; i >= 0; i--) {
            Rectangle b = list.get(i); b.y += speed;
            if (b.y < 0 || b.y > 600) list.remove(i);
        }
    }

    void gameOver() {
        state = "GAMEOVER"; repaint(); 
        if (score > highScore) {
            highScore = score;
            try {
                FileWriter w = new FileWriter("score.txt"); w.write(String.valueOf(score)); w.close();
            } catch (Exception ignored) {}
        }
    }

    void loadHighScore() {
        try {
            File f = new File("score.txt");
            if (f.exists()) {
                Scanner s = new Scanner(f);
                if (s.hasNextInt()) highScore = s.nextInt();
                s.close();
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (state.equals("MENU")) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) startGame();
            if (e.getKeyCode() == KeyEvent.VK_LEFT) { dificuldadeSelecionada = "LENTO"; repaint(); }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) { dificuldadeSelecionada = "NORMAL"; repaint(); }
            return;
        }

        if (state.equals("GAMEOVER") && e.getKeyCode() == KeyEvent.VK_R) { state = "MENU"; repaint(); return; }

        if (state.equals("PAUSE")) {
            if (e.getKeyCode() == KeyEvent.VK_P) { state = "PLAY"; repaint(); }
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { state = "MENU"; repaint(); }
            return;
        }

        if (state.equals("PLAY")) {
            if (e.getKeyCode() == KeyEvent.VK_P) { state = "PAUSE"; repaint(); return; }
            if (e.getKeyCode() == KeyEvent.VK_LEFT && playerX > 0) playerX -= 15;
            if (e.getKeyCode() == KeyEvent.VK_RIGHT && playerX < 435) playerX += 15;

            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (bullets.size() < (rapidFire ? 6 : 2)) {
                    bullets.add(new Rectangle(playerX + 22, 480, 5, 10));
                    if (rapidFire) {
                        bullets.add(new Rectangle(playerX + 5, 480, 5, 10));
                        bullets.add(new Rectangle(playerX + 40, 480, 5, 10));
                    }
                }
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame f = new JFrame("Space Invaders ULTIMATE BOSS EDITION");
        SpaceInvaders game = new SpaceInvaders();
        f.add(game); f.setSize(500, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(false); f.setLocationRelativeTo(null); f.setVisible(true);
    }
}