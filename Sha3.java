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
    private static int rows = 5;
    private static int columns = 5; 
    //w, how long each lane is
    
    

    private static int w = b/25; //should be 64 
    private static int l = 6; //logbase2 of laneLength, log_2(64) = 6

    //initial state, no data is put in we do the permutations first then hash
    static boolean[] state = new boolean[b]; //5 x 5 index, 64 deep

    //A[x,y,z]=S[w(5y+x)+z].

    private static int A(int x, int y, int z){
        return (w* (5 * y + x) + z);
    }

    //keccac_p is permutations wiith a specific number of rounds, sha-3256 uses 1600 bits and 24 rounds, so thats what we'll use
    //reference: https://keccak.team/keccakp.html
    public static void keccak_f(Object[] state){
            //theta

            //rho

            //pi

            //chi

            //iota

    }

    public static void main(String[] args) {
        //testing coordinates
        System.out.println(A(0,0,0));
        System.out.println(A(4,0,63));
    }
}
