/*
 * Copyright (C) 2019 Miguel Angel Barrero Díaz
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
package mas;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miguel
 */
public class UserAgent extends Agent {
    
    @Override
    protected void setup(){
        System.out.println("\nPerformed by:"+getLocalName()+":Hi¡ I am the user agent,I am here to help you¡ \n "
                + "If you want to train the agents type Train,if you want a prediction write Predict\n");
            
        addBehaviour(new sendToManagerBehaviour(this));
        addBehaviour(new UserInterfaceBehaviour(this));
        
        
    }

    private class sendToManagerBehaviour extends SimpleBehaviour {
        
        public sendToManagerBehaviour(Agent agent){
            super(agent);
        }
        private boolean finished = false;
        @Override
        public void action() {

            try {
                
                /*Load data from file*/
                
                InputStream file;
                file = new FileInputStream("imas.settings.properties");
                Properties prop = new Properties();
                prop.load(file);
                file.close();

                System.out.println("\nPerformed by "+getLocalName()+":The following data will be sent to manager"); 
                ACLMessage aclmsg = new ACLMessage(ACLMessage.REQUEST);
                aclmsg.addReceiver(new AID("manageragent", AID.ISLOCALNAME));
                aclmsg.setContentObject(prop);
                System.out.println("\nPerformed by "+getLocalName()+":Content of message with configuration"+aclmsg.getContentObject().toString());
                send(aclmsg);
                finished=true;
                
            } catch (IOException ex) {
            } catch (UnreadableException ex) {
                Logger.getLogger(UserAgent.class.getName()).log(Level.SEVERE, null, ex);
            }        
        }

        @Override
        public boolean done() {
            return finished;
        }
    }
   
    
    private class UserInterfaceBehaviour extends SimpleBehaviour{
  
        public UserInterfaceBehaviour (Agent agent){
            super(agent);
        }
        boolean agentscreated = false;
        boolean isTrained = false;
        @Override
        public void action() {
            
            
            try {
                //Receive messages
                ACLMessage recmsg = receive();
                if(recmsg!=null && recmsg.getConversationId()!="prediction"){
                    System.out.println("Received message from manager :"+recmsg.getContent());
                    agentscreated = true;
                }
                if(recmsg!=null && recmsg.getConversationId()=="prediction"){
                    String prediction = (String) recmsg.getContentObject();
                    System.out.println("RECEIVED MESSAGE WITH PREDICTION FROM MANAGER¡¡ :"+prediction);
                }
               
                //Read from keyboard
                System.out.println("\nPerformed by "+getLocalName()+": Type your selection-Train or Predict:");
                InputStreamReader isr = new InputStreamReader(System.in);
                BufferedReader br = new BufferedReader (isr);
                String stg = br.readLine();
                

                //Switch interface
                switch (stg) {
                    case "Train":
                        if(agentscreated == false){
                            System.out.println("\nPerformed by "+getLocalName()+":The agents will be trained");
                            ACLMessage aclmsg1 = new ACLMessage(ACLMessage.REQUEST);
                            aclmsg1.addReceiver(new AID("manageragent", AID.ISLOCALNAME));
                            aclmsg1.setContent("Create Agents");
                            send(aclmsg1);
                            isTrained=true;
                        }
                        else if(agentscreated == true){
                            System.out.println("\nPerformed by "+getLocalName()+":The agents have already been created,otherwise, we will train it again¡");
                            ACLMessage aclmsg1 = new ACLMessage(ACLMessage.REQUEST);
                            aclmsg1.addReceiver(new AID("manageragent", AID.ISLOCALNAME));
                            aclmsg1.setContent("Train again");
                            send(aclmsg1);
                        }
                        break;  
                    case "Predict":
                        if(isTrained == true){
                            System.out.println("\nPerformed by "+getLocalName()+":Predicition function");
                            System.out.println("\nPerformed by "+getLocalName()+":The agents will do a prediction");
                            ACLMessage aclmsg2 = new ACLMessage(ACLMessage.REQUEST);
                            aclmsg2.addReceiver(new AID("manageragent", AID.ISLOCALNAME));
                            aclmsg2.setContent("Carry out prediction");
                            send(aclmsg2);
                        }
                        else{
                            System.out.println("You must train first at least one time");
                        }
                        break;
                    default:
                        System.out.println("\nPerformed by "+getLocalName()+":Type one of the correct options");
                        break;
                }
            } catch (IOException | UnreadableException ex) {
                Logger.getLogger(UserAgent.class.getName()).log(Level.SEVERE, null, ex);
            }    
        }
       
        private boolean finished = false;
        @Override
        public boolean done() {
            block(500);
            return finished;     
        }   
    }
}