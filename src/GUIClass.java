import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Constructor;

class GUIClass extends JFrame {
    static final Color BASIC_LIST_ITEM_COLOR = Color.decode("#f6f6f6");
    static final Color SELECTED_LIST_ITEM_COLOR = Color.decode("#00a64b");
    static final Color REMOVE_BUTTON_COLOR = Color.decode("#ab4f50");
    static final Color EDIT_BUTTON_COLOR = Color.decode("#019ade");
    static final Color APPLY_BUTTON_COLOR = Color.decode("#fdb813");
    static final Color ADD_BUTTON_COLOR = Color.decode("#00b5c3");
    static final int FORM_HEIGHT = 700;
    static final int FORM_WIDTH = 1110;
    static final int TABLE_HEIGHT = 300;
    static final int TABLE_WIDTH = 900;
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