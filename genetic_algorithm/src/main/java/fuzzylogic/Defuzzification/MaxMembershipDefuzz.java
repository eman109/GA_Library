package fuzzylogic.Defuzzification;

import java.util.List;

public class MaxMembershipDefuzz {

    public double defuzzify(List<Double> zValues, List<Double> muValues) {
        if (zValues == null || muValues == null || zValues.size() == 0 || muValues.size() == 0)
            return 0.0;

        double maxMu = muValues.get(0);
        double result = zValues.get(0);

        for (int i = 1; i < zValues.size(); i++) {
            if (muValues.get(i) > maxMu) {
                maxMu = muValues.get(i);
                result = zValues.get(i);
            }
        }

        return result;
    }
}
