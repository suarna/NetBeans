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
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 *
 * @author miguel
 */
public class ClassifierAgent extends Agent {
    
    
    @Override
    protected void setup(){
        addBehaviour(new receiveDataBehaviour(this));
    }

    private class receiveDataBehaviour extends CyclicBehaviour {

        public receiveDataBehaviour(Agent agent) {
            super(agent);
        }
        Classifier tree;
        Training train;
        Instances predictSet;
        Instances trainSet;
        Instances instances;
        Properties prop;
        
        @Override
        public void action() {
             ACLMessage recmsg = receive();
             
             if(recmsg!=null){
                 try {
                   
                   String conversation = recmsg.getConversationId();
                   
                   if(conversation != "prediction"){
                   //Receiving Object  
                   Packager packet= (Packager) recmsg.getContentObject();
                   instances = packet.getInstances();
                   prop = packet.getProperties();
                   
                   
                   //Inform Message
                   ACLMessage reply = recmsg.createReply();
                   reply.setPerformative( ACLMessage.INFORM );
                   reply.setContent("Data Received" );
                   send(reply);
                   }
                   
                   //Preparing Data
                   Data data = new Data();
                   data.prepareData(instances, prop, getLocalName());
                   Instances trainSet = data.getTrainSet();
                   Instances predictSet = data.getPredictSet();
                   //System.out.println(trainSet);
                  
                   
                   //Train the algorithm
                   Training train = new Training();
                   tree = train.trainJ48(trainSet);
                   
                  
                   if(conversation == "prediction"){
                   switch(recmsg.getContent()){
                       
                   case "Train again":      
                   tree = train.trainJ48(trainSet);
                   System.out.println("The "+getLocalName() +" has trained the algorithm again!!");
                   break;
                       
                   case "Carry out prediction":
                   //Predict 
                   Predictor prediction = new Predictor();
                   prediction.evaluation(predictSet,trainSet, tree ,false);                     //With true show data on screen
                   String[] predictString = prediction.predict(predictSet, tree ,false) ;       //With true verbose
                 
                   //Sending Prediction
                   ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
                   msg.addReceiver(new AID("manageragent", AID.ISLOCALNAME));
                   msg.setConversationId("prediction");
                   msg.setContentObject(predictString);
                   //System.out.println("I am predicting for you");
                   send(msg);
                   break;
                   }
                   }
                   
                 }  catch (Exception ex) {
                     Logger.getLogger(ClassifierAgent.class.getName()).log(Level.SEVERE, null, ex);
                 }
                }
             else{
                 block();
             }
        }
    }         
}
