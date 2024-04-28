//reference (given from assignment) https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c

public class Sha3 {

    static boolean areWeShaking = false;
    //private byte[] right_encode = {0, 1};

    public static long[] keccak_f(long[] st){

        //round constant
        final long keccakf_rndc[] = { 
            0x0000000000000001L, 0x0000000000008082L, 0x800000000000808aL,
            0x8000000080008000L, 0x000000000000808bL, 0x0000000080000001L,
            0x8000000080008081L, 0x8000000000008009L, 0x000000000000008aL,
            0x0000000000000088L, 0x0000000080008009L, 0x000000008000000aL,
            0x000000008000808bL, 0x800000000000008bL, 0x8000000000008089L,
            0x8000000000008003L, 0x8000000000008002L, 0x8000000000000080L,
            0x000000000000800aL, 0x800000008000000aL, 0x8000000080008081L,
            0x8000000000008080L, 0x0000000080000001L, 0x8000000080008008L
        };

        //rotation constant
        final int keccakf_rotc[] = { 
            1,  3,  6,  10, 15, 21, 28, 36, 45, 55, 2,  14,
            27, 41, 56, 8,  25, 43, 62, 18, 39, 61, 20, 44
        };

        final int keccakf_piln[] = {
            10, 7,  11, 17, 18, 3, 5,  16, 8,  21, 24, 4,
            15, 23, 19, 13, 12, 2, 20, 14, 22, 9,  6,  1
        };

        //variables
        int i, j, r;
        long t, bc[] = new long[5];

        long v;

        for (i = 0; i < 25; i++) {
            v = st[i];
            st[i] = ((long) v & 0xFFL)  << 56  | 
                    (((long) v & 0xFF00L) << 40)   |
                    (((long) v & 0xFF0000L)<< 24)  |
                    (((long) v & 0xFF000000L) << 8)  |
                    (((long) v & 0xFF00000000L) >>> 8 )  |
                    (((long) v & 0xFF0000000000L) >>> 24 )  |
                    (((long) v & 0xFF000000000000L) >>> 40 )  |
                    (((long) v & 0xFF00000000000000L) >>> 56);
            }
 
        if (areWeShaking == true){
            for (i = 0; i < 25; i++) {
                v = st[i];
                st[i] = ((long) v & 0xFFL)  << 56  | 
                        (((long) v & 0xFF00L) << 40)   |
                        (((long) v & 0xFF0000L)<< 24)  |
                        (((long) v & 0xFF000000L) << 8)  |
                        (((long) v & 0xFF00000000L) >>> 8 )  |
                        (((long) v & 0xFF0000000000L) >>> 24 )  |
                        (((long) v & 0xFF000000000000L) >>> 40 )  |
                        (((long) v & 0xFF00000000000000L) >>> 56);
                }
        }
       
        // actual iteration
        //24 = keccakrounds
        for (r = 0; r < 24; r++) {

            // Theta
            for (i = 0; i < 5; i++)
                bc[i] = (st[i] ^ st[i + 5] ^ st[i + 10] ^ st[i + 15] ^ st[i + 20]);

            for (i = 0; i < 5; i++) {
                //had to change built in rotc to long.rotateleft, off by 1 error, 
                t = ((bc[(i + 4) % 5] ^ Long.rotateLeft(bc[(i + 1) % 5], 1)));
                for (j = 0; j < 25; j += 5)
                    st[j + i] ^= t;
            }
            //ccode
            //0x6A7D0B6E0BD168C3
            //0110 1010 0111 1101 0000 1011 0110 1110 0000 1011 1101 0001 0110 1000 1100 0011

            //java
            //0xc369d00b6f0a7d6a
            //1100 0011 0110 1001 1101 0000 0000 1011 0110 1111 0000 1010 0111 1101 0110 1010
        // Rho Pi
        t = st[1];
        for (i = 0; i < 24; i++) {
            j = keccakf_piln[i];
            bc[0] = st[j];
            st[j] =  Long.rotateLeft(t, keccakf_rotc[i]);
            t = bc[0];
        }

        //  Chi
        for (j = 0; j < 25; j += 5) {
            for (i = 0; i < 5; i++)
                bc[i] = st[j + i];
            for (i = 0; i < 5; i++)
                st[j + i] ^= (~bc[(i + 1) % 5]) & bc[(i + 2) % 5];
        }

        //  Iota
        st[0] ^= keccakf_rndc[r];     
        }

          /* 
        for (i = 0; i < 25; i++) {
            v = st[i];
            long test = ((long) v & 0xFFL) << 56;
            long test2 = ((long) v & 0xFF00L) << 40;
            long test3 = ((long) v & 0xFF0000L) << 24;
            long test4 = ((long) v & 0xFF000000L) << 8;
            long test5 = ((long) v & 0xFF00000000L) >> 8;
            long test6 = ((long) v & 0xFF0000000000L) >> 24;
            long test7 = ((long) v & 0xFF000000000000L) >> 40;
            long test8 = (long) (v >> 56) & 0xFF;

            st[i] = ((long) v & 0xFFL)  << 56                | 
                    (((long) v & 0xFF00L) << 40)             |
                    (((long) v & 0xFF0000L)<< 24)            |
                    (((long) v & 0xFF000000L) << 8)          |
                    (((long) v & 0xFF00000000L) >> 8 )       |
                    (((long) v & 0xFF0000000000L) >> 24 )    |
                    (((long) v & 0xFF000000000000L) >> 40 )  |
                    (((long) v & 0xFF00000000000000L) >> 56);
            }
*/
        return st;
    }

