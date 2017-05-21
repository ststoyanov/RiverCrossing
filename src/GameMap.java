import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 *  This class contains and controls a gameGrid of GameTiles as part of a JLayeredPanel.
 *
 *  Contains 3 layers:
 *  - mapPanel - shows the water, stumps and land
 *  - plankPanel - shows the planks
 *  - player - shows the player
 */
public class GameMap extends JLayeredPane {
    public static final int TILE_SIZE = 48; // size of a singe game tile (square) in pixels
    public static final int NUMBER_OF_ROWS = 13;
    public static final int NUMBER_OF_COLUMNS = 9;

    private JPanel mapPanel, plankPanel;
    public Player player;
    private ArrayList<Plank> plankList = new ArrayList<>();
    private JLabel ghostPlank;

    // The gameGrid held as a double array of GameTiles
    private GameTile[][] gameGrid = new GameTile[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];

    // Coordinates of the tile you have to reach to win
    private int winRow, winCol;

    private int currentLevel;

    /**
     * Constructor. Creates the game map and the gameGrid.
     */
    public GameMap(){
        //set the size of the GameMap to the size of the grid
        setPreferredSize(new Dimension(NUMBER_OF_COLUMNS * TILE_SIZE, NUMBER_OF_ROWS * TILE_SIZE));

        // Create the 3 layers
        // mapPanel - shows the water, stumps and land
        mapPanel = new JPanel(new GridLayout(NUMBER_OF_ROWS,NUMBER_OF_COLUMNS));
        mapPanel.setBounds(0,0,NUMBER_OF_COLUMNS * TILE_SIZE,NUMBER_OF_ROWS * TILE_SIZE);
        add(mapPanel, DEFAULT_LAYER);

        // Add each tile of the gameGrid to the mapPanel
        for(int i = 0; i < NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                gameGrid[i][j] = new GameTile(i, j);
                mapPanel.add(gameGrid[i][j]);
            }
        }

        // plankPanel - shows the planks
        plankPanel = new JPanel(null);
        plankPanel.setBounds(0,0,NUMBER_OF_COLUMNS * TILE_SIZE,NUMBER_OF_ROWS * TILE_SIZE);
        plankPanel.setOpaque(false);
        add(plankPanel, new Integer(10));

        //ghostPlank - shows the plank, the player is holding
        ghostPlank = new JLabel();
        add(ghostPlank, new Integer(11));

