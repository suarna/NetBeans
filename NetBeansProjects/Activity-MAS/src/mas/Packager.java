package mas;


import java.io.Serializable;
import java.util.Properties;
import weka.core.Instances;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
========================================================================
THIS CLASS PREPARE A PACKAGE WITH THE OBJECTS TO SEND TO THE CLASSIFIERS
========================================================================
 */

/**
 *
 * @author miguel
 */
class Packager implements Serializable {

    private Instances objectToSend;
    private Properties propertiesToSend;
    
    public void setInstances(Instances object){
        this.objectToSend = object;
    }
    public void setProperties(Properties file){
        this.propertiesToSend = file;
    }
    public Instances getInstances(){   
        return objectToSend;  
    }
    public Properties getProperties(){
        return propertiesToSend;
    }
} 