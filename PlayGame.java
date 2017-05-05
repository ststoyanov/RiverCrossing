import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by zabraih on 04.05.2017.
 */
public class PlayGame extends javax.swing.JPanel {
    private JPanel parent;
    private JButton menuButton;
    private GameMap gameMap = new GameMap();

    public PlayGame(JPanel parent, int level) {
        this.parent = parent;
        createGamePanel();
        loadGame(level);
    }

    public PlayGame(JPanel parent){
        this(parent,1);
    }

    private void createGamePanel(){
        //remove any content from parent panel and replace it with this one
        parent.removeAll();
        parent.add(this);
        parent.revalidate();
        parent.repaint();

        add(gameMap);
        menuButton = new JButton("Menu");
        add(menuButton);
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.remove(PlayGame.this);
                parent.add(new MenuPanel(parent));
            }
        });
    }

    private void loadGame(int level){
        gameMap.loadLevel(level);
    }
}
