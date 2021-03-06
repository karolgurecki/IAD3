package org.karolgurecki.rbf;

import org.apache.log4j.Logger;
import org.karolgurecki.perceptron.funkcje.Function;

import java.util.Arrays;
import java.util.Random;


public class Neuron {
    private static final Logger LOGGER = Logger.getLogger(Neuron.class);

    private static double ALPHA = 0.1;
    private static double MOMENTUM = 0.9;

    private double[] weights;
    private double[] signals;
    private Function activateFunction;
    private double delta = 0.0;
    private double out;
    private boolean bias;

    public Neuron(Function function, double... weights) {
        this.weights = weights;
        this.activateFunction = function;
        //LOGGER.info(Arrays.toString(weights));
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

//    public Neuron(int inputSize, Function function) {
//
//        weights = new double[inputSize];
//        signals = new double[inputSize];
//        activateFunction = function;
//
//    }

    public double getResult() {
        return out;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    public double getDelta() {
        return delta;
    }

    public Neuron(int inputSize) {

        weights = new double[inputSize];
        signals = new double[inputSize];
    }

    public static void setAlpha(double alpha) {
        Neuron.ALPHA = alpha;
    }

    public static void setMomentum(double momentum) {
        Neuron.MOMENTUM = momentum;
    }

    public void setBiasEnabled(boolean bool) {

        if (bool) {
            weights = signals = new double[weights.length + 1];
            signals[0] = 1d;
            bias = bool;
        }
    }

    public void setActivateFunction(Function function) {
        this.activateFunction = function;
    }

    public Function getActivateFunction() {
        return activateFunction;
    }

    public double[] getWeights() {
        return weights;
    }


    @Override
    public String toString() {
        return "Neuron{" +
                "weights=" + Arrays.toString(weights) +
                '}';
    }

    public void initWeights(double lowerBound, double upperBound) {
        Random random = new Random();

        for (int i = 0; i < weights.length; i++) {
            weights[i] = lowerBound + (upperBound - lowerBound)
                    * random.nextDouble();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Weight: " + weights[i]);
        }
    }

    public void setSignals(double[] signals) {
        if(bias) {
            this.signals = new double[signals.length + 1];
            this.signals[0] = 1.0;
            System.arraycopy(signals, 0, this.signals, 1, signals.length);
        } else {
            this.signals = signals.clone();
        }

        for (int i = 0; i < signals.length; i++) {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Signal " + i + ": " + signals[i]);
        }
    }

    public double output() {

        double result = 0.0;

        for (int i = 0; i < weights.length; i++) {
            result += weights[i] * signals[i];
        }

        out = result;

        return activateFunction.calculate(result);

    }

    public void teach() {

        double [] weightsCopy = weights.clone();
        for (int i = 0; i < weights.length; i++) {
            weights[i] -= delta * 2 * ALPHA * signals[i];
            weights[i] -= MOMENTUM * (weights[i] - weightsCopy[i]);
        }
    }
}
