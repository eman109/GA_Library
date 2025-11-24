package fuzzylogic.membership;

public class TriangularMF implements MembershipFunction{
    private final double s1, s2, s3;
    //s1 -> start of triangle
    //s2 -> peak
    //s3 -> end of triangle

    public TriangularMF(double s1, double s2, double s3) {
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
    }
    // TriangularMF
    public double getS1() { return s1; }
    public double getS2() { return s2; }
    public double getS3() { return s3; }

    @Override
    public double evaluate(double x) {
        double leftslope = (x - s1) / (s2 - s1);
        double rightslope = (s3 - x) / (s3 - s2);
        if (x <= s1 || x >= s3) return 0.0; //outside the triangle
        else if (x == s2) return 1.0; //peak
        else if (x > s1 && x < s2) return leftslope ; // getting the equation of left side slope
        else return rightslope; //getting the equation of right side slope
    }

    @Override
    public String toString() {
        return "Triangular MF(" + s1 + ", " + s2 + ", " + s3 + ")";
    }
}
