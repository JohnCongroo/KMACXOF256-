public class sha3_ctx_t{
    public byte[] b;
    public long[] q;
    public int pt, rsiz, mdlen;

    public sha3_ctx_t(){
        b = new byte[200];
        q = new long[25];
        pt = 0;
        rsiz = 0;
        mdlen = 0;
    }

    //refernce
    //https://stackoverflow.com/questions/1586882/how-do-i-convert-a-byte-array-to-a-long-in-java
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
    
    public void update_b(){
        for(int i = 0; i < q.length; i++){
            long v = q[i];
            b[i * 8]     = (byte) (v & 0xFF);
            b[1 + i * 8] = (byte) (v >> 8 & 0xFF);
            b[2 + i * 8] = (byte) (v >> 16 & 0xFF);
            b[3 + i * 8] = (byte) (v >> 24 & 0xFF);
            b[4 + i * 8] = (byte) (v >> 32 & 0xFF);
            b[5 + i * 8] = (byte) (v >> 40 & 0xFF);
            b[6 + i * 8] = (byte) (v >> 48 & 0xFF);
            b[7 + i * 8] = (byte) (v >> 56 & 0xFF);
        }
    }
}
