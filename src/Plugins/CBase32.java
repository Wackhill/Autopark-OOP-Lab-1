package Plugins;

public class CBase32 {
    /* lookup table used to encode() groups of 5 bits of data */
    private static final String base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    /* lookup table used to decode() characters in Base32 strings */
    private static final byte[] base32Lookup = { 26, 27, 28, 29, 30, 31, -1,
            -1, -1, -1, -1, -1, -1, -1, // 23456789:;<=>?
            -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, // @ABCDEFGHIJKLMNO
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, // PQRSTUVWXYZ[\]^_
            -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, // `abcdefghijklmno
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 // pqrstuvwxyz
    };

    static public byte[] decode(final String base32) throws IllegalArgumentException {
        byte[] bytes = new byte[base32.length() * 5 / 8];
        int offset = 0, i = 0, lookup;
        byte nextByte, digit;
        while (i < base32.length()) {
            lookup = base32.charAt(i++) - '2';
            digit = base32Lookup[lookup];
            nextByte = (byte) (digit << 3);
            lookup = base32.charAt(i++) - '2';
            digit = base32Lookup[lookup];
            bytes[offset++] = (byte) (nextByte | (digit >> 2));
            nextByte = (byte) ((digit & 3) << 6);
            if (i >= base32.length()) {
                break;
            }
            lookup = base32.charAt(i++) - '2';
            digit = base32Lookup[lookup];
            nextByte |= (byte) (digit << 1);
            lookup = base32.charAt(i++) - '2';
            digit = base32Lookup[lookup];
            bytes[offset++] = (byte) (nextByte | (digit >> 4));
            nextByte = (byte) ((digit & 15) << 4);
            if (i >= base32.length()) {
                break;
            }
            lookup = base32.charAt(i++) - '2';
            digit = base32Lookup[lookup];
            bytes[offset++] = (byte) (nextByte | (digit >> 1));
            nextByte = (byte) ((digit & 1) << 7);
            if (i >= base32.length()) {
                break;
            }
            lookup = base32.charAt(i++) - '2';
            digit = base32Lookup[lookup];
            nextByte |= (byte) (digit << 2);
            lookup = base32.charAt(i++) - '2';
            digit = base32Lookup[lookup];
            bytes[offset++] = (byte) (nextByte | (digit >> 3));
            nextByte = (byte) ((digit & 7) << 5);
            if (i >= base32.length()) {
                break;
            }
            lookup = base32.charAt(i++) - '2';
            digit = base32Lookup[lookup];
            bytes[offset++] = (byte) (nextByte | digit);
        }
        return bytes;
    }

    static public String encode(final byte[] bytes) {
        StringBuffer base32 = new StringBuffer((bytes.length * 8 + 4) / 5);
        int currByte, digit, i = 0;
        while (i < bytes.length) {
            currByte = bytes[i++] & 255;
            base32.append(base32Chars.charAt(currByte >> 3));
            digit = (currByte & 7) << 2;
            if (i >= bytes.length) {
                base32.append(base32Chars.charAt(digit));
                break;
            }
            currByte = bytes[i++] & 255;
            base32.append(base32Chars.charAt(digit | (currByte >> 6)));
            base32.append(base32Chars.charAt((currByte >> 1) & 31));
            digit = (currByte & 1) << 4;
            if (i >= bytes.length) {
                base32.append(base32Chars.charAt(digit));
                break;
            }
            currByte = bytes[i++] & 255;
            base32.append(base32Chars.charAt(digit | (currByte >> 4)));
            digit = (currByte & 15) << 1;
            if (i >= bytes.length) { // put the last 4 bits
                base32.append(base32Chars.charAt(digit));
                break;
            }
            currByte = bytes[i++] & 255;
            base32.append(base32Chars.charAt(digit | (currByte >> 7)));
            base32.append(base32Chars.charAt((currByte >> 2) & 31));
            digit = (currByte & 3) << 3;
            if (i >= bytes.length) { // put the last 2 bits
                base32.append(base32Chars.charAt(digit));
                break;
            }
            currByte = bytes[i++] & 255;
            base32.append(base32Chars.charAt(digit | (currByte >> 5)));
            base32.append(base32Chars.charAt(currByte & 31));
        }
        return base32.toString();
    }
}
