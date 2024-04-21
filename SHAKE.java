public class SHAKE {
    private static byte[] right_encode = {0, 1};

    private byte[] key;
    private byte[] customization;
    private static sha3_ctx_t state;

    public byte[] KMACXOF256(byte[] K, byte[] X, int L, byte[] S) {
       //1. newX = bytepad(encode_string(K), 136) || X || right_encode(0).
       byte[] bytepad = Internal.bytepad(Internal.encode_string(K), 136);
       byte[] newX = new byte[bytepad.length + X.length + right_encode.length];
       System.arraycopy(bytepad, 0, newX, 0, bytepad.length);
       System.arraycopy(X, 0, newX, bytepad.length, X.length);
       System.arraycopy(right_encode, 0, newX, bytepad.length + X.length, right_encode.length);
       return cSHAKE256(newX, L, "KMAC".getBytes(), S);
   }

   public byte[] cSHAKE256(byte[] X, int L, byte[] N, byte[] S) {
       //return KECCAK[512](bytepad(encode_string(N) || encode_string(S), 136) || X || 00, L).
       byte[] encodeN = Internal.encode_string(N);
       byte[] encodeS = Internal.encode_string(S);
       byte[] encode = new byte[encodeN.length + encodeS.length];
       System.arraycopy(encodeN, 0, encode, 0, encodeN.length);
       System.arraycopy(encodeS, 0, encode, encodeN.length, encodeS.length);
       byte[] pad = Internal.bytepad(encode, 136);

       byte[] finalString = new byte[pad.length + X.length + 2];
       System.arraycopy(pad, 0, finalString, 0, pad.length);
       System.arraycopy(X, 0, finalString, pad.length, X.length);
       // This needs to be fixed
//        finalString[finalString.length - 2] = 0;
//        finalString[finalString.length - 1] = 0;
       // Need to implement KECCAK[512]
       return Sha3.sha3(finalString, finalString.length, new byte[64], L / 2);
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
            long temp;
            v = st[i];
            temp = 0;
            for (j = 0; j < 8; j++){
                temp = temp << 8;
                temp = temp | (v >> (j*8) & 0xFFL);
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

            //will always need this cause we are in java
            #if __BYTE_ORDER__ != __ORDER_LITTLE_ENDIAN__
            // endianess conversion. this is redundant on little-endian targets
            */
            /*
            for (i = 0; i < 25; i++) {
                long temp;
                v = st[i];
                temp = 0;
                for (j = 0; j < 8; j++){
                    temp = temp << 8;
                    temp = temp | (v >> (j*8) & 0xFF);
                }
                st[i] = temp;
        }
        */
        //to change
        return st;
    }

    public void kinit256(byte[] K, byte[] S) {
        key = K;
        customization = S;
    }
    public int sha3_update(byte[] data, int len)
    {
        // newX = bytepad(encode_string(K), 136) || X || right_encode(0).
        byte[] bytepad = Internal.bytepad(Internal.encode_string(key), 136);
        byte[] newX = new byte[bytepad.length + data.length + right_encode.length];
        System.arraycopy(bytepad, 0, newX, 0, bytepad.length);
        System.arraycopy(data, 0, newX, bytepad.length, data.length);
        System.arraycopy(right_encode, 0, newX, bytepad.length + data.length, right_encode.length);

        // N = bytepad(encode_string(N) || encode_string(S), 136) || X || 00
        byte[] encodeN = Internal.encode_string("KMAC".getBytes());
        byte[] encodeS = Internal.encode_string(customization);
        byte[] encode = new byte[encodeN.length + encodeS.length];
        System.arraycopy(encodeN, 0, encode, 0, encodeN.length);
        System.arraycopy(encodeS, 0, encode, encodeN.length, encodeS.length);
        byte[] pad = Internal.bytepad(encode, 136);

        byte[] kecString = new byte[pad.length + data.length + 2];
        System.arraycopy(pad, 0, kecString, 0, pad.length);
        System.arraycopy(data, 0, kecString, pad.length, data.length);
        // Need to append 2 zero bits to the end

        int i;
        int j;

        j = state.pt;
        for (i = 0; i < len; i++) {
            state.b[j++] ^= kecString[i];
            state.update_q();
            if (j >= state.rsiz) {
                keccak_f(state.q);
                state.update_b();
                j = 0;
            }
        }
        state.pt = j;

        return 1;
    }

    public static int sha3_init(sha3_ctx_t c, int mdlen){
        int i;
        for (i = 0; i < 25; i++) {
            c.q[i] = 0;
        }

        c.mdlen = mdlen;
        c.rsiz = 200 - 2 * mdlen;
        c.pt = 0;

        return 1;
    }

    public static int sha3_final(byte[] md)
    {
        int i;
        state.b[state.pt] ^= (byte) 0x06;
        state.update_q();
        state.b[state.rsiz - 1] ^= (byte) 0x80;
        state.update_q();
        keccak_f(state.q);
        state.update_b();

        for (i = 0; i < state.mdlen; i++) {
            md[i] = state.b[i];
        }
        return 1;
    }

    // SHAKE128 and SHAKE256 extensible-output functionality

    public void shake_xof()
    {
        state.b[state.pt] ^= 0x1F;
        state.update_q();
        state.b[state.rsiz - 1] ^= 0x80;
        state.update_q();
        keccak_f(state.q);
        state.update_b();
        state.pt = 0;
    }

    public void shake_out(byte[] out, int len)
    {
        int i;
        int j;

        j = state.pt;
        for (i = 0; i < len; i++) {
            if (j >= state.rsiz) {
                keccak_f(state.q);
                state.update_b();
                j = 0;
            }
            out[i] = state.b[j++];
        }
        state.pt = j;
    }
}
