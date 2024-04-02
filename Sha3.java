//reference (given from assignment) https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c


public class Sha3 {


/*
 * In this section, the KECCAK-p permutations are specified, with two parameters: 1) the fixed
length of the strings that are permuted, called the width of the permutation, and 2) the number of
iterations of an internal transformation, called a round. The width is denoted by b, and the
number of rounds is denoted by nr. The KECCAK-p permutation with nr rounds and width b is
denoted by KECCAK-p[b, nr]; the permutation is defined for any b in {25, 50, 100, 200, 400, 800,
1600} and any positive integer nr. 

/width refers to lane width

 */

//b = length of permnutation
//rounds = nr

//w = lane width,

//most definitely will have to change from byte data type


//permutation is defined for any b in {25, 50, 100, 200, 400, 800,1600} and any positive integer nr.
 static int rounds = 25;
 static int laneWidth = 25/3;
 byte[][][] state = new byte[laneWidth][laneWidth][laneWidth];

     public static void keccakp(byte[] state){

        byte[] bitColumn = new byte[laneWidth];


            //theta
            for (int i = 0; i < laneWidth; i++){
                bitColumn[i] = (byte) (state[i] ^ state[i + 5] ^ state[ i + 10] ^ state[i + 15 ^ state[i + 20]]);
            }

            for (int i = 0; i < laneWidth; i++){
                

            }
            //rho

            //pi

            //chi

            //iota

        }
    }
}
