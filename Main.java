//java uses utf-8 encoding when reading in from the scanner
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.security.SecureRandom;

public class Main {
    private static byte[] right_encode = {0, 1};

    // IMPLEMENTATION
    public static byte[] KMACXOF256(byte[] K, byte[] X, int L, byte[] S) {
        if ((L & 7) != 0) {
            throw new RuntimeException("Implementation restriction: " +
                    "output length (in bits) must be a multiple of 8");
        }

        // Preprocessing
        byte[] bytepadK = Internal.bytepad(Internal.encode_string(K), 136);
        byte[] newX = new byte[bytepadK.length + X.length + right_encode.length];
        System.arraycopy(bytepadK, 0, newX, 0, bytepadK.length);
        System.arraycopy(X, 0, newX, bytepadK.length, X.length);
        System.arraycopy(right_encode, 0, newX, bytepadK.length + X.length, right_encode.length);

        byte[] encodeN = Internal.encode_string("KMAC".getBytes());
        byte[] encodeS = Internal.encode_string(S);
        byte[] encodeNS = new byte[encodeN.length + encodeS.length];
        System.arraycopy(encodeN, 0, encodeNS, 0, encodeN.length);
        System.arraycopy(encodeS, 0, encodeNS, encodeN.length, encodeS.length);
        byte[] bytepadNS = Internal.bytepad(encodeNS, 136);

        byte[] finalString = new byte[bytepadNS.length + newX.length];
        System.arraycopy(bytepadNS, 0, finalString, 0, bytepadNS.length);
        System.arraycopy(newX, 0, finalString, bytepadNS.length, newX.length);

        // KECCAK
        Sha3 sha3 = new Sha3();
        sha3_ctx_t c = new sha3_ctx_t();
        Sha3.sha3_init(c, 32);
        
        for (int i = 0; i < finalString.length / 136; i++){
            Sha3.sha3_update(c, Arrays.copyOfRange(finalString, i * 136, (i + 1) * 136), 136);
        }

        //non ambiguity principle assume padding is always there
        byte[] paddedData = new byte[136];
        System.arraycopy(finalString, finalString.length/136 * 136, paddedData, 0, finalString.length % 136);
        if (finalString.length % 136 == 135) {
            paddedData[135] = 0x48;
        } else {
            paddedData[finalString.length % 136] = 0x04;
            paddedData[135] = (byte) 0x80;
        }
        Sha3.sha3_update(c, paddedData, paddedData.length);
        byte[] val = Arrays.copyOfRange(c.b, 0, L / 8);
        return val;
    }

    // cryptographic hash function
    public static byte[] cryptographic_hash(byte[] m) {
        return KMACXOF256("".getBytes(),  m, 512, "D".getBytes());
    }


    public static void main(String[] args) {
        byte[] m = {0x00, 0x01, 0x02, 0x03};
        byte[] test = {
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
            0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17,
            0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F,
            0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27,
            0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D, 0x2E, 0x2F,
            0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,
            0x38, 0x39, 0x3A, 0x3B, 0x3C, 0x3D, 0x3E, 0x3F,
            0x40, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47,
            0x48, 0x49, 0x4A, 0x4B, 0x4C, 0x4D, 0x4E, 0x4F,
            0x50, 0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57,
            0x58, 0x59, 0x5A, 0x5B, 0x5C, 0x5D, 0x5E, 0x5F,
            0x60, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67,
            0x68, 0x69, 0x6A, 0x6B, 0x6C, 0x6D, 0x6E, 0x6F,
            0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77,
            0x78, 0x79, 0x7A, 0x7B, 0x7C, 0x7D, 0x7E, 0x7F,
            (byte)0x80, (byte)0x81, (byte)0x82, (byte)0x83
            
        };

        System.out.println(test.length);

        byte[] zct = new byte[0];
        byte[] pw = {0x40, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47,
                0x48, 0x49, 0x4A, 0x4B, 0x4C, 0x4D, 0x4E, 0x4F, 0x50, 0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x5B, 0x5C, 0x5D, 0x5E, 0x5F};
        byte[] finalOutput = KMACXOF256(pw, test, 512, "My Tagged Application".getBytes());

        byte[] a = finalOutput;
        /* 
        if (args.length > 0) {
            /*
            argument expectations/outputs:
            Hash: <program_name> hash <filepath_m>
                input: byte array m from file (probably .bin)
                output: cryptographic hash h
            Auth: <program_name> auth <filepath_m> <passphrase_pw>
                input: byte array m, passphrase pw
                output: authentication tag t
            Encrypt: <program_name> encrypt <filepath_m> <passphrase_pw>
                input: byte array m, passphrase pw
                output: symmetric cryptogram (z,c,t)
            Decrypt: <program_name> decrypt <filepath_(zct)> <passphrase_pw>
                input: symmetric cryptogram (z,c,t), passphrase pw
                output: decrypted byte array t_prime
             */
             /* 
            switch (args[0]) {
                case "hash":
                    m = fileToByteArray(args[1]);
                    byte[] h = cryptographic_hash(m);
                    // TODO: file output
                    System.out.println("Hash Algorithm");
                    break;
                case "auth":
                    m = fileToByteArray(args[1]);
                    pw = args[2].getBytes();
                    // TODO: place function
                    // TODO: file output
                    System.out.println("Auth Algorithm");
                    break;
                case "encrypt":
                    m = fileToByteArray(args[1]);
                    pw = args[2].getBytes();
                    zct = encrypt(m, pw);
                    // TODO: file output
                    System.out.println("encrypt Algorithm");
                    break;
                case "decrypt":
                    zct = fileToByteArray(args[1]);
                    pw = args[2].getBytes();
                    m = decrypt(zct, pw);
                    // TODO: file output
                    System.out.println("decrypt Algorithm");
                    break;
                default:
                    System.out.println("Invalid Argument");
                    break;
            }
        } else {
            System.out.println("No acceptable arguments.");
            System.out.println("Please use the argument hash, auth, encrypt, or decrypt to use this program");
        }
        */
    }

