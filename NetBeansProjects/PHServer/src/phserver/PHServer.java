/*
 * Copyright (C) 2020 miguel
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

/*
The software is developed based on the paper "A Provably Secure Additive and Multiplicative Privacy Homomorphism" by JOSEP DOMINGO-FERRER
Domingo-Ferrer, Josep. (2002). A Provably Secure Additive and Multiplicative Privacy Homomorphism*. Information Security. 471-483. 10.1007/3-540-45811-5_37
*/

/*Server side computes the multiplication on the encrypted data receceived,due to the homomorfism of the primitive when the client receives the data
and once decrypted the result of the operation is obtained.
*/
package phserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PHServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        
        int clientN = 0;
        ServerSocket ss = new ServerSocket(28222);
        //Infinite loop opening new threads to attend clients
        while(true){
            Socket socket = ss.accept();
            (new Thread(new ClientThread(socket, ++clientN))).start();
        } 
    }
}
