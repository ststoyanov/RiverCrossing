import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * Creates the Menu Panel of the game.
 */
public class MenuPanel extends JPanel {
    private JPanel parent;
    private JPanel buttonPanel;

    private JButton[] menuButtons = new JGameButton[]{
            new JGameButton("Play Classic"),
            new JGameButton("Speed Run"),
            new JGameButton("How to play"),
            new JGameButton("Exit")
    };

    /**
     * Constructor.
     *
     * @param parent parent panel of the MenuPanel
     */
    public MenuPanel(JPanel parent) {
        this.parent = parent;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        loadMenu();
    }

    /**
     * Load the menu GUI.
     */
    private void loadMenu() {
        //remove any content from parent panel and replace it with this one

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.PAGE_AXIS));
        buttonPanel.setOpaque(false);
        add(Box.createHorizontalGlue());
        add(buttonPanel);
        add(Box.createHorizontalGlue());
        for (JButton menuButton : menuButtons) buttonPanel.add(menuButton);

        menuButtons[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAll();
                loadLevels(GameControl.CLASSIC_MODE);
                revalidate();
                repaint();
            }
        });

        menuButtons[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAll();
                loadLevels(GameControl.SPEED_RUN);
                revalidate();
                repaint();
            }
        });

        menuButtons[3].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = (JFrame) SwingUtilities.getRoot(parent);
                frame.dispose();
            }
        });
    }

    /**
     * Load the available levels list and display it in the form of JButtons which load them
     */
    public void loadLevels(int mode) {
        if (mode == GameControl.CLASSIC_MODE) {
            JButton[] levelButton = new JButton[40];
            ActionListener levelListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    parent.removeAll();
                    parent.add(new GamePanel(parent, GameControl.CLASSIC_MODE, Arrays.asList(levelButton).indexOf(e.getSource()) + 1));
                    parent.revalidate();
                    parent.repaint();
                }
            };

            JPanel buttonPanel = new JPanel(new GridLayout(8, 5,0,0));
            buttonPanel.setOpaque(false);
            for (int i = 0; i < 40; i++) {
                levelButton[i] = new JButton(Integer.toString(i + 1));
                buttonPanel.add(levelButton[i]);
                levelButton[i].addActionListener(levelListener);
                levelButton[i].setFont(new Font("Wide Latin", Font.BOLD, 18));
                levelButton[i].setForeground(Color.white);
                levelButton[i].setIcon(lvlIcon[i/10]);
                levelButton[i].setRolloverIcon(lvlROIcon[i/10]);
                levelButton[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
                levelButton[i].setHorizontalTextPosition(JButton.CENTER);
                levelButton[i].setVerticalTextPosition(JButton.CENTER);
                levelButton[i].setOpaque(false);
                levelButton[i].setContentAreaFilled(false);
                levelButton[i].setBorderPainted(false);
                levelButton[i].setFocusable(false);
                levelButton[i].setPreferredSize(new Dimension(60, 90));
            }
            add(Box.createHorizontalGlue());
            add(buttonPanel);
        } else if (mode == GameControl.SPEED_RUN) {
            JButton[] diffButtons = new JButton[]{
                    new JButton("Easy Run (1-10)"),
                    new JButton("Normal Run (11-20)"),
                    new JButton("Intermediate Run (21-30)"),
                    new JButton("Expert Run (31-40)"),
                    new JButton("ULTIMATE Run")
            };
            JButton highScoresButton = new JButton("High Scores");

            ActionListener levelListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    parent.removeAll();
                    parent.add(new GamePanel(parent, GameControl.SPEED_RUN, Arrays.asList(diffButtons).indexOf(e.getSource())));
                    parent.revalidate();
                    parent.repaint();
                }
            };


            for (JButton diffButton : diffButtons) {
                add(diffButton);
                diffButton.addActionListener(levelListener);
            }

            diffButtons[0].setBackground(Color.green);
            diffButtons[1].setBackground(Color.orange);
            diffButtons[2].setBackground(Color.blue);
            diffButtons[3].setBackground(Color.red);
            diffButtons[4].setBackground(Color.white);
            add(highScoresButton);
        }

        JButton backButton = new JGameButton("Back");
        add(backButton);
        add(Box.createHorizontalGlue());
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAll();
                loadMenu();
                revalidate();
                repaint();
            }
        });
    }

    private final ImageIcon lvlIcon[] = {
            new ImageIcon(getClass().getResource("buttons/lvlbutton1.png")),
            new ImageIcon(getClass().getResource("buttons/lvlbutton2.png")),
            new ImageIcon(getClass().getResource("buttons/lvlbutton3.png")),
            new ImageIcon(getClass().getResource("buttons/lvlbutton4.png"))
    };

    private final ImageIcon lvlROIcon[] = {
            new ImageIcon(getClass().getResource("buttons/lvlbutton1r.png")),
            new ImageIcon(getClass().getResource("buttons/lvlbutton2r.png")),
            new ImageIcon(getClass().getResource("buttons/lvlbutton3r.png")),
            new ImageIcon(getClass().getResource("buttons/lvlbutton4r.png"))
    };

}

