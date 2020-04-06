import Dialogs.EncoderChooser;
import SafeSerializers.SafeBinarySerializer;
import SafeSerializers.SafeJsonSerializer;
import SafeSerializers.SafeTextSerializer;
import Serializators.BinarySerializer;
import Serializators.JsonSerializer;
import Serializators.TextSerializer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

public class Autopark {
    private static final Class OBJECTS_SOURCE_CLASS = AutoparkResources.class;
    private static Object[] resourcesLists;     //Тут лежат непосредственно листы, в которое можно что-то добавить
    private static ArrayList<Field> mainObjectsList;
    private static ArrayList<Field>[] fieldsList;
    private static ArrayList<Field>[] objContainingFields;
    private static String objectsDirPath = null;

    //GUI
    private static GUIClass guiClass;
    private static JButton addButton = null;
    private static JScrollPane currentTable = null;
    private static JTable justTable;
    private static ArrayList<Field> activeList = null;
    private static JLabel[] editableFieldsNames = null;
    private static JTextField[] editableObjectFields = null;
    private static boolean isEditing = false;

    JFileChooser directoryChooser;
    String chosenDirectory = "";

    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        javax.swing.SwingUtilities.invokeLater(() -> {
            guiClass = new GUIClass();
            guiClass.setVisible(true);
        });

        mainObjectsList = getMainObjects(new ArrayList<>());
        fieldsList = getFieldsList(mainObjectsList, true);
        resourcesLists = new Object[mainObjectsList.size()];
        for (int i = 0; i < resourcesLists.length; i++) {
            resourcesLists[i] = AutoparkResources.class.getDeclaredFields()[i].getType().getDeclaredConstructor().newInstance();
        }

        /*
        for (int i = 0; i < fieldsList.length; i++) {
            for (int j = 0; j < fieldsList[i].size(); j++) {
                System.out.println(fieldsList[i].get(j) + "   ");
            }
            System.out.println("");
        }
         */

        JList mainObjectChooser = makeMainObjectsList(mainObjectsList);
        guiClass.mainLayout.add(mainObjectChooser);
        mainObjectChooser.setSelectedIndex(0);

        currentTable = generateTable(fieldsList[0], 0);
        guiClass.mainLayout.add(currentTable);
        generateEditFields(0);
        guiClass.repaint();

        //////////
        JButton loadButton = new JButton("Load");
        loadButton.setLocation(GUIClass.MARGIN_LEFT, GUIClass.TABLE_HEIGHT - 3 * GUIClass.CELL_HEIGHT - GUIClass.MARGIN_TOP);
        loadButton.setSize(GUIClass.CELL_WIDTH / 2 - 5, GUIClass.CELL_HEIGHT);
        guiClass.mainLayout.add(loadButton);

