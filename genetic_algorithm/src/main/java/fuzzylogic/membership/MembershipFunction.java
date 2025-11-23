package fuzzylogic.membership;

public interface MembershipFunction {

    double evaluate(double x); // x in the crisp input
    // returns a double returning the membership degree belong to range from 0 to 1
}
