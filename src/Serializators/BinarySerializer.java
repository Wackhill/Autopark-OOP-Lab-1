package Serializators;

import java.io.*;

public class BinarySerializer implements Serializer {
    private Object serializableList;
    public BinarySerializer(Object serializableList) {
        this.serializableList = serializableList;
    }

    @Override
    public void serialize(String filePath) throws IOException {
        FileOutputStream fileOutputStream;
        ObjectOutputStream objectOutputStream;
        fileOutputStream = new FileOutputStream(filePath);
        objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(this.serializableList);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    @Override
    public Object deserialize(String filePath) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        this.serializableList = objectInputStream.readObject();
        return this.serializableList;
    }
}
