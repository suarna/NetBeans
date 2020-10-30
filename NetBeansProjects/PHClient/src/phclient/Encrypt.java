/*
 * Copyright (C) 2020 Miguel Angel Barrero Díaz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package phclient;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class Encrypt{
    //Method to split the data 
    public BigInteger[] split(BigInteger a,BigInteger d){    
        
        //Aplication of the splitting algorithm
        
        BigInteger[] vector_a = new BigInteger[d.intValueExact()];
        Random random = new Random();
        
        //Split the value of a in as parts as the value of d
        //result[0] is the quotient of division an dresult[1] is the remainder.
        BigInteger[] result = a.divideAndRemainder(d);
        //System.out.println("Quotient: "+result[0]+" Remainder: "+result[1]);
        //System.out.println("Int exact value: " +result[0].intValueExact());
        
        //If remainder is zero
        if(result[1] == BigInteger.ZERO){
            for (int i = 0; i <= vector_a.length-1; i = i+2){
                
                //Randon value less than the quotient to sum and substract to two consecutive elements of the vector
                BigInteger sum = new BigInteger(result[0].intValueExact(),random);
                
                
                //if it is the last element simply put the quotient
                if(i == vector_a.length-1){
                    vector_a[i] = result[0];
                }
                
                //For the rest of elements different to last one we add the random number to the first value and subtract it to the next
                else{
                vector_a[i] = result[0].add(sum);
                vector_a[i+1] = result[0].subtract(sum);
                }
            }
        }
        //If remainder is different to zero
        else{
            for (int i = 0; i <= vector_a.length-1; i = i+2){
                
                //Random number to sum and substract
                BigInteger sum = new BigInteger(result[0].bitLength(),random);
                
                //if i is equal to the length...
                if(i == vector_a.length-1){
                    //We add to the last value the remainder
                    vector_a[i] = result[0].add(result[1]);
                }
                
                //else
                else{
                vector_a[i] = result[0].add(sum);
                    //if i+1 is the last element we substarct and sum the remainder
                    if(i+1 == vector_a.length-1){
                        vector_a[i+1] = result[0].subtract(sum).add(result[1]);
                    } 
                    //else we only substarct
                    else{
                        vector_a[i+1] = result[0].subtract(sum);
                    }
                }
            }
        }
        
        return vector_a;
    }
    //Classified level encryption
    public BigInteger[] encrypt(BigInteger[] vector_a,BigInteger m,BigInteger r){
        
        BigInteger[] encripted = new BigInteger[vector_a.length];
        for (int i = 0; i <=vector_a.length-1;i++){
             //Compute a.1 r^(i+1) mod m
             encripted[i] = vector_a[i].multiply(r.pow(i+1)).mod(m);
        }
           
        return encripted;
    }
    //Method to compute the modular inverse (modular inverse using extended euclidean algorithm)
    public BigInteger multiplicativeInverse(BigInteger r, BigInteger m){
        BigInteger d = r.gcd(m);
        BigInteger inverse = null;
        if(d.compareTo(BigInteger.ONE)==0){
            System.out.println("r is invertible¡¡");
            inverse = r.modInverse(m);
        }
        else{
            System.out.println("r is NOT invertible¡¡");
        }
    return inverse;
    }
    //Classified level decription
    public BigInteger decrypt(BigInteger[] encrypted, BigInteger m,BigInteger mp, BigInteger r_inverse){
        BigInteger[] result_array = new BigInteger[encrypted.length];
        BigInteger result = BigInteger.ZERO;
        for(int i=0;i<encrypted.length;i++){
            result_array[i] =encrypted[i].multiply((r_inverse).pow(i+2)).mod(m);
        }
        for(int i=0;i<result_array.length;i++){
            result = result.add(result_array[i]);
        }
    //System.out.println("Array to sum modulus m': "+Arrays.toString(result_array));
    return result.mod(mp);    
    }
}
