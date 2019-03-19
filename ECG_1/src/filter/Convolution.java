/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filter;

/**
 *
 * @author bon
 */
public class Convolution {
    private int[] coef;
    public Convolution(int[] coef)
    {
        this.coef = coef;
    }
    public int[] getValue(int[] data)
    {
        int[] newdata = new int[data.length-coef.length];
        for(int i=0;i<data.length-coef.length;i++)
        {
            int area = 0;
            for(int j=0;j<coef.length;j++)
            {
                area += Math.abs(data[i+j]+coef[j]);
            }
            newdata[i] = area/10;
        }
        return newdata;
    }

}
