import javax.swing.*;
import java.awt.*;

class GUIClass extends JFrame {
    static final Color BASIC_BUTTON_COLOR = Color.decode("#f6f6f6");
    static final Color SELECTED_BUTTON_COLOR = Color.decode("#00a64b");
    static final int BUTTON_HEIGHT = 35;
    static final int BUTTON_WIDTH = 150;
    static final int MARGIN_LEFT = 10;
    static final int MARGIN_TOP = 10;
    JPanel mainLayout;

    GUIClass() {
        super("Autopark");
        setBounds(100, 50, 1200, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainLayout = new JPanel();
        mainLayout.setLayout(null);
        add(mainLayout);
    }
}

