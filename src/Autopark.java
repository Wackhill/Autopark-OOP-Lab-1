import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Autopark {
    private AutoparkItems autoparkItems = new AutoparkItems();
    private static GUIClass guiClass;
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                guiClass = new GUIClass();
                guiClass.setVisible(true);
            }
        });

        int curPos = 10;
        List fieldsList = getAllFields(new ArrayList<Field>(), AutoparkItems.class);
        for (int i = 0; i < fieldsList.size(); i++) {
            //System.out.println(fieldsList.get(i));
            JButton jButton = new JButton(fieldsList.get(i).toString());
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

        //Bus bus = new Bus(1, 2, 3, 4, Vehicle.GearboxType.AUTOMATIC, Vehicle.FuelType.ELECTRIC, 3, 5);
        //System.out.println(bus.getWidth());

            /*
    private Route[] route = {new Route(1, 2, "Немига"),
                             new Route(2, 3, "Купаловская"),
                             new Route(4, 5, "Партизанская")};

    ArrayList<String> categoriesList = new ArrayList<>();
    */
    }

    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            String fieldType = field.getType().toString();
            //System.out.println("Field type: " + fieldType);
            //System.out.println(field.toString());

            if (fieldType.contains("List")) {
                String listVar = field.toString().substring(field.toString().lastIndexOf('.') + 1);

                Field stringListField = null;
                try {
                    stringListField = type.getDeclaredField(listVar);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }

                ParameterizedType stringListType = (ParameterizedType) stringListField.getGenericType();
                Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
                System.out.println(stringListClass);
                //System.out.println("+++" + listVar + "+++");
            }

            //fields.add(field);
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
        }

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
}
