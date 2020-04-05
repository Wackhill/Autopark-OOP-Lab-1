package SafeSerializers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface SafeSerializer {
    public void serialize(String filePath) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException;
    public Object deserialize(String filePath) throws IOException, ClassNotFoundException, Exception;
}
