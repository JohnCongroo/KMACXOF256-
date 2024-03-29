public class Internal { 
	public static byte[] bytepad(byte[] X, int w) {
		if (w > 0) {
			byte[] left = left_encode(w);
			byte[] z = new byte[left.length + X.length];
			
			// 1. z = left_encode(w) || X.
			int index = 0;
			for (byte encode: left) {
				z[index] = encode;
				index++;
			}
			for (byte encode: X) {
				z[index] = encode;
				index++;
			}
			
			// 2. while len(z) mod 8 ≠ 0
			if (z.length % 8 != 0) {
				index = 0;
				byte[] temp = new byte[z.length + 1];
				for (byte encode: z) {
					temp[index] = encode;
				}
				temp[temp.length - 1] = 0;
				z = temp;
			}
			//3. while (len(z)/8) mod w ≠ 0:
			if (z.length/8 % w != 0) {
				index = 0;
				byte[] temp = new byte[z.length + 1];
				for (byte encode: z) {
					temp[index] = encode;
				}
				temp[temp.length - 1] = 00000000;
				z = temp;
			}
			return z;
		} else {
			System.out.println("fail");
            return new byte[] {1};
		}
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
            System.out.println("fail");
            return new byte[] {1};
        }
	}
	
    public static byte[] right_encode(int x){
        //validate input
        if (x>= 0 && x < Math.pow(2,2040)) {

            //1. Let n be the smallest positive integer for which 28n > x
            int n = 1;
            while (Math.pow(2, 8 * n) <= x){
                n++;
            }
            //System.out.println(n);

            //2. Let x1, x2, …, xn be the base-256...
            int[] xi = new int[n + 1];
            for (int i = n; i >= 1; i--){
                xi[i] = (x % 256);
                x = x / 256;
                System.out.println(xi[i]);
            }

            //3. Let Oi = enc8(xi), for i = 1 to n.
            byte[] byteString = new byte[n + 1];
            for (int i = 1; i < n + 1; i++){
                byteString[i - 1] = (byte) xi[i];
                System.out.println(byteString[i]);
            }

            //4. Let On+1 = enc8(n).
            byteString[n] = (byte) n;
            return byteString;
        } 
        else {
            throw new IllegalArgumentException("error");
        }
    }
    
    public static byte[] left_encode(int x){
        //validate input
        if (x>= 0 && x < Math.pow(2,2040)) {

            //1. Let n be the smallest positive integer for which 28n > x
            int n = 1;
            while (Math.pow(2, 8 * n) <= x){
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
                byteString[i] = (byte) xi[i];
            }

            //4. Let O0 = enc8(n).
            byteString[0] = (byte) n;
            return byteString;
        } 
        else {
            throw new IllegalArgumentException("error");
        }
    }

    public static void main(String[] args) {
        byte[] yield1 = right_encode(314);
        byte[] yield2 = left_encode(314);

        for (byte b : yield1) {
            //check bits, prints from, clears up how bits are stored
            //reference: https://stackoverflow.com/questions/141525/what-are-bitwise-shift-bit-shift-operators-and-how-do-they-work
            
            for (int i = 0; i < 8; i++){
                int bit = (b >> i) & 1;
                System.out.print(bit);
            }
            
            System.out.print(" ");
            System.out.print("= " + b + ", ");
        }
        System.out.println("aegaeg");

        for (byte b : yield2) {
            //check bits, prints from, clears up how bits are stored
            //reference: https://stackoverflow.com/questions/141525/what-are-bitwise-shift-bit-shift-operators-and-how-do-they-work
            
            for (int i = 0; i < 8; i++){
                int bit = (b >> i) & 1;
                System.out.print(bit);
            }
            
            System.out.print(" ");
            System.out.print("= " + b + ", ");
        }
    }
}


//clarifications needed:

//use unnamed pacakges but Java BigInteger needs to be used to reach (2^2048) - 1 upper bound
//text file and bonus user input implementation both needed?
