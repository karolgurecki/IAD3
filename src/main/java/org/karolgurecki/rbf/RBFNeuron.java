package org.karolgurecki.rbf;

/**
 * Created with IntelliJ IDEA.
 * User: GodDamnItNappa!
 * Date: 30.06.13
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
public class RBFNeuron extends Point {
    private double dist;
    private double sigma;
    public RBFNeuron(double... args) {
        super(args);
        sigma = 1;
    }

    /**
     * Zwraca wyjsciowa wartosc RBFNeuronu.
     *
     * @param x
     *            wektor wejsciowy
     * @return odpowiedz RBFNeuronu
     */
    public double getOut(Point x) {
        double dist = x.distance(this);
        return Math.exp(-dist * dist / sigma);
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    public double getSigma() {
        return sigma;
    }

    /**
     * Ustawia wartosc sigmy. W przypadku gdy zazadanoustawic nowa sigme na 0
     * program automatycznie ustawia sigme na 1, poniewaz sigma musi byc rozna
     * od 0.
     *
     * @param sigma
     *            nowa wartosc sigmy
     */
    public void setSigma(double sigma) {
        if (0. != sigma)
            this.sigma = sigma;
        else
            this.sigma = 1;

    }

}