package Serializators;

import java.io.IOException;

public class TextSerializer implements Serializer {
    @Override
    public void serialize(Object data, String filePath) throws IOException {

    }

    @Override
    public Object deserialize(String filePath) throws IOException, ClassNotFoundException, Exception {
        return null;
    }
}
