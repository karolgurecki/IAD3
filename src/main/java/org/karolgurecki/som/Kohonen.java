package org.karolgurecki.som;

import org.karolgurecki.perceptron.funkcje.IdentityFunction;
import org.karolgurecki.rbf.Neuron;

import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: GodDamnItNappa!
 * Date: 29.06.13
 * Time: 18:27
 * To change this template use File | Settings | File Templates.
 */
public class Kohonen extends SOM{

   ;

    public Kohonen(int neuronCounter) {
        this.neuronCounter = neuronCounter;
        lambda = 0.05;
        learnFactor = 0.4
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
        double distance = 0;
        for(int i=0; i<eras; i++) {
            for(int j=0; j< learnPattern.size(); j++) {
                Collections.sort(neurons, new NeuronComparator(learnPattern.get(j)));
                //updating winner
                distance = NeuronComparator.countDistance(neurons.get(0), learnPattern.get(j));
                double[] weights = neurons.get(0).getWeights();
                for(int l=0; l<weights.length; l++) {
                    weights[l] = weights[l] + (learnFactor * Math.exp(-(distance*distance)/(2.0*lambda*lambda)) * (learnPattern.get(j).getWeights()[l] - neurons.get(0).getWeights()[l]));
                }
                neurons.get(0).setWeights(weights);
                //updating neighbourhood
                for(int k=0; k<neurons.size(); k++) {
                    //counting distance between winner and another neurons
                    distance = NeuronComparator.countDistance(neurons.get(k),neurons.get(0));
                    for(int l=0; l<weights.length; l++) {
                        weights[l] = weights[l] + (learnFactor * Math.exp(-(distance*distance)/(2.0*lambda*lambda)) * (learnPattern.get(j).getWeights()[l] - neurons.get(k).getWeights()[l]));
                    }
                }
            }
        }
    }
}