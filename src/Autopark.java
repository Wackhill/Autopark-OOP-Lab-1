import javax.swing.*;
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
        2.2. String
        2.3. Поля, представленные кастомными объектами ☑
        2.4. Enum
        2.5. Массивы и листы
 */

public class Autopark {
    //private AutoparkItems autoparkItems = new AutoparkItems();
    private static GUIClass guiClass;
    private static JButton selectedButton = null;
    private static final Class OBJECTS_SOURCE_CLASS = AutoparkResources.class;

    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                guiClass = new GUIClass();
                //guiClass.setVisible(true);
            }
        });

        //Достать "главные" объекты
        ArrayList<Field> mainObjectsList = getMainObjects(new ArrayList<>());
        //Обойти главные объекты
        ArrayList[] fieldsList = new ArrayList[mainObjectsList.size()];

        //Выв

        for (int fieldNumber = 0; fieldNumber < mainObjectsList.size(); fieldNumber++) {
            System.out.println("");
            System.out.println("================= " + mainObjectsList.get(fieldNumber) + " =================");
            fieldsList[fieldNumber] = getAllFields(new ArrayList<>(), classByField(mainObjectsList.get(fieldNumber), OBJECTS_SOURCE_CLASS));//TODO---
            for (int i = 0; i < fieldsList[fieldNumber].size(); i++) {
                //System.out.println(fieldsList[fieldNumber].get(i).toString());
            }
        }

        /*
        int curPos = GUIClass.MARGIN_TOP;
        for (int i = 0; i < mainObjectsList.size(); i++) {
            JButton selectObjectButton =
                    new JButton(String.valueOf(classByField(mainObjectsList.get(i), OBJECTS_SOURCE_CLASS))
                    .replace("class ", ""));//"✎"
            selectObjectButton.setSize(GUIClass.BUTTON_WIDTH, GUIClass.BUTTON_HEIGHT);
            selectObjectButton.setLocation(GUIClass.MARGIN_LEFT, curPos);
            selectObjectButton.setBackground(GUIClass.BASIC_BUTTON_COLOR);
            curPos += GUIClass.BUTTON_HEIGHT;

            guiClass.mainLayout.add(selectObjectButton);

            int finalI = i;//FIXME
            selectObjectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (selectedButton != null) {
                        selectedButton.setBackground(GUIClass.BASIC_BUTTON_COLOR);
                    }
                    selectObjectButton.setBackground(GUIClass.SELECTED_BUTTON_COLOR);
                    selectedButton = selectObjectButton;

                    //TODO

                }
            });
            //Container parent = selectObjectButton.getParent();
            //parent.remove(selectObjectButton);
        }
        guiClass.repaint();
        guiClass.mainLayout.add(generateTable(fieldsList[0]));
        guiClass.repaint();
        */
    }

    private static JScrollPane generateTable(ArrayList<Field> fields) {
        Object[][] dataA = new String[fields.size()][fields.size()];
        //Object[] tableHeaders = new String[fields.size()];

        Vector<Vector<String>> data = new Vector<Vector<String>>();
        Vector<String> header = new Vector<>();
        for (int j = 0; j < fields.size(); j++) {
            header.add(fields.get(j).getName());
            Vector<String> row = new Vector<String>();
            for (int i = 0; i < dataA[j].length; i++) {
                row.add((String) dataA[j][i]);
            }
            data.add(row);
        }

        JTable table = new JTable(data, header);
        table.setSize(800, 300);
        table.setRowHeight(GUIClass.BUTTON_HEIGHT);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setLocation(GUIClass.MARGIN_LEFT + GUIClass.BUTTON_WIDTH + 10, GUIClass.MARGIN_TOP);
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
                //System.out.println("Here is list!");
                String listVarName = field.toString().substring(field.toString().lastIndexOf('.') + 1);
                Field listField = type.getDeclaredField(listVarName);
                ParameterizedType listType = (ParameterizedType) listField.getGenericType();
                Class listClass = (Class) listType.getActualTypeArguments()[0];
                fields.add(field);
                getAllFields(fields, listClass);
            } else if (fieldType.contains("class") && !fieldType.contains("lang") && !fieldType.contains("$")) {
                //System.out.println("Here is class!");
                getAllFields(fields, Class.forName(fieldType.substring(fieldType.lastIndexOf(" ") + 1)));
            } else if (fieldType.contains("class") && fieldType.contains("$")) {
                //System.out.println("Here is enum!");
                //System.out.println(fieldType);
                getEnumFields(fieldType.substring(fieldType.lastIndexOf(" ") + 1));

            } else {
                System.out.print("Kinda primitive type: ");
                //System.out.println("Dunno what is it :(");
                //System.out.println("================================");
                System.out.println(field.toString());
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
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
            System.out.format("Enum name:  %s%nEnum constants:  %s%n",
                    enumClass.getName(),
                    Arrays.asList(enumClass.getEnumConstants()));
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

    private static <E extends Enum> E[] getEnumValues(Class<?> enumClass) throws NoSuchFieldException, IllegalAccessException {
        Field f = enumClass.getDeclaredField("Engine$FuelType");
        System.out.println(f);

        System.out.println(Modifier.toString(f.getModifiers()));
        f.setAccessible(true);
        Object o = f.get(null);
        return (E[]) o;
    }
}