    private static String print_bytes(byte[] byteString){
            StringBuilder hexBuilder = new StringBuilder();
            for (byte b : byteString) {
                hexBuilder.append(String.format("%02X", b));
                hexBuilder.append(' ');
            }
            String hexString = hexBuilder.toString();
            System.out.println(hexString);
            return hexString;
    }
    public static byte[] fileToByteArray(String path){
        byte[] fileBytes = new byte[0];
        try {
            fileBytes = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileBytes;
    }

    public byte[] authentication_tag(byte[] m, byte[] pw) {
        return KMACXOF256(pw, m, 512, "T".getBytes());
    }

    // encrypt
    public static byte[] encrypt(byte [] m, byte[] password){
        //encrypt
        byte[] z = new SecureRandom().generateSeed(256);
        byte[] zAndPw = new byte[z.length + password.length];
        // (ke || ka) is equal to the function call of kmacxof256 with the following paramaters
        // z (secure random number)
        // pw passphrase (password)
        // these two are combined above into zAndPw
        // 1024 = N, a function-name bit string, used by NIST to define functions based on cSHAKE
        // "S" = is a customization bit string. The user selects this string to define a variant of the function.
        //      when no customization is desired, S is set to the empty string
        byte[] S = new byte[0];
        byte[] keAndKa = KMACXOF256(zAndPw, "".getBytes(), 1024, "S".getBytes());
        //splitting keAndKa in half into two arrays
        // not checking for rounding, as kmax should return a 256 bit string
        byte[] ke = new byte[keAndKa.length / 2];
        byte[] ka = new byte[keAndKa.length / 2];
        System.arraycopy(keAndKa, 0, ke, 0, ke.length);
        System.arraycopy(keAndKa, ke.length, ka, 0, ka.length);

        // c is the encrypted message
        // |m| is the length of the message m
        // ^ is the xor operator

        byte[] c = KMACXOF256(ke, "".getBytes(), m.length, "SKE".getBytes());
        for (int i = 0; i < c.length; i++) {
            c[i] ^= m[i];
        }
        byte[] t = KMACXOF256(ka, m, 512, "SKA".getBytes());
        byte[] zct;
        zct = new byte[z.length + c.length + t.length];
        System.arraycopy(z, 0, zct, 0, z.length );
        System.arraycopy(c, 0, zct, z.length, c.length );
        System.arraycopy(t, 0, zct, z.length + c.length, t.length );
        return zct;
    }
    public static byte[] decrypt(byte [] zct, byte[] password){
        // get z, c, t, from array start and end positions of byte zct input
        byte[] z = new byte[256];
        System.arraycopy(zct, 0, z, 0, z.length);
        byte[] c = new byte[256];
        System.arraycopy(zct, z.length, c, 0, c.length);
        byte[] t = new byte[256];
        System.arraycopy(zct, z.length + c.length, t, 0, t.length);
        byte[] m = new byte[0];
        byte[] tPrime = new byte[0];
        byte[] zAndPw = new byte[z.length + password.length];
        byte[] keAndKa = KMACXOF256(zAndPw, "".getBytes(), 1024, "S".getBytes());
        byte[] ke = new byte[keAndKa.length / 2];
        byte[] ka = new byte[keAndKa.length / 2];
        System.arraycopy(keAndKa, 0, ke, 0, ke.length);
        System.arraycopy(keAndKa, ke.length, ka, 0, ka.length);
        m = KMACXOF256(ke, "".getBytes(), c.length, "SKE".getBytes());
        for (int i = 0; i < m.length; i++) {
            m[i] ^= c[i];
        }
        tPrime = KMACXOF256(ka, m, 512, "SKA".getBytes());
        if (Arrays.equals(tPrime, t)){
            return m;
        } else {
            System.out.println("Decryption failed.");
            return new byte[0];
        }
    }
}