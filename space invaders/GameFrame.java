import javax.swing.JFrame;

public class GameFrame extends JFrame {

    public GameFrame() {
        add(new GamePanel());

        setTitle("Space Invaders");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new GameFrame();
    }
}