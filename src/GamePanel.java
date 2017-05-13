import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controls the user input and game output.
 */
public class GamePanel extends javax.swing.JPanel {
    private JPanel parent;
    private GameMap gameMap = new GameMap();

    /**
     * Constructor. Load the game from a specified lvl.
     *
     * @param parent parent panel of the GamePanel panel
     * @param level  starting level
     */
    public GamePanel(JPanel parent, int level) {
        this.parent = parent;
        createGamePanel();
        loadGame(level);
    }

    /**
     * Constructor. Start the game from lvl 1.
     *
     * @param parent parent panel of the GamePanel panel
     */
    public GamePanel(JPanel parent) {
        this(parent, 1);
    }

    /**
     * Create the GamePanel
     */
    private void createGamePanel() {
        setLayout(new BorderLayout());
        add(gameMap,BorderLayout.CENTER);
        new GameControl(gameMap);
        JButton menuButton = new JButton("Menu");
        JButton ghostPlankButton = new JButton("Hide \"ghost\" plank.");
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.PAGE_AXIS));

        add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.add(menuButton);
        buttonPanel.add(ghostPlankButton);

        menuButton.setFocusable(false);
        ghostPlankButton.setFocusable(false);

        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.removeAll();
                parent.add(new MenuPanel(parent));
                parent.revalidate();
                parent.repaint();
            }
        });

        ghostPlankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(ghostPlankButton.getText().equals("Hide \"ghost\" plank.")) {
                    gameMap.hideGhostPlank();
                    ghostPlankButton.setText("Show \"ghost\" plank.");
                }
                else {
                    gameMap.showGhostPlank();
                    ghostPlankButton.setText("Hide \"ghost\" plank.");
                }
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
