import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by zabraih on 04.05.2017.
 */
public class MenuPanel extends javax.swing.JPanel {
    private JPanel parent;

    public MenuPanel(JPanel parent){
        this.parent = parent;

        loadMenu();
    }

    private void loadMenu() {
        //remove any content from parent panel and replace it with this one
        parent.removeAll();
        parent.add(this);
        parent.revalidate();
        parent.repaint();

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

            private void getLevels() {
                ActionListener levelListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new PlayGame(parent,Integer.parseInt(e.getActionCommand()));
                    }
                };

                JButton[] levelButton = new JButton[40];
                JPanel buttonPanel = new JPanel(new GridLayout(8,5));
                for(int i = 0; i < 40 ; i++){
                    levelButton[i] = new JButton(Integer.toString(i+1));
                    buttonPanel.add(levelButton[i]);
                    levelButton[i].addActionListener(levelListener);
                }
                add(buttonPanel);
            }
        });
    }
}
