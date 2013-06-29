package org.karolgurecki.perceptron.funkcje;

public interface Function {
    public double derivative(double... x);

    double calculate(double... x);
}
