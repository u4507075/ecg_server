package filter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * Class: ChallengeEntry
 *        Algorithm to compute classification of ECG records. 
 *        Returned value = 1 (acceptable) or 0 (unacceptable)   
 *        
 *		  @ authors: Philip Langley, Philip.Langley@ncl.ac.uk
 *                   David Duncan, david.duncan@nuth.nhs.uk
 *        Note: This is a partial implementation of the algorithm developed
 *        for part 1. Flat line, Saturation, Baseline drift and Low Amplitude
 *        checks are implemented. High amplitude and Steep slope are not implemented.           
 */
public class baselinewander {

	final static int FS =	 500; // Sampling Frequency
	final static int DW = FS * 2; // 2s (skip first DW samples in BL analysis)
	final static int DW_Sat = Math.round(200 * FS / 1000);
	// ======= THRESHOLDS ============
	final static int SAT_THR = 400;
	final static int D1_Thr = 50;
	final static int dE_min_Thr  = 25;   // units [LSB] max elongation of ECG from BL
	static int D1_Thr_Count = 0;
	final static int BLW_max_Thr = 500; // units [LSB] Baseline amplitude swing
										// (max)
	final static int CH = 12;
	final static int MAX_RT = 220; // Max expected beats in minutes
	final static int WIN = FS * 10;
	static int[][] Thr_Count_Array = new int[12][2];
	//static double[][] filtered_ecg_array = new double[WIN][CH];
	//int[] data = new int[WIN * CH];

