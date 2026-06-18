import javax.swing.*;

public class SnakeGame {
    public static void main(String[] args) {
        JFrame janela = new JFrame("Snake Xenzia");
        GamePanel painel = new GamePanel();

        janela.add(painel);
        janela.pack();
        janela.setLocationRelativeTo(null);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setResizable(false);
        
        janela.addKeyListener(painel);
        janela.setVisible(true);
    }
}