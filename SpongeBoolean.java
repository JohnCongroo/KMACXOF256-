
public class SpongeBoolean {
	public static final int capacity = 512;  // from KECCAK[c] 
	public static final int b = 1600;
	public static final int rate = b - capacity;
	
	public static boolean[] sponge(boolean[] N, int d) {
		boolean[] pad = pad(rate, N.length);
		boolean[] P = new boolean[N.length + pad.length];
		
		// 1. Let P=N || pad(r, len(N)).
		System.arraycopy(N, 0, P, 0, N.length);
		System.arraycopy(pad, 0, P, N.length, pad.length);

		// 2. Let n=len(P)/r.
		int n = P.length / rate;
		
		// 3. Let c=b-r.
		int c = b - rate;
		
		// 4. Let P_0, … , P_n-1 be the unique sequence of strings of length r such that P = P0 || … || P_n-1.
		Object[] uniqueSeq = new Object[n];
		int len = P.length / n;
		for (int i = 0; i < n; i++) {
			boolean[] Pi = new boolean[len];
			System.arraycopy(P, len * i, Pi, 0, len);
			uniqueSeq[i] = Pi;
		}
		
		// 5. Let S=0^b.
		boolean[] S = new boolean[b];
		for (int i = 0; i < b; i++) {
			S[i] = false;
		} 
		
		// ABSORPTION PART
		
		// 6. For i from 0 to n1, let S=f (S ⊕ (P_i || 0^c)).
		boolean[] other = new boolean[S.length];
		for (int i = 0; i < rate; i++) {
			for (int j = 0; j < len; j++) {
				boolean[] Pi = (boolean[]) uniqueSeq[i];
				other[j] = Pi[j];
			}
			for (int j = 1; j < c; j++) {
				other[j] = false;
			}
			//S ⊕ (P_i || 0^c)
			absorption(S, other);
			S = keccak_f(S); 
		}
		
		
		// SQUEEZE PART
		
		// 7. Let Z be the empty string.
		boolean[] Z = new boolean[rate];
		// 8. Let Z=Z || Trunc_r(S).
		System.arraycopy(S, 0, Z, 0, rate);
		while (d >= Z.length) {
			// 10. Let S=f(S), and continue with Step 8.
			S = keccak_f(S);
			System.arraycopy(S, 0, Z, 0, rate);
		}
		
		// 9. If d≤|Z|, then return Trunc_d (Z); else continue.
		boolean[] bitString = new boolean[d];
		System.arraycopy(Z, 0, bitString, 0, d);
		return bitString;		
	}

	public static void absorption(boolean[] S, boolean[] other) {
		for (int i = 0; i < S.length; i++) {
			S[i] = S[i] ^ other[i]; 
		}
	}
	
	public static boolean[] keccak_f(boolean[] string) {
		// TO BE ADDED
	}
	
	public static boolean[] pad(int x, int m) {
		if (x > 0 && m >= 0) {
			//1. Let j = (– m – 2) mod x.
			int j = (-1 * m - 2) % x;
			
			
			boolean[] P = new boolean[2 + j];
			//2. Return P = 1 || 0^j || 1
			P[0] = true;
			P[j + 1] = true;
			for (int i = 0; i < 1 + j; i++) {
				P[i] = false;
			}
			return P;
		} else {
			throw new IllegalArgumentException("error");
		}
	}
}
