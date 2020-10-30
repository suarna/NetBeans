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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.*/


package phclient;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class PublicKey {
    
   public BigInteger[] generateKey() throws IOException{
        
        BigInteger m;
        BigInteger d = BigInteger.ONE;
        
        //Compute m
        while(true){
            Random random = new Random(); 
        
            //We generate a big prime
            int exp_p = 1;
            while (exp_p < 500){
                exp_p = random.nextInt(600);
            }
        
            int length_p = 2^exp_p;
            BigInteger p = BigInteger.probablePrime(length_p, random);
            //Generate a prime 2 of type BigInteger
            BigInteger three = BigInteger.ONE.add(BigInteger.ONE).add(BigInteger.ONE);
            //Generate a prime eleven of type BigInteger
            BigInteger eleven = BigInteger.TEN.add(BigInteger.ONE);
            //If p is prime with certainty 10000 ; probability=1-1/2^certainty compute m
            if(p.isProbablePrime(10000)){
                
                //Computing m
                m = p.multiply(three.pow(random.nextInt(20))).multiply(eleven.pow(random.nextInt(20)));
                break;
            }
        }
        
        //Once computed m we compute d>2 with four bits length
        Random random = new Random();
        while(d.intValueExact()<= 2){
            d = new BigInteger(4,random); 
        }
        
        //Public key
        BigInteger[] pk = new BigInteger[2];
        pk[0] = m;
        pk[1] = d;
         
        return pk;
   }
    
}
