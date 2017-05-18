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
    private GameControl gameControl = new GameControl(gameMap);

    /**
     * Constructor. Load the game from a specified lvl.
     *
     * @param parent parent panel of the GamePanel panel
     * @param level  starting level
     */
    public GamePanel(JPanel parent, int mode, int level) {
        this.parent = parent;
        createGamePanel();
        gameControl.loadLevel(level,mode);
    }

    /**
     * Create the GamePanel
     */
    private void createGamePanel() {
        setLayout(new BorderLayout());
        add(gameMap,BorderLayout.CENTER);
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
}
