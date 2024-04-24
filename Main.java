//java uses utf-8 encoding when reading in from the scanner


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
<<<<<<< Updated upstream
=======
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
        byte[] correctInput = new byte[202];
        System.arraycopy(kecString, 0, correctInput, 0, 202);

        byte[] val = new byte[L >>> 3];        

        sha3_ctx_t c = new sha3_ctx_t();
        Sha3.sha3_init(c, 32);
        Sha3.sha3_update(c, correctInput, correctInput.length);

        
        Sha3.shake_xof(c);
        Sha3.shake_out(c, val, L >>> 3);
        return val; // SHAKE256(X, L) = KECCAK512(X||1111, L) or KECCAK512(prefix || X || 00, L)
    } 

    // cryptographic hash function
    public static byte[] cryptographic_hash(byte[] m) {
        return new byte[10];
    }
    
>>>>>>> Stashed changes
    public static void main(String[] args) {
        System.out.println("Welcome to TCSS487 Encrypter.");
        System.out.println("Program by Andrew, Jasmine, and Max");
        System.out.println("***********************************");
        System.out.println("Choose a number from the list to encrypt or decrypt:");
        System.out.println("1: Encrypt");
        System.out.println("2: Decrypt");
        Scanner in = new Scanner(System.in);
        byte[] fileBytes = new byte[1];
        // this handles incorrect input
        int selection = inputChecker();

<<<<<<< Updated upstream
        if (selection == 1) {
            // encryption menu
            System.out.println("**********************************");
            System.out.println("Encryption selected.");
            System.out.println("Type the number from the list to select input type:");
            System.out.println("1. From .txt file");
            System.out.println("2. From console input");
            // declares

            selection = inputChecker();
            // Encryption from File
            if (selection == 1){
                // Encryption from File selection
                System.out.println("**********************************");
                System.out.println("Please input your path:");
                in = new Scanner(System.in);
                String filePath = in.nextLine();
                try {
                    fileBytes = Files.readAllBytes(Paths.get(filePath));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            // Encryption from console menu
            } else if (selection == 2){
                System.out.println("**********************************");
                System.out.println("Please input your text to be encrypted:");
                String consoleInput = in.next();
                fileBytes = consoleInput.getBytes();

            } else {
                System.out.println("**********************************");
                System.out.println("Invalid selection. Program ending");
                System.exit(0);
=======
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
>>>>>>> Stashed changes
            }

            //TODO: use fileBytes in the new created functions
            //TODO: output encrypted byte[] to byte file.

        // Decryption Menu
        }else if (selection == 2) {
            //TODO: implement decryption function.
            System.out.println("**********************************");
            System.out.println("Decryption selected.");
            System.out.println("Type the number from the list to select input type:");
            System.out.println("1. From file");
            System.out.println("2. From console input");
            in = new Scanner(System.in);
            // checks for valid input
            selection = inputChecker();

            // decrypt from file
            if (selection == 1){
                System.out.println("**********************************");
                System.out.println("Please input your path:");
                String filePath = in.nextLine();
                try {
                    fileBytes = Files.readAllBytes(Paths.get(filePath));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            //decrypt from console input
            } else if (selection == 2){
                System.out.println("**********************************");
                System.out.println("Please input your bits to be decrypted:");
                String consoleInput = in.next();
                // TODO: read in bytes in console, parse into byte array, Waiting for details on input.

            } else {
                System.out.println("**********************************");
                System.out.println("Invalid selection. Program ending");
                System.exit(0);
            }
            //TODO: apply decryption method
            //TODO: write decrypted message to file.

        } else {
            System.out.println("Invalid input, exiting program");
            System.exit(0);
        }
        in.close();






//        if (args.length > 0){
//            byte[] btest;
//            //System.out.println(args[0]);
//            //testing user input bytes
//            for (int i = 0; i < args.length; i++){
//                btest = args[i].getBytes();
//                for (byte b : btest){
//                    for (int j = 7; j >= 0; j--){
//                        System.out.print((b >> j) & 1);
//                    }
//                    System.out.print(" ");
//                    System.out.print("= " + b + ", ");
//                }
//            }
//        }
    }
    // this handles input checks for values 1 and 2
    private static int inputChecker(){
        Scanner in = new Scanner(System.in);
        int selection;
        while(true){
            String value = in.next();
            if (value.equals("1") || value.equals("2")){
                selection = Integer.parseInt(value);
                break;
            }else {
                System.out.println("Invalid input.");
                System.out.println("Input 1 or 2.");
                in = new Scanner(System.in);
            }
        }
        return selection;
    }
}