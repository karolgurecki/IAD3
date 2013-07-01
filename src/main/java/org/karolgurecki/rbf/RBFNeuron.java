package org.karolgurecki.rbf;

/**
 * Created with IntelliJ IDEA.
 * User: GodDamnItNappa!
 * Date: 30.06.13
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
public class RBFNeuron extends Point {
    private double sigma;
    public RBFNeuron(double... args) {
        super(args);
        sigma = 1;
    }

    public double getOut(Point x) {
        double dist = x.distance(this);
        return Math.exp(-dist * dist / sigma);
    }



    public void setSigma(double sigma) {
        if (0. != sigma)
            this.sigma = sigma;
        else
            this.sigma = 1;

    }

}