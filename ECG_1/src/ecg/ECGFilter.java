/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ecg;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author bon
 */
public class ECGFilter {
    public int[] max(int[] sig,int smoothing)
    {
       if(smoothing==1)
       {
           sig = selectedLowpassfilter(sig, null, median(sig),smoothing);
           return sig;
       }
       else
       {
        int range = 20;
        int totalmax = 0;
        int max = 0;
        int min = 0;
        int median = median(sig);
        int[] qrs = new int[sig.length];

        for(int i=0;i<qrs.length-range;i++)
        {
            int[] sub = new int[range];
            for(int j=0;j<sub.length;j++)
            {
                sub[j] = sig[i+j];
                if(j==0)
                {
                    max=sub[j];
                    min=sub[j];
                }
                else
                {
                max = Math.max(max, sub[j]);
                min = Math.min(min, sub[j]);
                }
            }
            qrs[i+range] = max-min;
            totalmax = Math.max(totalmax, max-min);
        }

        ArrayList QRSranges = new ArrayList();
        for(int i=0;i<qrs.length;i++)
        {
            if(qrs[i]>totalmax*0.5)
            {
                if(i>=10)
                {
                    QRSranges.add(i-10);
                }
                else
                {
                    QRSranges.add(0);
                }

                i = i+60;
            }
        }
        sig = selectedLowpassfilter(sig, QRSranges, median,smoothing);
        return sig;
       }
    }
private int median(int[] sig) {

    int[] m = new int[sig.length];
    System.arraycopy(sig, 0, m, 0, sig.length);
    Arrays.sort(m);

    if (m.length % 2 == 1)
    {
        return m[(m.length+1)/2-1];
    }
    else
    {
        return (int) ((m[m.length/2-1] + m[m.length/2]) / 2.0);
    }	
    }
private int[] selectedLowpassfilter(int[] sig, ArrayList qrs, int median, int smoothing)
     {
         // smoothing: the strength of the smoothing filter; 1=no change, larger values smoothes more
         //int smoothing = 5;
         
         int v0 = sig[0];
         int start = 0;
         for(int i=1;i<sig.length;i++)
         {
             if(qrs!=null&&!qrs.isEmpty()&&Integer.parseInt(qrs.get(start).toString())<=i&&i<=Integer.parseInt(qrs.get(start).toString())+60)
             {
                int v = sig[i];
                v0 += (v-v0);
                sig[i] = sig[i]-median;
             if(start<qrs.size()-1&&Integer.parseInt(qrs.get(start).toString())+30==i)
             {
                start++;
             }
             }
             else
             {
                int v = sig[i];
                v0 += (v-v0)/smoothing;
                sig[i] = v0-median;
             }
         }
         return sig;
     }
public int[] QRSdetection(int[] sig)
{
        int range = 20;
        int totalmax = 0;
        int max = 0;
        int min = 0;
        int[] qrs = new int[sig.length];
        for(int i=0;i<qrs.length-range;i++)
        {
            int[] sub = new int[range];
            for(int j=0;j<sub.length;j++)
            {
                sub[j] = sig[i+j];
                if(j==0)
                {
                    max=sub[j];
                    min=sub[j];
                }
                max = Math.max(max, sub[j]);
                min = Math.min(min, sub[j]);
            }
            qrs[i+range] = max-min;
            totalmax = Math.max(totalmax, max-min);
        }
        int start=0;
        for(int i=0;i<qrs.length;i++)
        {
            if(qrs[i]>totalmax*0.5)
            {
                if(start<60)
                {
                for(int j=0;j<10;j++)
                {         
                    if(i>5)
                    {
                    qrs[i-j] = 100;
                    }
                }
                qrs[i]=100;
                }
                else
                {
                    qrs[i] = 0;
                }
                start++;
            }
            else
            {
                start=0;
                qrs[i] =0;
            }
        }
        return qrs;
}
}
