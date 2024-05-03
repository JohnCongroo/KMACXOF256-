/**
 * Contains supporting functions: bytepad, encode_string, left_encode
 *
 * Sources:
 * NIST Special Publication 800-185 <https://dx.doi.org/10.6028/NIST.SP.800-185>
 * @author Max Yim, Jasmine Sellers, Andrew Holmes
 * @version 3 May 2024
 */
public class Internal {
    /**
     * Prepends X with the left encode of the integer w. It then pads the result with zeros until the length
     * in bytes is a multiple of w.
     * @param X the input string
     * @param w the integer
     * @return the bytepaded X
     * @throws IllegalArgumentException if w <= 0
     */
    public static byte[] bytepad(byte[] X, int w){
        if (w > 0) {
            // 1. z = left_encode(w) || X.
            byte[] leftEncode = left_encode(w);
            byte[] z = new byte[leftEncode.length + X.length];
            System.arraycopy(leftEncode, 0, z, 0, leftEncode.length);
            System.arraycopy(X, 0, z, leftEncode.length, X.length);

            // 3. while (len(z)/8) mod w ≠ 0:
            int appendBytes = 0;
            while ((z.length + appendBytes) * 8 / 8 % w != 0) {
                appendBytes++;
            }
            byte[] final_bytepad = new byte[z.length + appendBytes];
            System.arraycopy(z, 0, final_bytepad, 0, z.length);
            return final_bytepad;
        } else {
            throw new IllegalArgumentException("error");
        }
    }

    /**
     * Concatenates the left encode of the string S to S.
     * @param S byte[] the string to encode
     * @return the encoded string
     * @throws IllegalArgumentException if X is < 0 or x > 2^2040
     */
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

    /**
     * Prepends the length of the byte string of x to the encoded integer x as a byte string.
     * @param x int the integer to encode as a byte string
     * @return byte[] the left encoded byte string of x.
     * @throws IllegalArgumentException if X is < 0 or x > 2^2040
     */
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
}
