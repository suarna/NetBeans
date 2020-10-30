/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
====================================================================
THIS CLASS WRITES DOWN THE DATA TO A FILE
====================================================================
 */
package mas;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.String;
import java.util.Arrays;
/**
 *
 * @author miguel
 */
public class DataArray {
    public String[] prepareArray(String[] dataString , String agentName ) throws IOException{
    
     
    /*String newItem = agentName;
    int currentSize = dataString.length;
    int newSize = currentSize + 1;
    String[] tempArray = new String[ newSize ];
    for (int i=0; i < currentSize; i++)
        {
        tempArray[i] = dataString [i];
        }
    tempArray[newSize- 1] = newItem;
    dataString = tempArray;*/  
   
    BufferedWriter writer = new BufferedWriter(new FileWriter("prediction.txt", true));
    writer.append('\n');
    writer.append(Arrays.deepToString(dataString));
    writer.close();
    
    return dataString;
    }
    public int getAgentNumber(String agentName){
        int idx = agentName.length();
        char charAt = agentName.charAt(idx-1);
        int value = Character.getNumericValue(charAt);
        return value;
    }
}
  