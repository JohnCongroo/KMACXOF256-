//java uses utf-8 encoding when reading in from the scanner
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.security.SecureRandom;

/**
 * Runs symmetric functionality based on SHA-3 (Keccak) depending on command line input.
 * Functionalities:
 * - cryptographic hash (text input or file)
 * - authentication tag (text input or file)
 * - encryption
 * - decryption
 * Sources:
 * NIST Special Publication 800-185 <https://dx.doi.org/10.6028/NIST.SP.800-185>
 * @author Max Yim, Jasmine Sellers, Andrew Holmes
 * @version 3 May 2024
 */
public class Main {
    private static byte[] right_encode = {0, 1};

    /**
     * KECCAK Message Authentication Code (KMAC) used as a XOF.
     * @param K the key of any length including zero
     * @param X the main input/message string of any length including zero
     * @param L the requested output length in bits
     * @param S (optional) the customizable string
     * @return
     */
    public static byte[] KMACXOF256(byte[] K, byte[] X, int L, byte[] S) {
        if ((L & 7) != 0) {
            throw new RuntimeException("Implementation restriction: " +
                    "output length (in bits) must be a multiple of 8");
        }

        // KMACXOF256 preprocessing: newX = bytepad(encode_string(K), 136) || X || right_encode(0).
        byte[] bytepadK = Internal.bytepad(Internal.encode_string(K), 136);
        byte[] newX = new byte[bytepadK.length + X.length + right_encode.length];
        System.arraycopy(bytepadK, 0, newX, 0, bytepadK.length);
        System.arraycopy(X, 0, newX, bytepadK.length, X.length);
        System.arraycopy(right_encode, 0, newX, bytepadK.length + X.length, right_encode.length);

        // cSHAKE256 preprocessing: bytepad(encode_string(N) || encode_string(S), 136) || X
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
        sha3_ctx_t c = new sha3_ctx_t();
        Sha3.sha3_init(c, 32);
        Sha3.sha3_update(c, finalString, finalString.length);
        Sha3.shake_xof(c);
        byte[] val = new byte[L>>>3];
        Sha3.shake_out(c, val, L>>>3);
        return val;
    }

