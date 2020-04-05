package Dialogs;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class EncoderChooser extends JDialog {
    private static final int FORM_HEIGHT = 200;
    private static final int FORM_WIDTH = 235;
    public JButton applyButton;
    public JComboBox comboBox;

    public EncoderChooser(ArrayList<String> pluginsList) {
        super((Dialog) null, "Choose encoder", true);
        setBounds(300, 300, FORM_WIDTH, FORM_HEIGHT);

        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel mainLayout = new JPanel();
        mainLayout.setLayout(null);

        JLabel label = new JLabel("Encryption methods");
        label.setLocation(10, 10);
        label.setSize(200, 20);
        mainLayout.add(label);

        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel<String>();
        comboBoxModel.addAll(pluginsList);
        comboBox = new JComboBox(comboBoxModel);
        comboBox.setLocation(10, 40);
        comboBox.setSize(200, 40);
        if (pluginsList.size() > 0) {
            comboBox.setSelectedIndex(0);
        }
        mainLayout.add(comboBox);

        applyButton = new JButton("Apply");
        applyButton.setLocation(10, 110);
        applyButton.setSize(200, 35);
        applyButton.addActionListener(actionEvent -> {
            this.setVisible(!this.isVisible());
        });
        mainLayout.add(applyButton);


        add(mainLayout);
    }
}
