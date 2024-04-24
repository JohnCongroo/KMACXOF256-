
public class Internal {
    public static byte[] bytepad(byte[] x, int w){
        // 1
        byte[] lEncode = left_encode(w);
        byte[] z = new byte[lEncode.length + x.length];
        System.arraycopy(lEncode, 0, z, 0, lEncode.length);
        System.arraycopy(x, 0, z, lEncode.length, x.length);
        //2 check not necessary, java works in bytes, will be encoded to be divisible by 8
        //3
        int i = 0;
        byte[] z_padding = new byte[z.length];
        while ((z_padding.length / 8) % w != 0){
            z_padding = new byte[z.length + i];
            i++;
        }
        System.arraycopy(z, 0, z_padding, 0, z.length);
        return z_padding;
    }
	public static byte[] encode_string(byte[] S) {
		if (0 <= S.length && S.length < Math.pow(2, 2040)) {
			byte[] left = left_encode(S.length);
			byte[] byteString = new byte[left.length + S.length];
			
			
			// 1. Return left_encode(len(S)) || S.
			int index = 0;
			for (byte encode: left) {
				byteString[index] = encode;
				index++;
			} 
			for (byte encode: S) {
				byteString[index] = encode;
				index++;
			} 
			return byteString;
		} else {
            throw new IllegalArgumentException("error");
        }
	}
	
    public static byte[] right_encode(int x){
        //validate input
        if (x>= 0 && x < Math.pow(2,2040)) {

            //1. Let n be the smallest positive integer for which 28n > x
            int n = 1;
            while (Math.pow(2, 8*n) <= x){
                n++;
            }
            //System.out.println(n);

            //2. Let x1, x2, …, xn be the base-256...
            int[] xi = new int[n + 1];
            for (int i = n; i >= 1; i--){
                xi[i] = (x % 256);
                x = x / 256;
                //System.out.println(xi[i]);
            }

            //3. Let Oi = enc8(xi), for i = 1 to n.
            byte[] byteString = new byte[n + 1];
            for (int i = 1; i < n + 1; i++){
                byteString[i - 1] = (byte) xi[i];
                //System.out.println(byteString[i]);
            }

            //4. Let On+1 = enc8(n).
            byteString[n] = (byte) n;

	    //5. Return O = O0 || O1 || … || On−1 || On.
            return byteString;
        } 
        else {
            throw new IllegalArgumentException("error");
        }
    }
    
    public static byte[] left_encode(int x){
        //validate input
        if (x>= 0 && x < Math.pow(2,2040)) {

            //1. Let n be the smallest positive integer for which 2^(8n) > x
            int n = 1;
            while (Math.pow(2, 8*n) <= x){
                n++;
            }
            //System.out.println(n);

            //2. Let x1, x2, …, xn be the base-256...
            int[] xi = new int[n + 1];
            for (int i = n; i >= 1; i--){
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

            //5. Return O = O0 || O1 || … || On−1 || On.
            return byteString;
        } 
        else {
            throw new IllegalArgumentException("error");
        }
    }

    //test
    public static void main(String[] args) {
        byte[] rightYield = right_encode(314);
        byte[] leftYield = left_encode(314);

        System.out.println("right encode output");
        print_bytes(rightYield);
        System.out.println();
        System.out.println("left encode output");
        print_bytes(leftYield);



        //byte[] test = encode_string("");


    }

    //helper to print bits for each byte in byte string
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
}


//clarifications needed:

//use unnamed pacakges but Java BigInteger needs to be used to reach 2^2048 - 1 upper bound
//text file and bonus user input implementation both needed?

//bits are signed in java, the c code given uses uint64_t to prevent unsigned bits.

//
