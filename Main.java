//java uses utf-8 encoding when reading in from the scanner


public class Main {
    public static void main(String[] args) {


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