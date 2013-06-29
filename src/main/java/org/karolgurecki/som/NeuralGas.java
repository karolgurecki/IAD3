package org.karolgurecki.som;

import org.karolgurecki.perceptron.funkcje.IdentityFunction;
import org.karolgurecki.rbf.Neuron;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: GodDamnItNappa!
 * Date: 29.06.13
 * Time: 21:11
 * To change this template use File | Settings | File Templates.
 */
public class NeuralGas extends SOM{
    private double lambdaMax;
    private double lambdaMin = 0.01;
    private double learnFactorMax = 0.4;
    private double learnFactorMin = 0.003;

    public List<Neuron> getNeurons() {
        return neurons;
    }

    public NeuralGas(int neuronCounter) {
        this.neuronCounter = neuronCounter;
        lambdaMax = neuronCounter / 2;
        lambda = lambdaMax;
        learnFactor = learnFactorMax;
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
    public void teach(int eras) throws IOException {
        // neurons and pattern initialization
        initNeurons(learnPattern.get(0).getWeights().length);
        // neural gas algorithm
        for(int i=0; i<eras; i++) {
            lambda = lambdaMax * Math.pow(lambdaMin/lambdaMax,(double)i/eras);
            for (Neuron aLearnPattern : learnPattern) {
                //sorting neurons
                Collections.sort(neurons, new NeuronComparator(aLearnPattern));
                //updating neurons weights and learn factor
                for (int k = 0; k < neurons.size(); k++) {
                    learnFactor = learnFactorMax * Math.pow(learnFactorMin / learnFactorMax, i / eras);
                    double[] weights = neurons.get(k).getWeights();
                    for (int l = 0; l < weights.length; l++) {
                        weights[l] = weights[l] + (learnFactor * Math.exp((double) -k / lambda) * (aLearnPattern.getWeights()[l] - neurons.get(k).getWeights()[l]));
                    }
                    neurons.get(k).setWeights(weights);
                }
            }
        }
    }
}