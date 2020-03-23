package Serializators;

import java.io.IOException;

public interface Serializer {
    public void serialize(Object data, String filePath) throws IOException;
    public Object deserialize(String filePath) throws IOException, ClassNotFoundException, Exception;
}
