package Plugins;

import org.apache.commons.codec.binary.Base64;

public class JBase64 {
    public String encode(byte[] objectBytes) {
        return Base64.encodeBase64String(objectBytes);
    }

    public byte[] decode(String encodedObject) {
        return Base64.decodeBase64(encodedObject);
    }
}
