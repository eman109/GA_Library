package fuzzylogic.Defuzzification;

import java.util.List;

public class CentroidDefuzzificationSimple {

    public double defuzzify(List<Double> zValues, List<Double> muValues) {
        double  MZ= 0.0;   // ∑ μ(z_i) * z_i
        double M = 0.0; // ∑ μ(z_i)

        for (int i = 0; i < zValues.size(); i++) {
            double z = zValues.get(i);
            double mu = muValues.get(i);

            MZ += mu * z;
            M += mu;
        }

        if (M == 0.0) {
            return 0.0;
        }
        return MZ / M;
    }
}