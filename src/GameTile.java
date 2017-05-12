import javax.swing.*;
import java.util.Random;

/**
 * This class contains and controls a single tile of the game grid.
 */
public class GameTile extends JButton {
    private Content content; // type of content the tile holds
    private int row;
    private int col;
    private GameMap.Plank plankPiece = null; // index of the plank placed on this field, -1 if no plank

    // Types of content the gameGrid can contain
    public enum Content{
        LAND, WATER, STUMP, PLANK
    }
    
    /**
     * Constructor. Creates the GameTile with set coordinates and content.
     * @param content type of content the tile holds
     * @param row row position of the tile
     * @param col column position of the tile
     */
    public GameTile(Content content, int row, int col) {
        setBorder(BorderFactory.createEmptyBorder());
        setFocusable(false);

        this.content = content;
        this.row = row;
        this.col = col;
        if(content != null)
            setContent(content);
    }

    /**
     * Constructor. Creates the GameTile with set coordinates and no content.
     * @param row row position of the tile
     * @param col column position of the tile
     */
    public GameTile(int row, int col){
        this(null,row,col);
    }

    /**
     * Get the index of the plank contained.
     * @return plankPiece the plank present in the tile
     */
    public GameMap.Plank getPlankPiece() {
        return plankPiece;
    }

    /**
     * Set the index of the plank held by this tile.
     * @param plank plank present in the tile
     */
    public void setPlankPiece(GameMap.Plank plank) {
        this.plankPiece = plank;
    }

    /**
     * Get the row position of the tile
     * @return row position of the tile
     */
    public int getRow() {
        return row;
    }

    /**
     * Set the row positon of the tile
     * @param row row position of the tile
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Get the column position of the tile
     * @return column position of the tile
     */
    public int getCol() {
        return col;
    }

    /**
     * Set the column positon of the tile
     * @param col column position of the tile
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Get the current type of content of the tile
     * @return content of the tile
     */
    public Content getContent() {
        return content;
    }

    /**
     * Sets the content of the tile and loads its image.
     * @param content new tile content type
     */
    public void setContent(Content content) {
        switch (content) {
            case LAND:
                if (row < 1)
                    setIcon(bankDownIcon);
                else
                    setIcon(bankUpIcon);
                break;
            case WATER:
                // allow the conent to change between plank and water without changing the image
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
        }
        this.content = content;
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


}
