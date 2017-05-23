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
    private JLabel levelIcon = new JLabel();
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
        setOpaque(false);
        createGamePanel();

        gameControl.loadLevel(level, mode);
        if (mode == GameControl.SPEED_RUN) {
            mainPanel.add(timerLabel, BorderLayout.NORTH);
            if (level == 4)
                setLevelIcon(1);
            else
                setLevelIcon(10 * level + 1);
        } else setLevelIcon(level);
    }

    public void updateTimer(long elapsed) {
        msTimer = elapsed;
        timerLabel.setText(String.format("%02d:%02d:%02d", elapsed / 1000 / 60, elapsed / 1000 % 60, elapsed % 1000 / 10));
    }

    /**
     * Create the GamePanel
     */
    private void createGamePanel() {
        setPreferredSize(new Dimension(MainWindow.WINDOW_WIDTH, MainWindow.WINDOW_HEIGHT));
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new FlowLayout(0, 0, 0));
        mainPanel.add(gameMap);


        levelIcon.setHorizontalTextPosition(JLabel.CENTER);
        levelIcon.setVerticalTextPosition(JLabel.CENTER);
        levelIcon.setFont(new Font("Wide Latin", Font.BOLD, 18));
        levelIcon.setForeground(Color.white);

        JGameButton menuButton = new JGameButton("Menu");
        JGameButton ghostPlankButton = new JGameButton("Hide \"ghost\" plank.");

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));
        sidePanel.setOpaque(false);

        mainPanel.add(sidePanel);
        sidePanel.add(levelIcon);
        sidePanel.add(menuButton);
        sidePanel.add(ghostPlankButton);

        levelIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        ghostPlankButton.setAlignmentX(Component.CENTER_ALIGNMENT);

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

        mainPanel.setBounds(MainWindow.WINDOW_WIDTH / 2 - GameMap.NUMBER_OF_COLUMNS * GameMap.TILE_SIZE / 2, 0, MainWindow.WINDOW_WIDTH - GameMap.NUMBER_OF_COLUMNS * GameMap.TILE_SIZE / 2, MainWindow.WINDOW_HEIGHT);
        add(mainPanel, DEFAULT_LAYER);
    }

    public void setLevelIcon(int level) {
        levelIcon.setIcon(lvlIcon[level / 10]);
        levelIcon.setText(level + "");
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
                    gameControl.nextLevel();
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

    // load resources
    private final ImageIcon lvlIcon[] = {
            new ImageIcon(getClass().getResource("buttons/lvlbutton1.png")),
            new ImageIcon(getClass().getResource("buttons/lvlbutton2.png")),
            new ImageIcon(getClass().getResource("buttons/lvlbutton3.png")),
            new ImageIcon(getClass().getResource("buttons/lvlbutton4.png"))
    };
}
