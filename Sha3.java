//reference (given from assignment) https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c



//S = S[0] || S[1] || … || S[b-2] || S[b-1].
//Specification has bits in little endian, most significant is at Array[n-1]
//java is big endian, most significant is at Array[0]
//how will we tackle this as a group, adjust our code, or the specification

public class Sha3 {
    /*
    In this section, the KECCAK-p permutations are specified, with two parameters: 1) the fixed
    length of the strings that are permuted, called the width of the permutation, and 2) the number of
    iterations of an internal transformation, called a round. The width is denoted by b, and the
    number of rounds is denoted by nr. The KECCAK-p permutation with nr rounds and width b is
    denoted by KECCAK-p[b, nr]; the permutation is defined for any b in {25, 50, 100, 200, 400, 800,
    1600} and any positive integer nr. 
    */

    //probably needs its own method, find better way to add notes rather than comments that fill up the entire screen

    //b parameter, length of array of bits,
    //apparently sha-3 uses a state array of 1600 bits so im using that number for now
    
    //sha-3 standards
    private static int b = 1600; //inital 


    //a lot of these are constants for sha3 or keccak in particular

    //set by keccak
    private static int ROWS = 5;
    private static int COLUMNS = 5; 
    //w, how long each lane is
    
    

    private static int W = b/25; //should be 64 
    private static int L = 6; //logbase2 of laneLength, log_2(64) = 6

    //initial state, no data is put in we do the permutations first then hash
    static boolean[] STATE = new boolean[b]; //5 x 5 index, 64 deep

    //A[x,y,z]=S[w(5y+x)+z].

    private static boolean A(int x, int y, int z){
        return STATE[W * (5 * y + x) + z];
    }

    private static boolean A(int x, int y, int z, boolean[] state){
        return state[W * (5 * y + x) + z];
    }


    private static int getCoords(int x, int y, int z){
        return W * (5 * y + x) + z;
    }



    //keccac_p is permutations wiith a specific number of rounds, sha-3256 uses 1600 bits and 24 rounds, so thats what we'll use
    //reference: https://keccak.team/keccakp.html
    public static void keccak_f(boolean[] state){


            boolean[][] C = new boolean[5][W];
            boolean[][] D = new boolean[5][W];
            boolean[] tempState = state;

            //theta
                for (int x = 0; x < 5; x++) {
                    for(int z = 0; z < W; z++){
                        C[x][z] = A(x, 0, z) ^ A(x, 1, z) ^ A(x, 2, z) ^ A(x, 3, z) ^ A(x, 4, z);
                    }
                }

                for (int x = 0; x < 5; x++) {
                    for(int z = 0; z < W; z++){
                        D[x][z] = C[(x-1 + 5) % 5][z] ^ C[(x+1 + 5) % 5][(z-1 + W) % W];                    }
                }

                for (int x = 0; x < 5; x++){
                    for (int y = 0; y < 5; y++){
                        for(int z = 0; z < W; z++){
                            tempState[getCoords(x, y, z)] = A(x, y, z, tempState) ^ D[x][z];   
                        }
                    }
                }

            //rho

            //pi

            //chi

            //iota

    }

    public static void main(String[] args) {
        //testing coordinates
        System.out.println(A(0,0,0));
        System.out.println(A(0,1,1));
        System.out.println(A(4,0,63));

        keccak_f(STATE);
    }
}