    /**
     * Runs the symmetric functionalities (arguments specified below):
     * Cryptographic Hash Instructions:
     *     <program_name> hash <filename_in> <filename_out>
     * Authentication tag (MAC) Instructions:
     *     <program_name> auth <filename_in> <filename_out> <passphrase_file>
     * Encrypt Instructions:
     *     <program_name> encrypt <filename_in> <filename_out> <passphrase_file>
     * Decrypt Instructions:
     *     <program_name> decrypt <filename_in> <filename_out> <passphrase_file>
     * Hash_keyboard Instructions (Bonus):
     *     <program_name> hash_keyboard <filename_out> <Typed Input>
     * Auth_keyboard Instructions (Bonus):
     *     <program_name> auth_keyboard <filename_out> <passphrase_file> <Typed input>
     * @param args
     */
    public static void main(String[] args) {

        byte[] zct = new byte[0];
        byte[] pw = new byte[0];
        byte[] m = new byte[0];
        byte[] h = new byte[0];
        String keyboard = "";
        String fileName = "";
        /*
        randomByteFileGenerator(8, "randomKey1");
        randomByteFileGenerator(23, "randomKey2");
        randomByteFileGenerator(16, "randomKey3");
        randomByteFileGenerator(2, "randomKey4");
        randomByteFileGenerator(32, "randomMessage1");
        randomByteFileGenerator(64, "randomMessage2");
        randomByteFileGenerator(136, "randomMessage3");
        randomByteFileGenerator(212, "randomMessage4");
        */
        if (args.length > 0) {
            /*
            argument expectations/outputs:
            Hash: <program_name> hash <filepath_in> <filepath_out>
                input: byte array m from file (probably .bin)
                output: cryptographic hash h
            Auth: <program_name> auth <filepath_in> <filepath_out> <passphrase_in>
                input: byte array m, passphrase pw
                output: authentication tag t
            Encrypt: <program_name> encrypt <filepath_in> <filepath_out> <passphrase_in>
                input: byte array m, passphrase pw
                output: symmetric cryptogram (z,c,t)
            Decrypt: <program_name> decrypt <filepath_in> <filepath_out> <passphrase_in>
                input: symmetric cryptogram (z,c,t), passphrase pw
                output: decrypted byte array t_prime
             */
            switch (args[0]) {
                case "hash":
                    m = fileToByteArray(args[1]);
                    fileName = args[2];
                    h = cryptographic_hash(m);
                    byteArrayToFile(h, fileName);
                    System.out.println("Message hashed with name " + fileName);
                    break;
                case "auth":
                    m = fileToByteArray(args[1]);
                    fileName = args[2];
                    pw = fileToByteArray(args[3]);
                    byteArrayToFile(authentication_tag(m, pw), fileName);
                    System.out.println("Authentication tag written with name " + fileName);
                    break;
                case "encrypt":
                    m = fileToByteArray(args[1]);
                    fileName = args[2];
                    pw = fileToByteArray(args[3]);
                    zct = encrypt(m, pw);
                    byteArrayToFile(zct, fileName);
                    System.out.println("File encrypted with name " + fileName);
                    break;
                case "decrypt":
                    zct = fileToByteArray(args[1]);
                    fileName = args[2];
                    pw = fileToByteArray(args[3]);
                    m = decrypt(zct, pw);
                    byteArrayToFile(m, fileName);
                    System.out.println("File decrypted with name " + fileName);
                    break;
                case "hash_keyboard":
                    // hash_keyboard <output_filename> <typed input>
                    fileName = args[1];
                    keyboard = args[2];
                    for (int i = 3; i < args.length ; i++) {
                        keyboard = keyboard + ' ' + args[i];
                    }
                    m = keyboard.getBytes();
                    h = cryptographic_hash(m);
                    byteArrayToFile(h, fileName);
                    System.out.println("Message hashed with name " + fileName);
                    break;
                case "auth_keyboard":
                    // auth_keyboard <output_filename> <key_file> <typed input>
                    fileName = args[1];
                    pw = fileToByteArray(args[2]);
                    keyboard = args[3];
                    for (int i = 4; i < args.length ; i++) {
                        keyboard = keyboard + ' ' + args[i];
                    }
                    m = keyboard.getBytes();
                    byteArrayToFile(authentication_tag(m, pw), fileName);
                    System.out.println("Authentication tag written with name " + fileName);
                    break;
                default:
                    System.out.println("Invalid Argument");
                    break;
            }
        } else {
            System.out.println("No acceptable arguments.");
            System.out.println("Please use the argument hash, auth, encrypt, or decrypt to use this program");
        }
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
    public static byte[] fileToByteArray(String name){
        byte[] fileBytes = new byte[0];
        try {
            fileBytes = Files.readAllBytes(Paths.get(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileBytes;
    }
    public static void byteArrayToFile(byte[] output, String name){
        try {
            OutputStream stream = new FileOutputStream(name);
            stream.write(output);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] cryptographic_hash(byte[] m) {
        return KMACXOF256("".getBytes(),  m, 512, "D".getBytes());
    }
    public static byte[] authentication_tag(byte[] m, byte[] pw) {
        return KMACXOF256(pw, m, 512, "T".getBytes());
    }

    // encrypt
    public static byte[] encrypt(byte [] m, byte[] password){
        //encrypt
        byte[] z = new SecureRandom().generateSeed(64);
        byte[] zAndPw = new byte[z.length + password.length];
        System.arraycopy(z, 0, zAndPw, 0, z.length);
        System.arraycopy(password, 0, zAndPw, z.length, password.length);
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

        byte[] c = KMACXOF256(ke, "".getBytes(), m.length * 8, "SKE".getBytes());

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
        byte[] z = new byte[64];
        System.arraycopy(zct, 0, z, 0, z.length);
        byte[] t = new byte[64];
        System.arraycopy(zct, zct.length - t.length, t, 0, t.length);
        byte[] c = new byte[zct.length - z.length - t.length];
        System.arraycopy(zct, z.length, c, 0, c.length);

        byte[] m = new byte[0];
        byte[] tPrime = new byte[0];
        byte[] zAndPw = new byte[z.length + password.length];
        System.arraycopy(z, 0, zAndPw, 0, z.length);
        System.arraycopy(password, 0, zAndPw, z.length, password.length);
        byte[] keAndKa = KMACXOF256(zAndPw, "".getBytes(), 1024, "S".getBytes());
        byte[] ke = new byte[keAndKa.length / 2];
        byte[] ka = new byte[keAndKa.length / 2];
        System.arraycopy(keAndKa, 0, ke, 0, ke.length);
        System.arraycopy(keAndKa, ke.length, ka, 0, ka.length);
        m = KMACXOF256(ke, "".getBytes(), c.length * 8, "SKE".getBytes());
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
    public static void randomByteFileGenerator(int numBytes, String filename){
        byte[] a = new SecureRandom().generateSeed(numBytes);
        byteArrayToFile(a, filename);
    }
}