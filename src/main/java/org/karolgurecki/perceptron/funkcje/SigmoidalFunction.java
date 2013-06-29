package org.karolgurecki.perceptron.funkcje;

public class SigmoidalFunction implements Function {
    protected static final double DEFAULT_BETA = 1.0;
    protected Double beta;

    public SigmoidalFunction() {
        this(DEFAULT_BETA);
    }

    public SigmoidalFunction(Double beta) {
        super();
        this.beta = beta;
    }

    public Double getBeta() {
        return beta;
    }

    public void setBeta(Double beta) {
        this.beta = beta;
    }

    @Override
    public double calculate(double... x) {
        return (DEFAULT_BETA / (DEFAULT_BETA + Math.pow(Math.E, -beta * x[0])));
    }

    @Override
    public double derivative(double... x) {
        final Double calculatedDouble = this.calculate(x[0]);
        return calculatedDouble * (1 - calculatedDouble) * this.beta;
    }

}
