package Serializators;

import Dialogs.EncoderChooser;

import java.io.*;
import java.util.ArrayList;

public class BinarySerializer implements Serializer {
    private Object serializableList;
    public BinarySerializer(Object serializableList) {
        this.serializableList = serializableList;
    }

    @Override
    public void serialize(String filePath) throws IOException {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Gsdjchbsd");
        arrayList.add("Gsdjchbsd1");
        arrayList.add("Gsdjchbsd2");
        EncoderChooser codingChooser = new EncoderChooser(arrayList);
        codingChooser.setVisible(true);
        /*
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(serializableList);
            objectOutputStream.flush();
            byte[] objectBytes = byteArrayOutputStream.toByteArray();

            FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
            fileOutputStream.write(objectBytes);
            fileOutputStream.close();
        }
        finally {
            try {
                byteArrayOutputStream.close();
            }
            catch (IOException ignored) { }
        }

         */

        /*
        FileOutputStream fileOutputStream;
        ObjectOutputStream objectOutputStream;
        fileOutputStream = new FileOutputStream(filePath);
        objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(this.serializableList);
        objectOutputStream.flush();
        objectOutputStream.close();
         */
    }

    @Override
    public Object deserialize(String filePath) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        this.serializableList = objectInputStream.readObject();
        return this.serializableList;
    }
}
