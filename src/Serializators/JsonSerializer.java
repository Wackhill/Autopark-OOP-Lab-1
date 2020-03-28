package Serializators;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class JsonSerializer implements Serializer {
    Object serializableList;
    public JsonSerializer(Object serializableList) {
        this.serializableList = serializableList;
    }

    @Override
    public void serialize(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTyping();
        String jsonuser = objectMapper.writeValueAsString(this.serializableList);
        objectMapper.writeValue(new File(filePath),  this.serializableList);
    }

    @Override
    public Object deserialize(String filePath) throws IOException, ClassNotFoundException, Exception {
        StringBuilder restoredObject = new StringBuilder();
        Scanner in = new Scanner(new File(filePath));
        while(in.hasNext())
            restoredObject.append(in.nextLine());
        in.close();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTyping();
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, Object.class);
        return objectMapper.readValue(restoredObject.toString(), collectionType);
    }
}
