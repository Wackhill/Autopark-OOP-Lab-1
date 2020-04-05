package Plugins;

import java.util.Base64;

public class CBase64 {
    public static String encode(byte[] objectBytes) {
        return new String(Base64.getEncoder().encode(objectBytes));
    }

    public static byte[] decode(String encodedObject) {
        return Base64.getDecoder().decode(encodedObject);
    }
}
