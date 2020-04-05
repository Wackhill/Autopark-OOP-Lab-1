package SafeSerializers;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class SafeBinarySerializer implements SafeSerializer {
    private Object serializableList;
    private Class cryptoClass;

    public SafeBinarySerializer(Object serializableList, Class cryptoClass) {
        this.serializableList = serializableList;
        this.cryptoClass = cryptoClass;
    }

    @Override
    public void serialize(String filePath) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(serializableList);
            objectOutputStream.flush();
            byte[] objectBytes = byteArrayOutputStream.toByteArray();

            Method encryptionMethod = cryptoClass.getDeclaredMethod("encode", byte[].class);
            String encryptedObject = (String) encryptionMethod.invoke(cryptoClass.getDeclaredConstructor().newInstance(), objectBytes);

            FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
            fileOutputStream.write(encryptedObject.getBytes());
            fileOutputStream.close();
        }
        finally {
            try {
                byteArrayOutputStream.close();
            }
            catch (IOException ignored) { }
        }
    }

    @Override
    public Object deserialize(String filePath) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        StringBuilder objectString = new StringBuilder();
        String line;
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "cp1251"));
            while ((line = bufferedReader.readLine()) != null) {
                objectString.append(line);
            }
            bufferedReader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Method decryptionMethod = cryptoClass.getDeclaredMethod("decode", String.class);
        byte[] objectBytes = (byte[]) decryptionMethod.invoke(cryptoClass.getDeclaredConstructor().newInstance(), objectString.toString());

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(objectBytes);
        ObjectInput objectInput = null;
        Object object = null;
        try {
            objectInput = new ObjectInputStream(byteArrayInputStream);
            object = objectInput.readObject();
        } finally {
            try {
                if (object != null) {
                    objectInput.close();
                }
            } catch (IOException ignored) {  }
        }



        this.serializableList = (ArrayList<Object>) object;
        return this.serializableList;
    }
}
