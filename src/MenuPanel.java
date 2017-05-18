import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Creates the Menu Panel of the game.
 */
public class MenuPanel extends javax.swing.JPanel {


    private JPanel parent;
    private JButton[] menuButtons = new JButton[]{
      new JButton("Play Classic"),
            new JButton("Speed Run"),
            new JButton("How to play"),
            new JButton("Exit")
    };

    /**
     * Constructor.
     *
     * @param parent parent panel of the MenuPanel
     */
    public MenuPanel(JPanel parent) {
        this.parent = parent;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        loadMenu();
    }

    /**
     * Load the menu GUI.
     */
    private void loadMenu() {
        //remove any content from parent panel and replace it with this one

        for (JButton menuButton : menuButtons) add(menuButton);

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
    }

    /**
     * Load the available levels list and display it in the form of JButtons which load them
     */
    public void loadLevels(int mode) {
        if (mode == GameControl.CLASSIC_MODE) {
            ActionListener levelListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    parent.removeAll();
                    parent.add(new GamePanel(parent, GameControl.CLASSIC_MODE, Integer.parseInt(e.getActionCommand())));
                    parent.revalidate();
                    parent.repaint();
                }
            };

            JButton[] levelButton = new JButton[40];
            JPanel buttonPanel = new JPanel(new GridLayout(8, 5));
            for (int i = 0; i < 40; i++) {
                levelButton[i] = new JButton(Integer.toString(i + 1));
                buttonPanel.add(levelButton[i]);
                levelButton[i].addActionListener(levelListener);
            }
            add(buttonPanel);
        } else if (mode == GameControl.SPEED_RUN) {
            JButton[] diffButtons = new JButton[]{
                    new JButton("Normal Run (1-20)"),
                    new JButton("Intermediate Run (21-30)"),
                    new JButton("Expert Run (31-40)"),
                    new JButton("ULTIMATE Run")
            };

            ActionListener levelListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    parent.removeAll();
                    parent.add(new GamePanel(parent, GameControl.SPEED_RUN, Arrays.asList(diffButtons).indexOf(e.getSource())));
                    parent.revalidate();
                    parent.repaint();
                }
            };


            for(JButton diffButton : diffButtons){
                add(diffButton);
                diffButton.addActionListener(levelListener);
            }
        }

        JButton backButton = new JButton("Back");
        add(backButton);
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
}

