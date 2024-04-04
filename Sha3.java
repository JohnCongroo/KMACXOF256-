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
    private static int width = 1600; //inital 


    private static int rows = 5;
    private static int columns = 5; 
    //w, how long each lane is 
    private static int laneLength = width/25; //should be 64 
    private static int LengthBits = 6; //logbase2 of laneLength, log_2(64) = 6

    long[] state1d = new long[rows * columns]; //5 x 5 index, 64 deep

    //A[x, y,z]=S[w(5y+x)+z].

    public static void keccak_p(Object[] state){
            //theta

            //rho

            //pi

            //chi

            //iota

    }
}
