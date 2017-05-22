import javax.swing.*;

/**
 * Created the main window of the River Crossing game
 */
public class MainWindow {
    private JFrame window;
    private JPanel mainPanel;

    /**
     * Constructor; creates the game window.
     */
    public MainWindow() {
        window = new JFrame("River Crossing: The Perilous Plank Puzzle");

        mainPanel = new JPanel();
        mainPanel.add(new MenuPanel(mainPanel));

        //finalize and show window
        window.setSize(800, 700);
        window.setContentPane(mainPanel);
        window.setResizable(false);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new MainWindow();
    }
}
