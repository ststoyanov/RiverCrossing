import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class controlling the user input and the logic of the game.
 */
public class GameControl {
    public static final int CLASSIC_MODE = 0;
    public static final int SPEED_RUN = 1;
    private GameMap gameMap;
    private GamePanel parent;
    private Player player;
    private int mode;
    private int difficulty;
    private int winLevel;
    private Timer timer;
    private long startTime;
    private long elapsed;

    public enum Direction {
        LEFT, RIGHT, UP, DOWN;

        static Direction getReverse(Direction direction) {
            if (direction == LEFT)
                return RIGHT;
            if (direction == RIGHT)
                return LEFT;
            if (direction == UP)
                return DOWN;

            return UP;
        }
    }

    /**
     * Constructor. Creates a game control for a GameMap
     *
     * @param gameMap gameMap to be played in
     */
    public GameControl(GamePanel parent, GameMap gameMap) {
        this.parent = parent;
        this.gameMap = gameMap;
        this.player = gameMap.player;
        createInputControl();
    }

    public void loadLevel(int level, int mode) {
        this.mode = mode;
        if (mode == CLASSIC_MODE) gameMap.loadLevel(level);
        else if (mode == SPEED_RUN) {
            difficulty = level;
            startSpeedRun(level);
        }
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long now = System.currentTimeMillis();
                elapsed = now - startTime;
                parent.updateTimer(String.format("%02d:%02d:%02d", elapsed / 1000 / 60, elapsed / 1000 % 60, elapsed % 1000 / 10));
                timer.start();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Create the control for user input. Set InputMap and ActionMap.
     */
    private void createInputControl() {
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
     *
     * @param direction direction for player movement or face
     */
    private void movePlayer(Direction direction) {
        // if the player has finished the level, don't do anything
        if (player.getTile() == gameMap.getWinTile())
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
    private void plankInteraction() {
        // if the player is holding a plank, place it in front of him if possible
        if (player.getPlankHeldSize() > 0) {
            if (gameMap.placePlank(player.getTile(), player.getDirection(), player.getPlankHeldSize()) > 0) {
                player.setPlankHeldSize(0);
                gameMap.removeGhostPlank();
            }
        }
        // if the player is not holding a plank and the tile in front of them holds one, pick it up
        else if (gameMap.getNextTile(player.getTile(), player.getDirection()) != null) {
            if (gameMap.getNextTile(player.getTile(), player.getDirection()).getContent() == GameTile.Content.PLANK) {
                player.setPlankHeldSize(gameMap.removePlank(player.getTile(), player.getDirection()));
                gameMap.updateGhostPlank();
            }
        }
    }

    private void finishLevel() {
        if (mode == CLASSIC_MODE) parent.displayWinMessage(CLASSIC_MODE, gameMap.getCurrentLevel());
        else if (mode == SPEED_RUN) {
            if (gameMap.getCurrentLevel() == winLevel) {
                timer.stop();
                parent.displayWinMessage(SPEED_RUN, difficulty);
                HighScoresPanel hsp = new HighScoresPanel("scores",elapsed);
                hsp.setBounds(GameMap.TILE_SIZE * GameMap.NUMBER_OF_COLUMNS /2, GameMap.TILE_SIZE * GameMap.NUMBER_OF_ROWS /2  - 200,200,500);
                parent.add(hsp,new Integer(200));
            } else gameMap.loadLevel(gameMap.getCurrentLevel() + 1);
        }
    }

    private void startSpeedRun(int level) {
        if (level == 0) {
            gameMap.loadLevel(1);
            winLevel = 2;
        } else if (level == 1) {
            gameMap.loadLevel(11);
            winLevel = 20;
        } else if (level == 2) {
            gameMap.loadLevel(21);
            winLevel = 30;
        } else if (level == 3) {
            gameMap.loadLevel(31);
            winLevel = 40;
        } else {
            gameMap.loadLevel(1);
            winLevel = 40;
        }

        startTimer();
    }
}


