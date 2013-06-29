package org.karolgurecki.som;

import org.karolgurecki.perceptron.funkcje.IdentityFunction;
import org.karolgurecki.rbf.Neuron;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: GodDamnItNappa!
 * Date: 29.06.13
 * Time: 18:27
 * To change this template use File | Settings | File Templates.
 */
public class Kohonen implements SOM{

    private List<Neuron> neurons = new ArrayList<Neuron>();
    private List<Neuron> pattern = new ArrayList<Neuron>();

    private int neuronCounter = 100;
    private double lambda = 0.05;
    private double learnFactor = 0.4;
    private double epochsCounter = 20;

    public Kohonen(int neuronCounter) throws IOException {
        this.neuronCounter = neuronCounter;
    }

    private void initNeurons(int counter, int size) {
        double []weights = new double[size];
        for (int i = 0; i < counter; i++) {
            for(int j=0;j<size;j++) {
                weights[j] = Math.random();
            }
            neurons.add(new Neuron(new IdentityFunction(), weights.clone()));
        }
    }

    public void teach(int epochs) {
        this.epochsCounter = epochs;
        initNeurons(neuronCounter,pattern.get(0).getWeights().length);
        double distance = 0;
        for(int i=0; i<epochsCounter; i++) {
            for(int j=0; j<pattern.size(); j++) {
                Collections.sort(neurons, new NeuronComparator(pattern.get(j)));
                //updating winner
                distance = NeuronComparator.countDistance(neurons.get(0),pattern.get(j));
                double[] weights = neurons.get(0).getWeights();
                for(int l=0; l<weights.length; l++) {
                    weights[l] = weights[l] + (learnFactor * Math.exp(-(distance*distance)/(2.0*lambda*lambda)) * (pattern.get(j).getWeights()[l] - neurons.get(0).getWeights()[l]));
                }
                neurons.get(0).setWeights(weights);
                //updating neighbourhood
                for(int k=0; k<neurons.size(); k++) {
                    //counting distance between winner and another neurons
                    distance = NeuronComparator.countDistance(neurons.get(k),neurons.get(0));
                    for(int l=0; l<weights.length; l++) {
                        weights[l] = weights[l] + (learnFactor * Math.exp(-(distance*distance)/(2.0*lambda*lambda)) * (pattern.get(j).getWeights()[l] - neurons.get(k).getWeights()[l]));
                    }
                }
            }
        }
    }

    public void plot() {
        Plot.plot("Kohonen", neurons, pattern);
    }

    public List<Neuron> getNeurons() {
        return neurons;
    }

    public void setPattern(List<Neuron> pattern) {
        this.pattern = pattern;
    }

}