package Serializators;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class JsonSerialization implements Serializer {
    @Override
    public void serialize(Object data, String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        String jsonuser = mapper.writeValueAsString(data);
        mapper.writeValue(new File(filePath),  data);
        System.out.println(jsonuser);
    }

    @Override
    public Object deserialize(String filePath) throws IOException, ClassNotFoundException, Exception {
//        FileInputStream fileInputStream = new FileInputStream(filePath);
//        XMLDecoder decoder = new XMLDecoder(fileInputStream);
//        Object object = decoder.readObject();
//        decoder.close();
//        fileInputStream.close();
//        return object;
        String jsonuser = "";
        Scanner in = new Scanner(new File(filePath));
        while(in.hasNext())
            jsonuser += in.nextLine();
        in.close();
        System.out.println(jsonuser);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        CollectionType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Object.class);
        Object arrayListResult = mapper.readValue(jsonuser, type);
        return arrayListResult;
    }
}
