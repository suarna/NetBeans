/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
====================================================================
THIS CLASS PREPARE HTE DATA SPLITTING THE ARFF FILE
====================================================================
 */
package mas;

import java.util.Properties;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemovePercentage;
import weka.filters.unsupervised.instance.RemoveRange;

/**
 *
 * @author miguel
 */
public class Data {
    
    private Instances predictReturn;
    private Instances trainReturn;
    
    public void prepareData(Instances instances , Properties prop ,String localname) throws Exception{
        
        String classifyValue = prop.getProperty("classifyInstances");
        String [] trainSettings = (prop.getProperty("trainingSettings")).split(",");
        int idx = localname.length();
        char charAt = localname.charAt(idx-1);
        String trainsetValue = trainSettings [Character.getNumericValue(charAt)-1];
        
        RemoveRange remove = new RemoveRange();
        remove.setInstancesIndices("first-"+trainsetValue);
        remove.setInvertSelection(true);
        remove.setInputFormat(instances);
        Instances trainSet = Filter.useFilter(instances, remove);
        //System.out.println(trainSet.numInstances() + " Train instances");
        
        RemovePercentage remover = new RemovePercentage();
        remover.setPercentage(Integer.parseInt(classifyValue));
        remover.setInvertSelection(true);
        remover.setInputFormat(instances);
        Instances predictSet = Filter.useFilter(instances, remover);
        this.predictReturn = predictSet;
        this.trainReturn = trainSet;
        //System.out.println(predictSet.numInstances() + " Prediction instances");
    }
   public Instances getTrainSet(){
       return trainReturn;
   }
   public Instances getPredictSet(){
       return predictReturn;
   }
}
