//java uses utf-8 encoding when reading in from the scanner


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String filePath;
        System.out.println("Welcome to TCSS487 Encrypter.");
        System.out.println("Program by Andrew, Jasmine, and Max");
        System.out.println("***********************************");
        System.out.println("Type the number from the list to select input type:");
        System.out.println("1. From .txt file");
        System.out.println("2. From console input");
        // declares
        byte[] fileBytes = new byte[1];

        // getting input from user
        Scanner in = new Scanner(System.in);


        int selection = in.nextInt();
        if (selection == 1){
            System.out.println("Please input your path:");
            in = new Scanner(System.in);
            filePath = in.nextLine();
            try {
                fileBytes = Files.readAllBytes(Paths.get(filePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (selection == 2){
            // TODO: still working on this
            System.out.println("In Progress");
        } else {
            System.out.println("Invalid selection. Program ending");
            System.exit(0);
        }



        if (args.length > 0){
            byte[] btest;
            //System.out.println(args[0]);
            //testing user input bytes
            for (int i = 0; i < args.length; i++){
                btest = args[i].getBytes();
                for (byte b : btest){
                    for (int j = 7; j >= 0; j--){
                        System.out.print((b >> j) & 1);
                    }
                    System.out.print(" ");
                    System.out.print("= " + b + ", ");
                }
            }
        }
    }
}