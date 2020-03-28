package Serializators;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

public interface Serializer {
    public void serialize(String filePath) throws IOException;
    public Object deserialize(String filePath) throws IOException, ClassNotFoundException, Exception;
}
