package filter;

// Preprocessing.java

public class Preprocessing {
    public int[] process(int[] sample)
    {
        Butterworth bw = new Butterworth(2, 0.25f, true);
        Filter filter = new Filter(bw.computeB(), bw.computeA());
        int[] output = filter.filter(sample);
        return output;
    }
}
