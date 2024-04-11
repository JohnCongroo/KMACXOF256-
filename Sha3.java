//reference (given from assignment) https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c


public class Sha3 {
    //change to return long[] after
    
    private static long[] keccak_f(long[] st){

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

        int i, j, r;
        long t, bc[] = new long[5];

        // actual iteration
        //24 = keccakrounds
        for (r = 0; r < 24; r++) {

            // Theta
            for (i = 0; i < 5; i++)
                bc[i] = st[i] ^ st[i + 5] ^ st[i + 10] ^ st[i + 15] ^ st[i + 20];

            for (i = 0; i < 5; i++) {
                t = bc[(i + 4) % 5] ^ Long.rotateLeft(bc[(i + 1) % 5], 1);
                for (j = 0; j < 25; j += 5)
                    st[j + i] ^= t;
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

        return new long[1];
    }

    
// Initialize the context for SHA3

public static int sha3_init(Sha3.sha3_ctx_t c, int mdlen){
    int i;
    for (i = 0; i < 25; i++) {
        c.q[i] = 0;
    }

    c.mdlen = mdlen;
    c.rsiz = 200 - 2 * mdlen;
    c.pt = 0;

    return 1;
}

private class sha3_ctx_t{
    private byte[] b;
    private long[] q;
    private int pt, rsiz, mdlen;

    private sha3_ctx_t(){
        b = new byte[200];
        q = new long[25];
        pt = 0;
        rsiz = 0;
        mdlen = 0;
    }
}



// update state with more data

int sha3_update(sha3_ctx_t c, byte[] data, int len)
{
    int i;
    int j;

    j = c.pt;
    for (i = 0; i < len; i++) {
        c.b[j++] ^= data[i];
        if (j >= c.rsiz) {
            keccak_f(c.q);
            j = 0;
        }
    }
    c.pt = j;

    return 1;
}

// finalize and output a hash

int sha3_final(byte[] md, sha3_ctx_t c)
{
    int i;

    c.b[c.pt] ^= 0x06;
    c.b[c.rsiz - 1] ^= 0x80;
    keccak_f(c.q);

    for (i = 0; i < c.mdlen; i++) {
        md[i] = c.b[i];
    }

    return 1;
}

// compute a SHA-3 hash (md) of given byte length from "in"

public byte[] sha3(byte[] in, int inlen, byte[] md, int mdlen)
{
    sha3_ctx_t sha3 = new sha3_ctx_t();

    sha3_init(sha3, mdlen);
    sha3_update(sha3, in, inlen);
    sha3_final(md, sha3);

    return md;
}

// SHAKE128 and SHAKE256 extensible-output functionality

void shake_xof(sha3_ctx_t c)
{
    c.b[c.pt] ^= 0x1F;
    c.b[c.rsiz - 1] ^= 0x80;
    keccak_f(c.q);
    c.pt = 0;
}

void shake_out(sha3_ctx_t c, byte[] out, int len)
{
    int i;
    int j;

    j = c.pt;
    for (i = 0; i < len; i++) {
        if (j >= c.rsiz) {
            keccak_f(c.q);
            j = 0;
        }
        out[i] = c.b[j++];
    }
    c.pt = j;
}

    public static void main(String[] args) {
        //testing coordinates
    }

}
