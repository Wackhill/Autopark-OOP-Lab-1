import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Vector;

public class Autopark {
    private static final Class OBJECTS_SOURCE_CLASS = AutoparkResources.class;
    private static Object[] resourcesLists;
    private static ArrayList<Field> mainObjectsList;
    private static ArrayList<Field>[] fieldsList;

    //GUI
    private static GUIClass guiClass;
    private static JButton addButton = null;
    private static JScrollPane currentTable = null;
    private static JTable justTable;
    private static ArrayList<Field> activeList = null;
    private static JLabel[] editableFieldsNames = null;
    private static JTextField[] editableObjectFields = null;
    private static boolean isEditing = false;

    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        javax.swing.SwingUtilities.invokeLater(() -> {
            guiClass = new GUIClass();
            guiClass.setVisible(true);
        });

        mainObjectsList = getMainObjects(new ArrayList<>());
        fieldsList = getFieldsList(mainObjectsList);

        resourcesLists = new Object[mainObjectsList.size()];
        for (int i = 0; i < resourcesLists.length; i++) {
            resourcesLists[i] = AutoparkResources.class.getDeclaredFields()[i].getType().getDeclaredConstructor().newInstance();
        }

        JList mainObjectChooser = makeMainObjectsList(mainObjectsList);
        guiClass.mainLayout.add(mainObjectChooser);
        mainObjectChooser.setSelectedIndex(0);                                //При запуске выбирается самый первый

        currentTable = generateTable(fieldsList[0], 0);
        guiClass.mainLayout.add(currentTable);
        generateEditFields(0);
        guiClass.repaint();

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
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
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
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
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
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | NoSuchFieldException e) {
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
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
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

    public static Object createObject(Constructor constructor, Object[] arguments) {
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

    private static ArrayList<Field>[] getFieldsList(ArrayList<Field> mainObjectsList) throws NoSuchFieldException, ClassNotFoundException {
        //Обойти главные объекты
        ArrayList[] primaryFieldsList = new ArrayList[mainObjectsList.size()];
        for (int fieldNumber = 0; fieldNumber < mainObjectsList.size(); fieldNumber++) {
            primaryFieldsList[fieldNumber] = getAllFields(new ArrayList<>(), classByField(mainObjectsList.get(fieldNumber), OBJECTS_SOURCE_CLASS));
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
            return -1;
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

    private static JScrollPane generateTable(ArrayList<Field> fieldsNames, int objectId) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
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
            for (int j = 0; j < fields.length; j++) {
                fields[j].setAccessible(true);
                Object value = fields[j].get((get.invoke(resourcesLists[objectId], i)));
                dataA[i][j] = String.valueOf(value);
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

    private static ArrayList<Field> getAllFields(ArrayList<Field> fields, Class type) throws ClassNotFoundException, NoSuchFieldException {
        for (Field field : type.getDeclaredFields()) {
            String fieldType = field.getType().toString();

            if (fieldType.contains("List")) {
                String listVarName = field.toString().substring(field.toString().lastIndexOf('.') + 1);
                Field listField = type.getDeclaredField(listVarName);
                ParameterizedType listType = (ParameterizedType) listField.getGenericType();
                Class listClass = (Class) listType.getActualTypeArguments()[0];
                fields.add(field);
                getAllFields(fields, listClass);
                field.setAccessible(true);
                fields.add(field);
            }
            else if (fieldType.contains("class") && !fieldType.contains("lang") && !fieldType.contains("$")) {
                getAllFields(fields, Class.forName(fieldType.substring(fieldType.lastIndexOf(" ") + 1)));
            }
            else {
                field.setAccessible(true);
                fields.add(field);
            }
        }
        //Если класс от от чего-то наследуется, то вытягиваем поля суперкласса
        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
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
