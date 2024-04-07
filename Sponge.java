
public class Sponge {
	public static final int capacity = 512;  // from KECCAK[c] 
	public static final int b = 1600; // for SHA-3
	public static final int r = b - capacity;
	
	// assume d and r are divisible by 8
	/** 
	 * Arbitrary number of input bits are “absorbed” into the state of the function, 
	 * The sponge the "squeezes" out an arbitrary number of output bits from its state.
	 * 
	 * @param N bit String
	 * @param d bit length
	 * @return a string of length d.
	 */
	public static byte[] sponge(byte[] N, int d) {
		// 1. Let P=N || pad(r, len(N)).
		byte[] pad = pad10_1(r, N.length);
		byte[] P = new byte[N.length + pad.length];
		System.arraycopy(N, 0, P, 0, N.length);
		System.arraycopy(pad, 0, P, N.length, pad.length);
		
		// 2. Let n=len(P)/r.
		int n = P.length * 8 / r;
		
		// 3. Let c=b-r.
		int c = b - r;
		
		// 4. Let P0, … , Pn-1 be the unique sequence of strings of length r such that P = P0 || … || Pn1.
		int byteLengthR = (r + 7) / 8;
		byte[][] Pi = new byte[n][byteLengthR];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < byteLengthR; j++) {
				Pi[i][j] = P[i * byteLengthR + j];
			}
		}
		
		// 5. Let S=0^b. 
		byte[] S = new byte[(b + 7)/ 8];
		for (int i = 0; i < S.length; i++) {
			S[i] = (byte)0;
		}
		
		// 6. For i from 0 to n1, let S=f (S ⊕ (Pi || 0^c)).
		absorption(S, Pi, c);
		
		// 7. Let Z be the empty string.  
		byte[] Z = new byte[byteLengthR];
		
		// 8. Let Z=Z || Truncr(S).
		System.arraycopy(S, 0, Z, 0, r);
		
		// 9. If d≤|Z|, then return Trunc d (Z); else continue.
		// 10. Let S=f(S), and continue with Step 8.	
		while (d > Z.length) {
			S = keccak_f(S);
			System.arraycopy(S, 0, Z, 0, d);
		}
		
		byte[] toReturn = new byte[(d + 7) / 8];
		System.arraycopy(Z, 0, toReturn, 0, toReturn.length);
		return toReturn;
	}

	/**
	 * S=f (S ⊕ (Pi || 0^c)).
	 * 
	 * @param S
	 * @param other
	 */
	public static void absorption(byte[] S, byte[][] other, int c) {
		for (int i = 0; i < other.length; i++) {
			byte[] Pi = (byte[]) other[i];
			byte[] curOther = new byte[S.length];
			System.arraycopy(Pi, 0, curOther, 0, Pi.length);
			for (int j = Pi.length + 1; j < curOther.length; j++) {
				curOther[j] = (byte)0;
			} 
			
			byte[] xorOutput = new byte[S.length];
			for (int j = 0; j < xorOutput.length; j++) {
				xorOutput[j] = (byte) (S[j] ^ curOther[j]); 
			}
			S = keccak_f(xorOutput);
		}
	}

	public static byte[] keccak_f(byte[] string) {
		// TO BE ADDED
	}
	
	
	/**
	 * Produces an output string of the desired length.
	 * 
	 * @param x positive integer
	 * @param m non-negative integer
	 * @return string P such that m + len(P) is a positive multiple of x
	 */
	public static byte[] pad10_1(int x, int m) {
		if (x > 0 && m >= 0) {
			//1. Let j = (– m – 2) mod x.
			int j = (-1 * m - 2) % x;
			if (j < 0) {
				j += x;
			}
			
			// May need to change depending on endian
			//2. Return P = 1 || 0^j || 1
			int padLength = (j + 2 + 7) / 8; // +7 to ensure enough bytes
			byte[] P = new byte[padLength];
			P[0] = (byte)1;
			for (int i = 1; i < padLength - 1; i++) {
				P[i] = (byte)0;
			}
			P[padLength - 1] = (byte) Math.pow(2, (j % 8)); 
			return P;
		} else {
			throw new IllegalArgumentException("error");
		}
	}
}
