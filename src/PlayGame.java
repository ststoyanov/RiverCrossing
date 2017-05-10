import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controls the user input and game output.
 */
public class PlayGame extends javax.swing.JPanel {
    private JPanel parent;
    private JButton menuButton;
    private GameMap gameMap = new GameMap();

    /**
     * Constructor. Load the game from a specified lvl.
     *
     * @param parent parent panel of the PlayGame panel
     * @param level  starting level
     */
    public PlayGame(JPanel parent, int level) {
        this.parent = parent;
        createGamePanel();
        loadGame(level);
    }

    /**
     * Constructor. Start the game from lvl 1.
     *
     * @param parent
     */
    public PlayGame(JPanel parent) {
        this(parent, 1);
    }

    /**
     * Create the PlayGame JPanel.
     */
    private void createGamePanel() {
        add(gameMap);

        menuButton = new JButton("Menu");
        add(menuButton);
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.removeAll();
                parent.add(new MenuPanel(parent));
                parent.revalidate();
                parent.repaint();
            }
        });
    }

    /**
     * Load the game at a lvl.
     *
     * @param level lvl to be loaded
     */
    private void loadGame(int level) {
        gameMap.loadLevel(level);
    }
}