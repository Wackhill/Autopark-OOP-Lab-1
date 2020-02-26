import javax.swing.*;

public class GUIClass extends JFrame {
    public JPanel mainLayout;

    public GUIClass() {
        super("Autopark");
        setBounds(300, 50, 500, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainLayout = new JPanel();
        mainLayout.setLayout(null);
        add(mainLayout);
    }
}

