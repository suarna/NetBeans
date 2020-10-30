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

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class SecretKey {

    public BigInteger[] generateKey(BigInteger m) throws IOException{
        
            Random random = new Random();
            
            //Compute r
            BigInteger r = new BigInteger(m.bitLength()/2,random);
                        
            //m and r must be corpimes
            //While gcd(m,r)!=1 and r>m 
            while(r.gcd(m).compareTo(BigInteger.ONE) != 0 || r.compareTo(m) > 0 ){
                r = new BigInteger(m.bitLength()/2,random);
                //System.out.println("Is negative? "+r.subtract(m));
            }
              
            //Compute m' divisor of m with length of 16 bits,the divisor must return remainder 0
            BigInteger mp = new BigInteger(16,random);
            
            while(m.divideAndRemainder(mp)[1].compareTo(BigInteger.ZERO) != 0  || mp.compareTo(BigInteger.ZERO) == 0  || mp.compareTo(BigInteger.ONE) == 0){
                mp = new BigInteger(30,random);
            }
            //System.out.println("Remainder m%m': "+m.divideAndRemainder(mp)[1]);
            BigInteger[] sk = new BigInteger[2];
            sk[0] = r;
            sk[1] = mp;
        return sk;
    }
}
