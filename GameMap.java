import javax.swing.*;
import java.awt.*;

/**
 * Created by zabraih on 04.05.2017.
 */
public class GameMap extends javax.swing.JPanel {



    private GameSquare[][] grid = new GameSquare[13][9];

    public GameMap(){
        setLayout(new GridLayout(13,9));
    }

    public void loadLevel(int level) {
        // water layout here

        switch(level){
            case 1:
                for(int i = 0; i < 13; i++)
                    for(int j = 0; j < 9; j++){
                        if(i == 0){
                            grid[i][j] = new GameSquare(Content.LAND,i * 32 + 16, j * 32 + 16);
                            add(grid[i][j].getIconHolder());
                        } else if(i == 12){
                            grid[i][j] = new GameSquare(Content.LAND,i * 32 + 16, j * 32 + 16);
                            add(grid[i][j].getIconHolder());
                        } else if(j == 3 && (i == 13||i == 9||i == 7)){
                            grid[i][j] = new GameSquare(Content.STUMP, i * 32 + 16, j * 32 + 16);
                            add(grid[i][j].getIconHolder());
                        }

                        else{
                            grid[i][j] = new GameSquare(Content.WATER, i * 32 + 16, j * 32 + 16);
                            add(grid[i][j].getIconHolder());
                        }
                    }


        }
    }

    private enum Content{
        LAND,WATER,STUMP,PLANK
    }

    private class GameSquare {
        private Content content;
        private int xPos;
        private int yPos;
        private JLabel iconHolder;

        GameSquare(Content content, int xPos, int yPos){
            this.content = content;
            this.xPos = xPos;
            this.yPos = yPos;
            iconHolder = new JLabel();
            switch (content) {
                case LAND:
                    if(xPos<100)
                        iconHolder.setIcon(new ImageIcon(getClass().getResource("bank2.jpg")));
                    else
                        iconHolder.setIcon(new ImageIcon(getClass().getResource("bank1.jpg")));
                    break;
                case WATER:
                    iconHolder.setIcon(new ImageIcon(getClass().getResource("water1.jpg")));
                    break;
                case STUMP:
                    iconHolder.setIcon(new ImageIcon(getClass().getResource("stump1.jpg")));
                    break;
                case PLANK:
                    iconHolder.setIcon(new ImageIcon(getClass().getResource("plank2.jpg")));
                    break;
            }
        }

        public void setContent(Content content) {
            this.content = content;
        }

        public void setXPos(int xPos) {
            this.xPos = xPos;
        }

        public void setYPos(int yPos) {
            this.yPos = yPos;
        }

        public JLabel getIconHolder() {
            return iconHolder;
        }
    }
}
