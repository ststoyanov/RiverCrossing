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
        // add the logo at the top of the panel
        parent.add(new JLabel(logo), BorderLayout.NORTH);

        //create the button panel add the buttons to it and then add it in the middle of the MenuPanel
        JPanel buttonPanel = new JPanel();

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
        buttonPanel.setOpaque(false);

        for (JButton menuButton : menuButtons) buttonPanel.add(menuButton);

        add(Box.createHorizontalGlue());
        add(buttonPanel);
        add(Box.createHorizontalGlue());


        // create the action listeners for the menu buttons
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

        menuButtons[2].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAll();
                loadHelpMenu();
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
    private void loadLevels(int mode) {
        // create the back button
        JButton backButton = new JGameButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAll();
                loadMenu();
                revalidate();
                repaint();
            }
        });

        // if the mode is classic load a list of 40 levels
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

            // create a button panel - a 8x5 grid of level buttons
            JPanel buttonPanel = new JPanel(new GridLayout(8, 5, 0, 0));
            buttonPanel.setOpaque(false);

            add(Box.createHorizontalGlue());
            add(buttonPanel);
            add(backButton);
            add(Box.createHorizontalGlue());

            // load the level buttons and set their style
            for (int i = 0; i < 40; i++) {
                levelButton[i] = new JButton(Integer.toString(i + 1));
                buttonPanel.add(levelButton[i]);
                levelButton[i].addActionListener(levelListener);
                levelButton[i].setFont(new Font("Wide Latin", Font.BOLD, 18));
                levelButton[i].setForeground(Color.white);
                levelButton[i].setIcon(lvlIcon[i / 10]);
                levelButton[i].setRolloverIcon(lvlROIcon[i / 10]);
                levelButton[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
                levelButton[i].setHorizontalTextPosition(JButton.CENTER);
                levelButton[i].setVerticalTextPosition(JButton.CENTER);
                levelButton[i].setOpaque(false);
                levelButton[i].setContentAreaFilled(false);
                levelButton[i].setBorderPainted(false);
                levelButton[i].setFocusable(false);
                levelButton[i].setPreferredSize(new Dimension(60, 90));
            }
        }

        // if the mode is speed run, load the speed run difficulty buttons
        else if (mode == GameControl.SPEED_RUN) {
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
            buttonPanel.setOpaque(false);
            add(Box.createHorizontalGlue());
            add(buttonPanel);
            add(Box.createHorizontalGlue());

            JButton[] diffButton = new JButton[]{
                    new JButton("Easy Run (1-10)"),
                    new JButton("Normal Run (11-20)"),
                    new JButton("Intermediate Run (21-30)"),
                    new JButton("Expert Run (31-40)"),
                    new JButton("ULTIMATE Run")
            };

            ActionListener levelListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    parent.removeAll();
                    parent.add(new GamePanel(parent, GameControl.SPEED_RUN, Arrays.asList(diffButton).indexOf(e.getSource())));
                    parent.revalidate();
                    parent.repaint();
                }
            };

            // load the difficulty buttons and set their style
            for (int i = 0; i < 5; i++) {
                buttonPanel.add(diffButton[i]);
                diffButton[i].addActionListener(levelListener);
                diffButton[i].setForeground(Color.white);
                diffButton[i].setIcon(diffIcon[i]);
                diffButton[i].setRolloverIcon(diffROIcon[i]);
                diffButton[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
                diffButton[i].setHorizontalTextPosition(JButton.CENTER);
                diffButton[i].setVerticalTextPosition(JButton.CENTER);
                diffButton[i].setOpaque(false);
                diffButton[i].setContentAreaFilled(false);
                diffButton[i].setBorderPainted(false);
                diffButton[i].setFocusable(false);
                diffButton[i].setPreferredSize(new Dimension(60, 90));
            }
            buttonPanel.add(backButton);
        }
    }

    private void loadHelpMenu(){

        // create the back button
        JButton backButton = new JGameButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAll();
                loadMenu();
                revalidate();
                repaint();
            }
        });

        JLabel helpLabel = new JLabel(helpBG);

        add(Box.createHorizontalGlue());
        add(helpLabel);
        add(backButton);
        add(Box.createHorizontalGlue());
    }

    // load resources
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

    private final ImageIcon diffIcon[] = {
            new ImageIcon(getClass().getResource("buttons/diffbutton1.png")),
            new ImageIcon(getClass().getResource("buttons/diffbutton2.png")),
            new ImageIcon(getClass().getResource("buttons/diffbutton3.png")),
            new ImageIcon(getClass().getResource("buttons/diffbutton4.png")),
            new ImageIcon(getClass().getResource("buttons/diffbutton5.png")),
    };

    private final ImageIcon diffROIcon[] = {
            new ImageIcon(getClass().getResource("buttons/diffbutton1r.png")),
            new ImageIcon(getClass().getResource("buttons/diffbutton2r.png")),
            new ImageIcon(getClass().getResource("buttons/diffbutton3r.png")),
            new ImageIcon(getClass().getResource("buttons/diffbutton4r.png")),
            new ImageIcon(getClass().getResource("buttons/diffbutton5r.png")),
    };

    private final ImageIcon logo = new ImageIcon(getClass().getResource("logo.jpg"));

    private final ImageIcon helpBG = new ImageIcon(getClass().getResource("help.jpg")); // help panel background

}

