package Serializators;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TextSerializer implements Serializer {
    private Object serializableList;
    private Field listAsField;
    public TextSerializer(Object serializableList, Field listAsField) {
        this.serializableList = serializableList;
        this.listAsField = listAsField;
    }

    @Override
    public void serialize(String filePath) throws IOException {
        StringBuilder csvData = new StringBuilder();
        ArrayList list = (ArrayList <Field>)serializableList;
        try {
            for (int i = 0; i < list.size(); i++) {
                ArrayList<Field> fieldsList = getFieldsList(list.get(i), true);
                String fieldsValues = objectToCSV(fieldsList, list.get(i));
                csvData.append(fieldsValues);
            }
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
            if (csvData.length() != 0) {
                bufferedWriter.write(csvData.toString().substring(0, csvData.length() - 1));
            }
            bufferedWriter.flush();
            bufferedWriter.close();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object deserialize(String filePath) throws IOException, ClassNotFoundException, Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        String objectLine;
        while ((objectLine = bufferedReader.readLine()) != null) {
            String[] objectFieldsTypedValues = objectLine.split(",");
            Object[] objectFields = new Object[objectFieldsTypedValues.length];
            for (int i = 0; i < objectFieldsTypedValues.length; i++) {
                String fieldType = objectFieldsTypedValues[i].substring(0, objectFieldsTypedValues[i].indexOf(":"));
                String fieldValue = objectFieldsTypedValues[i].substring(objectFieldsTypedValues[i].indexOf(":") + 1);
                if (fieldType.contains("int")) {
                    objectFields[i] = Integer.parseInt(fieldValue);
                } else if (fieldType.contains("double")) {
                    objectFields[i] = Double.parseDouble(fieldValue);
                } else {
                    objectFields[i] = fieldValue;
                }
            }
            ParameterizedType arrayListType = (ParameterizedType) listAsField.getGenericType();
            Class<?> arrayListClass = (Class<?>) arrayListType.getActualTypeArguments()[0];
            Constructor workingConstructor;

            if (arrayListClass.getConstructors()[0].toString().contains("()")) {
                workingConstructor = arrayListClass.getConstructors()[1];
            }
            else {
                workingConstructor = arrayListClass.getConstructors()[0];
            }
            Object object = createObject(workingConstructor, objectFields);
            Method add = ArrayList.class.getDeclaredMethod("add", Object.class);
            add.invoke(this.serializableList, object);
        }
        return this.serializableList;
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

    private static ArrayList<Field> getFieldsList(Object object, boolean parseObjects) throws NoSuchFieldException, ClassNotFoundException {
        ArrayList<Field> primaryFieldsList = getAllFields(new ArrayList<>(), object.getClass(), parseObjects);
        //for (int i = 0; i < primaryFieldsList.size(); i++) {
        //    System.out.println(primaryFieldsList.get(i));
        //}
        //Создать лист реальных полей, согласно полям конструктора
        ArrayList<Field> fieldsList = new ArrayList();

        String constructorMap = getConstructorMap(object);
        //System.out.println("ConstructorMap: " + constructorMap);
        String objectMap = getObjectMap(primaryFieldsList);
        String mainPart = getSameParts(constructorMap, objectMap);
        //System.out.println(constructorMap + "  " + objectMap + "  " + mainPart);

        int matchStart = objectMap.indexOf(mainPart);

        int j = mainPart.length();
        while (j > 0) {
            fieldsList.add(primaryFieldsList.get(matchStart));
            primaryFieldsList.remove(matchStart);
            j--;
        }

        constructorMap = constructorMap.replace(mainPart, "");
        objectMap = objectMap.replace(mainPart, "");

        int loopCounter = constructorMap.length();
        while (loopCounter > 0) {
            char typeToFit = constructorMap.charAt(0);
            int fieldIndex = findInFields(typeToFit, objectMap);
            if (fieldIndex != -1) {
                fieldsList.add(primaryFieldsList.get(fieldIndex));
                primaryFieldsList.remove(fieldIndex);
                constructorMap = constructorMap.replaceFirst(String.valueOf(typeToFit), "");
                objectMap = objectMap.replaceFirst(String.valueOf(typeToFit), "");
            }
            loopCounter--;
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

    private static String getConstructorMap(Object object) {
        Constructor[] constructors = object.getClass().getDeclaredConstructors();
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

    private static String objectToCSV(ArrayList<Field> orderedFieldsList, Object object) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        ArrayList<Field> fieldsList = getAllFields(new ArrayList<>(), object.getClass(), false);
        StringBuilder objectFieldsBuilder = new StringBuilder();

        for (Field field : orderedFieldsList) {
            String curFieldName = String.valueOf(field);

            for (Field value : fieldsList) {
                value.setAccessible(true);
                if (!value.getType().toString().contains("class") || (value.getType().toString().contains("class") && value.getType().toString().contains("java"))) {
                    if (curFieldName.contains(value.getName())) {
                        String fieldType = value.getType().toString();
                        if (fieldType.contains(".")) {
                            fieldType = fieldType.substring(fieldType.lastIndexOf(".") + 1);
                        }
                        objectFieldsBuilder.append(fieldType).append(":");
                        objectFieldsBuilder.append(value.get(object)).append(",");
                        break;
                    }
                } else {
                    Object someObject = value.get(object);
                    ArrayList<Field> arrayList = getAllFields(new ArrayList<>(), someObject.getClass(), false);
                    for (Field field1 : arrayList) {
                        field1.setAccessible(true);
                        if (curFieldName.contains(field1.getName())) {
                            objectFieldsBuilder.append(field1.getType()).append(":");
                            objectFieldsBuilder.append(field1.get(someObject)).append(",");
                            break;
                        }
                    }
                }
            }
        }
        String csvLine = objectFieldsBuilder.toString();
        return csvLine.substring(0, csvLine.length() - 1) + "\n";
    }
}
