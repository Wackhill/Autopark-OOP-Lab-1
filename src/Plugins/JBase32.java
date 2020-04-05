package Plugins;

import org.apache.commons.codec.binary.Base32;

public class JBase32 {
    public String encode(byte[] objectBytes) {
        Base32 base32 = new Base32();
        return new String(base32.encode(objectBytes));
    }

    public byte[] decode(String encodedObject) {
        Base32 base32 = new Base32();
        return base32.decode(encodedObject);
    }
}
