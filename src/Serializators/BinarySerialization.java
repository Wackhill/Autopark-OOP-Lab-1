
package Serializators;

import java.io.*;

public class BinarySerialization implements Serializer {
    private FileOutputStream fileOutputStream;
    private ObjectOutputStream objectOutputStream;

    @Override
    public void serialize(Object data, String filePath) throws IOException {
        fileOutputStream = new FileOutputStream(filePath);
        objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(data);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    @Override
    public Object deserialize(String filePath) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        return objectInputStream.readObject();
    }
}
