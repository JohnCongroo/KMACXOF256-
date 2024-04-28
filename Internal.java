
public class Internal {
    // REMOVE //
//    private static byte convertToLittleEndian(byte bigEndian) {
//        byte littleEndian = 0;
//        for (int i = 0; i < 8; i++) {
//            littleEndian |= ((bigEndian >> i) & 1) << (7 - i);
//        }
//        return littleEndian;
//    }

    // KEEP //
    public static byte[] bytepad(byte[] X, int w){
        // 1. z = left_encode(w) || X.
        byte[] leftEncode = left_encode(w);
        byte[] z = new byte[leftEncode.length + X.length];
        System.arraycopy(leftEncode, 0, z, 0, leftEncode.length);
        System.arraycopy(X, 0, z, leftEncode.length, X.length);

        int appendBytes = 0;
        while ((z.length + appendBytes) * 8 / 8 % w != 0) {
            appendBytes++;
        }

        byte[] final_bytepad = new byte[z.length + appendBytes];
        System.arraycopy(z, 0, final_bytepad, 0, z.length);

        return final_bytepad;
    }

    public static byte[] encode_string(byte[] S) {
        if (0 <= S.length && S.length < Math.pow(2, 2040)) {
            // 1. Return left_encode(len(S)) || S.
            byte[] left = left_encode(S.length * 8);
            byte[] encodedString = new byte[left.length + S.length];

            System.arraycopy(left, 0, encodedString, 0, left.length);
            System.arraycopy(S, 0, encodedString, left.length, S.length);
            return encodedString;
        } else {
            throw new IllegalArgumentException("error");
        }
    }
    public static byte[] left_encode(int x){
        //validate input
        if (x>= 0 && x < Math.pow(2,2040)) {
            //1. Let n be the smallest positive integer for which 2^(8n) > x
            int n = 1;
            while (Math.pow(2, 8 * n) <= x) {
                n++;
            }
            //2. Let x1, x2, …, xn be the base-256...
            byte[] xi = new byte[n + 1];
            for (int i = n; i >= 1; i--) {
                xi[i] = ((byte) (x % 256));
                x /= 256;
            }
            xi[0] = (byte) n;
            return xi;
        }
        else {
            throw new IllegalArgumentException("error");
        }
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
