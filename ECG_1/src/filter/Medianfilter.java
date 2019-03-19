/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filter;

import java.util.Arrays;

/**
 *
 * @author bon
 */
public class Medianfilter {
    private int length = 20;
    public int[] getMedian(int[] data)
    {
        int[] newdata = new int[data.length];
        for(int i=0;i<data.length;i++)
        {
            int[] range = new int[length+1];
            int n = 0;
            for(int j=-1*(length/2);j<(length/2)+1;j++)
            {
                int index = j+i;
            if(index<0 || index>=data.length)
            {
                range[n] = data[i];
            }
            else
            {
                range[n] = data[index];
            }
            n++;
            }
            newdata[i] = median(range);
            
        }
        return newdata;
    }
    public int median(int[] numArray)
        {
            Arrays.sort(numArray);
            int middle = ((numArray.length) / 2);
            if(numArray.length % 2 == 0)
            {
            int medianA = numArray[middle];
            int medianB = numArray[middle-1];
            int median = (medianA + medianB) / 2;
            return median;
            } 
            else
            {
            int median = numArray[middle + 1];
            return median;
            }
        }
}
