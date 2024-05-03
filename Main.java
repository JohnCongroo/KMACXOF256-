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
 *
 * All References:
 * NIST Special Publication 800-185 <https://dx.doi.org/10.6028/NIST.SP.800-185>
 * Markku-Juhani Saarinen’s C implementation: <https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c>
 * Conversion of long and byte arrays: <https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java>
 * UWT TCSS487 Cryptography KMACXOF256 slides by Paulo Barreto
 *
 * @author Andrew Holmes, Jasmine Sellers, Max Yim
 * @version 3 May 2024
 */
public class Main {
    /** value of right_encode(0) */
    private static byte[] right_encode = {0, 1};

    /**
     * KECCAK Message Authentication Code (KMAC) used as a XOF.
     * @param K the key of any length including zero
     * @param X the main input/message string of any length including zero
     * @param L the requested output length in bits
     * @param S (optional) the customizable string
     * @return val the output val
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

        //SPONGE
        //intialize internal state
        sha3_ctx_t c = new sha3_ctx_t();
        //32 bytes at a time for kmacxof256 specifications
        Sha3.sha3_init(c, 32);

        //absorb stage of sponge
        Sha3.sha3_update(c, finalString, finalString.length);
        //padding for extending
        Sha3.shake_xof(c);

        //squeeze
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

        if (args.length > 0) {
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

    /**
     * Compute a cryptographic hash h of a byte array m:
     * @param m the message
     * @return cryptographic_hash of m
     */
    public static byte[] cryptographic_hash(byte[] m) {
        return KMACXOF256("".getBytes(),  m, 512, "D".getBytes());
    }
    /**
     * Compute an authentication tag (MAC) of a given file under a give passphrase.
     * @param m the contents in the given file.
     * @param pw the given passphrase.
     * @return an authentication tag (MAC) of a given file under a given passphrase.
     */
    public static byte[] authentication_tag(byte[] m, byte[] pw) {
        return KMACXOF256(pw, m, 512, "T".getBytes());
    }

    /**
     * Encrypts a byte array m symmetrically under passphrase pw:
     * @param m message to be encrypted in a byte string
     * @param password byte string of password to use as key
     * @return symmetric crytpogram (z,c,t)
     */
    public static byte[] encrypt(byte [] m, byte[] password){
        // z is randomized bits
        byte[] z = new SecureRandom().generateSeed(64);
        byte[] zAndPw = new byte[z.length + password.length];
        System.arraycopy(z, 0, zAndPw, 0, z.length);
        System.arraycopy(password, 0, zAndPw, z.length, password.length);

        byte[] keAndKa = KMACXOF256(zAndPw, "".getBytes(), 1024, "S".getBytes());
        //splitting keAndKa in half into two arrays
        byte[] ke = new byte[keAndKa.length / 2];
        byte[] ka = new byte[keAndKa.length / 2];
        System.arraycopy(keAndKa, 0, ke, 0, ke.length);
        System.arraycopy(keAndKa, ke.length, ka, 0, ka.length);

        // c is the encrypted message, uses ke
        byte[] c = KMACXOF256(ke, "".getBytes(), m.length * 8, "SKE".getBytes());
        // XOR m onto c
        for (int i = 0; i < c.length; i++) {
            c[i] ^= m[i];
        }
        // building auth tag t
        byte[] t = KMACXOF256(ka, m, 512, "SKA".getBytes());
        // zct is combined symmetric cryptogram
        byte[] zct;
        zct = new byte[z.length + c.length + t.length];
        System.arraycopy(z, 0, zct, 0, z.length );
        System.arraycopy(c, 0, zct, z.length, c.length );
        System.arraycopy(t, 0, zct, z.length + c.length, t.length );
        return zct;
    }

    /**
     * Decrypting a symmetric cryptogram (z,c,t) under passphrase pw:
     * @param zct symmetric cryptogram as byte string
     * @param password key as byte string
     * @return decoded message as byte string
     */
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

    /**
     * Helper method that converts the contents of the file to a byte array.
     * @param name the file name
     * @return the byte array containing the contents from the file
     */
    private static byte[] fileToByteArray(String name){
        byte[] fileBytes = new byte[0];
        try {
            fileBytes = Files.readAllBytes(Paths.get(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileBytes;
    }

    /**
     * Helper method that writes the contents from the byte array to the file.
     * @param output the byte array containing content to be copied over
     * @param name the output file name
     */
    private static void byteArrayToFile(byte[] output, String name){
        try {
            OutputStream stream = new FileOutputStream(name);
            stream.write(output);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Testing method to generate random byte files to use for testing.
     * @param numBytes the number of bytes to output
     * @param filename the file name
     */
    private static void randomByteFileGenerator(int numBytes, String filename){
        byte[] a = new SecureRandom().generateSeed(numBytes);
        byteArrayToFile(a, filename);
    }
}