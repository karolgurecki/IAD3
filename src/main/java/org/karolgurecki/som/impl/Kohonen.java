package org.karolgurecki.som.impl;

import org.karolgurecki.perceptron.funkcje.IdentityFunction;
import org.karolgurecki.rbf.Neuron;
import org.karolgurecki.som.NeuronComparator;
import org.karolgurecki.som.SOM;

import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: GodDamnItNappa!
 * Date: 29.06.13
 * Time: 18:27
 * To change this template use File | Settings | File Templates.
 */
public class Kohonen extends SOM {

   ;

    public Kohonen(int neuronCounter) {
        this.neuronCounter = neuronCounter;
        lambda = 0.005;
        learnFactor = 0.005;
    }

    protected void initNeurons(int size) {
        double []weights = new double[size];
        for (int i = 0; i < neuronCounter; i++) {
            for(int j=0;j<size;j++) {
                weights[j] = Math.random();
            }
            neurons.add(new Neuron(new IdentityFunction(), weights.clone()));
        }
    }

    public void teach(int eras) {
        initNeurons(learnPattern.get(0).getWeights().length);
        double distance;
        for(int i=0; i<eras; i++) {
            for (Neuron aLearnPattern : learnPattern) {
                Collections.sort(neurons, new NeuronComparator(aLearnPattern));
                //updating winner
                distance = NeuronComparator.countDistance(neurons.get(0), aLearnPattern);
                double[] weights = neurons.get(0).getWeights();
                for (int l = 0; l < weights.length; l++) {
                    weights[l] = weights[l] + (learnFactor * Math.exp(-(distance * distance) / (2.0 * lambda * lambda)) * (aLearnPattern.getWeights()[l] - neurons.get(0).getWeights()[l]));
                }
                neurons.get(0).setWeights(weights);
                //updating neighbourhood
                for (Neuron neuron : neurons) {
                    //counting distance between winner and another neurons
                    distance = NeuronComparator.countDistance(neuron, neurons.get(0));
                    for (int l = 0; l < weights.length; l++) {
                        weights[l] = weights[l] + (learnFactor * Math.exp(-(distance * distance) / (2.0 * lambda * lambda)) * (aLearnPattern.getWeights()[l] - neuron.getWeights()[l]));
                    }
                }
            }
        }
    }
}