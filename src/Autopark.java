import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Vector;

public class Autopark {
    //private AutoparkItems autoparkItems = new AutoparkItems();
    private static GUIClass guiClass;
    private static JButton selectedButton = null;
    private static final Class OBJECTS_SOURCE_CLASS = AutoparkResources.class;

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                guiClass = new GUIClass();
                guiClass.setVisible(true);
            }
        });

        ArrayList<Field> mainObjectsList = getMainObjects(new ArrayList<>());//FIXME==================
        ArrayList[] fieldsList = new ArrayList[mainObjectsList.size()];//FIXME========================
        for (int fieldNumber = 0; fieldNumber < mainObjectsList.size(); fieldNumber++) {
            System.out.println("===== " + mainObjectsList.get(fieldNumber) + " ======");
            fieldsList[fieldNumber] = getAllFields(new ArrayList<>(), classByField(mainObjectsList.get(0), OBJECTS_SOURCE_CLASS));
            for (int i = 0; i < fieldsList[fieldNumber].size(); i++) {
                //System.out.println(fieldsList[fieldNumber].get(i).toString());
            }
        }

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
            /*TODO Container parent = selectObjectButton.getParent();
                parent.remove(selectObjectButton);*/
        }
        guiClass.repaint();
        guiClass.mainLayout.add(generateTable(fieldsList[0]));
        guiClass.repaint();
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

    private static ArrayList<Field> getMainObjects(ArrayList<Field> mainFields) {
        for (Field field : OBJECTS_SOURCE_CLASS.getDeclaredFields()) {
            String fieldType = field.getType().toString();
            if (fieldType.contains("List")) {
                mainFields.add(field);
            }
        }
        return mainFields;
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
/*
    private static ArrayList<Field> getAllFields(ArrayList<Field> fields, Class type) {
        System.out.println("Input class " + type);
        for (Field field : type.getDeclaredFields()) {
            String fieldType = field.getType().toString();


            if (fieldType.contains("List")) {
                String listVarName = field.toString().substring(field.toString().lastIndexOf('.') + 1);

                Field listField = null;
                try {
                    listField = type.getDeclaredField(listVarName);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }

                ParameterizedType listType = (ParameterizedType) listField.getGenericType();
                Class listClass = (Class) listType.getActualTypeArguments()[0];
                System.out.println(listClass);

                getAllFields(fields, listClass);
                //System.out.println(listClass); //Ternary operator booleanExpression ? expression1 : expression2
            }


            System.out.println(fieldType);
            fields.add(field);//TODO:====================
        }




            try {
                if (isClass(Class.forName(fieldType.substring(fieldType.lastIndexOf(" ") + 1)))) {
                    System.out.println("OH, SHIT, I'M CLASSSS!!!!!!!");
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (fieldType.contains("class") && fieldType.contains("$")) {
                String enumName = fieldType.substring(fieldType.lastIndexOf("$") + 1);

            }
            else if (fieldType.contains("class") && !fieldType.contains("lang")) {


                System.out.println("===== BAN =====");
                try {
                    getAllFields(fields, Class.forName(fieldType.substring(fieldType.lastIndexOf(" ") + 1)));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            //}

//        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }
    */

    private static ArrayList<Field> getAllFields(ArrayList<Field> fields, Class type) {
        for (Field field : type.getDeclaredFields()) {
            String fieldType = field.getType().toString();
            if (fieldType.contains("List")) {
                String listVarName = field.toString().substring(field.toString().lastIndexOf('.') + 1);
                Field listField = null;
                try {
                    listField = type.getDeclaredField(listVarName);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                ParameterizedType listType = (ParameterizedType) listField.getGenericType();
                Class listClass = (Class) listType.getActualTypeArguments()[0];
                getAllFields(fields, listClass);
            }
            else if (fieldType.contains("class") && !fieldType.contains("lang") && !fieldType.contains("$")) {
                try {
                    getAllFields(fields, Class.forName(fieldType.substring(fieldType.lastIndexOf(" ") + 1)));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("================================");
                System.out.println(field.toString());
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            }
            fields.add(field);
        }
        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
        return fields;
    }

    /*
    public static ArrayList<Field> getAllFields(ArrayList<Field> fields, Class type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        //fields.addAll(Arrays.asList(type.getSimpleName()));
        //System.out.println(type.getTypeName());
        //Field[] declaredFields = type.getDeclaredFields();
        //for (Field field : declaredFields) {
        //System.out.println(field + " | " + field.getType());
        //if ((field.getType() + "").matches("class")) {
        //getAllFields(fields, field.getName().getClass());
        //}
        //}

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

     */

    //Bus bus = new Bus(1, 2, 3, 4, Vehicle.GearboxType.AUTOMATIC, Vehicle.FuelType.ELECTRIC, 3, 5);
    //System.out.println(bus.getWidth());

    /*
    private Route[] route = {new Route(1, 2, "Немига"),
                             new Route(2, 3, "Купаловская"),
                             new Route(4, 5, "Партизанская")};

    ArrayList<String> categoriesList = new ArrayList<>();
    */
}
