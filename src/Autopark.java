import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/*TODO
    1. Достать "главные" объекты ☑
    2. Обойти "главные" объекты и вытащить:
        2.1. Поля примитивных типов ☑
        2.2. String ☑
        2.3. Поля, представленные кастомными объектами ☑
        2.4. Enum ☑
        2.5. Массивы и листы
    3. Подготовить форму:
        3.1. Генерировать лист главных объектов с обработчиком выбора

 */

public class Autopark {
    //private AutoparkItems autoparkItems = new AutoparkItems();
    private static GUIClass guiClass;
    private static final Class OBJECTS_SOURCE_CLASS = AutoparkResources.class;
    private static JScrollPane currentTable = null;
    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                guiClass = new GUIClass();
                guiClass.setVisible(true);
            }
        });

        //Достать главные объекты
        ArrayList<Field> mainObjectsList = getMainObjects(new ArrayList<>());
        //Обойти главные объекты
        ArrayList[] fieldsList = new ArrayList[mainObjectsList.size()];

        JList mainObjectChooser = makeMainObjectsList(mainObjectsList);
        guiClass.mainLayout.add(mainObjectChooser);
        mainObjectChooser.setSelectedIndex(0);


        mainObjectChooser.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (mainObjectChooser.getValueIsAdjusting()) {
                    System.out.println(mainObjectChooser.getSelectedIndex() + " ");
                    if (currentTable != null) {
                        currentTable.setVisible(false);
                    }
                    currentTable = generateTable(fieldsList[mainObjectChooser.getSelectedIndex()]);
                    guiClass.mainLayout.add(currentTable);
                    guiClass.repaint();

                    Constructor[] constructors = new Constructor[0];
                    try {
                        //System.out.println("Look: " + fieldsList[mainObjectChooser.getSelectedIndex()]);
                        constructors = Class.forName(mainObjectsList.get(mainObjectChooser.getSelectedIndex()).getName()).getConstructors();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    System.out.println(constructors[0]);
                }
            }
        });

        for (int fieldNumber = 0; fieldNumber < mainObjectsList.size(); fieldNumber++) {
            System.out.println("");
            System.out.println("================= " + mainObjectsList.get(fieldNumber) + " =================");
            fieldsList[fieldNumber] = getAllFields(new ArrayList<>(), classByField(mainObjectsList.get(fieldNumber), OBJECTS_SOURCE_CLASS));//TODO---
            for (int i = 0; i < fieldsList[fieldNumber].size(); i++) {
                System.out.println(fieldsList[fieldNumber].get(i).toString());
            }
        }
        guiClass.repaint();
    }

    private static JList makeMainObjectsList(ArrayList<Field> mainObjectsList) {
        JList list = new JList(getMainObjectsArray(mainObjectsList));
        list.setLocation(GUIClass.MARGIN_LEFT, GUIClass.MARGIN_TOP);
        list.setFixedCellHeight(GUIClass.CELL_HEIGHT);
        list.setBackground(GUIClass.BASIC_LIST_ITEM_COLOR);
        list.setSelectionBackground(GUIClass.SELECTED_LIST_ITEM_COLOR);
        list.setSize(GUIClass.CELL_WIDTH, GUIClass.CELL_HEIGHT * mainObjectsList.size());
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

    private static JScrollPane generateTable(ArrayList<Field> fields) {
        Object[][] dataA = new String[fields.size()][fields.size()];
        //Object[] tableHeaders = new String[fields.size()];

        Vector<Vector<String>> data = new Vector<Vector<String>>();
        Vector<String> header = new Vector<>();
        for (int j = 0; j < fields.size(); j++) {
            header.add(fields.get(j).getName());
            Vector<String> row = new Vector<>();
            for (int i = 0; i < dataA[j].length; i++) {
                row.add((String) dataA[j][i]);
            }
            data.add(row);
        }

        JTable table = new JTable(data, header);
        table.setSize(800, 300);
        table.setRowHeight(GUIClass.CELL_HEIGHT);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setLocation(GUIClass.MARGIN_LEFT + GUIClass.CELL_WIDTH + 10, GUIClass.MARGIN_TOP);
        scrollPane.setSize(800, 300);
        scrollPane.setViewportView(table);
        return scrollPane;
    }

    //Метод получения "главных" объектов
    private static ArrayList<Field> getMainObjects(ArrayList<Field> mainFields) {
        for (Field field : OBJECTS_SOURCE_CLASS.getDeclaredFields()) {
            String fieldType = field.getType().toString();
            if (fieldType.contains("List")) {
                mainFields.add(field);
            }
        }
        return mainFields;
    }

    //Метод получения полей главных объектов
    private static ArrayList<Field> getAllFields(ArrayList<Field> fields, Class type) throws ClassNotFoundException, NoSuchFieldException {
        for (Field field : type.getDeclaredFields()) {
            String fieldType = field.getType().toString();

            if (fieldType.contains("List")) {
                //FIXME: Листы пока работают некорректно
                String listVarName = field.toString().substring(field.toString().lastIndexOf('.') + 1);
                Field listField = type.getDeclaredField(listVarName);
                ParameterizedType listType = (ParameterizedType) listField.getGenericType();
                Class listClass = (Class) listType.getActualTypeArguments()[0];
                fields.add(field);
                getAllFields(fields, listClass);
            } else if (fieldType.contains("class") && !fieldType.contains("lang") && !fieldType.contains("$")) {
                getAllFields(fields, Class.forName(fieldType.substring(fieldType.lastIndexOf(" ") + 1)));
            } else if (fieldType.contains("class") && fieldType.contains("$")) {
                getEnumFields(fieldType.substring(fieldType.lastIndexOf(" ") + 1));
            }
            fields.add(field);
        }
        //Если класс от от чего-то наследуется, то вытягиваем поля суперкласса
        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
        return fields;
    }

    //TODO: Переделать под метод, водвращающий лист
    private static void getEnumFields(String enumName) throws ClassNotFoundException {
        Class enumClass = Class.forName(enumName);
        if (enumClass.isEnum()) {
            //System.out.format("Enum name:  %s%nEnum constants:  %s%n",
            //        enumClass.getName(),
            //        Arrays.asList(enumClass.getEnumConstants()));
        }
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
}
