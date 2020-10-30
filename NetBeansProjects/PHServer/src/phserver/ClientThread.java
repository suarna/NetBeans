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
package phserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miguel
 */
public class ClientThread implements Runnable {
    
    Socket socket;
    int number;
    
    public ClientThread(Socket sc, int i) {
        
        socket = sc;
        number = i;
        System.out.println("This is the thread serving client number: " + number);
        System.out.println("Connecting from the socket: " + sc.getRemoteSocketAddress());
    }
    
    @Override
    public void run() {
           
        try {
            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());

            BigInteger[] encrypted_base = (BigInteger[]) is.readObject();
            BigInteger[] encrypted_heigth = (BigInteger[]) is.readObject();
            System.out.println("Received encrypted base: "+Arrays.toString(encrypted_base));
            System.out.println("Received encrypted heigth: "+Arrays.toString(encrypted_heigth));
            BigInteger m = (BigInteger) is.readObject();
            BigInteger d = (BigInteger) is.readObject();
           
            BigInteger[] sum = new BigInteger[encrypted_base.length+encrypted_heigth.length-1];
            BigInteger[] result = new BigInteger[sum.length];
            BigInteger multiply;
            
            //Fill the array with zeros
            for(int i=0;i<sum.length;i++){
                sum[i] = BigInteger.ZERO;
            }
            //Unclassified level. Compute polinomial multiplication of heigth an base
            int idx = 1;
            for(int i=0;i<encrypted_base.length;i++){
                for(int j=0;j<encrypted_heigth.length;j++){
                    //Multiply the coefficients and store added into array
                    multiply = encrypted_base[i].multiply(encrypted_heigth[j]);
                    sum[i+j]= sum[i+j].add(multiply);
                }
                idx++;
            }
            //System.out.println("Sum array: "+Arrays.toString(sum));
            
            //Now we apply the modulus to the elements of the array
            for(int i=0;i<sum.length;i++){
                result[i] = sum[i].mod(m);
            }
            System.out.println("Resultant array:"+Arrays.toString(result));
        
        //Send data to the client    
        os.writeObject(result);
        //Close socket
        socket.close();
        
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
}
