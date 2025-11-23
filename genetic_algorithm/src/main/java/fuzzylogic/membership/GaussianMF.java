package fuzzylogic.membership;

public class GaussianMF implements MembershipFunction{
    //μ(x)=e−0.5((x−mean)/sigma)2 so we will need the mean and sigma
    private final double sigma;
    private final double mean;

    public GaussianMF(double sigma, double mean){
        this.sigma = sigma;
        this.mean = mean;
    }

    @Override
    public double evaluate(double x) {
        return Math.exp(-0.5 * Math.pow((x - mean) / sigma, 2)); //μ(x)=e−0.5((x−mean)/sigma)2
    }

    @Override
    public String toString() {
        return "Gaussian MF(mean = " + mean + ", sigma = " + sigma + ")";
    }

}
