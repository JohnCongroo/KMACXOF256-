//reference (given from assignment) https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
public class Sha3 {


    private static long ROTL64(long x, long y){
        return (((x) << (y)) | ((x) >> (64 - (y))));
    }


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

        /* 
            //for java is always big-endian, C is system-dependent 
            #if __BYTE_ORDER__ != __ORDER_LITTLE_ENDIAN__
                uint8_t *v;
//https://stackoverflow.com/questions/14713102/what-does-and-0xff-do
        */
        // endianess conversion. this is redundant on little-endian targets

        
        for (i = 0; i < 25; i++) {
            v = st[i];
            long temp = 0;
            for (j = 0; j < 8; j++){
                temp = temp << 8; //to make room
                temp = temp | (v >> (j*8) & 0xFF); //grab only the relevant bits
            }
            st[i] = temp;
        }
        
/* */
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
                    st[j + i] ^= t; //breakpoint steps 6 + 10
            }


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
            long temp = 0;
            for (j = 0; j < 8; j++){
                temp = temp << 8;
                temp = temp | (v >> (j*8) & 0xFF);
            }
            st[i] = temp;
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
            c.b[j++] ^= data[i];
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
        c.b[c.pt] ^= 0x1F;
        c.update_q();
        c.b[c.rsiz - 1] ^= 0x80;
        c.update_q();
        keccak_f(c.q);
        c.update_b();
        c.pt = 0;
    }

    public static void shake_out(sha3_ctx_t c, byte[] out, int len)
    {
        int i;
        int j;

        j = c.pt;
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
