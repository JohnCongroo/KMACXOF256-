public class Internal { 

    public static byte[] left_encode(int x){
        //validate input
        if (x>= 0 && x < Math.pow(2,2040)) {

            //1. Let n be the smallest positive integer for which 28n > x
            int n = 1;
            while (Math.pow(2, 8 * n) <= x){
                n++;
            }

            //System.out.println("aegoihjaegoihjaeg");
            //System.out.println(n);

            //2. Let x1, x2, …, xn be the base-256...
            int[] xi = new int[n + 1];
            for (int i = 1; i < n + 1; i++){
                xi[i] = (x % 256);
                x = x / 256;
                //System.out.println(xi[i]);
            }

            //3. Let Oi = enc8(xi), for i = 1 to n.
            byte[] byteString = new byte[n + 1];
            for (int i = 1; i <= n; i++){
                byteString[i] = (byte) xi[i];
            }

            //4. Let O0 = enc8(n).
            byteString[0] = (byte) n;
            return byteString;
        } 
        else {
            System.out.println("fail");
            return new byte[] {1};
        }
    }

    public static void main(String[] args) {
        byte[] yield = left_encode(0);

        for (byte b : yield) {
            //check bits, prints from, clears up how bits are stored
            //reference: https://stackoverflow.com/questions/15529893/could-someone-explain-this-for-me-for-int-i-0-i-8-i
            for (int i = 0; i < 8; i++){
                int bit = (b >> i);
                System.out.print(bit);
            }
            System.out.print(" ");
        }
    }
}


//clarifications needed:

//use unnamed pacakges but Java BigInteger needs to be used to reach (2^2048) - 1 upper bound
//text file and bonus user input implementation both needed?