        public float median(int[] numArray)
        {
            Arrays.sort(numArray);
            int middle = ((numArray.length) / 2);
            if(numArray.length % 2 == 0)
            {
            int medianA = numArray[middle];
            int medianB = numArray[middle-1];
            float median = (medianA + medianB) / 2;
            return median;
            } 
            else
            {
            float median = numArray[middle + 1];
            return median;
            }
        }
        public ArrayList getShiftpoint(int[] data)
        {
            ArrayList d = new ArrayList();
            d.add(0);
            //int refractory = 0;
            int upgrade = 1;
            int increase = 0;
            for(int i=1;i<data.length;i++)
            {
                int diff = Math.abs(data[i]-data[i-1]);
                if(diff>((256*upgrade)-30))
                {
                    //if(refractory==0)
                    {
                        int dub = diff/256;
                        if(diff%256>128)
                        {
                            dub++;
                        }
                        //System.out.println("Dub = "+(dub- increase));
                        upgrade=upgrade + dub - increase;
                        increase = dub;
                        //upgrade++;
                        d.add(i);
                    }
                   // refractory = 50;
                    
                }
                /*
                else
                {
                    if(refractory>0)
                    {
                        refractory--;
                    }
                }
                */
            }
            d.add(data.length);
            return d;

        }
        public int[] getShift(int[] data)
        {
            int[] d = new int[data.length];
            d[0] = 0;
            //int refractory = 0;
            int upgrade = 1;
            int increase = 0;
            boolean positive = true;
            //System.out.println("Start");
            for(int i=1;i<data.length;i++)
            {
                int diff = data[i]-data[i-1];//Math.abs(data[i]-data[i-1]);
                if(diff>((256*upgrade)-30))
                {
                    d[i] = upgrade*256;
                    //if(refractory==0)
                    {
                        /*
                        BigDecimal a = new BigDecimal(diff);
                        BigDecimal b = new BigDecimal(256);
                        BigDecimal c = a.divide(b,1, BigDecimal.ROUND_HALF_UP);
                        upgrade=upgrade+1;
                        System.out.println(c.intValue());
                        upgrade=upgrade+c.intValue();*/
                        //upgrade=upgrade+1;
                        
                        int dub = diff/256;
                        
                        
                        //System.out.println(diff%256);
                        if(diff%256>128)
                        {
                            dub++;
                        }
                        //System.out.println("Dub = "+(dub- increase));
                        upgrade=upgrade + dub - increase;
                        increase = dub;
                    }
                    //refractory = 50;
                    positive = true;
                }
                else if(diff<((-256*upgrade)+30))
                {
                    d[i] = upgrade*-256;
                    //if(refractory==0)
                    {
                        /*
                        BigDecimal a = new BigDecimal(diff);
                        BigDecimal b = new BigDecimal(256);
                        BigDecimal c = a.divide(b,1, BigDecimal.ROUND_HALF_UP);
                        upgrade=upgrade+1;
                        System.out.println(c.intValue());
                        upgrade=upgrade+c.intValue();*/
                        //upgrade=upgrade+1;
                        
                        int dub = -1*diff/256;
                        
                        
                        //System.out.println(diff%256);
                        if((-1*diff)%256>128)
                        {
                            dub++;
                        }
                        //System.out.println("Dub negative = "+(dub- increase));
                        upgrade=upgrade + dub - increase;
                        increase = dub;
                    }
                    //refractory = 50;
                    positive = false;
                }
                else
                {
                    if(positive)
                    {
                        d[i] = (upgrade-1)*256;
                    }
                    else
                    {
                        d[i] = (upgrade-1)*-256;
                    }
                    /*
                    if(refractory>0)
                    {
                        refractory--;
                    }*/
                }
                
            }
            /*
            for(int i=1;i<data.length;i++)
            {
                int diff = Math.abs(data[i]-data[i-1]);
                if(diff>(230*upgrade))
                {
                    if(refractory==0)
                    {
                        int dub = diff/256;
                        if(diff%256>128)
                        {
                            dub++;
                        }
                        upgrade=upgrade+dub;
                    }
                    refractory = 50;
                    d[i]=(upgrade-1)*256;
                    
                }
                else
                {
                    d[i]=(upgrade-1)*256;
                    if(refractory>0)
                    {
                        refractory--;
                    }
                }
                
            }*/
            return d;

        }
        /*
        public int[] getValue(int[] data)
        {
            int[] d = new int[data.length];
            for(int i=0;i<data.length;i++)
            {
                int v = data[i]%256;
                if(v>128)
                {
                    d[i]=data[i]+(256-v);
                }
                else if(v<-128)
                {
                    d[i] = data[i]+(-256-(v));
                }
                else if(v>0)
                {
                    d[i] = data[i] - v;
                }
                else if(v<=0)
                {
                    d[i] = data[i] - v;
                }
            }
            return d;
        }*/
        private int min(int a, int b)
        {
            if(a<b)
            {
                return a;
            }
            else
            {
                return b;
            }
        }
        public int[] getdiff(int[] data)
        {
            int[] d = new int[data.length];
            d[0] = 0;
            for(int i=1;i<data.length;i++)
            {
                d[i]=data[i]-data[i-1];
            }
            return d;

        }
        public int[] getV(int[] data)
        {
            //int[] d = new int[data.length];
            for(int j=0;j<20;j++)
            {
                int a = 0;
                boolean complete = true;
                for(int i=1;i<data.length;i++)
                {
                    int diff =data[i]-data[i-1];
                    if(diff<200)
                    {

                    }
                    else if(diff>200)
                    {
                            complete = false;
                            a=a+256;
                            data[i] = data[i]-a;
                    }

                }
                if(complete)
                {
                    break;
                }
            }
            return data;
        }
        public int[] getResult(int[] data) throws IOException 
        {
                                 //this.data = data;

		// preset result (1=acceptable)
		int result = 1;
		int bUnacc = 0;

		// Load data into 2D array
		int COLS = CH;
		int ROWS = WIN;
		//int[][] ecg_array = loadArrayData(data, COLS, ROWS);

		// verify flat line
                /*
		FlatLine: for (int c = 0; c < ecg_array[1].length; c++) {
			int[] col_array = new int[ROWS];
			for (int r = 0; r < ecg_array.length; r++) {
				col_array[r] = ecg_array[r][c];
			}
			// Detect Flat Line
			if (detectFlat(col_array, c)) {
				bUnacc = 1;
				break FlatLine;
			}
		//}*/
                            double[] d = new double[data.length];
                            double[] filtered_ecg_array = new double[data.length];
                            for(int i=0;i<data.length;i++)
                            {
                                d[i] = (double)data[i];
                            }
                            boolean flat = detectFlat(data,0);
                            if(flat)
                            {
                                System.out.println("Flat");
                                return data;
                            }
                            else
                            {
                                boolean sat = detectSaturation(data, 0);
                                if(sat)
                                {
                                    System.out.println("Sat");
                                    return data;
                                }
                                else
                                {
                                    setFilteredECGData(filter(d), filtered_ecg_array);
                                    double[] dE = subtractArray(data, filtered_ecg_array);
                                    double[] ecgLPF = LPF_ecg(dE);
                                    int[] out = new int[dE.length];
                                    for(int i=0;i<dE.length;i++)
                                    {
                                        out[i] = (int)(dE[i]);
                                    }
                                    System.out.println("Baseline wander");
                                    return out;
                                }
/*

		// test for saturation
		if (bUnacc == 0) {
			Saturation: for (int c = 0; c < ecg_array[1].length; c++) {
				int[] col_array = new int[ROWS];
				for (int r = 0; r < ecg_array.length; r++) {
					col_array[r] = ecg_array[r][c];
				}
				// Detect Saturation
				if (detectSaturation(col_array, c)) {
					bUnacc = 1;
					break Saturation;
				}
			}
		}*/
                                /*
		if (bUnacc == 0) {
			// determine BL for all leads
			for (int c = 0; c < ecg_array[1].length; c++) {
				double[] col_array = new double[ROWS];
				for (int r = 0; r < ecg_array.length; r++) {
					col_array[r] = ecg_array[r][c];
				}
				// filter
				setFilteredECGData(c, filter(col_array), filtered_ecg_array);
			}
			// ECG elongation from BL (dE)
			double[][] dE = subtractArray(ecg_array, filtered_ecg_array);

			// LPF for PM rejection
			//double[][] ecgLPF = LPF_ecg(dE);

			// collect baseline wander (BLW) and elongation (dE) for all leads
			double[][] v = new double[12][3];
			for (int c = 0; c < filtered_ecg_array[1].length; c++) {
				double[] blwander_array = new double[filtered_ecg_array.length];
				double[] v2_array = new double[filtered_ecg_array.length];
				for (int r = 0; r < filtered_ecg_array.length; r++) {
					blwander_array[r] = filtered_ecg_array[r][c];
					v2_array[r] = dE[r][c];
				}
				v[c][0] = getBLWander(blwander_array); // BLW units [LSB]
				v[c][1] = getAbsMax(v2_array); // dE max, units [LSB]				
				// v(ld, 3) = max(abs(yLPF(:,ld))); % dE-LPF max, units [LSB]
			}
			
			double min_val = v[0][0];
			for (int r = 0; r < 12; r++) {
				if (v[r][0] < min_val) {
					min_val = v[r][0];
				}
			}
			if ((min_val < BLW_max_Thr)) {
				// stable baseline
				// rejection criterion:: 1) amplitude lower than dE min threshold in ANY lead
				int dE_min_Thr2 = dE_min_Thr + 10; // units [uV]
				double min_value = v[0][1];
				int cnt = 0;
				for (int r = 0; r < 12; r++) {
					if (v[r][1] < min_value) {
						min_value = v[r][1];
					}
					if (v[r][1] < dE_min_Thr2) {
						cnt++;
					}
				}
				if( min_value<dE_min_Thr ){
					// mark unacceptable
			        bUnacc = 1;
				}else if (min_value<dE_min_Thr2){
					if( cnt>=3 ){
						// mark unacceptable  
			            bUnacc = 1;
					}
				}
			}else{
				//unstable baseline
				bUnacc = 1;
			}
		}
		
		if (bUnacc == 1){
			// set result to unacceptable
			result = 0;
		}	
	//	System.out.println("Result = " + result + " Flat = " + flatFlag
	//			+ ", Saturation = " + saturationFlag + ", SBL = " + isStableBL+", MinThresh1 = "+minThresh1
	//			+ ", MinThresh2 = "+minThresh2);

		// clean-up
*/
                            }

	}

