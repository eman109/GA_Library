package fuzzylogic.operators;


public class Implication {


    public static double[] clipImplication(double firingStrength, double[] outputMF) {
        double[] clipped = new double[outputMF.length];
        for (int i = 0; i < outputMF.length; i++) {
            clipped[i] = Math.min(firingStrength, outputMF[i]);
        }
        return clipped;
    }
}
