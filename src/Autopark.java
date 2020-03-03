import javax.management.ObjectName;
import javax.swing.*;
import java.awt.*;
import java.awt.image.AreaAveragingScaleFilter;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Vector;

/*TODO
    1. Достать главные объекты ☑
    2. Обойти главные объекты и вытащить:
        2.1. Поля примитивных типов ☑
        2.2. String ☑
        2.3. Поля, представленные кастомными объектами ☑
        2.4. Enum ☑
        2.5. Массивы и листы
    3. Подготовить форму:
        3.1. Генерировать лист главных объектов с обработчиком выбора ☑
        3.2. Генерировать таблицы параметров объектов
 */

public class Autopark {
    private static GUIClass guiClass;
    private static final Class OBJECTS_SOURCE_CLASS = AutoparkResources.class;
    private static JScrollPane currentTable = null;
    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        javax.swing.SwingUtilities.invokeLater(() -> {
            guiClass = new GUIClass();
            //guiClass.setVisible(true);
        });
        ArrayList<Field> mainObjectsList = getMainObjects(new ArrayList<>()); //Список главных объектов
        ArrayList<Field>[] fieldsList = getFieldsList(mainObjectsList);       //Список полей главных объектов, их родителей и полей, ...
                                                                              // ... также представленных объектами

        //FIXME=========================================================================================================

        Class toAddClass = classByField(mainObjectsList.get(3), OBJECTS_SOURCE_CLASS);          //Выбираем класс, в который надо добалять объект
        Constructor[] constructors = toAddClass.getDeclaredConstructors();                      //Получаем его конструктор

        Object[] objectConstructor = new Object[2];                                             //Теперь делаем сам объект
        objectConstructor[0] = "Ivan";
        objectConstructor[1] = 12;
        Object objectToAdd = createObject(constructors[0], objectConstructor);                  //Создали объект

        /////////////////////////
        objectConstructor = new Object[2];
        objectConstructor[0] = "Not Ivan";
        objectConstructor[1] = 1200;
        Object objectToAdd1 = createObject(constructors[0], objectConstructor);
        /////////////

        Object[] resourceLists = new Object[mainObjectsList.size()];                            //Тут будут листы, в которые надо что-то добавлять
        for (int i = 0; i < resourceLists.length; i++) {
            resourceLists[i] = AutoparkResources.class.getDeclaredFields()[i].getType().getDeclaredConstructor().newInstance();
        }

        Method add = ArrayList.class.getDeclaredMethod("add", Object.class);
        add.invoke(resourceLists[3], objectToAdd);
        add.invoke(resourceLists[3], objectToAdd1);

        Method get = ArrayList.class.getDeclaredMethod("get", int.class);

        Field[] fields = ((Object) get.invoke(resourceLists[3], 1)).getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            Object value = fields[i].get((get.invoke(resourceLists[3], 1)));
            System.out.println("Value of Field " + fields[i].getName() + " is " + value);
        }

        //FIXME=========================================================================================================

        JList mainObjectChooser = makeMainObjectsList(mainObjectsList);       //Кликабельный лист, в котором выбирается объект для редактирования
        guiClass.mainLayout.add(mainObjectChooser);
        assert mainObjectsList.size() > 0;
        mainObjectChooser.setSelectedIndex(0);                                //При запуске выбирается самый первый
        currentTable = generateTable(fieldsList[0]);
        guiClass.mainLayout.add(currentTable);
        guiClass.repaint();

        mainObjectChooser.addListSelectionListener(listSelectionEvent -> {    //Обработка выбора объекта
            if (mainObjectChooser.getValueIsAdjusting()) {
                if (currentTable != null) {
                    currentTable.setVisible(false);
                }
                currentTable = generateTable(fieldsList[mainObjectChooser.getSelectedIndex()]); //Генерация таблицы параметров объекта
                guiClass.mainLayout.add(currentTable);
                guiClass.repaint();
            }
        });
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

    //Метод для получения значений параметров главных объектов
    private static ArrayList<Field>[] getFieldsList(ArrayList<Field> mainObjectsList) throws NoSuchFieldException, ClassNotFoundException {
        //Обойти главные объекты
        ArrayList[] primaryFieldsList = new ArrayList[mainObjectsList.size()];
        for (int fieldNumber = 0; fieldNumber < mainObjectsList.size(); fieldNumber++) {
            primaryFieldsList[fieldNumber] = getAllFields(new ArrayList<>(), classByField(mainObjectsList.get(fieldNumber), OBJECTS_SOURCE_CLASS));//TODO---
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
                //else {
                //FIXME, PLEASE
                //}
                loopCounter--;
            }
        }
        return fieldsList;
    }

    private static int findInFields(char toFind, String objectMap) {
        int index = objectMap.indexOf(toFind);
        if (objectMap.indexOf(toFind, index) != -1) {
            //return -1;//FIXME, PLEASE!
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

    private static JScrollPane generateTable(ArrayList<Field> fieldsNames) {
        Object[][] dataA = new String[fieldsNames.size()][5];

        Vector<Vector<String>> data = new Vector<Vector<String>>();
        Vector<String> header = new Vector<>();
        for (int j = 0; j < fieldsNames.size(); j++) {
            header.add(fieldsNames.get(j).getName());
            Vector<String> row = new Vector<>();
            for (int i = 0; i < dataA[j].length; i++) {
                row.add((String) dataA[j][i]);
            }
            data.add(row);
        }

        JTable table = new JTable(data, header);
        table.setSize(GUIClass.TABLE_WIDTH, GUIClass.TABLE_HEIGHT);
        table.setRowHeight(GUIClass.CELL_HEIGHT);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setLocation(GUIClass.MARGIN_LEFT + GUIClass.CELL_WIDTH + 10, GUIClass.MARGIN_TOP);
        scrollPane.setSize(GUIClass.TABLE_WIDTH, GUIClass.TABLE_HEIGHT);
        scrollPane.setViewportView(table);
        return scrollPane;
    }

    //Метод получения главных объектов
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
                field.setAccessible(true);
                fields.add(field);
            } else if (fieldType.contains("class") && !fieldType.contains("lang") && !fieldType.contains("$")) {
                getAllFields(fields, Class.forName(fieldType.substring(fieldType.lastIndexOf(" ") + 1)));
            } else if (fieldType.contains("class") && fieldType.contains("$")) {
                getEnumFields(fieldType.substring(fieldType.lastIndexOf(" ") + 1));
                field.setAccessible(true);
                fields.add(field);
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
