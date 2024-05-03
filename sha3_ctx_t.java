
/**
 * Contains the internal state of the sponge
 *
 * References:
 * Conversion of long and byte arrays: <https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java>
 *
 * @author Andrew Holmes, Jasmine Sellers, Max Yim
 * @version 3 May 2024
 */

public class sha3_ctx_t{
    public byte[] b;
    public long[] q;
    public int pt, rsiz, mdlen;

    public sha3_ctx_t(){
        b = new byte[200];
        q = new long[25];
        pt = 0; //pointer for position of state array
        rsiz = 0; //pointer for end of position
        mdlen = 0; //size of message digest length
    }

    /**
     * update the long array with the contents of the byte array
     */
    public void update_q(){
        long temp = 0;
        for (int i = 0; i < q.length; i++) {
            temp = 0;
            for (int j = 0; j < 8; j++){
                temp = temp << 8;
                temp |= (b[i * 8 + j] & 0xFF);
            }
            q[i] = temp;
        }
    }

    /**
     * update the byte array with the contents of the long array
     */
    public void update_b(){
        long temp;
        for (int i = 0; i < q.length; i++) {
            temp = q[i];
            for (int j = 0; j < 8; j++){
                b[j+i*8] = (byte) (temp & 0xFF); 
                temp = temp >>> 8;
            }
        }
    }

}
