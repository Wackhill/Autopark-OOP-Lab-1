import javax.swing.*;
import java.awt.*;

class GUIClass extends JFrame {
    static final Color BASIC_LIST_ITEM_COLOR = Color.decode("#f6f6f6");
    static final Color SELECTED_LIST_ITEM_COLOR = Color.decode("#00a64b");
    static final int FORM_HEIGHT = 715;
    static final int FORM_WIDTH = 1300;
    static final int TABLE_HEIGHT = 300;
    static final int TABLE_WIDTH = 1000;
    static final int CELL_HEIGHT = 35;
    static final int CELL_WIDTH = 160;
    static final int MARGIN_LEFT = 10;
    static final int MARGIN_TOP = 10;
    static final int EDIT_FIELD_HEIGHT = 25;
    static final int EDIT_FIELD_WIDTH = 300;
    static final int LABEL_HEIGHT = 25;
    static final int LABEL_WIDTH = 150;
    JPanel mainLayout;

    GUIClass() {
        super("Autopark");
        setBounds(100, 10, FORM_WIDTH, FORM_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainLayout = new JPanel();
        mainLayout.setLayout(null);
        add(mainLayout);
    }
}