	/*
	 * Load 1D array of data into a 2D array
	 */
	private int[][] loadArrayData(int[] data, int cols, int rows)
			throws NumberFormatException, IOException {

		int[][] ecg_array = new int[rows][cols];
		int linear_index = 0;

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				ecg_array[r][c] = data[linear_index];
				linear_index++;
			}
		}
		return ecg_array;
	}
	
	private double getAbsMax(double[] dE){
		double max_val = dE[0];
		for (int r = 0; r < dE.length; r++) {
			if (Math.abs(dE[r]) > max_val) {
				max_val = Math.abs(dE[r]);
			}
		}
		return max_val;
	}
	
	private double getBLWander(double[] ecg) {
		int Le = ecg.length;
		double max_val = 0;

		for (int i = DW; i < Le; i++) {
			if (ecg[i] > max_val) {
				max_val = ecg[i];
			}
		}

		double min_val = max_val;

		for (int i = DW; i < Le; i++) {
			if (ecg[i] < min_val) {
				min_val = ecg[i];
			}
		}
		double BLW = max_val - min_val;
		return BLW;
	}
        private void setFilteredECGData(double[] filtered1DArray,double[] filteredecg) {
                                System.arraycopy(filtered1DArray, 0, filteredecg, 0, filtered1DArray.length);
		//return null;
	}
