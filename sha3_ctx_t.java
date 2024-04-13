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
}
