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
    private GameControl gameControl = new GameControl(this, gameMap);
    private JLabel timerLabel = new JLabel();
    private float time;
    Timer timer;
    long startTime;
    long elapsed;

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
        if(mode == GameControl.SPEED_RUN){
            startTimer();
        }
    }

    private void startTimer(){
        startTime=System.currentTimeMillis();
        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long now = System.currentTimeMillis();
                elapsed = now - startTime;
                timerLabel.setText(String.format("%02d:%02d:%02d",elapsed/1000/60,elapsed/1000%60,elapsed%1000/10));
                timer.start();
            }
        });
        timer.setRepeats(false);
        timer.start();
        add(timerLabel, BorderLayout.NORTH);
    }

    public void stopTimer(){
        timer.stop();
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
