package SafeSerializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Scanner;

public class SafeJsonSerializer implements SafeSerializer {
    Object serializableList;
    Class cryptoClass;

    public SafeJsonSerializer(Object serializableList, Class cryptoClass) {
        this.serializableList = serializableList;
        this.cryptoClass = cryptoClass;
    }

    @Override
    public void serialize(String filePath) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTyping();
        String jsonuser = objectMapper.writeValueAsString(this.serializableList);

        Method encryptionMethod = cryptoClass.getDeclaredMethod("encode", byte[].class);
        String encryptedObject = (String) encryptionMethod.invoke(cryptoClass.getDeclaredConstructor().newInstance(), jsonuser.getBytes());

        FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
        fileOutputStream.write(encryptedObject.getBytes());
        fileOutputStream.close();
        //objectMapper.writeValue(new File(filePath),  this.serializableList);
    }

    @Override
    public Object deserialize(String filePath) throws IOException, ClassNotFoundException, Exception {
        StringBuilder restoredObject = new StringBuilder();
        Scanner in = new Scanner(new File(filePath));
        while(in.hasNext()) {
            restoredObject.append(in.nextLine());
        }
        in.close();

        Method decryptionMethod = cryptoClass.getDeclaredMethod("decode", String.class);
        byte[] objectBytes = (byte[]) decryptionMethod.invoke(cryptoClass.getDeclaredConstructor().newInstance(), restoredObject.toString());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTyping();
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, Object.class);
        return objectMapper.readValue(new String(objectBytes), collectionType);
    }
}