        loadButton.addActionListener(actionEvent -> {
            Class<?>[] availablePlugins = null;
            try {
                availablePlugins = getClassesArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<String> pluginsList = new ArrayList<>();

            for (int i = 0; i < availablePlugins.length; i++) {
                pluginsList.add(availablePlugins[i].toString().substring(availablePlugins[i].toString().indexOf('.') + 1));
            }

            //EncoderChooser encoderChooser = new EncoderChooser(pluginsList);
            //encoderChooser.setVisible(true);

            //int selectedPluginNumber = encoderChooser.comboBox.getSelectedIndex();

            //if (selectedPluginNumber != -1) {
                JFileChooser fileChooser = new JFileChooser("D:\\ObjectStore");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Binary file", "bin"));
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON file", "json"));
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Text file", "txt"));
                int retCode = fileChooser.showDialog(null, "Выбрать файл");
                if (retCode == JFileChooser.APPROVE_OPTION) {
                    File chosenFile = fileChooser.getSelectedFile();
                    objectsDirPath = chosenFile.getPath();
                    String folderName = objectsDirPath.substring(objectsDirPath.lastIndexOf("\\") + 1);
                    boolean found = false;
                    int id = 0;
                    for (int i = 0; i < pluginsList.size(); i++) {
                        if (pluginsList.get(i).contains(folderName)) {
                            found = true;
                            id = i;
                            break;
                        }
                    }
                    if (found) {
                        if (objectsDirPath != null) {
                            String extensionConstruction = String.valueOf(fileChooser.getFileFilter());
                            String extension = extensionConstruction.substring(extensionConstruction.indexOf("=[") + 2, extensionConstruction.length() - 2);

                            switch (extension) {
                                case ("json"):
                                    for (int i = 0; i < resourcesLists.length; i++) {
                                        try {
                                            //JsonSerializer jsonSerializer = new JsonSerializer(resourcesLists[i]);
                                            //resourcesLists[i] = jsonSerializer.deserialize(objectsDirPath + "\\" + mainObjectsList.get(i).getName() + ".json");
                                            SafeJsonSerializer jsonSerializer = new SafeJsonSerializer(resourcesLists[i], availablePlugins[id]);
                                            resourcesLists[i] = jsonSerializer.deserialize(objectsDirPath + "\\" + mainObjectsList.get(i).getName() + ".json");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    break;
                                case ("bin"):
                                    for (int i = 0; i < resourcesLists.length; i++) {
                                        try {
                                            //BinarySerializer binarySerializer = new BinarySerializer(resourcesLists[i]);
                                            //resourcesLists[i] = binarySerializer.deserialize(objectsDirPath + "\\" + mainObjectsList.get(i).getName() + ".bin");
                                            SafeBinarySerializer binarySerializer = new SafeBinarySerializer(resourcesLists[i], availablePlugins[id]);
                                            resourcesLists[i] = binarySerializer.deserialize(objectsDirPath + "\\" + mainObjectsList.get(i).getName() + ".bin");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    break;
                                case ("txt"):
                                default:
                                    for (int i = 0; i < resourcesLists.length; i++) {
                                        try {
                                            //TextSerializer textSerializer = new TextSerializer(resourcesLists[i], mainObjectsList.get(i));
                                            //resourcesLists[i] = textSerializer.deserialize(objectsDirPath + "\\" + mainObjectsList.get(i).getName() + ".txt");
                                            SafeTextSerializer textSerializer = new SafeTextSerializer(resourcesLists[i], mainObjectsList.get(i), availablePlugins[id]);
                                            resourcesLists[i] = textSerializer.deserialize(objectsDirPath + "\\" + mainObjectsList.get(i).getName() + ".txt");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    break;
                            }

                            if (currentTable != null) {
                                currentTable.setVisible(false);
                            }
                            try {
                                currentTable = generateTable(fieldsList[0], 0);
                            } catch (NoSuchMethodException | InvocationTargetException | NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            guiClass.mainLayout.add(currentTable);
                            guiClass.repaint();

                        }
                    }
                    else {
                        System.out.print("Lol u kiddin'");
                    }

                    //System.out.println(objectsDirPath);
                }
            //}

        });

        JButton saveButton = new JButton("Save");
        saveButton.setLocation(GUIClass.MARGIN_LEFT + GUIClass.CELL_WIDTH / 2, GUIClass.TABLE_HEIGHT - 3 * GUIClass.CELL_HEIGHT - GUIClass.MARGIN_TOP);
        saveButton.setSize(GUIClass.CELL_WIDTH / 2, GUIClass.CELL_HEIGHT);
        guiClass.mainLayout.add(saveButton);

        saveButton.addActionListener(actionEvent -> {
            Class<?>[] availablePlugins = null;
            try {
                availablePlugins = getClassesArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<String> pluginsList = new ArrayList<>();

            for (int i = 0; i < availablePlugins.length; i++) {
                pluginsList.add(availablePlugins[i].toString().substring(availablePlugins[i].toString().indexOf('.') + 1));
            }

            EncoderChooser encoderChooser = new EncoderChooser(pluginsList);
            encoderChooser.setVisible(true);

            int selectedPluginNumber = encoderChooser.comboBox.getSelectedIndex();

            if (selectedPluginNumber != -1) {
                JFileChooser fileChooser = new JFileChooser("D:\\ObjectStore");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Binary file","bin"));
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON file","json"));
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Text file","txt"));

                int retCode = fileChooser.showDialog(null, "Выбрать файл");
                if (retCode == JFileChooser.APPROVE_OPTION) {
                    File chosenFile = fileChooser.getSelectedFile();
                    objectsDirPath = chosenFile.getPath();
                    if (objectsDirPath != null) {
                        String extensionConstruction = String.valueOf(fileChooser.getFileFilter());
                        String extension = extensionConstruction.substring(extensionConstruction.indexOf("=[") + 2, extensionConstruction.length() - 2);

                        switch (extension) {
                            case ("json"):
                                for (int i = 0; i < resourcesLists.length; i++) {
                                    try {
                                        //JsonSerializer jsonSerializer = new JsonSerializer(resourcesLists[i]);
                                        //jsonSerializer.serialize(objectsDirPath + "\\" + mainObjectsList.get(i).getName() + ".json");
                                        SafeJsonSerializer jsonSerializer = new SafeJsonSerializer(resourcesLists[i], availablePlugins[selectedPluginNumber]);
                                        if (!(new File(objectsDirPath + "\\" + pluginsList.get(selectedPluginNumber)).exists())) {
                                            new File(objectsDirPath + "\\" + pluginsList.get(selectedPluginNumber)).mkdir();
                                        }
                                        jsonSerializer.serialize(objectsDirPath + "\\" + pluginsList.get(selectedPluginNumber) + "\\" + mainObjectsList.get(i).getName() + ".json");
                                    } catch (IOException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            case ("bin"):
                                for (int i = 0; i < resourcesLists.length; i++) {
                                    try {
                                        //BinarySerializer binarySerializer = new BinarySerializer(resourcesLists[i]);
                                        //binarySerializer.serialize(objectsDirPath + "\\" + mainObjectsList.get(i).getName() + ".bin");
                                        SafeBinarySerializer binarySerializer = new SafeBinarySerializer(resourcesLists[i], availablePlugins[selectedPluginNumber]);
                                        if (!(new File(objectsDirPath + "\\" + pluginsList.get(selectedPluginNumber)).exists())) {
                                            new File(objectsDirPath + "\\" + pluginsList.get(selectedPluginNumber)).mkdir();
                                        }
                                        binarySerializer.serialize(objectsDirPath + "\\" + pluginsList.get(selectedPluginNumber) + "\\" + mainObjectsList.get(i).getName() + ".bin");
                                    } catch (IOException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            case ("txt"):
                            default:
                                for (int i = 0; i < resourcesLists.length; i++) {
                                    try {
                                        //TextSerializer textSerializer = new TextSerializer(resourcesLists[i], mainObjectsList.get(i));
                                        //textSerializer.serialize(objectsDirPath + "\\" + mainObjectsList.get(i).getName() + ".txt");
                                        SafeTextSerializer textSerializer = new SafeTextSerializer(resourcesLists[i], mainObjectsList.get(i), availablePlugins[selectedPluginNumber]);
                                        if (!(new File(objectsDirPath + "\\" + pluginsList.get(selectedPluginNumber)).exists())) {
                                            new File(objectsDirPath + "\\" + pluginsList.get(selectedPluginNumber)).mkdir();
                                        }
                                        textSerializer.serialize(objectsDirPath + "\\" + pluginsList.get(selectedPluginNumber) + "\\" + mainObjectsList.get(i).getName() + ".txt");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                        }
                    }
                }
            }
            else {
                JOptionPane.showMessageDialog(guiClass, "Encryption plugin is not selected!");
            }

            /*
            JFileChooser fileChooser = new JFileChooser("D:\\ObjectStore");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Binary file","bin"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON file","json"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Text file","txt"));

            int retCode = fileChooser.showDialog(null, "Выбрать файл");
            if (retCode == JFileChooser.APPROVE_OPTION) {
                File chosenFile = fileChooser.getSelectedFile();
                objectsDirPath = chosenFile.getPath();
                if (objectsDirPath != null) {
                    String extensionConstruction = String.valueOf(fileChooser.getFileFilter());
                    String extension = extensionConstruction.substring(extensionConstruction.indexOf("=[") + 2, extensionConstruction.length() - 2);

                    switch (extension) {
                        case ("json"):
                            for (int i = 0; i < resourcesLists.length; i++) {
                                try {
                                    JsonSerializer jsonSerializer = new JsonSerializer(resourcesLists[i]);
                                    jsonSerializer.serialize(objectsDirPath + "\\" + mainObjectsList.get(i).getName() + ".json");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case ("bin"):
                            for (int i = 0; i < resourcesLists.length; i++) {
                                try {
                                    BinarySerializer binarySerializer = new BinarySerializer(resourcesLists[i]);
                                    binarySerializer.serialize(objectsDirPath + "\\" + mainObjectsList.get(i).getName() + ".bin");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case ("txt"):
                        default:
                            for (int i = 0; i < resourcesLists.length; i++) {
                                try {
                                    TextSerializer textSerializer = new TextSerializer(resourcesLists[i], mainObjectsList.get(i));
                                    textSerializer.serialize(objectsDirPath + "\\" + mainObjectsList.get(i).getName() + ".txt");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                    }
                }
            }

            //BinarySerialization binarySerialization = new BinarySerialization();
            //JsonSerialization jsonSerialization = new JsonSerialization();
            //TextSerializer textSerializer = new TextSerializer();

             */

        });
        //////////

        JButton removeButton = new JButton("Remove");
        removeButton.setLocation(GUIClass.MARGIN_LEFT, GUIClass.TABLE_HEIGHT - 2 * GUIClass.CELL_HEIGHT);
        removeButton.setSize(GUIClass.CELL_WIDTH, GUIClass.CELL_HEIGHT);
        removeButton.setBackground(GUIClass.REMOVE_BUTTON_COLOR);
        guiClass.mainLayout.add(removeButton);

        removeButton.addActionListener(actionEvent -> {
            if (justTable.getSelectedRow() != -1) {
                try {
                    Method remove = ArrayList.class.getDeclaredMethod("remove", int.class);
                    remove.invoke(resourcesLists[mainObjectChooser.getSelectedIndex()], justTable.getSelectedRow());
                    if (currentTable != null) {
                        currentTable.setVisible(false);
                    }
                    currentTable = generateTable(fieldsList[mainObjectChooser.getSelectedIndex()], mainObjectChooser.getSelectedIndex());
                    guiClass.mainLayout.add(currentTable);
                    guiClass.repaint();
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else {
                JOptionPane.showMessageDialog(guiClass, "Nothing selected");
            }
        });

        JButton editButton = new JButton("Edit");
        editButton.setLocation(GUIClass.MARGIN_LEFT, GUIClass.TABLE_HEIGHT - GUIClass.CELL_HEIGHT + GUIClass.MARGIN_TOP);
        editButton.setSize(GUIClass.CELL_WIDTH, GUIClass.CELL_HEIGHT);
        editButton.setBackground(GUIClass.EDIT_BUTTON_COLOR);
        guiClass.mainLayout.add(editButton);

        editButton.addActionListener(actionEvent -> {
            if (!isEditing) {
                if (justTable.getSelectedRow() != -1) {
                    for (int fieldNumber = 0; fieldNumber < editableObjectFields.length; fieldNumber++) {
                        editableObjectFields[fieldNumber].setText((String) justTable.getValueAt(justTable.getSelectedRow(), fieldNumber));
                    }
                    isEditing = true;
                    editButton.setText("Apply changes");
                    editButton.setBackground(GUIClass.APPLY_BUTTON_COLOR);

                    addButton.setEnabled(false);
                }
                else {
                    JOptionPane.showMessageDialog(guiClass, "Nothing selected");
                }
            }
            else {
                Class toAdd = classByField(mainObjectsList.get(mainObjectChooser.getSelectedIndex()), OBJECTS_SOURCE_CLASS);
                Constructor[] possibleConstructors = toAdd.getDeclaredConstructors();

                Object[] constructorArray = new Object[activeList.size()];
                boolean allOk = true;
                for (int i = 0; i < activeList.size(); i++) {
                    try {
                        if (fieldsList[mainObjectChooser.getSelectedIndex()].get(i).getType().toString().contains("int")) {
                            constructorArray[i] = Integer.parseInt(editableObjectFields[i].getText());
                        } else if (fieldsList[mainObjectChooser.getSelectedIndex()].get(i).getType().toString().contains("double")) {
                            constructorArray[i] = Double.parseDouble(editableObjectFields[i].getText());
                        } else {
                            constructorArray[i] = editableObjectFields[i].getText();
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(guiClass, "All fields must be filled according to their logical type!");
                        allOk = false;
                    }
                }

                if (allOk) {
                    Object objectToAdd = createObject(possibleConstructors[0], constructorArray);
                    try {
                        Method set = ArrayList.class.getDeclaredMethod("set", int.class, Object.class);
                        set.invoke(resourcesLists[mainObjectChooser.getSelectedIndex()], justTable.getSelectedRow(), objectToAdd);

                        if (currentTable != null) {
                            currentTable.setVisible(false);
                        }
                        currentTable = generateTable(fieldsList[mainObjectChooser.getSelectedIndex()], mainObjectChooser.getSelectedIndex());
                        guiClass.mainLayout.add(currentTable);
                        guiClass.repaint();
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    isEditing = false;
                    editButton.setText("Edit");
                    editButton.setBackground(GUIClass.EDIT_BUTTON_COLOR);
                    for (int fieldNumber = 0; fieldNumber < editableObjectFields.length; fieldNumber++) {
                        editableObjectFields[fieldNumber].setText("");
                    }
                    addButton.setEnabled(true);
                }
            }
        });

        mainObjectChooser.addListSelectionListener(listSelectionEvent -> {    //Обработка выбора объекта
            if (mainObjectChooser.getValueIsAdjusting()) {
                if (currentTable != null) {
                    currentTable.setVisible(false);
                }
                try {
                    currentTable = generateTable(fieldsList[mainObjectChooser.getSelectedIndex()], mainObjectChooser.getSelectedIndex());
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | NoSuchFieldException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

                generateEditFields(mainObjectChooser.getSelectedIndex());

                addButton.addActionListener(actionEvent -> {
                    Class toAdd = classByField(mainObjectsList.get(mainObjectChooser.getSelectedIndex()), OBJECTS_SOURCE_CLASS);
                    Constructor[] possibleConstructors = toAdd.getDeclaredConstructors();

                    Object[] constructorArray = new Object[activeList.size()];
                    boolean allOk = true;
                    for (int i = 0; i < activeList.size(); i++) {
                        try {
                            if (fieldsList[mainObjectChooser.getSelectedIndex()].get(i).getType().toString().contains("int")) {
                                constructorArray[i] = Integer.parseInt(editableObjectFields[i].getText());
                            } else if (fieldsList[mainObjectChooser.getSelectedIndex()].get(i).getType().toString().contains("double")) {
                                constructorArray[i] = Double.parseDouble(editableObjectFields[i].getText());
                            } else {
                                constructorArray[i] = editableObjectFields[i].getText();
                            }
                        }
                        catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(guiClass, "All fields must be filled according to their logical type!");
                            allOk = false;
                        }
                    }

                    if (allOk) {
                        Object objectToAdd = createObject(possibleConstructors[0], constructorArray);
                        try {
                            Method add = ArrayList.class.getDeclaredMethod("add", Object.class);
                            add.invoke(resourcesLists[mainObjectChooser.getSelectedIndex()], objectToAdd);

                            if (currentTable != null) {
                                currentTable.setVisible(false);
                            }
                            currentTable = generateTable(fieldsList[mainObjectChooser.getSelectedIndex()], mainObjectChooser.getSelectedIndex());
                            guiClass.mainLayout.add(currentTable);
                            guiClass.repaint();
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });

                guiClass.mainLayout.add(currentTable);
                guiClass.repaint();
            }

            isEditing = false;
            editButton.setText("Edit");
            editButton.setBackground(GUIClass.EDIT_BUTTON_COLOR);
            for (int fieldNumber = 0; fieldNumber < editableObjectFields.length; fieldNumber++) {
                editableObjectFields[fieldNumber].setText("");
            }
        });

        guiClass.repaint();

    }

    public static Class<?>[] getClassesArray() throws IOException {
        ArrayList<Class<?>> list = new ArrayList<Class<?>>(0);
        for(File f : getPackageContent("Plugins")) {
            String name = f.getName();
            if (name.contains(".")) {
                name = name.substring(0, name.lastIndexOf('.'));
            }
            try {
                Class<?> cl = Class.forName("Plugins." + name);
                list.add(cl);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return list.toArray(new Class<?>[]{});
    }

    private static File[] getPackageContent(String packageName) throws IOException {
        ArrayList<File> list = new ArrayList<File>(0);
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageName);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            File dir = new File(url.getFile());
            Collections.addAll(list, Objects.requireNonNull(dir.listFiles()));
        }
        return list.toArray(new File[]{});
    }

    private static ArrayList<String> getObjectStringFields(ArrayList<Field> orderedFieldsList, Object object) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        ArrayList<Field> fieldsList = getAllFields(new ArrayList<>(), object.getClass(), false);
        ArrayList<String> objectFieldsValues = new ArrayList<>();

        for (Field field : orderedFieldsList) {
            String curFieldName = String.valueOf(field);

            for (int i = 0; i < fieldsList.size(); i++) {
                fieldsList.get(i).setAccessible(true);
                if (!fieldsList.get(i).getType().toString().contains("class") || (fieldsList.get(i).getType().toString().contains("class") && fieldsList.get(i).getType().toString().contains("java"))) {
                    if (curFieldName.contains(fieldsList.get(i).getName())) {
                        objectFieldsValues.add(String.valueOf(fieldsList.get(i).get(object)));
                        break;
                    }
                } else {
                    Object someObject = fieldsList.get(i).get(object);
                    ArrayList<Field> arrayList = getAllFields(new ArrayList<>(), someObject.getClass(), false);
                    for (Field field1 : arrayList) {
                        field1.setAccessible(true);
                        if (curFieldName.contains(field1.getName())) {
                            objectFieldsValues.add(String.valueOf(field1.get(someObject)));
                            break;
                        }
                    }
                }
            }
        }
        return objectFieldsValues;
    }

    private static String getClassNameByField(String stringField) {
        return stringField.substring(stringField.lastIndexOf(" ") + 1, stringField.lastIndexOf("."));
    }

    private static void generateEditFields(int selectedObjectId) {
        if (editableFieldsNames != null) {
            for (int i = 0; i < editableFieldsNames.length; i++) {
                editableFieldsNames[i].setVisible(false);
                editableObjectFields[i].setVisible(false);
                addButton.setVisible(false);
            }
        }
        activeList = fieldsList[selectedObjectId];
        editableObjectFields = new JTextField[activeList.size()];
        editableFieldsNames = new JLabel[activeList.size()];
        int verticalShift = GUIClass.TABLE_HEIGHT + 2 * GUIClass.MARGIN_TOP;
        for (int i = 0; i < activeList.size(); i++) {
            editableFieldsNames[i] = new JLabel("   " + capFirst(activeList.get(i).getName()));
            editableFieldsNames[i].setSize(GUIClass.LABEL_WIDTH, GUIClass.LABEL_HEIGHT);
            editableFieldsNames[i].setLocation(GUIClass.MARGIN_LEFT, verticalShift);
            guiClass.mainLayout.add(editableFieldsNames[i]);

            editableObjectFields[i] = new JTextField();
            editableObjectFields[i].setSize(GUIClass.EDIT_FIELD_WIDTH, GUIClass.EDIT_FIELD_HEIGHT);
            editableObjectFields[i].setLocation(2 * GUIClass.MARGIN_LEFT + GUIClass.CELL_WIDTH, verticalShift);
            guiClass.mainLayout.add(editableObjectFields[i]);

            verticalShift += GUIClass.MARGIN_TOP + GUIClass.EDIT_FIELD_HEIGHT;
        }

        addButton = new JButton("Add");
        addButton.setSize(GUIClass.EDIT_FIELD_WIDTH, GUIClass.CELL_HEIGHT);
        addButton.setLocation(2 * GUIClass.MARGIN_LEFT + GUIClass.CELL_WIDTH, verticalShift);
        addButton.setBackground(GUIClass.ADD_BUTTON_COLOR);
        guiClass.mainLayout.add(addButton);
    }

    private static Object convert(Class<?> targetType, String text) {
        PropertyEditor editor = PropertyEditorManager.findEditor(targetType);
        editor.setAsText(text);
        return editor.getValue();
    }

    private static Object createObject(Constructor constructor, Object[] arguments) {
        //System.out.println("Constructor: " + constructor.toString());
        Object object = null;
        try {
            object = constructor.newInstance(arguments);
            //System.out.println("Object: " + object.toString());
            return object;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            System.out.println(e);
        }
        return object;
    }

    private static ArrayList<Field>[] getFieldsList(ArrayList<Field> mainObjectsList, boolean parseObjects) throws NoSuchFieldException, ClassNotFoundException {
        //Обойти главные объекты
        ArrayList[] primaryFieldsList = new ArrayList[mainObjectsList.size()];
        for (int fieldNumber = 0; fieldNumber < mainObjectsList.size(); fieldNumber++) {
            primaryFieldsList[fieldNumber] = getAllFields(new ArrayList<>(), classByField(mainObjectsList.get(fieldNumber), OBJECTS_SOURCE_CLASS), parseObjects);
        }

        //Создать лист реальных полей, согласно полям конструктора
        ArrayList[] fieldsList = new ArrayList[mainObjectsList.size()];
        for (int i = 0; i < fieldsList.length; i++) {
            fieldsList[i] = new ArrayList<Field>();
        }

        for (int i = 0; i < mainObjectsList.size(); i++) {
            String constructorMap = getConstructorMap(mainObjectsList.get(i), OBJECTS_SOURCE_CLASS);
            String objectMap = getObjectMap(primaryFieldsList[i]);
            String mainPart = getSameParts(constructorMap, objectMap);
            int matchStart = objectMap.indexOf(mainPart);

            int j = mainPart.length();
            while (j > 0) {
                fieldsList[i].add(primaryFieldsList[i].get(matchStart));
                primaryFieldsList[i].remove(matchStart);
                j--;
            }

            constructorMap = constructorMap.replace(mainPart, "");
            objectMap = objectMap.replace(mainPart, "");

            int loopCounter = constructorMap.length();
            while (loopCounter > 0) {
                char typeToFit = constructorMap.charAt(0);
                int fieldIndex = findInFields(typeToFit, objectMap);
                if (fieldIndex != -1) {
                    fieldsList[i].add(primaryFieldsList[i].get(fieldIndex));
                    primaryFieldsList[i].remove(fieldIndex);
                    constructorMap = constructorMap.replaceFirst(String.valueOf(typeToFit), "");
                    objectMap = objectMap.replaceFirst(String.valueOf(typeToFit), "");
                }
                loopCounter--;
            }
        }
        return fieldsList;
    }

    private static int findInFields(char toFind, String objectMap) {
        int index = objectMap.indexOf(toFind);
        if (objectMap.indexOf(toFind, index) != -1) {
            //return -1;
        }
        return index;
    }

    private static String getSameParts(String str1, String str2) {
        int substringLength = 1;
        String maxMatch = "";
        int maxMatchLength = 0;
        while (substringLength <= str1.length()) {
            int startIndex = 0;
            while (startIndex + substringLength <= str1.length()) {
                String analyzingSubstring = str1.substring(startIndex, startIndex + substringLength);
                if (str2.contains(analyzingSubstring)) {
                    maxMatch = analyzingSubstring;
                    maxMatchLength = substringLength;
                }
                startIndex++;
            }
            substringLength++;
        }
        if (maxMatchLength == 0) {
            return "";
        }
        return maxMatch;
    }

    private static String getObjectMap(ArrayList<Field> fields) {
        if (fields.size() > 0) {
            StringBuilder map = new StringBuilder();
            for (int i = 0; i < fields.size(); i++) {
                String fieldType = fields.get(i).getType().toString();
                map.append(fields.get(i).getType().toString().charAt(fieldType.indexOf(" ") + 1));
            }
            return map.toString();
        }
        else {
            return "";
        }
    }

    private static String getConstructorMap(Field mainObject, Class sourceClass) {
        Class aClass = classByField(mainObject, sourceClass);
        Constructor[] constructors = aClass.getDeclaredConstructors();
        if (constructors.length > 0) {
            String constructor = constructors[0].toString();
            //System.out.println(constructor);
            String[] constructorFields = constructor.substring(constructor.indexOf("(") + 1, constructor.indexOf(")")).split(",");
            StringBuilder map = new StringBuilder();
            for (String cFieldType: constructorFields) {
                map.append(cFieldType.charAt(0));
            }
            return map.toString();
        }
        else {
            return "";
        }
    }

    private static JList makeMainObjectsList(ArrayList<Field> mainObjectsList) {
        JList list = new JList(getMainObjectsArray(mainObjectsList));
        list.setLocation(GUIClass.MARGIN_LEFT, GUIClass.MARGIN_TOP);
        list.setFixedCellHeight(GUIClass.CELL_HEIGHT);
        list.setBackground(GUIClass.BASIC_LIST_ITEM_COLOR);
        list.setSelectionBackground(GUIClass.SELECTED_LIST_ITEM_COLOR);
        list.setSize(GUIClass.CELL_WIDTH, GUIClass.CELL_HEIGHT * mainObjectsList.size());

        DefaultListCellRenderer renderer = (DefaultListCellRenderer) list.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        list.setFont(new Font("Arial", Font.BOLD, 14));
        return list;
    }

    private static String[] getMainObjectsArray(ArrayList<Field> fieldsList) {
        String[] objectsArray = new String[fieldsList.size()];
        for (int i = 0; i < fieldsList.size(); i++) {
            String firstLetter = String.valueOf(fieldsList.get(i).getName().charAt(0));
            objectsArray[i] = firstLetter.toUpperCase() + ((objectsArray.length > 1) ? fieldsList.get(i).getName().substring(1) : "");
        }
        return objectsArray;
    }

    private static JScrollPane generateTable(ArrayList<Field> fieldsNames, int objectId) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        Method get = ArrayList.class.getDeclaredMethod("get", int.class);
        Method size = ArrayList.class.getDeclaredMethod("size");

        String fieldName = String.valueOf(mainObjectsList.get(objectId));
        Field field = OBJECTS_SOURCE_CLASS.getDeclaredField(fieldName.substring(fieldName.lastIndexOf(".") + 1));
        ParameterizedType fieldType = (ParameterizedType) field.getGenericType();
        Class<?> listClass = (Class<?>) fieldType.getActualTypeArguments()[0];

        Field[] fields = new Field[fieldsList[objectId].size()];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fieldsList[objectId].get(i);
        }

        int resourcesListSize = (int) size.invoke(resourcesLists[objectId]);
        Object[][] dataA = new String[resourcesListSize][fieldsNames.size()];
        for (int i = 0; i < resourcesListSize; i++) {
            ArrayList<String> objRow = getObjectStringFields(fieldsList[objectId], get.invoke(resourcesLists[objectId], i));
            for (int j = 0; j < objRow.size(); j++) {
                dataA[i][j] = objRow.get(j);
            }
        }

        Vector<String> header = new Vector<>();
        for (Field fieldsName : fieldsNames) {
            header.add(capFirst(fieldsName.getName()));
        }

        Vector<Vector<String>> data = new Vector<Vector<String>>();
        for (Object[] objects : dataA) {
            Vector<String> row = new Vector<>();
            for (int j = 0; j < dataA[0].length; j++) {
                row.add((String) objects[j]);
            }
            data.add(row);
        }

        JTable table = new JTable(data, header);
        table.setSize(GUIClass.TABLE_WIDTH, GUIClass.TABLE_HEIGHT);
        table.setRowHeight(GUIClass.CELL_HEIGHT);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        for (int columnNumber = 0; columnNumber < table.getColumnCount(); columnNumber++) {
            table.getColumnModel().getColumn(columnNumber).setCellRenderer(centerRenderer);
        }
        justTable = table;

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setLocation(GUIClass.MARGIN_LEFT + GUIClass.CELL_WIDTH + 10, GUIClass.MARGIN_TOP);
        scrollPane.setSize(GUIClass.TABLE_WIDTH, GUIClass.TABLE_HEIGHT);
        scrollPane.setViewportView(table);

        return scrollPane;
    }

    private static ArrayList<Field> getMainObjects(ArrayList<Field> mainFields) {
        for (Field field : OBJECTS_SOURCE_CLASS.getDeclaredFields()) {
            String fieldType = field.getType().toString();
            if (fieldType.contains("List")) {
                mainFields.add(field);
            }
            field.setAccessible(true);
        }
        return mainFields;
    }

    private static ArrayList<Field> getAllFields(ArrayList<Field> fields, Class type, boolean parseObjects) throws ClassNotFoundException, NoSuchFieldException {
        for (Field field : type.getDeclaredFields()) {
            String fieldType = field.getType().toString();

            if (fieldType.contains("List")) {
                String listVarName = field.toString().substring(field.toString().lastIndexOf('.') + 1);
                Field listField = type.getDeclaredField(listVarName);
                ParameterizedType listType = (ParameterizedType) listField.getGenericType();
                Class listClass = (Class) listType.getActualTypeArguments()[0];
                fields.add(field);
                getAllFields(fields, listClass, parseObjects);
                field.setAccessible(true);
                fields.add(field);
            }
            else if (fieldType.contains("class") && !fieldType.contains("lang") && !fieldType.contains("$")) {
                if (parseObjects) {
                    getAllFields(fields, Class.forName(fieldType.substring(fieldType.lastIndexOf(" ") + 1)), parseObjects);
                }
                else {
                    fields.add(field);
                }
            }
            else {
                field.setAccessible(true);
                fields.add(field);
            }
        }
        //Если класс от от чего-то наследуется, то вытягиваем поля суперкласса
        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass(), parseObjects);
        }
        return fields;
    }

    private static Class classByField(Field field, Class fieldClass) {
        String fieldName = field.toString().substring(field.toString().lastIndexOf('.') + 1);
        Field listField = null;
        try {
            listField = fieldClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        assert listField != null;
        ParameterizedType listType = (ParameterizedType) listField.getGenericType();
        return (Class) listType.getActualTypeArguments()[0];
    }

    private static String capFirst(String string) {
        if (string.length() > 0) {
            String firstLetter = String.valueOf(string.charAt(0));
            string = firstLetter.toUpperCase() + ((string.length() > 1) ? string.substring(1) : "");
        }
        return string;
    }
}