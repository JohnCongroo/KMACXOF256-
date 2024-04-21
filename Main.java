//java uses utf-8 encoding when reading in from the scanner
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.security.SecureRandom;

public class Main {
    public static byte[] KMACXOF256(byte[] K, byte[] X, int L, byte[] S) {
        // Validity Conditions: len(K) < 2^2040 and 0 ≤ L and len(S) < 2^2040
        if ((L & 7) != 0) {
            throw new RuntimeException("Implementation restriction: " +
                    "output length (in bits) must be a multiple of 8");
        }
        byte[] output = new byte[10];
        byte[] val = new byte[L >>> 3];
        //SHAKE shake = new SHAKE();
        //Sha3.kinit256(K, S); preprocess message
        sha3_ctx_t c = new sha3_ctx_t();
        
        //sha3
        Sha3.sha3_init(c, L);
        Sha3.sha3_update(c, X, X.length);
        Sha3.sha3_final(output, c);
        return val; // SHAKE256(X, L) = KECCAK512(X||1111, L) or KECCAK512(prefix || X || 00, L)
    } 
    public static void main(String[] args) {
        //byte[] m = "aeioguhaeguhaeg".getBytes();
        //byte[] pw = new byte[0];
        //byte[] zct = new byte[0];
        //String outputPath = "";
        //System.out.println("asdasd".getBytes().getClass());

        cryptographic_hash("asdasd".getBytes());

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
    

    // file to bytes converter
    public static byte[] fileToByteArray(String path){
        byte[] fileBytes = new byte[0];
        try {
            fileBytes = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileBytes;
    }

    // cryptographic hash function
    public static byte[] cryptographic_hash(byte[] m) {
        return KMACXOF256("".getBytes(), m, 512, "D".getBytes());
    }
/* 
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
        byte[] keAndKa = KMACXOF256(zAndPw, "", 1024, "S".getBytes());
        //splitting keAndKa in half into two arrays
        // not checking for rounding, as kmax should return a 256 bit string
        byte[] ke = new byte[keAndKa.length / 2];
        byte[] ka = new byte[keAndKa.length / 2];
        System.arraycopy(keAndKa, 0, ke, 0, ke.length);
        System.arraycopy(keAndKa, ke.length, ka, 0, ka.length);

        // c is the encrypted message
        // |m| is the length of the message m
        // ^ is the xor operator

        byte[] c = KMACXOF256(ke, "", m.length, "SKE".getBytes()) ^ m;
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
        byte[] keAndKa = KMACXOF256(zAndPw, "", 1024, "S".getBytes());
        byte[] ke = new byte[keAndKa.length / 2];
        byte[] ka = new byte[keAndKa.length / 2];
        System.arraycopy(keAndKa, 0, ke, 0, ke.length);
        System.arraycopy(keAndKa, ke.length, ka, 0, ka.length);
        m = KMACXOF256(ke, "", c.length, "SKE".getBytes()) ^ c;
        tPrime = KMACXOF256(ka, m, 512, "SKA".getBytes());
        if (Arrays.equals(tPrime, t)){
            return m;
        } else {
            System.out.println("Decryption failed.");
            return new byte[0];
        }
        // literally so i can commit
    }
*/

}