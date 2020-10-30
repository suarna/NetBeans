/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author miguel
 */
public class Predictor {
    
   
    public String[] predict(Instances dataSet , Classifier tree ,boolean verbose) throws Exception{
        dataSet.setClassIndex(dataSet.numAttributes() - 1);
        Instances predicted = new Instances(dataSet);
        String[] output =new String[dataSet.numInstances()];
            for (int i = 0; i < dataSet.numInstances(); i++) {
                double actualClass = dataSet.instance(i).classValue();
                String actual = dataSet.classAttribute().value((int)actualClass);
                Instance newInstance = dataSet.instance(i) ;
                double clsLabel = tree.classifyInstance(newInstance);
                String predictedString = dataSet.classAttribute().value((int) clsLabel);
                output[i] = predictedString;
                predicted.instance(i).setClassValue(clsLabel);
                if(verbose){
                System.out.println(actual+" , "+predictedString);
                }
            }
        return output;
    }
    public void evaluation(Instances dataSetPredict ,Instances dataSetTrain , Classifier tree ,boolean verbose) throws Exception{
         dataSetPredict.setClassIndex(dataSetPredict.numAttributes() - 1);
         Evaluation eval = new Evaluation(dataSetTrain);
         eval.evaluateModel(tree, dataSetPredict);
         if(verbose){
         System.out.println(eval.toSummaryString("\nResults\n======\n", true));
         }
    }
}
