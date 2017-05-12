import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by zabraih on 11.05.2017.
 */
public class GameControl {
    private  GameMap gameMap;
    private GameMap.Player player;

    public enum Direction{
        LEFT, RIGHT, UP, DOWN
    }

    public GameControl(GameMap gameMap){
        this.gameMap = gameMap;
        this.player = gameMap.player;
        createInputControl();
    }

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

    private void movePlayer(Direction dir){
        if(dir != player.getDirection()){
            player.setDirection(dir);
        } else {
            int rowPos = player.getRow();
            int colPos = player.getCol();
            if(dir == Direction.RIGHT) {
                if(gameMap.getTileContent(rowPos, colPos + 1) == GameMap.Content.PLANK) {
                    int i = player.getCol();
                    do{
                        i++;
                        player.setCol(i);
                    }while (gameMap.getTileContent(rowPos, i) != GameMap.Content.STUMP);
                }
            } else if(dir == Direction.LEFT){
                if(gameMap.getTileContent(rowPos, colPos - 1) == GameMap.Content.PLANK) {
                    int i = colPos;
                    do{
                        i--;
                        player.setCol(i);
                    } while(gameMap.getTileContent(rowPos, i) != GameMap.Content.STUMP);
                }
            } else if(dir == Direction.UP){
                if(gameMap.getTileContent(rowPos - 1, colPos) == GameMap.Content.PLANK) {
                    int i = rowPos;
                    do{
                        i--;
                        player.setRow(i);
                    } while(gameMap.getTileContent(i,colPos) != GameMap.Content.STUMP);
                }
            } else if(dir == Direction.DOWN){
                if(gameMap.getTileContent(rowPos + 1, colPos) == GameMap.Content.PLANK) {
                    int i = player.getRow();
                    do{
                        i++;
                        player.setRow(i);
                    }while (gameMap.getTileContent(i,colPos) != GameMap.Content.STUMP);
                }
            }
        }
    }

    private void plankInteraction(){
        Direction dir = player.getDirection();
        if(player.plankHeldSize > 0){
            if(gameMap.placePlank(player.getTile(), gameMap.getNextTile(GameMap.Content.STUMP, dir)) > 0)
                player.plankHeldSize = 0;
        } else if(gameMap.getTileContent(gameMap.getNextTile(dir)) == GameMap.Content.PLANK){
            player.plankHeldSize = gameMap.removePlank(gameMap.getNextTile(dir));
        }
    }
}
