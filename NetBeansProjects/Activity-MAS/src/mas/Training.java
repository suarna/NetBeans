/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
====================================================================
THIS CLASS TRAIN THE ALGORTIHM TO USE,BY THE MOMENT ONLY J48
====================================================================
 */
package mas;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;
import weka.core.Instances;

/**
 *
 * @author miguel
 */
public class Training {
    
    public Classifier trainJ48(Instances dataSet) throws Exception{
        dataSet.setClassIndex(dataSet.numAttributes() -1);
        Classifier tree = new J48();
        String[] options = new String[1];
        options[0] = "-U";
        tree.setOptions(options);
        tree.buildClassifier(dataSet);
        return tree;
    }
    public Classifier trainLMT (Instances dataSet){    //No implemented yet
        dataSet.setClassIndex(dataSet.numAttributes() -1);
        Classifier tree = new LMT();
        return tree;
    }
}
