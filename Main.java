//java uses utf-8 encoding when reading in from the scanner


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
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
        //comment here

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