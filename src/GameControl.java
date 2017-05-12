import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class controlling the user input and the logic of the game.
 */
public class GameControl {
    private  GameMap gameMap;
    private Player player;

    public enum Direction{
        LEFT, RIGHT, UP, DOWN;

        static Direction getReverse(Direction direction){
            if(direction == LEFT)
                return RIGHT;
            if(direction == RIGHT)
                return LEFT;
            if(direction == UP)
                return DOWN;

                return UP;
        }
    }

    /**
     * Constructor. Creates a game control for a GameMap
     * @param gameMap gameMap to be played in
     */
    public GameControl(GameMap gameMap){
        this.gameMap = gameMap;
        this.player = gameMap.player;
        createInputControl();
    }

    /**
     * Create the control for user input. Set InputMap and ActionMap.
     */
    private void createInputControl(){
        gameMap.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "left");
        gameMap.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "right");
        gameMap.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "up");
        gameMap.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "down");
        gameMap.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("A"), "left");
        gameMap.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"), "right");
        gameMap.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"), "up");
        gameMap.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "down");
        gameMap.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "space");

        gameMap.getActionMap().put("left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movePlayer(Direction.LEFT);
            }
        });

        gameMap.getActionMap().put("right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movePlayer(Direction.RIGHT);
            }
        });

        gameMap.getActionMap().put("up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movePlayer(Direction.UP);
            }
        });

        gameMap.getActionMap().put("down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movePlayer(Direction.DOWN);
            }
        });

        gameMap.getActionMap().put("space", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                plankInteraction();
            }
        });
    }

    /**
     * Move the player in a direction or change his facing direction
     * @param direction direction for player movement or face
     */
    private void movePlayer(Direction direction){
        if(direction != player.getDirection()){
            player.setDirection(direction);
        } else if(gameMap.getNextTile(player.getTile(),direction) != null){
            if(gameMap.getNextTile(player.getTile(),direction).getContent() == GameTile.Content.PLANK) {
                gameMap.movePlayerTo(gameMap.getNextTile(player.getTile(), direction, GameTile.Content.STUMP));
                if(player.getTile() == gameMap.getWinTile()){
                    displayWinMessage();
                    gameMap.getActionMap().clear();
                }
            }
        }
    }

    private void plankInteraction(){
        if(player.getPlankHeldSize() > 0){
            if(gameMap.placePlank(player.getTile(),player.getDirection(),player.getPlankHeldSize()) > 0) {
                player.setPlankHeldSize(0);
            }
        } else if(gameMap.getNextTile(player.getTile(), player.getDirection()) != null){
            if (gameMap.getNextTile(player.getTile(), player.getDirection()).getContent() == GameTile.Content.PLANK) {
                player.setPlankHeldSize(gameMap.removePlank(player.getTile(), player.getDirection()));
            }
        }
    }

    public void displayWinMessage(){
        JPanel winPanel = new JPanel();
        JPanel parent = (JPanel) gameMap.getParent();

        JButton menuButton = new JButton("Menu");
        JButton restartButton = new JButton("Restart Level");
        winPanel.setBounds(gameMap.TILE_SIZE*gameMap.NUMBER_OF_COLUMNS/2 - 100,gameMap.TILE_SIZE*gameMap.NUMBER_OF_ROWS/2 + - 40,200,80);

        winPanel.add(new JLabel("Congratulations, level " + gameMap.getCurrentLevel() + " completed!"));
        winPanel.add(menuButton);
        winPanel.add(restartButton);
        menuButton.requestFocusInWindow();

        gameMap.add(winPanel,new Integer(100));

        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.removeAll();
                parent.add(new MenuPanel(parent));
                parent.revalidate();
                parent.repaint();
            }
        });

        restartButton.addActionListener((new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameMap.remove(winPanel);
                gameMap.loadLevel(gameMap.getCurrentLevel());
            }
        }));
    }
}
