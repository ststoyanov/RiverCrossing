import javax.swing.*;
import java.awt.*;

/**
 * Created the main window of the River Crossing game
 */
public class MainWindow {
    public static final int WINDOW_WIDTH = 900;
    public static final int WINDOW_HEIGHT = 650;

    private JFrame window;
    private JPanel mainPanel;

    /**
     * Constructor; creates the game window.
     */
    public MainWindow() {
        window = new JFrame("River Crossing: The Perilous Plank Puzzle");

        mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgIMG, 0, 0, null);
            }
        };

        mainPanel.add(new MenuPanel(mainPanel), BorderLayout.CENTER);

        window.setIconImage(new ImageIcon(getClass().getResource("icon.png")).getImage());

        //finalize and show window
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setLocationRelativeTo(null);
        window.setContentPane(mainPanel);
        window.setResizable(false);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Start the game.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        new MainWindow();
    }

    // background image
    private final Image bgIMG = new ImageIcon(getClass().getResource("Background.jpg")).getImage();
}
