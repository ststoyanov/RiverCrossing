import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Creates the Menu Panel of the game.
 */
public class MenuPanel extends javax.swing.JPanel {
    private JPanel parent;

    /**
     * Constructor.
     *
     * @param parent parent panel of the MenuPanel
     */
    public MenuPanel(JPanel parent) {
        this.parent = parent;

        loadMenu();
    }

    /**
     * Load the menu GUI.
     */
    private void loadMenu() {
        //remove any content from parent panel and replace it with this one

        JButton playButton;
        add(playButton = new JButton("Play"));
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAll();
                getLevels();
                revalidate();
                repaint();
            }
        });
    }

    /**
     * Load the available levels lift
     */
    private void getLevels() {
        ActionListener levelListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.removeAll();
                parent.add(new GamePanel(parent, Integer.parseInt(e.getActionCommand())));
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
    }
}

