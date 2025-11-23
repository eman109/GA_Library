package fuzzylogic.variables;

import fuzzylogic.membership.MembershipFunction;

public class FuzzySet {
    private final String setName; //set name ex:- low , medium , high
    private final MembershipFunction membershipF; //holds the membership function object && Delegates μ(x) calculations to the MF

    public FuzzySet(String setName, MembershipFunction membershipF) {
        this.setName = setName;
        this.membershipF = membershipF;
    }

    //Computes μ(x) by calling the underlying membership function
    public double evaluateMembership(double x) { // x membership value
        return membershipF.evaluate(x);
    }

    //getter methods for name of the set and Membership function used
    public String getSetName() {
        return setName;
    }

    public MembershipFunction getMembershipF() {
        return membershipF;
    }

    @Override
    public String toString() {
        return "FuzzySet [name=" + setName + ", membershipF=" + membershipF + "]";
    }
}
