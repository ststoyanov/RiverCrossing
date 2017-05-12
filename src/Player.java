import javax.swing.*;

/**
 * Class representing a Player object.
 * Consist of an image, direction the player is facing, tile position and size of the plank they are holding.
 * Constructors are inherited from JLabel.
 */
public class Player extends JLabel {
    private GameTile tile; // GameTile position of the player
    private GameControl.Direction direction; // direction the player is facing
    private int plankHeldSize = 0; // size of the plank the player is holding

    /**
     * Get tile the player is positioned in
     * @return tile of the player
     */
    public GameTile getTile() {
        return tile;
    }

    /**
     * Move the player to a tile
     * @param tile tile destination
     */
    public void setTile(GameTile tile){
        this.tile = tile;
        setLocation(tile.getCol() * GameMap.TILE_SIZE,tile.getRow() * GameMap.TILE_SIZE);
    }

    /**
     * Get direction the player is facing
     * @return direction the player is facing
     */
    public GameControl.Direction getDirection() {
        return direction;
    }

    /**
     * Set direction for the player to face
     * @param direction direction for the player to face
     */
    public void setDirection(GameControl.Direction direction) {
        this.direction = direction;
        setIcon(playerIcon[direction.ordinal()]);
    }

    /**
     * Get the size of the plank the player is holding
     * @return size of the plank the player is holding
     */
    public int getPlankHeldSize() {
        return plankHeldSize;
    }

    /**
     * Set the size of the plank the player is holding
     * @param plankHeldSize size of the plank the player is holding
     */
    public void setPlankHeldSize(int plankHeldSize) {
        this.plankHeldSize = plankHeldSize;
    }

    // Load resources
    public final ImageIcon playerIcon[] = {
            new ImageIcon(getClass().getResource("manL.png")),
            new ImageIcon(getClass().getResource("manR.png")),
            new ImageIcon(getClass().getResource("manU.png")),
            new ImageIcon(getClass().getResource("manD.png"))
    };
}