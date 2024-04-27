//java uses utf-8 encoding when reading in from the scanner
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.security.SecureRandom;

public class Main {
    private static byte[] right_encode = {0, 1};

    public static byte[] KMACXOF256(byte[] K, byte[] X, int L, byte[] S) {
        // Validity Conditions: len(K) < 2^2040 and 0 ≤ L and len(S) < 2^2040
        if ((L & 7) != 0) {
            throw new RuntimeException("Implementation restriction: " +
                    "output length (in bits) must be a multiple of 8");
        }
        //SHAKE shake = new SHAKE();
        //Sha3.kinit256(K, S); preprocess message

        // newX = bytepad(encode_string(K), 136) || X || right_encode(0).
        byte[] bytepad = Internal.bytepad(Internal.encode_string(K), 136);
        byte[] newX = new byte[bytepad.length + X.length + right_encode.length];
        System.arraycopy(bytepad, 0, newX, 0, bytepad.length);
        System.arraycopy(X, 0, newX, bytepad.length, X.length);
        System.arraycopy(right_encode, 0, newX, bytepad.length + X.length, right_encode.length);

        // N = bytepad(encode_string(N) || encode_string(S), 136) || X || 00
        byte[] encodeN = Internal.encode_string("KMAC".getBytes());
        byte[] encodeS = Internal.encode_string(S);
        byte[] encode = new byte[encodeN.length + encodeS.length];
        System.arraycopy(encodeN, 0, encode, 0, encodeN.length);
        System.arraycopy(encodeS, 0, encode, encodeN.length, encodeS.length);

        byte[] pad = Internal.bytepad(encode, 136);

        //
        byte[] kecString = new byte[pad.length + X.length + 2];
        System.arraycopy(pad, 0, kecString, 0, pad.length);
        System.arraycopy(X, 0, kecString, pad.length, X.length);
        // Need to append 2 zero bits to the end

        //hardcoding the right input
        kecString[3] = 0x20;
        kecString[9] = (byte) 0xA8;

    //hardcode newX
        newX[2] = 0x02;
        newX[3] = 0x01;

        byte[] secondStage = new byte[136];

        System.arraycopy(newX, 0, secondStage, 0, 136);
        System.arraycopy(newX, 0, secondStage, 0, 136);
        secondStage[4] = 0x00;
        System.arraycopy(newX, 4, secondStage, 5, 131);
        byte[] firstStage = new byte[136];
        System.arraycopy(kecString, 0, firstStage, 0, 136);

        byte[] actualData = new byte[136];
        actualData[1] = 0x01;
        actualData[2] = 0x02;
        actualData[3] = 0x03;
        actualData[5] = 0x01;
        actualData[6] = 0x04;
        actualData[135] = (byte) 0x80;

        sha3_ctx_t c = new sha3_ctx_t();
        Sha3.sha3_init(c, 32);    
        Sha3.sha3_update(c, firstStage, firstStage.length);
        c.pt = 0;
        Sha3.sha3_update(c, secondStage, secondStage.length);
        c.pt = 0;
        Sha3.sha3_update(c, actualData, actualData.length);

        byte[] val = new byte[L / 8]; 

        //Sha3.shake_xof(c);
        //Sha3.shake_out(c, val, L >>> 3);
        return val; // SHAKE256(X, L) = KECCAK512(X||1111, L) or KECCAK512(prefix || X || 00, L)
    } 

    // cryptographic hash function
    public static byte[] cryptographic_hash(byte[] m) {
        return new byte[10];
    }
    
    public static void main(String[] args) {
        //byte[] pw = new byte[0];
        //byte[] m = "aeioguhaeguhaeg".getBytes();
        //byte[] zct = new byte[0];
        //String outputPath = "";
        //System.out.println("asdasd".getBytes().getClass());
        byte[] input = {0x00, 0x01, 0x02, 0x03};
        byte[] key = {0x40, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47,
             0x48, 0x49, 0x4A, 0x4B, 0x4C, 0x4D, 0x4E, 0x4F, 0x50, 0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x5B, 0x5C, 0x5D, 0x5E, 0x5F};
        byte[] finalOutput = KMACXOF256(key, input, 512, "My Tagged Application".getBytes());

        System.out.println("asdasdasd");
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

    private static void print_bytes(byte[] byteString){
        for (byte b : byteString) {
            //reference: https://stackoverflow.com/questions/9280654/c-printing-bits
                        
            //print bits
            for (int i = 7; i >= 0; i--){
                int bit = (b >> i) & 1;
                System.out.print(bit);
            }
            
            System.out.print(" ");
            System.out.print("= " + b + ", ");
        }
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