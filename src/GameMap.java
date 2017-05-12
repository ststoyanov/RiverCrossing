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
    public static final int TILE_SIZE = 32; // size of a singe game tile (square) in pixels
    public static final int NUMBER_OF_ROWS = 13;
    public static final int NUMBER_OF_COLUMNS = 9;

    // Types of content the gameGrid can contain
    public enum Content{
        LAND, WATER, STUMP, PLANK
    }

    private JPanel mapPanel, plankPanel;
    public Player player;
    private ArrayList<Plank> plankList = new ArrayList<>();

    // The gameGrid held as a double array of GameTiles
    private GameTile[][] gameGrid = new GameTile[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];

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

        // plankPanel - shows the planks
        plankPanel = new JPanel(null);
        plankPanel.setBounds(0,0,NUMBER_OF_COLUMNS * TILE_SIZE,NUMBER_OF_ROWS * TILE_SIZE);
        plankPanel.setOpaque(false);
        add(plankPanel, new Integer(10));

        // playerPanel - shows the player
        player = new Player();
        player.setDirection(GameControl.Direction.UP);
        player.setSize(TILE_SIZE,TILE_SIZE);
        add(player, new Integer(20));

        // Add each tile of the gameGrid to the mapPanel
        for(int i = 0; i < NUMBER_OF_ROWS; i++)
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                gameGrid[i][j] = new GameTile(i, j);
                mapPanel.add(gameGrid[i][j]);
            }
    }

    /**
     * Load a level. Populates the gameGrid with content, predefined for each level.
     * @param level a predefined level layout
     */
    public void loadLevel(int level) {
        switch(level){
            case 1:
                for(int i = 0; i < 13; i++) {
                    for (int j = 0; j < 9; j++) {
                        if ((j == 2 && (i == 12 || i == 8 || i == 6)) || j == 6 && (i == 6 || i == 4 || i == 0)) {
                            gameGrid[i][j].setContent(Content.STUMP);
                        } else if (i == 0 || i == 12) {
                            gameGrid[i][j].setContent(Content.LAND);
                        } else {
                            gameGrid[i][j].setContent(Content.WATER);
                        }
                    }
                }
                placePlank(gameGrid[12][2],gameGrid[8][2]);
                placePlank(gameGrid[8][2],gameGrid[6][2]);
                placePlank(gameGrid[6][6],gameGrid[6][2]);
                movePlayerTo(12,2);
        }
    }

    public GameTile getNextTile(Content content, GameControl.Direction direction){
        for(GameTile i = getNextTile(direction); ; i = getNextTile(i,direction)){
            if(i.getContent() == content)
                return i;
        }
    }

    /**
     * Place a plank between stumpA and stumpB
     * @param stumpA stumpA of the plank
     * @param stumpB end of the plank
     */
    public int placePlank(GameTile stumpA, GameTile stumpB){
        if(stumpA.getContent() != Content.STUMP || stumpB.getContent() != Content.STUMP) {
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
                    gameGrid[rowA][j].setContent(Content.PLANK);
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
                    gameGrid[rowA][j].setContent(Content.PLANK);
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
                    gameGrid[i][colA].setContent(Content.PLANK);
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
                    gameGrid[i][colA].setContent(Content.PLANK);
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
     * Remove a plank, accessing it from a singe tile
     * @param plankTile tile part of the plank
     */
    public int removePlank(GameTile plankTile){
        if(plankTile.getContent() != Content.PLANK) {
            return 0;
        }

        Plank plank = plankTile.getPlankPiece();
        System.out.print(plank.size);

        for(int i = 0; i < plank.size; i++){
            plank.span[i].setContent(Content.WATER);
        }

        plankList.remove(plank);
        plankPanel.remove(plank);
        revalidate();
        repaint();

        return plank.size;
    }

    /**
     * Remove a plank, accessing it by the two stumps surrounding it
     * @param stumpA stump at one end of the plank
     * @param stumpB stump at other end of the plank
     */
    public int removePlank(GameTile stumpA, GameTile stumpB){
        if(stumpA.getContent() != Content.STUMP || stumpB.getContent() != Content.STUMP) {
            return 0;
        }

        int plankRow = (stumpA.getRow() + stumpB.getRow())/2;
        int plankCol = (stumpA.getCol()+stumpB.getCol())/2;

        return(removePlank(gameGrid[plankRow][plankCol]));
    }

    /**
     * Moves the player to a place in the gameGrid
     * @param row grid row destination
     * @param col grid column destination
     */
    public void movePlayerTo(int row, int col){
        if(gameGrid[row][col].getContent() != Content.STUMP && gameGrid[row][col].getContent() != Content.PLANK ) {
            return;
        }
        player.setLocationInGrid(row,col);
    }

    public void movePlayerTo(GameTile tile){
        movePlayerTo(tile.getRow(),tile.getCol());
    }

    public GameTile getNextTile(GameTile tile, GameControl.Direction dir){
        if(dir == GameControl.Direction.LEFT) {
            return gameGrid[tile.getRow()][tile.getCol()-1];
        } else if(dir == GameControl.Direction.RIGHT){
            return gameGrid[tile.getRow()][tile.getCol()+1];
        } else if(dir == GameControl.Direction.UP){
            return gameGrid[tile.getRow()-1][tile.getCol()];
        } else if(dir == GameControl.Direction.DOWN){
            return gameGrid[tile.getRow()+1][tile.getCol()];
        }
        return null;
    }

    public GameTile getNextTile(GameControl.Direction dir){
        return getNextTile(player.getTile(),dir);
    }

    public Content getTileContent(int row, int col){
        if(row >= 0 && row < 13 && col >= 0 && col < 9)
            return gameGrid[row][col].getContent();
        else
            return null;
    }

    public Content getTileContent(GameTile tile){
        return getTileContent(tile.getRow(),tile.getCol());
    }

    class Player extends JLabel{
        private int row;
        private int col;
        private GameControl.Direction direction;
        public int plankHeldSize = 0;

        public void setLocationInGrid(int row, int col){
            this.col = col;
            this.row = row;
            setLocation(col * TILE_SIZE,row * TILE_SIZE);
        }

        public void setLocationInGrid(GameTile tile){
            setLocationInGrid(tile.getRow(),tile.getCol());
        }

        public GameTile getTile(){
            return gameGrid[row][col];
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
            setLocation(col * TILE_SIZE,row * TILE_SIZE);
        }

        public int getRow() {
            return row;
        }
        
        public void setRow(int row) {
            this.row = row;
            setLocation(col * TILE_SIZE,row * TILE_SIZE);
        }

        public GameControl.Direction getDirection() {
            return direction;
        }

        public void setDirection(GameControl.Direction direction) {
            this.direction = direction;
            setIcon(playerIcon[direction.ordinal()]);
        }
    }

    class Plank extends JButton{
        private int size;
        private int orientation; // 0 for picked up, positive for horizontal and negative for vertical
        private GameTile[] span = new GameTile[3]; // GameTiles the plank spans over

        private Plank(int size, int orientation) {
            setBorder(BorderFactory.createEmptyBorder());
            setContentAreaFilled(false);

            this.size = size;
            this.orientation = orientation;
            if (orientation < 0) {
                setIcon(vPlankIcon);
            } else if (orientation > 0) {
                setIcon(hPlankIcon);
            }
        }

        private Plank(){
            this(0,0);
        }

        private void setSize(int size) {
            this.size = size;
        }

        private void setOrientation(int orientation) {
            this.orientation = orientation;
            if (orientation < 0) {
                setIcon(vPlankIcon);
            } else if (orientation > 0) {
                setIcon(hPlankIcon);
            }
        }
    }

    public final ImageIcon playerIcon[] = {
            new ImageIcon(getClass().getResource("manL.png")),
            new ImageIcon(getClass().getResource("manR.png")),
            new ImageIcon(getClass().getResource("manU.png")),
            new ImageIcon(getClass().getResource("manD.png"))
    };

    public final ImageIcon hPlankIcon = new ImageIcon(getClass().getResource("plank1.gif")); // horizontal plank
    public final ImageIcon vPlankIcon = new ImageIcon(getClass().getResource("plank2.gif")); // vertical plank
}
