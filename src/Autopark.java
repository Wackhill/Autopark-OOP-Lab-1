import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class Autopark {
    private AutoparkItems autoparkItems = new AutoparkItems();
    private static GUIClass guiClass;
    private static JButton selectedButton = null;

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                guiClass = new GUIClass();
                guiClass.setVisible(true);
            }
        });

        int curPos = 10;
        ArrayList<Field> mainObjectsList = getMainObjects(new ArrayList<Field>(), AutoparkItems.class);
        for (int i = 0; i < mainObjectsList.size(); i++) {
            JButton selectObjectButton =
                    new JButton(String.valueOf(classByField(mainObjectsList.get(i), AutoparkItems.class))
                    .replace("class ", ""));//"✎"
            selectObjectButton.setSize(150, 35);
            selectObjectButton.setLocation(10, curPos);
            selectObjectButton.setBackground(GUIClass.BASIC_BUTTON_COLOR);
            curPos += 35;

            guiClass.mainLayout.add(selectObjectButton);

            int finalI = i;
            selectObjectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (selectedButton != null) {
                        selectedButton.setBackground(GUIClass.BASIC_BUTTON_COLOR);
                    }
                    selectObjectButton.setBackground(GUIClass.SELECTED_BUTTON_COLOR);
                    selectedButton = selectObjectButton;
                }
            });
        }
        guiClass.repaint();
/*
        List fieldsList = getAllFields(new ArrayList<Field>(), AutoparkItems.class);
        for (int i = 0; i < fieldsList.size(); i++) {
            //System.out.println(fieldsList.get(i));
            JButton jButton;
            if (fieldsList.get(i).toString().contains("List")) {
                jButton = new JButton("=====================");
            }
            else {
                jButton = new JButton(fieldsList.get(i).toString());
            }
            jButton.setSize(400, 35);
            jButton.setLocation(20, curPos);
            curPos += 30;
            guiClass.mainLayout.add(jButton);

            int finalI = i;
            jButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    //jButton.setText(fieldsList.get(finalI) + " Click");
                }
            });
        }

 */
    }

    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
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
                Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
                System.out.println(listClass);

                getAllFields(fields, listClass);
                //System.out.println(listClass); //Ternary operator booleanExpression ? expression1 : expression2
            }
            fields.add(field);//TODO:====================
        }
            /*


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
             */
                /*
                System.out.println("===== BAN =====");
                try {
                    getAllFields(fields, Class.forName(fieldType.substring(fieldType.lastIndexOf(" ") + 1)));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                 */
            //}

//        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    private boolean isAnEnum(Class<?> type) {
        return Enum.class.isAssignableFrom(type);
    }

    private boolean isSameType(Field input, Class<?> ownerType) {
        return input.getType().equals(ownerType);
    }

    public static boolean isClass(Class<?> type) {
        return Class.class.isAssignableFrom(type);
    }

    public static boolean isList(Class<?> type) {
        return ArrayList.class.isAssignableFrom(type);
    }

    private static ArrayList<Field> getMainObjects(ArrayList<Field> fields, Class entryClass) {
        for (Field field : entryClass.getDeclaredFields()) {
            String fieldType = field.getType().toString();
            if (fieldType.contains("List")) {
                fields.add(field);
            }
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
        ParameterizedType listType = (ParameterizedType) listField.getGenericType();
        return (Class) listType.getActualTypeArguments()[0];
    }

    /*
    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        //fields.addAll(Arrays.asList(type.getDeclaredFields()));
        //fields.addAll(Arrays.asList(type.getSimpleName()));
        //System.out.println(type.getTypeName());
        Field[] declaredFields = type.getDeclaredFields();
        for (Field field : declaredFields) {
            //System.out.println(field + " | " + field.getType());
            //if ((field.getType() + "").matches("class")) {
                //getAllFields(fields, field.getName().getClass());
            //}
        }

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
