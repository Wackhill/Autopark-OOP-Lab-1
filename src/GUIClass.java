import javax.swing.*;
import java.awt.*;

public class GUIClass extends JFrame {
    public static final Color BASIC_BUTTON_COLOR = Color.decode("#f6f6f6");
    public static final Color SELECTED_BUTTON_COLOR = Color.decode("#00a64b");
    JPanel mainLayout;

    GUIClass() {
        super("Autopark");
        setBounds(300, 50, 700, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainLayout = new JPanel();
        mainLayout.setLayout(null);
        add(mainLayout);
    }
}

