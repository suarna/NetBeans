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

/*
The software is developed based on the paper "A Provably Secure Additive and Multiplicative Privacy Homomorphism" by JOSEP DOMINGO-FERRER
Domingo-Ferrer, Josep. (2002). A Provably Secure Additive and Multiplicative Privacy Homomorphism*. Information Security. 471-483. 10.1007/3-540-45811-5_37
*/

/*The implementation computes an multiplication, as a single example multiplies the base*heigth of a rectangle without reveal the data to 
the server that carry out the computation,the client encrypt the data to be computed and send it to a server,the server returns the data once
the operation has been carried out and the cilent side decryot it obtaining the result whitout reveal any kind of information*/

package phclient;
        
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class PHClient {

   
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        

        try{
            System.out.println("Welcome to PH implenetation by Miguel Angel Barrero Díaz");

            System.out.println("Generating Public Key...\n");
            //Comuting Public Key        
            BigInteger[] pk = new PublicKey().generateKey();

            System.out.println("The computed Public Key is:\nm: " +pk[0]+"\nd: "+pk[1]+"\n");     
            System.out.println("Generating private key...\n");

            //Computing Private Key
            BigInteger[] sk = new SecretKey().generateKey(pk[0]);
            //m'must be gretaer than d and a value greter than (100000 by design)
            while(pk[1].compareTo(sk[1]) > 0 || sk[1].intValueExact() < 20000){
                sk = new SecretKey().generateKey(pk[0]);
            }
            
            System.out.println("The computed Private Key is:\nr: " +sk[0]+"\nm':"+sk[1] +"\n");
            //System.out.println("The computed Private Key is:\nr: " +r+"\nm':"+mp +"\n");

            //Read from keyboard
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader (isr);
            System.out.println("Simple example computing the area of a rectangle without reveal the information to the server");
            System.out.println("The length of the dictionary is: "+sk[1]+" ,if the result of the product is larger than this number the computation is not accurate");
            System.out.println("\nWrite the value of your base: ");
            String base = br.readLine();
            System.out.println("\nWrite the value of your heigth: ");
            String heigth = br.readLine();


            // convert int to BigInteger
            BigInteger basebig = BigInteger.valueOf(Integer.parseInt(base));
            BigInteger heightbig = BigInteger.valueOf(Integer.parseInt(heigth));

            //encrypt the data multiplied per 10 to assure that its value is gretater than d
            Encrypt enc = new Encrypt();
            BigInteger[] vector_basebig = enc.split(basebig,pk[1]);
            //System.out.println("The splitted base value is: "+Arrays.toString(vector_basebig));

            BigInteger[] vector_heigthbig = enc.split(heightbig,pk[1]);
            //System.out.println("The splitted base value is: "+Arrays.toString(vector_heigthbig));


            BigInteger[] encrypted_base = enc.encrypt(vector_basebig,pk[0],sk[0]);
            BigInteger[] encrypted_heigth = enc.encrypt(vector_heigthbig,pk[0],sk[0]);

            //System.out.println("Encrypted output: "+Arrays.toString(encrypted_base));
            //System.out.println("Encrypted output :"+Arrays.toString(encrypted_heigth));

            //New connection to the server
            System.out.println("Type the server ip(i.e 127.0.0.1 for localhost)  : ");
            String ip_string = br.readLine();
            if(ip_string == ""){
                ip_string = "localhost";
            }
            InetAddress server_ip = InetAddress.getByName(ip_string);
            Socket cs = new Socket(server_ip,28222);   
            System.out.println("The local port is: "+cs.getLocalPort());

            ObjectOutputStream os = new ObjectOutputStream(cs.getOutputStream());
            ObjectInputStream is = new ObjectInputStream(cs.getInputStream());

            //Send the encripted data and the public key to the server
            os.writeObject(encrypted_base);
            os.writeObject(encrypted_heigth);
            os.writeObject(pk[0]);
            os.writeObject(pk[1]);

            BigInteger[] returned = (BigInteger[]) is.readObject();
            //System.out.println("The returned array from the server is: "+Arrays.toString(returned));

            //We obtain the modular inverse of r
            BigInteger r_inverse = enc.multiplicativeInverse(sk[0], pk[0]);
            System.out.println("The modular inverse is: "+r_inverse);

            //We decrypt the code
            BigInteger result = enc.decrypt(returned,pk[0],sk[1],r_inverse);
            System.out.println("The result is :"+result);
        }
        catch(ArithmeticException e){
            System.out.println("Error generating private key...try again");
        }
    }
}