/*
	private double[][] setFilteredECGData(int col, double[] filtered1DArray,
			double[][] filteredecg) {
		for (int r = 0; r < filtered1DArray.length; r++) {
			filteredecg[r][col] = filtered1DArray[r];
		}
		return null;
	}*/

/*
	private double[][] subtractArray(int[][] a, double[][] b) {
		double[][] dE = new double[a.length][a[1].length];
		for (int c = 0; c < a[1].length; c++) {
			for (int r = 0; r < a.length; r++) {
				dE[r][c] = a[r][c] - b[r][c];
			}
		}
		return dE;
	}*/
                private double[] subtractArray(int[] a, double[] b) {
		double[] dE = new double[a.length];
		for (int c = 0; c < a.length; c++) {
				dE[c] = a[c] - b[c];
		}
		return dE;
	}

	private double[] LPF_ecg(double[] ecg) {
		int DS_80MS = Math.round(80 * FS / 1000);
		int DS_100MS = Math.round(100 * FS / 1000);
		int DS_180MS = Math.round(180 * FS / 1000);

		// preset
		double[] yLPF = ecg;

		double Le = ecg.length;
		double[] col_array = new double[ecg.length];
		for (int c = 0; c < ecg.length; c++) {
			//for (int r = 0; r < ecg.length; r++) {
				col_array[c] = ecg[c];
			//}
			double[] d = diff_abs(col_array);

			int[] vI = findD1Threshold(d);
			int L = vI.length;
			double T_refr = 1;
			for (int j = 0; j < L; j++) {
				if (vI[j] > T_refr) {
					int t1 = vI[j] - DS_80MS;
					int t2 = (int) Math.min(Le, t1 + DS_180MS);
					if (t1 > 0) {
							yLPF[c] = ecg[c];
					} else {
							yLPF[c] = ecg[c];
					}
					T_refr = t2 + DS_100MS;
				}
			}
		}
		return yLPF;
	}

	private int[] diff(int[] ecg_column) {
		// maintain count of non-zero differences
		int[] diff_array = new int[ecg_column.length - 1];
		for (int row = 1; row < ecg_column.length; row++) {
			int df = (int) (ecg_column[row] - ecg_column[row - 1]);
			diff_array[row - 1] = df;
		}
		return diff_array;
	}

	private double[] diff_abs(double[] ecg_column) {
		D1_Thr_Count = 0;
		// maintain count of non-zero differences
		double[] diff_array = new double[ecg_column.length - 1];
		for (int row = 1; row < ecg_column.length; row++) {
			double df = (double) (ecg_column[row] - ecg_column[row - 1]);
			diff_array[row - 1] = Math.abs(df);
			if (diff_array[row - 1] > D1_Thr) {
				D1_Thr_Count++;
			}
		}
		return diff_array;
	}

	private int[] diff_find(int[] ecg_column, int data_number) {
		int abs_thr_count = 0;
		// maintain count of non-zero differences
		int non_zero_count = 0;
		int[] diff_array = new int[ecg_column.length - 1];
		for (int row = 1; row < ecg_column.length; row++) {
			int df = (int) (ecg_column[row] - ecg_column[row - 1]);
			diff_array[row - 1] = df;
			if (df != 0) {
				non_zero_count++;
			}
			// also collect number of instances where abs(ecg) < 400
			if (Math.abs(ecg_column[row]) < 400) {
				abs_thr_count++;
			}
		}
		// set number of abs_thr finds for future use
		if (Math.abs(ecg_column[0]) < 400) {
			abs_thr_count++;
		}
		Thr_Count_Array[data_number][1] = abs_thr_count;

		int[] find_array = find(diff_array, non_zero_count);
		return find_array;
	}

	// get all non-zero indices from diff array
	private int[] find(int[] diff_array, int count) {

		int[] find_array = new int[count];
		int index_counter = 0;
		int find_array_counter = 0;

		for (int row = 0; row < diff_array.length; row++) {
			if ((int) diff_array[row] != 0) {
				find_array[find_array_counter] = index_counter;
				find_array_counter++;
			}
			index_counter++;
		}
		index_counter++;

		return find_array;
	}

	// get all non-zero indices from diff array
	private int[] findThreshold(int[] diff_array, int count) {

		int[] find_array = new int[count];
		int index_counter = 0;
		int find_array_counter = 0;

		for (int row = 0; row < diff_array.length; row++) {
			if (Math.abs(diff_array[row]) < SAT_THR) {
				find_array[find_array_counter] = index_counter;
				find_array_counter++;
			}
			index_counter++;
		}
		index_counter++;

		return find_array;
	}

	private int[] findD1Threshold(double[] diff_array) {

		int[] find_array = new int[D1_Thr_Count];
		int index_counter = 0;
		int find_array_counter = 0;

		for (int row = 0; row < diff_array.length; row++) {
			if (diff_array[row] > D1_Thr) {
				find_array[find_array_counter] = index_counter;
				find_array_counter++;
			}
			index_counter++;
		}
		index_counter++;

		return find_array;
	}

	private int getMaxDiff(int[] find_array) {
		int max_val = 0;
		for (int i = 1; i < find_array.length; i++) {
			int temp_val = find_array[i] - find_array[i - 1];
			if (temp_val > max_val) {
				max_val = temp_val;
			}
		}
		return max_val;
	}

	private boolean detectFlat(int[] col_array, int c_number) {
		int[] non_zero_indice_array = diff_find(col_array, c_number);

		if (non_zero_indice_array.length > 0) {
			int[] diff_array = diff(non_zero_indice_array);
			int max = getMaxDiff(diff_array);
			if (max > FS) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	private boolean detectSaturation(int[] col_array, int c_number) {
		int[] below_threshold_array = findThreshold(col_array,
				Thr_Count_Array[c_number][1]);

		if (below_threshold_array.length > 0) {
			int[] diff_array = diff(below_threshold_array);
			int max = getMaxDiff(diff_array);
			if (max > DW_Sat) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	private double[] filter(double[] ecg_unfilt) {
		// order 6 filter
		int order = 6;
		double[] b = new double[order + 1];
		double[] a = new double[order + 1];
		double[] x = ecg_unfilt;
		int nDataPoints = x.length;
		b[0] = 0.0000000000000600231669922735;
		b[1] = 0.000000000000360139001953641;
		b[2] = 0.0000000000009003475048841025;
		b[3] = 0.00000000000120046333984547;
		b[4] = 0.0000000000009003475048841025;
		b[5] = 0.000000000000360139001953641;
		b[0] = 0.0000000000000600231669922735;
		a[0] = 1.0000;
		a[1] = -5.951447334984479;
		a[2] = 14.758413952404254;
		a[3] = -19.5191643872012;
		a[4] = 14.521482981667374;
		a[5] = -5.761891935232455;
		a[6] = 0.952606723350349;

		double[] y = new double[nDataPoints];

		double[] filteredEcg_1 = filterECG(order, a, b, nDataPoints, x, y);

		return filteredEcg_1;
	}

	private double[] filterECG(int order, double[] a, double[] b,
			int nDataPoints, double[] x, double[] y) {
		y[0] = b[0] * x[0];
		for (int i = 0; i < order; i++) {
			y[i] = 0.0;
			for (int j = 0; j < i + 1; j++)
				y[i] = y[i] + b[j] * x[i - j];
			for (int j = 0; j < i; j++)
				y[i] = y[i] - a[j + 1] * y[i - j - 1];
		}
		for (int i = order; i < nDataPoints; i++) {
			y[i] = 0.0;
			for (int j = 0; j < order + 1; j++)
				y[i] = y[i] + b[j] * x[i - j];
			for (int j = 0; j < order; j++)
				y[i] = y[i] - a[j + 1] * y[i - j - 1];
		}
		return y;
	}

}