    // Initialize the context for SHA3
    public static int sha3_init(sha3_ctx_t c, int mdlen){
        int i;
        for (i = 0; i < 25; i++) {
            c.q[i] = 0;
        }
        for (i = 0; i < 200; i++) {
            c.b[i] = 0;
        }

        //initializating state array params
        c.mdlen = mdlen;
        c.rsiz = 200 - 2 * mdlen;
        c.pt = 0;

        return 1;
        
    }

    // update state with more data

    public static int sha3_update(sha3_ctx_t c, byte[] data, int len)
    {

        int i;
        int j;

        j = c.pt;
        for (i = 0; i < len; i++) {
            c.b[j++] ^= (byte) data[i];
            c.update_q();
            if (j >= c.rsiz) {
                keccak_f(c.q);
                c.update_b();
                j = 0;
            }
        }
        c.pt = j;

        return 1;
    }

    // finalize and output a hash

    public static int sha3_final(byte[] md, sha3_ctx_t c)
    {
        int i;

        c.b[c.pt] ^= (byte) 0x06;
        c.update_q();
        c.b[c.rsiz - 1] ^= (byte) 0x80;
        c.update_q();
        keccak_f(c.q);
        c.update_b();

        for (i = 0; i < c.mdlen; i++) {
            md[i] = c.b[i];
        }
        return 1;
    }

    // compute a SHA-3 hash (md) of given byte length from "in"

    public static byte[] sha3(byte[] in, int inlen, byte[] md, int mdlen)
    {
        sha3_ctx_t sha3 = new sha3_ctx_t();

        sha3_init(sha3, mdlen);
        sha3_update(sha3, in, inlen);
        sha3_final(md, sha3);

        return md;
    }

    // SHAKE128 and SHAKE256 extensible-output functionality

    public static void shake_xof(sha3_ctx_t c)
    {
        //c.b[c.pt] ^= (byte) 0x04;
        c.b[c.pt] ^= (byte) 0x04;
        c.update_q();
        c.b[c.rsiz - 1] ^= (byte) 0x80;
        c.update_q();
        keccak_f(c.q);
        c.update_b();
        c.pt = 0;
    }

    static int debug = 0;
    public static void shake_out(sha3_ctx_t c, byte[] out, int len)
    {
        areWeShaking = true;
        int i;
        int j;
        //debug++;
        j = c.pt ;
        for (i = 0; i < len; i++) {
            if (j >= c.rsiz) {
                keccak_f(c.q);
                c.update_b();
                j = 0;
            }
            out[i] = c.b[j++];
        }
        c.pt = j;
    }
}
