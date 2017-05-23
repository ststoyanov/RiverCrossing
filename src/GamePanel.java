import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class handles the anything in the panel while the game is being played
 */
public class GamePanel extends JLayeredPane {
    private JPanel parent;
    private JPanel mainPanel;

    private GameMap gameMap = new GameMap();
    private GameControl gameControl = new GameControl(this, gameMap);

    // speed run fields
    private JLabel timerLabel = new JLabel();
    private long msTimer = 0;

    // icon of the currently played level
    private JLabel levelIcon = new JLabel();

    /**
     * Constructor. Load the game from a specified lvl.
     *
     * @param parent parent panel of the GamePanel panel
     * @param mode   the mode of the game
     * @param level  starting level
     */
    public GamePanel(JPanel parent, int mode, int level) {
        this.parent = parent;
        setOpaque(false);
        gameControl.loadGame(level, mode);
        createGamePanel();
        if (mode == GameControl.SPEED_RUN) {
            if (level == 4)
                setLevelIcon(1);
            else
                setLevelIcon(10 * level + 1);
        } else setLevelIcon(level);

    }


    /**
     * Create the GamePanel
     */
    private void createGamePanel() {
        setPreferredSize(new Dimension(MainWindow.WINDOW_WIDTH, MainWindow.WINDOW_HEIGHT));

        // create the mainGame panel and add it as the default layer
        mainPanel = new JPanel();
        add(mainPanel, DEFAULT_LAYER);
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new FlowLayout(0, 0, 0));
        mainPanel.setBounds(MainWindow.WINDOW_WIDTH / 2 - GameMap.NUMBER_OF_COLUMNS * GameMap.TILE_SIZE / 2, 0,
                MainWindow.WINDOW_WIDTH - GameMap.NUMBER_OF_COLUMNS * GameMap.TILE_SIZE / 2, MainWindow.WINDOW_HEIGHT);

        // create the panel on the side of the game
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));
        sidePanel.setOpaque(false);

        // add the gameMap as the main game and then the side panel
        mainPanel.add(gameMap);
        mainPanel.add(sidePanel);

        // set the style of the levelIcon
        levelIcon.setHorizontalTextPosition(JLabel.CENTER);
        levelIcon.setVerticalTextPosition(JLabel.CENTER);
        levelIcon.setFont(new Font("Wide Latin", Font.BOLD, 18));
        levelIcon.setForeground(Color.white);

        JGameButton menuButton = new JGameButton("Menu");
        JGameButton ghostPlankButton = new JGameButton("Hide \"ghost\" plank.");
        menuButton.setFocusable(false);
        ghostPlankButton.setFocusable(false);

        // add the content of the sidePanel and center it

        sidePanel.add(levelIcon);

        // if in speed run mode add a timer
        if (gameControl.getMode() == GameControl.SPEED_RUN) {
            sidePanel.add(timerLabel);
            timerLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
            timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        sidePanel.add(menuButton);
        sidePanel.add(ghostPlankButton);

        levelIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        ghostPlankButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // create action listeners for the buttons
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
    }

    /**
     * Update the level icon to represent a level
     *
     * @param level level for the icon
     */
    public void setLevelIcon(int level) {
        levelIcon.setIcon(lvlIcon[level / 10]);
        levelIcon.setText(level + "");
    }

    /**
     * Update the timer in a speed run
     *
     * @param elapsed time from start of the run
     */
    public void updateTimer(long elapsed) {
        msTimer = elapsed;
        timerLabel.setText(String.format("%02d:%02d:%02d", elapsed / 1000 / 60, elapsed / 1000 % 60, elapsed % 1000 / 10));
    }

    /**
     * Displays the Win message panel and controls, upon finishing a classic level or a speed run
     * <p>
     * If the mode is Classic display a congratulating message and 2 buttons - "Menu button" and "Next level" button
     * If the mode is Speed run display a congratulating message, 2 buttons - "Menu" and "Restart" - and the high scores
     *
     * @param mode  mode of the finished game
     * @param level level or difficulty of the finsihed game
     */
    public void displayWinMessage(int mode, int level) {
        // create a new panel for the win message and add it to the front layer
        JPanel winPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(winBG, 0, 0, null);
            }
        };;
        winPanel.setLayout(new BoxLayout(winPanel, BoxLayout.Y_AXIS));
        add(winPanel, new Integer(100));

        JLabel winMsg = new JLabel(); // congratulating message
        JPanel buttonPanel = new JPanel(); // buttons
        buttonPanel.setOpaque(false);

        // create the buttons
        JButton menuButton = new JButton("Menu");
        JButton lvlButton = new JButton("Next Level");
        lvlButton.setFocusable(true);
        lvlButton.requestFocusInWindow();

        // style
        winMsg.setForeground(Color.black);
        menuButton.setBackground(Color.orange);
        menuButton.setForeground(Color.black);
        lvlButton.setBackground(Color.orange);
        lvlButton.setForeground(Color.black);

        // add content to the panels
        winPanel.add(Box.createVerticalGlue());
        winPanel.add(winMsg);
        winPanel.add(buttonPanel);
        winPanel.add(Box.createVerticalGlue());

        winMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(menuButton);
        buttonPanel.add(lvlButton);

        // create the action listeners fro the buttons
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.removeAll();
                parent.add(new MenuPanel(parent));
                parent.revalidate();
                parent.repaint();
            }
        });

        // if the mode is classic display a congratulating message and 2 buttons
        // "Menu button" and "Next level" button
        if (mode == GameControl.CLASSIC_MODE) {
            winPanel.setBounds(MainWindow.WINDOW_WIDTH / 2 - 100, MainWindow.WINDOW_HEIGHT / 2 - 40,
                    200, 80);

            winMsg.setText("Congratulations, level " + level + " completed!");

            lvlButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    remove(winPanel);
                    gameControl.nextLevel();
                    revalidate();
                    repaint();
                }
            });
        }
        // if the mode is Speed run display a congratulating message, 2 buttons and the high scores
        else if (mode == GameControl.SPEED_RUN) {
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

            winMsg.setText(levelText + " completed in " + timerLabel.getText() + "!");
            JLabel hsText = new JLabel("High Scores:");
            hsText.setAlignmentX(Component.CENTER_ALIGNMENT);
            winPanel.add(hsText);

            HighScoresPanel hsp = new HighScoresPanel(this, new HighScoresControl(levelText), msTimer);
            add(hsp, new Integer(200));

            winPanel.setBounds(MainWindow.WINDOW_WIDTH / 2 - 100, MainWindow.WINDOW_HEIGHT / 2 - 180,
                    200, 80);
            hsp.setBounds(MainWindow.WINDOW_WIDTH / 2 - 100, MainWindow.WINDOW_HEIGHT / 2 - 100,
                    200, 300);

            lvlButton.setText("Restart Run");
            lvlButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    remove(winPanel);
                    remove(hsp);
                    gameControl.loadGame(level, GameControl.SPEED_RUN);
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

    private final Image winBG = new ImageIcon(getClass().getResource("winbg.jpg")).getImage();
}
