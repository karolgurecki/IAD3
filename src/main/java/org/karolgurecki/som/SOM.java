package org.karolgurecki.som;

import org.karolgurecki.rbf.Neuron;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public abstract class SOM {
    protected List<Neuron> neurons = new ArrayList<Neuron>();
    protected List<Neuron> learnPattern = new ArrayList<Neuron>();
    protected int neuronCounter;
    protected double lambda;
    protected double learnFactor;
    protected double lambdaMax;
    protected double lambdaMin = 0.001;
    protected double learnFactorMax = 0.5;
    protected double learnFactorMin = 0.001;
    public abstract void teach(int epochs) throws IOException;
    protected abstract void initNeurons(int size);
    public List<Neuron> getNeurons() {
        return neurons;
    }

    public void setNeurons(List<Neuron> neurons) {
        this.neurons = neurons;
    }


    public void setLearnPattern(List<Neuron> learnPattern) {
        this.learnPattern = learnPattern;
    }

    public int getNeuronCounter() {
        return neuronCounter;
    }

    public void setNeuronCounter(int neuronCounter) {
        this.neuronCounter = neuronCounter;
    }

    public double getLambda() {
        return lambda;
    }

    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    public double getLearnFactor() {
        return learnFactor;
    }

    public void setLearnFactor(double learnFactor) {
        this.learnFactor = learnFactor;
    }

    public double getLambdaMax() {
        return lambdaMax;
    }

    public void setLambdaMax(double lambdaMax) {
        this.lambdaMax = lambdaMax;
    }

    public double getLambdaMin() {
        return lambdaMin;
    }

    public void setLambdaMin(double lambdaMin) {
        this.lambdaMin = lambdaMin;
    }

    public double getLearnFactorMax() {
        return learnFactorMax;
    }

    public void setLearnFactorMax(double learnFactorMax) {
        this.learnFactorMax = learnFactorMax;
    }

    public double getLearnFactorMin() {
        return learnFactorMin;
    }

    public void setLearnFactorMin(double learnFactorMin) {
        this.learnFactorMin = learnFactorMin;
    }
}

