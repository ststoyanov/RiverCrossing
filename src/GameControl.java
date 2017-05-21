import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class controlling the user input and the logic of the game.
 */
public class GameControl {
    public static final int CLASSIC_MODE = 0;
    public static final int SPEED_RUN = 1;
    private  GameMap gameMap;
    private GamePanel parent;
    private Player player;
    private int mode;
    private int winLevel;
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
    public GameControl(GamePanel parent, GameMap gameMap){
        this.parent = parent;
        this.gameMap = gameMap;
        this.player = gameMap.player;
        createInputControl();
    }

    public void loadLevel(int level, int mode){
        this.mode = mode;
        if(mode == CLASSIC_MODE) gameMap.loadLevel(level);
        else if(mode == SPEED_RUN) startSpeedRun(level);
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
        // if the player has finished the level, don't do anything
        if(player.getTile() == gameMap.getWinTile())
            return;

        // if the player is not facing the direction pressed, change it
        if (direction != player.getDirection()) {
            player.setDirection(direction);
            gameMap.updateGhostPlank();
        }
        // if the player is facing the direction pressed move him in that direction if possible
        else if (gameMap.getNextTile(player.getTile(), direction) != null) {
            if (gameMap.getNextTile(player.getTile(), direction).getContent() == GameTile.Content.PLANK) {
                gameMap.movePlayerTo(gameMap.getNextTile(player.getTile(), direction, GameTile.Content.STUMP));
                gameMap.updateGhostPlank();
                // when the player reaches the end of the level display the Win message
                if (player.getTile() == gameMap.getWinTile()) {
                    finishLevel();
                }
            }
        }
    }

    /**
     * Controls the interaction with the plank on pressing the space key.
     */
    private void plankInteraction(){
        // if the player is holding a plank, place it in front of him if possible
        if(player.getPlankHeldSize() > 0){
            if(gameMap.placePlank(player.getTile(),player.getDirection(),player.getPlankHeldSize()) > 0) {
                player.setPlankHeldSize(0);
                gameMap.removeGhostPlank();
            }
        }
        // if the player is not holding a plank and the tile in front of them holds one, pick it up
        else if(gameMap.getNextTile(player.getTile(), player.getDirection()) != null){
            if (gameMap.getNextTile(player.getTile(), player.getDirection()).getContent() == GameTile.Content.PLANK) {
                player.setPlankHeldSize(gameMap.removePlank(player.getTile(), player.getDirection()));
                gameMap.updateGhostPlank();
            }
        }
    }

    private void finishLevel(){
        if(mode == CLASSIC_MODE) displayWinMessage();
        else if(mode == SPEED_RUN) {
            if(gameMap.getCurrentLevel() == winLevel){
                parent.stopTimer();
                displayWinMessage();
            }
            else gameMap.loadLevel(gameMap.getCurrentLevel()+1);
        }
    }

    private void startSpeedRun(int level){
        if(level == 0){
            gameMap.loadLevel(1);
            winLevel = 10;
        } else if(level == 1){
            gameMap.loadLevel(11);
            winLevel = 20;
        } else if(level == 2) {
            gameMap.loadLevel(21);
            winLevel = 30;
        } else if(level == 3) {
            gameMap.loadLevel(31);
            winLevel = 40;
        } else {
            gameMap.loadLevel(1);
            winLevel = 40;
        }
    }

    /**
     * Displays the Win message panel and controls, upon finishing a level
     */
    private void displayWinMessage(){
        JPanel winPanel = new JPanel();
        JPanel parent = (JPanel) gameMap.getParent();

        JButton menuButton = new JButton("Menu");
        JButton nextlvlButton = new JButton("Next Level");
        winPanel.setBounds(GameMap.TILE_SIZE * GameMap.NUMBER_OF_COLUMNS /2 - 100, GameMap.TILE_SIZE * GameMap.NUMBER_OF_ROWS /2 + - 40,200,80);

        winPanel.add(new JLabel("Congratulations, level " + gameMap.getCurrentLevel() + " completed!"));
        winPanel.add(menuButton);
        winPanel.add(nextlvlButton);
        nextlvlButton.setFocusable(true);
        nextlvlButton.requestFocusInWindow();

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

        nextlvlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameMap.remove(winPanel);
                gameMap.loadLevel(gameMap.getCurrentLevel() + 1);
                parent.revalidate();
                parent.repaint();
            }
        });
    }
}
