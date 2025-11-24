package fuzzylogic.membership;

public class TrapezoidalMF implements MembershipFunction{
    private final double s1, s2, s3, s4;
    //s1 -> start of trapezoid
    //s2 -> left top corner
    //s3 -> right top corner
    //s4 -> end of trapezoid

    public TrapezoidalMF(double s1, double s2, double s3, double s4) {
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
        this.s4 = s4;
    }
    public double getS1() { return s1; }
    public double getS2() { return s2; }
    public double getS3() { return s3; }
    public double getS4() { return s4; }
    @Override
    public double evaluate(double x) {
        double leftslope = (x - s1) / (s2 - s1);
        double rightslope = (s4 - x) / (s4 - s3);
        if (x <= s1 || x >= s4) return 0.0; //outside the trapezoid
        else if (x >= s2 && x <= s3) return 1.0; //plateau (maximum membership we can reach)
        else if (x > s1 && x < s2) return leftslope ; // getting the equation of left side slope
        else return rightslope; //getting the equation of right side slope
    }

    @Override
    public String toString() {
        return "TrapezoidalMF(" + s1 + ", " + s2 + ", " + s3 +  ", " + s4 + ")";
    }
}
