import javax.swing.*;
import java.awt.*;

/**
 * A custom styled JButton.
 */
public class JGameButton extends JButton {
    /**
     * Create JButton with the custom game style
     */
    public JGameButton() {
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

    /**
     * Create JButton with the custom game style and a title
     *
     * @param title text of the JButton
     */
    public JGameButton(String title) {
        this();
        setText(title);
    }

    // load resources
    private final ImageIcon buttonIcon = new ImageIcon(getClass().getResource("buttons/menubutton.png"));
    private final ImageIcon buttonROIcon = new ImageIcon(getClass().getResource("buttons/menubuttonRO.png"));
}
