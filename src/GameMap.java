import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

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

    private class Plank extends JLabel{
        private int size;
        private int orientation; // 0 for picked up, positive for horizontal and negative for vertical
        private GameTile[] span = new GameTile[3]; // GameTiles the plank spans over

        private Plank(int size, int orientation) {
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

        public void setSize(int size) {
            this.size = size;
        }

        public void setOrientation(int orientation) {
            this.orientation = orientation;
            if (orientation < 0) {
                setIcon(vPlankIcon);
            } else if (orientation > 0) {
                setIcon(hPlankIcon);
            }
        }
    }

    private JPanel mapPanel, plankPanel;
    private JLabel player;
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
        player = new JLabel(playerIcon);
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
                addPlank(gameGrid[12][2],gameGrid[8][2]);
                addPlank(gameGrid[8][2],gameGrid[6][2]);
                addPlank(gameGrid[6][6],gameGrid[6][2]);
                movePlayerTo(12,2);
        }
    }

    /**
     * Place a plank between stumpA and stumpB
     * @param stumpA stumpA of the plank
     * @param stumpB end of the plank
     */
    public void addPlank(GameTile stumpA, GameTile stumpB){
        if(stumpA.content != Content.STUMP || stumpB.content != Content.STUMP) {
            return;
        }

        Plank plank = new Plank(); // temp plank object
        int size = 0; // temp size

        plankPanel.add(plank); // add the plank to tha view
        plankList.add(plank); // add the plank to the plankList

        // check if stumpA and stumpB are in the same row
        if(stumpA.getRow() == stumpB.getRow()){
            // if so set orientation to 1 (horizontal)
            plank.setOrientation(1);

            // check if B comes before A
            if(stumpA.getCol() > stumpB.getCol()){
                // set the content of all tiles between A and B to PLANK
                // add the tiles to the span of the plank
                // also add the index of the plank to the tile
                for(int j = stumpB.getCol()+1;j < stumpA.getCol() ;j++){
                    gameGrid[stumpA.getRow()][j].setContent(Content.PLANK);
                    gameGrid[stumpA.getRow()][j].setPlankIndex(plankList.indexOf(plank));
                    plank.span[size] = gameGrid[stumpA.getRow()][j];
                    size++;
                }

                // set the plank's size and position
                plank.size = size;
                plank.setBounds(
                        (stumpB.getCol()+1)*TILE_SIZE,stumpA.getRow()*TILE_SIZE,
                        size*TILE_SIZE,TILE_SIZE
                );
            } else if(stumpB.getCol() > stumpA.getCol()) { // check if A comes before B and do the same as above
                for(int j = stumpA.getCol()+1; j < stumpB.getCol(); j++){
                    gameGrid[stumpA.getRow()][j].setContent(Content.PLANK);
                    gameGrid[stumpA.getRow()][j].setPlankIndex(plankList.indexOf(plank));
                    plank.span[size] = gameGrid[stumpA.getRow()][j];
                    size++;
                }

                plank.size = size;
                plank.setBounds(
                        (stumpA.getCol()+1)*TILE_SIZE,stumpA.getRow()*TILE_SIZE,
                        size*TILE_SIZE,TILE_SIZE
                );
            }
        } else if(stumpA.getCol() == stumpB.getCol()){ // check if stumpA and stumpB are in the same column and do the same as above
            plank.setOrientation(-1);

            if(stumpA.getRow() > stumpB.getRow()){
                for(int i = stumpB.getRow()+1;i < stumpA.getRow();i++){
                    gameGrid[i][stumpA.getCol()].setContent(Content.PLANK);
                    gameGrid[i][stumpA.getCol()].setPlankIndex(plankList.indexOf(plank));
                    plank.span[size] = gameGrid[i][stumpA.getCol()];
                    size++;
                }

                plank.setBounds(
                        stumpA.getCol()*TILE_SIZE,(stumpB.getRow()+1)*TILE_SIZE,
                        TILE_SIZE,size * TILE_SIZE
                );

                plankPanel.add( plankList.get(plankList.size()-1));
            } else if (stumpB.getRow() > stumpA.getRow()){
                for(int i = stumpA.getRow()+1;i < stumpB.getRow();i++) {
                    gameGrid[i][stumpA.getCol()].setContent(Content.PLANK);
                    gameGrid[i][stumpA.getCol()].setPlankIndex(plankList.indexOf(plank));
                    plank.span[size] = gameGrid[i][stumpA.getCol()];
                    size++;
                }

                plank.size = size;
                plank.setBounds(
                        stumpA.getCol()*TILE_SIZE,(stumpA.getRow()+1)*TILE_SIZE,
                        TILE_SIZE,size*TILE_SIZE
                );
            }
        }
    }

    /**
     * Remove a plank, accessing it from a singe tile
     * @param plankTile tile part of the plank
     */
    public void removePlank(GameTile plankTile){
        if(plankTile.content != Content.PLANK) {
            return;
        }

        Plank plank = plankList.get(plankTile.plankIndex);

        for(int i = 0; i < plank.size; i++){
            plankTile.setContent(Content.WATER);
        }

        plankList.remove(plank);
        plankPanel.remove(plank);
    }

    /**
     * Remove a plank, accessing it by the two stumps surrounding it
     * @param stumpA stump at one end of the plank
     * @param stumpB stump at other end of the plank
     */
    public void removePlank(GameTile stumpA, GameTile stumpB){
        if(stumpA.content != Content.STUMP || stumpB.content != Content.STUMP) {
            return;
        }

        int plankRow = (stumpA.getRow() + stumpB.getRow())/2;
        int plankCol = (stumpA.getCol()+stumpB.getCol())/2;

        removePlank(gameGrid[plankRow][plankCol]);
    }

    /**
     * Moves the player to a place in the gameGrid
     * @param row grid row destination
     * @param col grid column destination
     */
    public void movePlayerTo(int row, int col){
        player.setLocation(col * TILE_SIZE,row * TILE_SIZE);
    }

    /**
     * This class contains and controls a single tile of the game grid.
     */
    private class GameTile extends JLabel{
        private Content content; // type of content the tile holds
        private int row;
        private int col;
        private int plankIndex = -1; // index of the plank placed on this field, -1 if no plank

        /**
         * Constructor. Creates the GameTile with set coordinates and content.
         * @param content type of content the tile holds
         * @param row row position of the tile
         * @param col column position of the tile
         */
        private GameTile(Content content, int row, int col) {
            this.content = content;
            this.row = row;
            this.col = col;
            setContent(content);
        }

        /**
         * Constructor. Creates the GameTile with set coordinates and no content.
         * @param row row position of the tile
         * @param col column position of the tile
         */
        private GameTile(int row, int col){
            this.row = row;
            this.col = col;
        }

        /**
         * Sets the content of the tile.
         * @param content new tile content type
         */
        private void setContent(Content content) {
            switch (content) {
                case LAND:
                    if (row < 1)
                        setIcon(bankDownIcon);
                    else
                        setIcon(bankUpIcon);
                    break;
                case WATER:
                    if(this.content != Content.PLANK) {
                        Random rand = new Random();
                        int randW = rand.nextInt(40);
                        if(randW > 3) randW = 0;
                        setIcon(waterIcon[randW]);
                    }
                    break;
                case STUMP:
                    if (row < 1)
                        setIcon(stumpUPIcon);
                    else if (row > 11)
                        setIcon(stumpDBIcon);
                    else
                        setIcon(stumpIcon);
                    break;
                case PLANK:
                    if(this.content != Content.WATER) {
                        Random rand = new Random();
                        int randW = rand.nextInt(40);
                        if(randW > 3) randW = 0;
                        setIcon(waterIcon[randW]);
                    }
                    break;
            }

            this.content = content;
        }

        /**
         * Get the index of the plank contained. Return -1 if no plank contained.
         * @return plankIndex index of the plank in the plankList
         */
        public int getPlankIndex() {
            return plankIndex;
        }

        /**
         * Set the index of the plank held by this tile. Set to -1 if no plank to be contained.
         * @param plankIndex index of the plank in the plankList
         */
        public void setPlankIndex(int plankIndex) {
            this.plankIndex = plankIndex;
        }

        /**
         * Get the row position of the tile
         * @return row position of the tile
         */
        public int getRow() {
            return row;
        }

        /**
         * Get the column position of the tile
         * @return column position of the tile
         */
        public int getCol() {
            return col;
        }

        /**
         * Get the current type of content of the tile
         * @return content of the tile
         */
        public Content getContent() {
            return content;
        }
    }

    // Code for loading the resources for the rest of the class
    public final ImageIcon bankUpIcon = new ImageIcon(getClass().getResource("bank1.jpg"));
    public final ImageIcon bankDownIcon = new ImageIcon(getClass().getResource("bank2.jpg"));

    public final ImageIcon waterIcon[] = {
            new ImageIcon(getClass().getResource("water1.jpg")),
            new ImageIcon(getClass().getResource("water2.jpg")),
            new ImageIcon(getClass().getResource("water3.jpg")),
            new ImageIcon(getClass().getResource("water4.jpg"))
    };

    public final ImageIcon stumpIcon = new ImageIcon(getClass().getResource("stump1.jpg"));
    public final ImageIcon stumpDBIcon = new ImageIcon(getClass().getResource("stump2.jpg")); // downside bank stump
    public final ImageIcon stumpUPIcon = new ImageIcon(getClass().getResource("stump3.jpg")); // upper bank stump

    public final ImageIcon hPlankIcon = new ImageIcon(getClass().getResource("plank1.gif")); // horizontal plank
    public final ImageIcon vPlankIcon = new ImageIcon(getClass().getResource("plank2.gif")); // vertical plank

    public final ImageIcon playerIcon = new ImageIcon(getClass().getResource("man.gif"));
}
