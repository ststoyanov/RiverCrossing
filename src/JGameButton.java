import javax.swing.*;
import java.awt.*;

/**
 * Created by zabraih on 23.05.2017.
 */
public class JGameButton extends JButton {
    public JGameButton() {
      //  setFont(new Font("Wide Latin", Font.BOLD, 18));
        //setForeground(Color.white);
        setIcon(buttonIcon);
        setRolloverIcon(buttonROIcon);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setHorizontalTextPosition(JButton.CENTER);
        setVerticalTextPosition(JButton.CENTER);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusable(false);
    }

    public JGameButton(String title){
        this();
        setText(title);
    }

    private final ImageIcon buttonIcon = new ImageIcon(getClass().getResource("buttons/menubutton.png"));
    private final ImageIcon buttonROIcon = new ImageIcon(getClass().getResource("buttons/menubuttonRO.png"));
}