        // playerPanel - shows the player
        player = new Player();
        player.setDirection(GameControl.Direction.UP);
        player.setSize(TILE_SIZE,TILE_SIZE);
        add(player, new Integer(20));
    }

    /**
     * Get the currently loaded level.
     * @return the current level
     */
    public int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Get the first tile in a certain direction.
     * @param tile initial tile
     * @param direction direction from the initial tile
     * @return first GameTile in the direction from the initial tile
     */
    public GameTile getNextTile(GameTile tile, GameControl.Direction direction){
        if(direction == GameControl.Direction.LEFT) {
            if(tile.getCol()-1 >= 0)
                return gameGrid[tile.getRow()][tile.getCol()-1];
        } else if(direction == GameControl.Direction.RIGHT){
            if(tile.getCol()+1 < 9)
                return gameGrid[tile.getRow()][tile.getCol()+1];
        } else if(direction == GameControl.Direction.UP){
            if(tile.getRow()-1 >= 0)
                return gameGrid[tile.getRow()-1][tile.getCol()];
        } else if(direction == GameControl.Direction.DOWN){
            if(tile.getRow()+1 < 13)
                return gameGrid[tile.getRow()+1][tile.getCol()];
        }

        return null;
    }

    /**
     * Get the first tile in a certain direction with a specified content.
     *
     * @param tile initial tile
     * @param direction direction from the initial tile
     * @param content content to be searched for
     * @return first GameTile in the direction from the initial tile containing the specified content
     */
    public GameTile getNextTile(GameTile tile, GameControl.Direction direction, GameTile.Content content){
        GameTile tempTile = tile;
        while (true){
            tempTile = getNextTile(tempTile,direction);
            if(tempTile == null)
                return tile;

            if(tempTile.getContent() == content)
                return tempTile;

        }
    }

    /**
     * Calculates the distance between tileA and tile B
     * @param tileA GameTile 1
     * @param tileB GameTile 2
     * @return distance between the 2 tiles
     */
    private int distanceBetween(GameTile tileA, GameTile tileB) {
        int dist = 0;

        if (tileA.getCol() == tileB.getCol()){
            dist = Math.abs(tileA.getRow() - tileB.getRow()) - 1;
        } else if (tileA.getRow() == tileB.getRow()) {
            dist = Math.abs(tileA.getCol() - tileB.getCol()) - 1;
        }

        return dist;
    }

    /**
     * Check if a plank can be placed between two stumps.
     * @param stumpA stump at one end of the plank
     * @param stumpB stump at other end of the plank
     * @param size size of the plank
     * @return true if possible to place a plank of that size between the two stumps, false otherwise
     */
    private boolean canPlacePlank(GameTile stumpA, GameTile stumpB, int size){
        if(distanceBetween(stumpA,stumpB)!=size)
            return false;

        // find the direction of stumpB, relative to stumpA
        // then check if there is a plank between them
        for(GameControl.Direction dir : GameControl.Direction.values()) {
            if (getNextTile(stumpA, dir, GameTile.Content.STUMP) == stumpB) {
                GameTile tempTile = stumpA;
                while (tempTile != stumpB){
                    tempTile = getNextTile(tempTile,dir);
                    if(tempTile.getContent() == GameTile.Content.PLANK)
                        return false;
                }
                break; // break the for as the direction is already found
            }
        }

        return true;
    }

    /**
     * Place a plank between stumpA and stumpB
     *
     * @param stumpA stump at one end of the plank
     * @param stumpB stump at other end of the plank
     * @return 1 if successful, -1 if unsuccessful
     */
    public int placePlank(GameTile stumpA, GameTile stumpB){
        // unsuccessful if the content of the two tiles is not a stump
        if(stumpA.getContent() != GameTile.Content.STUMP || stumpB.getContent() != GameTile.Content.STUMP) {
            return -1;
        }
        // unsuccessful if a plank can't be placed between stumpA and stumpB
        if(!canPlacePlank(stumpA,stumpB,distanceBetween(stumpA,stumpB))){
            return -1;
        }

        Plank plank = new Plank(); // temp plank object
        int size = 0; // temp size

        int rowA = stumpA.getRow(), rowB = stumpB.getRow();
        int colA = stumpA.getCol(), colB = stumpB.getCol();

        plankPanel.add(plank); // add the plank to tha view
        plankList.add(plank); // add the plank to the plankList

        // check if stumpA and stumpB are in the same row
        if(rowA == rowB){
            // if so set orientation to 1 (horizontal)
            plank.setOrientation(1);

            // check if B comes before A
            if(colA > colB){
                // set the content of all tiles between A and B to PLANK
                // add the tiles to the span of the plank
                // also add the index of the plank to the tile
                for(int j = colB+1;j < colA ;j++){
                    gameGrid[rowA][j].setContent(GameTile.Content.PLANK);
                    gameGrid[rowA][j].setPlankPiece(plank);
                    plank.span[size] = gameGrid[rowA][j];
                    size++;
                }

                // set the plank's size and position
                plank.size = size;
                plank.setBounds(
                        (colB+1)*TILE_SIZE,rowA*TILE_SIZE,
                        size*TILE_SIZE,TILE_SIZE
                );
            } else if(colB > colA) { // check if A comes before B and do the same as above
                for(int j = colA+1; j < colB; j++){
                    gameGrid[rowA][j].setContent(GameTile.Content.PLANK);
                    gameGrid[rowA][j].setPlankPiece(plank);
                    plank.span[size] = gameGrid[rowA][j];
                    size++;
                }

                plank.size = size;
                plank.setBounds(
                        (colA+1)*TILE_SIZE,rowA*TILE_SIZE,
                        size*TILE_SIZE,TILE_SIZE
                );
            }
        } else if(colA == colB){ // check if stumpA and stumpB are in the same column and do the same as above
            plank.setOrientation(-1);

            if(rowA > rowB){
                for(int i = rowB+1;i < rowA;i++){
                    gameGrid[i][colA].setContent(GameTile.Content.PLANK);
                    gameGrid[i][colA].setPlankPiece(plank);
                    plank.span[size] = gameGrid[i][colA];
                    size++;
                }

                plank.size = size;
                plank.setBounds(
                        colA*TILE_SIZE,(rowB+1)*TILE_SIZE,
                        TILE_SIZE,size * TILE_SIZE
                );

                plankPanel.add( plankList.get(plankList.size()-1));
            } else if (rowB > rowA){
                for(int i = rowA+1;i < rowB;i++) {
                    gameGrid[i][colA].setContent(GameTile.Content.PLANK);
                    gameGrid[i][colA].setPlankPiece(plank);
                    plank.span[size] = gameGrid[i][colA];
                    size++;
                }

                plank.size = size;
                plank.setBounds(
                        colA*TILE_SIZE,(rowA+1)*TILE_SIZE,
                        TILE_SIZE,size*TILE_SIZE
                );
            }
        }
        return 1;
    }

    /**
     * Place a plank of a specified size between stumpA and stumpB
     *
     * @param stumpA stump at one end of the plank
     * @param stumpB stump at other end of the plank
     * @param size size of the plank
     * @return 1 if successful, -1 if unsuccessful
     */
    public int placePlank(GameTile stumpA, GameTile stumpB, int size){
        if(distanceBetween(stumpA,stumpB) == size){
            return placePlank(stumpA,stumpB);
        } else {
            return -1;
        }
    }

    /**
     * Places a plank from a stump in a direction relative to it
     *
     * @param stump stump at one end of the plank
     * @param direction direction towards other stump
     * @return 1 if successful, -1 if unsuccessful
     */
    public int placePlank(GameTile stump, GameControl.Direction direction){
        return placePlank(stump,getNextTile(stump,direction,GameTile.Content.STUMP));
    }

    /**
     * Places a plank of a specified size from a stump in a direction relative to it
     *
     * @param stump stump at one end of the plank
     * @param direction direction towards other stump
     * @param size size of the plank
     * @return 1 if successful, -1 if unsuccessful
     */
    public int placePlank(GameTile stump, GameControl.Direction direction, int size){
        return placePlank(stump,getNextTile(stump,direction,GameTile.Content.STUMP),size);
    }

    /**
     * Remove a plank, accessing it from a singe tile, part of the plank
     *
     * @param plankTile tile part of the plank
     * @return size of the plank removed
     */
    public int removePlank(GameTile plankTile){
        if(plankTile.getContent() != GameTile.Content.PLANK) {
            return 0;
        }

        Plank plank = plankTile.getPlankPiece();

        for(int i = 0; i < plank.size; i++){
            plank.span[i].setContent(GameTile.Content.WATER);
        }

        plankList.remove(plank);
        plankPanel.remove(plank);
        revalidate();
        repaint();

        return plank.size;
    }

    /**
     * Remove a plank, situated between two stumps
     *
     * @param stumpA stump at one end of the plank
     * @param stumpB stump at other end of the plank
     * @return size of the plank removed
     */
    public int removePlank(GameTile stumpA, GameTile stumpB){
        if(stumpA.getContent() != GameTile.Content.STUMP || stumpB.getContent() != GameTile.Content.STUMP) {
            return 0;
        }

        int plankRow = (stumpA.getRow() + stumpB.getRow())/2;
        int plankCol = (stumpA.getCol()+stumpB.getCol())/2;

        return(removePlank(gameGrid[plankRow][plankCol]));
    }


    /**
     * Remove a plank, situated in a direction relative to a stump
     *
     * @param stump stump at one end of the plank
     * @param direction direction of the plank, relative to the stump
     * @return size of the plank removed
     */
    public int removePlank(GameTile stump, GameControl.Direction direction){
        return removePlank(getNextTile(stump,direction));
    }



    /**
     * Updates the position of the "ghost" plank to be in front of the player
     */
    public void updateGhostPlank(){
        // update the ghost plank only if the player is holding a plank
        if(player.getPlankHeldSize() > 0) {
            GameControl.Direction direction = player.getDirection();
            int size = player.getPlankHeldSize();
            int orientation = 0;

            // set the orientation fo the ghost plank
            if (direction == GameControl.Direction.LEFT) {
                ghostPlank.setBounds(player.getX() - size * TILE_SIZE, player.getY(), size * TILE_SIZE, TILE_SIZE);
                orientation = -1;
            } else if (direction == GameControl.Direction.RIGHT) {
                ghostPlank.setBounds(player.getX()  + TILE_SIZE, player.getY(), size * TILE_SIZE, TILE_SIZE);
                orientation = -1;
            } else if (direction == GameControl.Direction.UP) {
                ghostPlank.setBounds(player.getX(), player.getY() - size * TILE_SIZE, TILE_SIZE, size * TILE_SIZE);
                orientation = 1;
            } else if (direction == GameControl.Direction.DOWN) {
                ghostPlank.setBounds(player.getX(), player.getY() + TILE_SIZE, TILE_SIZE, size * TILE_SIZE);
                orientation = 1;
            }

            // check if the plank can be placed in that direction
            boolean canPlacePlank = canPlacePlank(player.getTile(),getNextTile(player.getTile(),direction, GameTile.Content.STUMP),size);

            // display the ghost plank
            if(orientation > 0) {
                if (canPlacePlank) {
                    ghostPlank.setIcon(hGhostPlankIcon[1]);
                } else {
                    ghostPlank.setIcon(hGhostPlankIcon[0]);
                }
            } else if(orientation < 0){
                if (canPlacePlank) {
                    ghostPlank.setIcon(vGhostPlankIcon[1]);
                } else {
                    ghostPlank.setIcon(vGhostPlankIcon[0]);
                }
            }
        }
    }

    /**
     * Makes the "ghost" plank visible.
     */
    public void showGhostPlank(){
        ghostPlank.setVisible(true);
    }

    /**
     * Removes the "ghost" plank
     */
    public void removeGhostPlank(){
        ghostPlank.setIcon(null);
    }

    /**
     * Hides the "ghost" plank
     */
    public void hideGhostPlank(){
        ghostPlank.setVisible(false);
    }

    /**
     * Moves the player to a tile in the gameGrid
     *
     * @param tile tile of the gameGrid
     */
    public void movePlayerTo(GameTile tile){
        if(tile.getContent() != GameTile.Content.STUMP && tile.getContent() != GameTile.Content.PLANK ) {
            return;
        }
        player.setTile(tile);
    }

    /**
     * Set the win condition to a tile
     * @param winTile tile to be reached to win
     */
    private void setWinTile(GameTile winTile){
        winRow = winTile.getRow();
        winCol = winTile.getCol();
    }

    /**
     * Get the win condition tile.
     * @return tile to be reached to win
     */
    public GameTile getWinTile(){
        return gameGrid[winRow][winCol];
    }


    /**
     * Load a level. Populates the gameGrid with content, predefined for each level.
     * @param level a predefined level layout
     */
    public void loadLevel(int level) {
        currentLevel = level;

        // clear the current level planks
        plankPanel.removeAll();
        plankList.clear();
        player.setPlankHeldSize(0);

        switch(level){
            case 1:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if ((j == 2 && (i == 12 || i == 8 || i == 6))
                                || j == 6 && (i == 6 || i == 4 || i == 0)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][2], gameGrid[8][2]);
                placePlank(gameGrid[8][2], gameGrid[6][2]);
                movePlayerTo(gameGrid[12][2]);
                setWinTile(gameGrid[0][6]);
                break;
            case 2:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if ((j == 0 && (i == 12 || i == 10 || i == 8) ||
                                j == 4 && (i == 8 || i == 6 || i == 4)) ||
                                j == 8 && (i == 4 || i == 2 || i == 0)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][0], gameGrid[10][0]);
                placePlank(gameGrid[8][0], gameGrid[8][4]);
                placePlank(gameGrid[4][4] , gameGrid[4][8]);
                movePlayerTo(gameGrid[12][0]);
                setWinTile(gameGrid[0][8]);
                break;
            case 3:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 6) ||
                                j == 2 && (i == 10 || i == 6) ||
                                j == 4 && (i == 12 || i == 10) ||
                                j == 6 && (i == 6 || i == 2 || i == 0) ||
                                j == 8 && (i == 6)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][4], gameGrid[10][4]);
                placePlank(gameGrid[10][2], gameGrid[6][2]);
                movePlayerTo(gameGrid[12][4]);
                setWinTile(gameGrid[0][6]);
                break;
            case 4:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 10 || i == 6) ||
                                j == 2 && (i == 12 || i == 8) ||
                                j == 4 && (i == 10 || i == 4) ||
                                j == 6 && (i == 6 || i == 0) ||
                                j == 8 && (i == 8 || i == 4)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][2], gameGrid[8][2]);
                placePlank(gameGrid[8][2], gameGrid[8][8]);
                placePlank(gameGrid[8][8] , gameGrid[4][8]);
                movePlayerTo(gameGrid[12][2]);
                setWinTile(gameGrid[0][6]);
                break;
            case 5:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if ((j == 0 && (i == 10 || i == 8 || i == 6 || i == 2)) ||
                                j == 4 && (i == 12 || i == 8 || i == 4 || i == 0) ||
                                i == 6 && (j == 2 || j == 6)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][4], gameGrid[8][4]);
                placePlank(gameGrid[10][0], gameGrid[8][0]);
                placePlank(gameGrid[6][2] , gameGrid[6][6]);
                movePlayerTo(gameGrid[12][4]);
                setWinTile(gameGrid[0][4]);
                break;
            case 6:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 10 || i == 6 || i == 4) ||
                                j == 2 && (i == 12 || i == 10) ||
                                j == 4 && (i == 8 || i == 4) ||
                                j == 6 && (i == 2 || i == 0) ||
                                j == 8 && (i == 8 || i == 6 || i == 2)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][2], gameGrid[10][2]);
                placePlank(gameGrid[10][0], gameGrid[6][0]);
                placePlank(gameGrid[8][8] , gameGrid[6][8]);
                movePlayerTo(gameGrid[12][2]);
                setWinTile(gameGrid[0][6]);
                break;
            case 7:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 10 || i == 4) ||
                                j == 2 && (i == 12 || i == 8) ||
                                j == 6 && (i == 10 || i == 8 || i == 4 || i == 2 || i == 0) ||
                                j == 8 && (i == 10)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][2], gameGrid[8][2]);
                placePlank(gameGrid[4][0], gameGrid[4][6]);
                placePlank(gameGrid[10][6] , gameGrid[10][8]);
                movePlayerTo(gameGrid[12][2]);
                setWinTile(gameGrid[0][6]);
                break;
            case 8:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 4) ||
                                j == 2 && (i == 12 || i == 8 || i == 4) ||
                                j == 6 && (i == 8 || i == 2 || i == 0) ||
                                j == 8 && (i == 6 || i == 4)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][2], gameGrid[8][2]);
                placePlank(gameGrid[8][2], gameGrid[8][6]);
                placePlank(gameGrid[8][6] , gameGrid[2][6]);
                placePlank(gameGrid[6][8], gameGrid[4][8]);
                movePlayerTo(gameGrid[12][2]);
                setWinTile(gameGrid[0][6]);
                break;
            case 9:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 10 || i == 6) ||
                                j == 2 && (i == 2 || i == 0) ||
                                j == 4 && (i == 12 || i == 8 || i == 6) ||
                                j == 6 && (i == 6 || i == 2) ||
                                j == 8 && (i == 8 || i == 4)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][4], gameGrid[8][4]);
                placePlank(gameGrid[8][4], gameGrid[6][4]);
                placePlank(gameGrid[8][8] , gameGrid[4][8]);
                movePlayerTo(gameGrid[12][4]);
                setWinTile(gameGrid[0][2]);
                break;
            case 10:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 10 || i == 6 || i == 2) ||
                                j == 2 && (i == 10 || i == 2) ||
                                j == 4 && (i == 12 || i == 10 || i == 6 || i == 2 || i == 0) ||
                                j == 8 && (i == 6)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][4], gameGrid[10][4]);
                placePlank(gameGrid[10][0], gameGrid[6][0]);
                placePlank(gameGrid[6][4] , gameGrid[6][8]);
                movePlayerTo(gameGrid[12][4]);
                setWinTile(gameGrid[0][4]);
                break;
            case 11:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 10 || i == 6 || i == 4) ||
                                j == 2 && (i == 6) ||
                                j == 4 && (i == 10 || i == 4 || i == 0) ||
                                j == 6 && (i == 12 || i == 10 || i == 6) ||
                                j == 8 && (i == 6)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][6], gameGrid[10][6]);
                placePlank(gameGrid[10][0], gameGrid[10][4]);
                movePlayerTo(gameGrid[12][6]);
                setWinTile(gameGrid[0][4]);
                break;
            case 12:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 12 || i == 10 || i == 8 || i == 2) ||
                                j == 2 && (i == 8) ||
                                j == 6 && (i == 10 || i == 4 || i == 2 || i == 0) ||
                                j == 8 && (i == 10 || i == 8)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][0], gameGrid[10][0]);
                placePlank(gameGrid[8][2], gameGrid[8][8]);
                movePlayerTo(gameGrid[12][0]);
                setWinTile(gameGrid[0][6]);
                break;
            case 13:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 4) ||
                                j == 2 && (i == 12 || i == 8 || i == 2) ||
                                j == 4 && (i == 4) ||
                                j == 6 && (i == 8 || i == 4 || i == 0) ||
                                j == 8 && (i == 4 || i == 2)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][2], gameGrid[8][2]);
                placePlank(gameGrid[4][0], gameGrid[4][4]);
                placePlank(gameGrid[4][6] , gameGrid[4][8]);
                placePlank(gameGrid[2][2], gameGrid[2][8]);
                movePlayerTo(gameGrid[12][2]);
                setWinTile(gameGrid[0][6]);
                break;
            case 14:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 6 || i == 4) ||
                                j == 2 && (i == 12 || i == 8) ||
                                j == 4 && (i == 10 || i == 4 || i == 2) ||
                                j == 6 && (i == 6 || i == 0) ||
                                j == 8 && (i == 10 || i == 8 || i == 6)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][2], gameGrid[8][2]);
                placePlank(gameGrid[8][2], gameGrid[8][8]);
                placePlank(gameGrid[8][8] , gameGrid[6][8]);
                movePlayerTo(gameGrid[12][2]);
                setWinTile(gameGrid[0][6]);
                break;
            case 15:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 4 || i == 2) ||
                                j == 2 && (i == 8 || i == 6 || i == 2) ||
                                j == 4 && (i == 4 || i == 0) ||
                                j == 6 && (i == 12 || i == 10 || i == 6) ||
                                j == 8 && (i == 6)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][6], gameGrid[10][6]);
                placePlank(gameGrid[10][6], gameGrid[6][6]);
                placePlank(gameGrid[8][2] , gameGrid[6][2]);
                movePlayerTo(gameGrid[12][6]);
                setWinTile(gameGrid[0][4]);
                break;
            case 16:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 4 || i == 2) ||
                                j == 2 && (i == 6 || i == 0) ||
                                j == 4 && (i == 8 || i == 2) ||
                                j == 6 && (i == 12 || i == 10 || i == 4 || i == 2) ||
                                j == 8 && (i == 8 || i == 6)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][6], gameGrid[10][6]);
                placePlank(gameGrid[10][6], gameGrid[4][6]);
                placePlank(gameGrid[2][0] , gameGrid[2][4]);
                movePlayerTo(gameGrid[12][6]);
                setWinTile(gameGrid[0][2]);
                break;
            case 17:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 2 && (i == 12 || i == 6) ||
                                j == 4 && (i == 10 || i == 6 || i == 4 || i == 2) ||
                                j == 6 && (i == 6 || i == 0) ||
                                j == 8 && (i == 10 || i == 4|| i == 2)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][2], gameGrid[6][2]);
                placePlank(gameGrid[6][2], gameGrid[6][4]);
                placePlank(gameGrid[4][4] , gameGrid[4][8]);
                placePlank(gameGrid[2][4], gameGrid[2][8]);
                movePlayerTo(gameGrid[12][2]);
                setWinTile(gameGrid[0][6]);
                break;
            case 18:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 8 || i == 4) ||
                                j == 2 && (i == 6) ||
                                j == 4 && (i == 12 || i == 8 || i == 2 || i == 0) ||
                                j == 6 && (i == 6 || i == 4) ||
                                j == 8 && (i == 8 || i == 4)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][4], gameGrid[8][4]);
                placePlank(gameGrid[6][2], gameGrid[6][6]);
                placePlank(gameGrid[4][0] , gameGrid[4][6]);
                placePlank(gameGrid[4][6], gameGrid[4][8]);
                movePlayerTo(gameGrid[12][4]);
                setWinTile(gameGrid[0][4]);
                break;
            case 19:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 10 || i == 6) ||
                                j == 2 && (i == 10 || i == 4) ||
                                j == 4 && (i == 12 || i == 10 || i == 6 || i == 0) ||
                                j == 8 && (i == 10 || i == 4)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][4], gameGrid[10][4]);
                placePlank(gameGrid[10][0], gameGrid[6][0]);
                placePlank(gameGrid[10][8] , gameGrid[4][8]);
                movePlayerTo(gameGrid[12][4]);
                setWinTile(gameGrid[0][4]);
                break;
            case 20:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if ((j == 0 && (i == 10 || i == 6) ||
                                j == 2 && (i == 12 || i == 8 || i == 2) ||
                                j == 4 && (i == 10 || i == 4)) ||
                                j == 6 && (i == 8 || i == 6 || i == 4 || i == 0) ||
                                j == 8 && (i == 10 || i == 8 || i == 2)){
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][2], gameGrid[8][2]);
                placePlank(gameGrid[8][2], gameGrid[2][2]);
                placePlank(gameGrid[10][8] , gameGrid[8][8]);
                movePlayerTo(gameGrid[12][2]);
                setWinTile(gameGrid[0][6]);
                break;
            case 21:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 10 || i == 6 || i == 4) ||
                                j == 2 && (i == 8 || i == 2) ||
                                j == 4 && (i == 12 || i == 8 || i == 6 || i == 0) ||
                                j == 6 && (i == 10 || i == 4 || i == 2) ||
                                j == 8 && (i == 8 || i == 6 || i == 2)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][4], gameGrid[8][4]);
                placePlank(gameGrid[8][4], gameGrid[8][2]);
                placePlank(gameGrid[8][2] , gameGrid[2][2]);
                movePlayerTo(gameGrid[12][4]);
                setWinTile(gameGrid[0][4]);
                break;
            case 22:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 12 || i == 6 || i == 2) ||
                                j == 2 && (i == 8 || i == 6) ||
                                j == 4 && (i == 10 || i == 8 || i == 4 || i == 2) ||
                                j == 6 && (i == 6) ||
                                j == 8 && (i == 10 || i == 6 || i == 0)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][0], gameGrid[6][0]);
                placePlank(gameGrid[6][0], gameGrid[2][0]);
                placePlank(gameGrid[4][4] , gameGrid[2][4]);
                placePlank(gameGrid[6][6], gameGrid[6][8]);
                movePlayerTo(gameGrid[12][0]);
                setWinTile(gameGrid[0][8]);
                break;
            case 23:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 10 || i == 8 || i == 4) ||
                                j == 2 && (i == 4) ||
                                j == 4 && (i == 12 || i == 10 || i == 6 || i == 2 || i == 0) ||
                                j == 6 && (i == 4) ||
                                j == 8 && (i == 10 || i == 8 || i == 4)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][4], gameGrid[10][4]);
                placePlank(gameGrid[10][4], gameGrid[6][4]);
                placePlank(gameGrid[4][2] , gameGrid[4][6]);
                movePlayerTo(gameGrid[12][4]);
                setWinTile(gameGrid[0][4]);
                break;
            case 24:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 8 || i == 4) ||
                                j == 2 && (i == 12 || i == 10 || i == 4) ||
                                j == 4 && (i == 10 || i == 8 || i == 6 || i == 2) ||
                                j == 6 && (i == 8 || i == 2 || i == 0) ||
                                j == 8 && (i == 10 || i == 6 || i == 4)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][2], gameGrid[10][2]);
                placePlank(gameGrid[10][4], gameGrid[10][8]);
                placePlank(gameGrid[4][2] , gameGrid[4][8]);
                movePlayerTo(gameGrid[12][2]);
                setWinTile(gameGrid[0][6]);
                break;
            case 25:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 2 && (i == 10 || i == 8 || i == 4 || i == 2) ||
                                j == 4 && (i == 10 || i == 6 || i == 2) ||
                                j == 6 && (i == 12 || i == 8 || i == 6 || i == 4 || i == 0)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][6], gameGrid[8][6]);
                placePlank(gameGrid[8][6], gameGrid[6][6]);
                placePlank(gameGrid[10][4] , gameGrid[6][4]);
                movePlayerTo(gameGrid[12][6]);
                setWinTile(gameGrid[0][6]);
                break;
            case 26:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 8 || i == 4) ||
                                j == 2 && (i == 10 || i == 6 || i == 2) ||
                                j == 4 && (i == 8 || i == 2 || i == 0) ||
                                j == 6 && (i == 10 || i == 4) ||
                                j == 8 && (i == 12 || i == 10 || i == 8 || i == 4)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][8], gameGrid[10][8]);
                placePlank(gameGrid[8][8], gameGrid[4][8]);
                placePlank(gameGrid[10][2] , gameGrid[6][2]);
                placePlank(gameGrid[4][0], gameGrid[4][6]);
                movePlayerTo(gameGrid[12][8]);
                setWinTile(gameGrid[0][4]);
                break;
            case 27:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 12 || i == 8 || i == 4 || i == 2) ||
                                j == 2 && (i == 10 || i == 6 || i == 0) ||
                                j == 4 && (i == 10 || i == 4) ||
                                j == 6 && (i == 8 || i == 2) ||
                                j == 8 && (i == 10 || i == 8 || i == 4 || i == 2)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][0], gameGrid[8][0]);
                placePlank(gameGrid[2][0], gameGrid[2][6]);
                placePlank(gameGrid[8][6] , gameGrid[8][8]);
                movePlayerTo(gameGrid[12][0]);
                setWinTile(gameGrid[0][2]);
                break;
            case 28:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 10 || i == 6 || i == 2) ||
                                j == 2 && (i == 12 || i == 10 || i == 8 || i == 2) ||
                                j == 4 && (i == 8 || i == 6) ||
                                j == 6 && (i == 10 || i == 4 || i == 0) ||
                                j == 8 && (i == 8 || i == 6 || i == 2)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][2], gameGrid[10][2]);
                placePlank(gameGrid[8][2], gameGrid[2][2]);
                placePlank(gameGrid[8][4] , gameGrid[8][8]);
                placePlank(gameGrid[6][4], gameGrid[6][8]);
                movePlayerTo(gameGrid[12][2]);
                setWinTile(gameGrid[0][6]);
                break;
            case 29:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 6) ||
                                j == 2 && (i == 12 || i == 10 || i == 4) ||
                                j == 4 && (i == 10 || i == 8 || i == 2) ||
                                j == 6 && (i == 10 || i == 8 || i == 6 || i == 0) ||
                                j == 8 && (i == 10 || i == 4 || i == 2)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][2], gameGrid[10][2]);
                placePlank(gameGrid[10][8], gameGrid[4][8]);
                placePlank(gameGrid[4][8] , gameGrid[2][8]);
                movePlayerTo(gameGrid[12][2]);
                setWinTile(gameGrid[0][6]);
                break;
            case 30:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 10 || i == 6 || i == 4 || i == 2) ||
                                j == 2 && (i == 12 || i == 8 || i == 2) ||
                                j == 4 && (i == 10 || i == 4) ||
                                j == 6 && (i == 6 || i == 0) ||
                                j == 8 && (i == 10 || i == 8 || i == 4)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][2], gameGrid[8][2]);
                placePlank(gameGrid[8][2], gameGrid[2][2]);
                placePlank(gameGrid[4][0] , gameGrid[2][0]);
                placePlank(gameGrid[10][4], gameGrid[10][8]);
                movePlayerTo(gameGrid[12][2]);
                setWinTile(gameGrid[0][6]);
                break;
            case 31:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 10 || i == 8 || i == 4 || i == 2) ||
                                j == 2 && (i == 12 || i == 8 || i == 6 || i == 2) ||
                                j == 4 && (i == 10 || i == 4) ||
                                j == 6 && (i == 8 || i == 2 || i == 0) ||
                                j == 8 && (i == 10 || i == 6)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][2], gameGrid[8][2]);
                placePlank(gameGrid[6][2], gameGrid[2][2]);
                placePlank(gameGrid[4][0] , gameGrid[2][0]);
                placePlank(gameGrid[8][6], gameGrid[2][6]);
                movePlayerTo(gameGrid[12][2]);
                setWinTile(gameGrid[0][6]);
                break;
            case 32:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 8 || i == 4 || i == 2) ||
                                j == 2 && (i == 10 || i == 6 || i == 4) ||
                                j == 4 && (i == 12 || i == 8 || i == 2 || i == 0) ||
                                j == 6 && (i == 10 || i == 8 || i == 4) ||
                                j == 8 && (i == 10 || i == 6 || i == 2)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][4], gameGrid[8][4]);
                placePlank(gameGrid[8][4], gameGrid[8][6]);
                placePlank(gameGrid[2][0] , gameGrid[2][4]);
                movePlayerTo(gameGrid[12][4]);
                setWinTile(gameGrid[0][4]);
                break;
            case 33:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 12 || i == 10 || i == 6 || i == 4 || i == 2) ||
                                j == 2 && (i == 8 || i == 6 || i == 2) ||
                                j == 4 && (i == 6 || i == 0) ||
                                j == 6 && (i == 10 || i == 4) ||
                                j == 8 && (i == 10 || i == 8 || i == 4)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][0], gameGrid[10][0]);
                placePlank(gameGrid[10][0], gameGrid[10][6]);
                placePlank(gameGrid[6][2] , gameGrid[6][4]);
                movePlayerTo(gameGrid[12][0]);
                setWinTile(gameGrid[0][4]);
                break;
            case 34:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 10 || i == 6) ||
                                j == 2 && (i == 6 || i == 2) ||
                                j == 4 && (i == 12 || i == 10 || i == 8 || i == 6 || i == 4 || i == 2) ||
                                j == 6 && (i == 4 || i == 0) ||
                                j == 8 && (i == 10 || i == 8 || i == 2)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][4], gameGrid[10][4]);
                placePlank(gameGrid[2][4], gameGrid[2][8]);
                placePlank(gameGrid[10][8] , gameGrid[8][8]);
                movePlayerTo(gameGrid[12][4]);
                setWinTile(gameGrid[0][6]);
                break;
            case 35:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 10 || i == 8 || i == 4 || i == 2) ||
                                j == 2 && (i == 8 || i == 6 || i == 0) ||
                                j == 4 && (i == 4) ||
                                j == 6 && (i == 12 || i == 10 || i == 8 || i == 2) ||
                                j == 8 && (i == 10 || i == 4 || i == 2)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][6], gameGrid[10][6]);
                placePlank(gameGrid[4][0], gameGrid[4][4]);
                placePlank(gameGrid[10][8] , gameGrid[4][8]);
                movePlayerTo(gameGrid[12][6]);
                setWinTile(gameGrid[0][2]);
                break;
            case 36:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 10 || i == 4) ||
                                j == 2 && (i == 8 || i == 6 || i == 0) ||
                                j == 4 && (i == 10 || i == 6 || i == 2) ||
                                j == 6 && (i == 12 || i == 6 || i == 4) ||
                                j == 8 && (i == 10 || i == 8 || i == 4 || i == 2)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][6], gameGrid[6][6]);
                placePlank(gameGrid[6][6], gameGrid[6][4]);
                placePlank(gameGrid[10][0] , gameGrid[10][4]);
                placePlank(gameGrid[10][8], gameGrid[8][8]);
                movePlayerTo(gameGrid[12][6]);
                setWinTile(gameGrid[0][2]);
                break;
            case 37:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 10 || i == 6 || i == 4) ||
                                j == 2 && (i == 10 || i == 8 || i == 4) ||
                                j == 4 && (i == 12 || i == 10 || i == 2 || i == 0) ||
                                j == 6 && (i == 8 || i == 4) ||
                                j == 8 && (i == 10 || i == 8 || i == 6 || i == 2)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][4], gameGrid[10][4]);
                placePlank(gameGrid[10][0], gameGrid[6][0]);
                placePlank(gameGrid[4][2] , gameGrid[4][6]);
                placePlank(gameGrid[8][8], gameGrid[6][8]);
                movePlayerTo(gameGrid[12][4]);
                setWinTile(gameGrid[0][4]);
                break;
            case 38:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 6 || i == 4 || i == 2) ||
                                j == 2 && (i == 12 || i == 8) ||
                                j == 4 && (i == 10 || i == 6 || i == 4 || i == 0) ||
                                j == 6 && (i == 8 || i == 2) ||
                                j == 8 && (i == 10 || i == 8 || i == 4)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][2], gameGrid[8][2]);
                placePlank(gameGrid[4][0], gameGrid[2][0]);
                placePlank(gameGrid[6][0], gameGrid[6][4]);
                placePlank(gameGrid[8][6] , gameGrid[2][6]);
                placePlank(gameGrid[10][8], gameGrid[8][8]);
                movePlayerTo(gameGrid[12][2]);
                setWinTile(gameGrid[0][4]);
                break;
            case 39:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (j == 0 && (i == 10 || i == 6) ||
                                j == 2 && (i == 10 || i == 8 || i == 2) ||
                                j == 4 && (i == 12 || i == 6 || i == 4 || i == 0) ||
                                j == 6 && (i == 10 || i == 8) ||
                                j == 8 && (i == 6 || i == 2)) {
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][4], gameGrid[6][4]);
                placePlank(gameGrid[6][0], gameGrid[6][4]);
                placePlank(gameGrid[6][4] , gameGrid[6][8]);
                placePlank(gameGrid[10][2], gameGrid[8][2]);
                placePlank(gameGrid[10][6], gameGrid[8][6]);
                movePlayerTo(gameGrid[12][4]);
                setWinTile(gameGrid[0][4]);
                break;
            case 40:
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if ((j == 0 && (i == 10 || i == 6 || i == 2) ||
                                j == 2 && (i == 12 || i == 10 || i == 4 || i == 0) ||
                                j == 4 && (i == 10 || i == 6)) ||
                                j == 6 && (i == 8 || i == 2) ||
                                j == 8 && (i == 10 || i == 6 || i == 4 || i == 2)){
                            gameGrid[i][j].setContent(GameTile.Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(GameTile.Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(GameTile.Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[10][0], gameGrid[6][0]);
                placePlank(gameGrid[12][2], gameGrid[10][2]);
                placePlank(gameGrid[8][6] , gameGrid[2][6]);
                placePlank(gameGrid[10][8], gameGrid[6][8]);
                movePlayerTo(gameGrid[12][2]);
                setWinTile(gameGrid[0][2]);
                break;
        }
    }

    /**
     * Inner class, representing a Plank object.
     * Consist of an image, orientation of the plank, size of the plank, and span of the plank.
     * Extends JButton with no border.
     */
    class Plank extends JLabel{
        private int size; // size span of the plank - number of GameTiles it spans
        private int orientation; // 0 for picked up, positive for horizontal and negative for vertical
        private GameTile[] span = new GameTile[5]; // GameTiles the plank spans over

        /**
         * Constructor. Creates a JButton with no border
         * @param size plank size
         * @param orientation plank orientation
         */
        private Plank(int size, int orientation) {
            setFocusable(false);

            this.size = size;
            this.orientation = orientation;

            if (orientation < 0) {
                setIcon(vPlankIcon);
            } else if (orientation > 0) {
                setIcon(hPlankIcon);
            }
        }

        /**
         * Constructor. Creates a placeholder plank with no size or orientation.
         */
        private Plank(){
            this(0,0);
        }

        /**
         * Get the span size of the plank
         * @return number of GameTiles the plank spans across
         */
        public int getSpanSize() {
            return size;
        }

        /**
         * Set the span size of the plank
         * @param size number of GameTiles the plank spans across
         */
        private void setSpanSize(int size) {
            this.size = size;
        }

        /**
         * Get the orientation of the plank
         * @return negative for vertical, positive for horizontal
         */
        public int getOrientation() {
            return orientation;
        }

        /**
         * Set the orientation of the plank and it's image
         * @param orientation negative for vertical, positive for horizontal
         */
        private void setOrientation(int orientation) {
            this.orientation = orientation;
            if (orientation < 0) {
                setIcon(vPlankIcon);
            } else if (orientation > 0) {
                setIcon(hPlankIcon);
            }
        }
    }


    // Load resources
    public final ImageIcon hPlankIcon = new ImageIcon(getClass().getResource("plank1.png")); // horizontal plank
    public final ImageIcon vPlankIcon = new ImageIcon(getClass().getResource("plank2.png")); // vertical plank


    public final ImageIcon vGhostPlankIcon[] = {
            new ImageIcon(getClass().getResource("ghostplank1f.png")),
            new ImageIcon(getClass().getResource("ghostplank1t.png"))
    };

    public final ImageIcon hGhostPlankIcon[] = {
            new ImageIcon(getClass().getResource("ghostplank2f.png")),
            new ImageIcon(getClass().getResource("ghostplank2t.png"))
    };
}
