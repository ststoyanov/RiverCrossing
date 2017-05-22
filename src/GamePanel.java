import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controls the user input and game output.
 */
public class GamePanel extends JLayeredPane {
    private JPanel parent;
    private JPanel mainPanel = new JPanel();
    private GameMap gameMap = new GameMap();
    private GameControl gameControl = new GameControl(this, gameMap);
    private JLabel timerLabel = new JLabel();
    private long msTimer = 0;

    /**
     * Constructor. Load the game from a specified lvl.
     *
     * @param parent parent panel of the GamePanel panel
     * @param level  starting level
     */
    public GamePanel(JPanel parent, int mode, int level) {
        this.parent = parent;
        createGamePanel();
        gameControl.loadLevel(level, mode);
        if (mode == GameControl.SPEED_RUN) {
            mainPanel.add(timerLabel, BorderLayout.NORTH);
        }
    }

    public void updateTimer(long elapsed) {
        msTimer = elapsed;
        timerLabel.setText(String.format("%02d:%02d:%02d", elapsed / 1000 / 60, elapsed / 1000 % 60, elapsed % 1000 / 10));
    }

    /**
     * Create the GamePanel
     */
    private void createGamePanel() {
        setPreferredSize(new Dimension(800, 700));

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(gameMap, BorderLayout.CENTER);
        JButton menuButton = new JButton("Menu");
        JButton ghostPlankButton = new JButton("Hide \"ghost\" plank.");
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));

        mainPanel.add(buttonPanel, BorderLayout.EAST);
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
                if (ghostPlankButton.getText().equals("Hide \"ghost\" plank.")) {
                    gameMap.hideGhostPlank();
                    ghostPlankButton.setText("Show \"ghost\" plank.");
                } else {
                    gameMap.showGhostPlank();
                    ghostPlankButton.setText("Hide \"ghost\" plank.");
                }
            }
        });
        mainPanel.setBounds(100, 0, 600, 700);
        add(mainPanel, DEFAULT_LAYER);


    }

    /**
     * Displays the Win message panel and controls, upon finishing a level
     */
    public void displayWinMessage(int mode, int level) {
        JPanel winPanel = new JPanel();

        JLabel winMsg = new JLabel();

        JButton menuButton = new JButton("Menu");
        JButton nextlvlButton = new JButton("Next Level");
        winPanel.setBounds(GameMap.TILE_SIZE * GameMap.NUMBER_OF_COLUMNS / 2, GameMap.TILE_SIZE * GameMap.NUMBER_OF_ROWS / 2 + -40, 200, 80);

        winPanel.add(winMsg);
        winPanel.add(menuButton);
        winPanel.add(nextlvlButton);
        nextlvlButton.setFocusable(true);
        nextlvlButton.requestFocusInWindow();
        add(winPanel, new Integer(100));

        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.removeAll();
                parent.add(new MenuPanel(parent));
                parent.revalidate();
                parent.repaint();
            }
        });

        if (mode == GameControl.CLASSIC_MODE) {
            winMsg.setText("Congratulations level " + level + " completed!");
            nextlvlButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    remove(winPanel);
                    gameMap.loadLevel(level + 1);
                    revalidate();
                    repaint();
                }
            });
        } else if (mode == GameControl.SPEED_RUN) {
            String levelText = "";

            switch (level) {
                case 0:
                    levelText = "EasyRun";
                    break;
                case 1:
                    levelText = "NormalRun";
                    break;
                case 2:
                    levelText = "IntermediateRun";
                    break;
                case 3:
                    levelText = "ExpertRun";
                    break;
                case 4:
                    levelText = "ULTIMATERun";
                    break;
            }

            HighScoresPanel hsp = new HighScoresPanel(this, new HighScoresControl(levelText), msTimer);
            hsp.setBounds(GameMap.TILE_SIZE * GameMap.NUMBER_OF_COLUMNS / 2, GameMap.TILE_SIZE * GameMap.NUMBER_OF_ROWS / 2 - 200, 200, 500);
            add(hsp, new Integer(200));

            nextlvlButton.setText("Restart Run");
            nextlvlButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    remove(winPanel);
                    remove(hsp);
                    gameControl.loadLevel(level, GameControl.SPEED_RUN);
                    revalidate();
                    repaint();
                }
            });

        }
    }
}
