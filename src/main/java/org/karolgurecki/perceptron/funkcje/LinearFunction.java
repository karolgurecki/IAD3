package org.karolgurecki.perceptron.funkcje;

public class LinearFunction implements Function {

    private double a, b;

    public LinearFunction(double a, double b) {
        super();
        this.a = a;
        this.b = b;
    }

    @Override
    public double calculate(double... x) {
        return a * x[0] + b;
    }

    @Override
    public double derivative(double... x) {
        return a;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

}
