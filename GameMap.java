import javax.swing.*;
import java.awt.*;

/**
 * Created by zabraih on 04.05.2017.
 */
public class GameMap extends javax.swing.JPanel {



    private GameSquare[][] grid = new GameSquare[13][9];


    public GameMap(){
        setLayout(new GridLayout(13,9));
        loadResources();
        for(int i = 0; i < 13; i++)
            for(int j = 0; j < 9; j++)
                grid[i][j] = new GameSquare(i, j);
    }



    public void loadLevel(int level) {
        // water layout here
        switch(level){
            case 1:
                for(int i = 0; i < 13; i++)
                    for(int j = 0; j < 9; j++){
                        if((j == 2 && (i == 12||i == 8||i == 6))|| j == 6 && (i == 6||i == 4||i == 0)){
                            grid[i][j].setContent(Content.STUMP);
                            add(grid[i][j].getIconHolder());
                        }else if(i == 0 || i == 12){
                            grid[i][j].setContent(Content.LAND);
                            add(grid[i][j].getIconHolder());
                        } else {
                            grid[i][j].setContent(Content.WATER);
                            add(grid[i][j].getIconHolder());
                        }
                    }
                    addPlank(grid[12][2],grid[8][2]);
                    addPlank(grid[8][2],grid[6][2]);
                    revalidate();
                    repaint();
        }
    }

    public void addPlank(GameSquare start, GameSquare finish){
        if(start.getXPos() == finish.getXPos()){
            if(start.getYPos() > finish.getYPos()){
                for(int j = finish.getYPos()+1;j < start.getYPos() ;j++){
                    grid[start.getXPos()][j].setContent(Content.PLANK, 0);
                }
            } else {
                for(int j = start.getYPos()+1;j < finish.getYPos();j++){
                    grid[start.getXPos()][j].setContent(Content.PLANK, 0);
                }
            }
        } else if(start.getYPos() == finish.getYPos()){
            if(start.getXPos() > finish.getXPos()){
                for(int i = finish.getXPos()+1;i < start.getXPos();i++){
                    grid[i][start.getYPos()].setContent(Content.PLANK, 1);
                }
            } else {
                for(int i = start.getXPos()+1;i < finish.getXPos();i++){
                    grid[i][start.getYPos()].setContent(Content.PLANK, 1);
                }
            }
        }
    }

    private enum Content{
        LAND, WATER, STUMP, PLANK
    }

    private class GameSquare {
        private Content content;
        private int xPos;
        private int yPos;
        private JLabel iconHolder;

        public GameSquare(Content content, int xPos, int yPos) {
            this.content = content;
            this.xPos = xPos;
            this.yPos = yPos;
            iconHolder = new JLabel();
            setContent(content);
        }
        public GameSquare(int xPos, int yPos){
            this.xPos = xPos;
            this.yPos = yPos;
            iconHolder = new JLabel();
        }


        public void setContent(Content content) {
            this.content = content;
            switch (content) {
                case LAND:
                    if (xPos < 1)
                        iconHolder.setIcon(bankDownIcon);
                    else
                        iconHolder.setIcon(bankUpIcon);
                    break;
                case WATER:
                    iconHolder.setIcon(waterIcon[0]);
                    break;
                case STUMP:
                    if (xPos < 1)
                        iconHolder.setIcon(stumpUBIcon);
                    else if (xPos > 11)
                        iconHolder.setIcon(stumpDBIcon);
                    else
                        iconHolder.setIcon(stumpIcon);
                    break;
            }
        }

        public void setContent(Content plank,int orientation){
            if(plank != Content.PLANK)
                this.setContent(plank);
            else {
                this.content = Content.PLANK;
                if (orientation > 0) {
                    iconHolder.setIcon(vPlankIcon);
                } else {
                    iconHolder.setIcon(hPlankIcon);
                }
            }
        }


        public void setXPos(int xPos) {
            this.xPos = xPos;
        }

        public void setYPos(int yPos) {
            this.yPos = yPos;
        }

        public int getXPos() {
            return xPos;
        }

        public int getYPos() {
            return yPos;
        }

        public JLabel getIconHolder() {
            return iconHolder;
        }
    }

    // load resources
    public ImageIcon bankUpIcon;
    public ImageIcon bankDownIcon;
    public ImageIcon[] waterIcon = new ImageIcon[4];
    public ImageIcon stumpIcon;
    public ImageIcon stumpDBIcon;
    public ImageIcon stumpUBIcon;
    public ImageIcon vPlankIcon;
    public ImageIcon hPlankIcon;
    public ImageIcon p_stumpIcon;
    public ImageIcon p_stumpDBIcon;
    public ImageIcon p_stumpUPIcon;
    public ImageIcon p_hPlankIcon;
    public ImageIcon p_vPlankIcon;

    private void loadResources() {
        try {
            bankUpIcon = new ImageIcon(getClass().getResource("bank1.jpg"));
            bankDownIcon = new ImageIcon(getClass().getResource("bank2.jpg"));

            waterIcon[0] = new ImageIcon(getClass().getResource("water1.jpg"));
            waterIcon[1] = new ImageIcon(getClass().getResource("water2.jpg"));
            waterIcon[2] = new ImageIcon(getClass().getResource("water3.jpg"));
            waterIcon[3] = new ImageIcon(getClass().getResource("water4.jpg"));

            stumpIcon = new ImageIcon(getClass().getResource("stump1.jpg"));
            stumpDBIcon = new ImageIcon(getClass().getResource("stump2.jpg")); // downside bank stump
            stumpUBIcon = new ImageIcon(getClass().getResource("stump3.jpg")); // upper bank stump

            hPlankIcon = new ImageIcon(getClass().getResource("plank1.jpg")); // horizontal plank
            vPlankIcon = new ImageIcon(getClass().getResource("plank2.jpg")); // vertical plank

            // fields with player on them
            p_stumpIcon = new ImageIcon(getClass().getResource("stump1_man.jpg"));
            p_stumpDBIcon = new ImageIcon(getClass().getResource("stump2_man.jpg")); // downside bank stump
            p_stumpUPIcon = new ImageIcon(getClass().getResource("stump3_man.jpg")); // upper bank stump

            p_hPlankIcon = new ImageIcon(getClass().getResource("plank1_man.jpg")); // horizontal plank
            p_vPlankIcon = new ImageIcon(getClass().getResource("plank2_man.jpg")); // vertical plank
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
