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
import jade.wrapper.AgentContainer;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;


/**
 *
 * @author miguel
 */
public class ManagerAgent extends Agent{
    @Override
    protected void setup(){
         addBehaviour(new sendConfigurationBehaviour(this));            
    }
    
    private class sendConfigurationBehaviour extends CyclicBehaviour {

        public sendConfigurationBehaviour(Agent agent) {
            super(agent);
        }
        int nagents;
        Properties prop;
        String file;
        int i=0;
           
        @Override
        public void action() {
            ACLMessage msg = receive();  
                if(msg!=null){
                    try {
                        switch (msg.getContent()) {
                            case "Create Agents":
                                System.out.println("\nPerformed by "+getLocalName()+":The manager will create the classifier agents");
                                
                                //Load data from arff file
                                BufferedReader reader = new BufferedReader(new FileReader("data/"+file));
                                ArffReader arff = new ArffReader(reader);
                                Instances data = arff.getData();
                                //System.out.println("\nPerformed by "+getLocalName()+":The arff file contains the following data\n"+ data);
                                
                                Packager packet = new Packager();
                                packet.setInstances(data);
                                packet.setProperties(prop);
                                
                                for(int i = 1 ; i < nagents+1 ; i++){
                                    String name = "classifier"+i;
                                    AgentContainer container = getContainerController();
                                    AgentController agent = container.createNewAgent(name , "mas.ClassifierAgent", null);
                                    agent.start();   
                                    ACLMessage aclmsg = new ACLMessage(ACLMessage.REQUEST);
                                    aclmsg.addReceiver(new AID("classifier"+i, AID.ISLOCALNAME));
                                    aclmsg.setContentObject(packet);
                                    send(aclmsg);
                                }
                                
                               
                                ACLMessage reply = msg.createReply();
                                reply.setPerformative( ACLMessage.INFORM );
                                reply.setContent("Agents Created" );
                                send(reply);
                                
                            break;
                            
                            case "Data Received":
                                System.out.println("Data has been received by Classifier:"+msg.getSender().getLocalName());
                            break;
                            
                            case "Train again":
                                System.out.println("\nPerformed by "+getLocalName()+":The manager will do a request in order to train again the classifiers");
                                for(int i = 1 ; i < nagents+1 ; i++){  
                                    ACLMessage aclmsg = new ACLMessage(ACLMessage.REQUEST);
                                    aclmsg.setConversationId("prediction");
                                    aclmsg.addReceiver(new AID("classifier"+i, AID.ISLOCALNAME));
                                    aclmsg.setContent("Train again");
                                    send(aclmsg);
                                }     
                            break;
                            
                            case "Carry out prediction":
                                System.out.println("\nPerformed by "+getLocalName()+":The manager will get a prediciton from the classifiers");
                                i=0;
                                deleteFile("prediction.txt");
                                for(int i = 1 ; i < nagents+1 ; i++){ 
                                    //Send request
                                    ACLMessage aclmsg = new ACLMessage(ACLMessage.REQUEST);
                                    aclmsg.setConversationId("prediction");
                                    aclmsg.addReceiver(new AID("classifier"+i, AID.ISLOCALNAME));
                                    aclmsg.setContent("Carry out prediction");
                                    send(aclmsg);
                                }
                            break;
                            
                            default:
                                if(!"prediction".equals(msg.getConversationId())){
                                    prop = (Properties) msg.getContentObject();
                                    nagents = Integer.parseInt(prop.getProperty("classifiers")); 
                                    file = prop.getProperty("file");
                                }
                                //Preparing received data to predict
                                else if("prediction".equals(msg.getConversationId())){
                                    //Receiving data from classifiers and storing in a file
                                    String[] returnedData = (String[]) msg.getContentObject();
                                    String sender =  msg.getSender().getLocalName();
                                    DataArray prepArray = new DataArray();
                                    String[] arrayData=prepArray.prepareArray(returnedData, sender); 
                                   
                                    i++;
                                    if(i==nagents){
                                       String prediction= predict(returnedData.length,nagents);
                                       ACLMessage aclmsg = new ACLMessage(ACLMessage.PROPOSE);
                                       aclmsg.setConversationId("prediction");
                                       aclmsg.addReceiver(new AID("useragent", AID.ISLOCALNAME));
                                       aclmsg.setContentObject(prediction);
                                       send(aclmsg);
                                       }
                                }
                        }
                    } 
                    catch (UnreadableException | StaleProxyException | IOException | NumberFormatException ex) {
                        Logger.getLogger(ManagerAgent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else{ 
                    block();
                }   
        }
    }
    
    private String predict(int leng , int nagents) throws FileNotFoundException, IOException{
        
        BufferedReader reader;
        List<String> matrix=new ArrayList<>();
        int []votes = new int[nagents];
        reader = new BufferedReader(new FileReader("prediction.txt"));
        String line = reader.readLine();
        int i=0;
	while (line != null) {
	    // read next line
            String replace =line.replace("[","");
            String replace1 =replace.replace("]","");
            String replace2 =replace1.replace(",", "");
            matrix.add(replace2);
            line = reader.readLine();
            i++; 
        }
        matrix.remove(0);
        //System.out.println(matrix);
         int vote=0;
        for(int j=0 ; j<nagents ; j++){
            for(int k=0 ; k<nagents ; k++){
                if(matrix.get(j).equals(matrix.get(k))){
                    vote++;
                    votes[j]=vote;
                }
            }
            vote=0;
        }
        int maxidx = 0;
        
        for (i = 0; i < votes.length; i++) {
            maxidx = votes[i] > votes[maxidx] ? i : maxidx;
        }
        return matrix.get(maxidx);
    }
   private void deleteFile(String file){
        File filePred = new File(file);
        filePred.delete();
    }
}
