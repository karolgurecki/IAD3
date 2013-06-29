package org.karolgurecki.som;

import org.karolgurecki.rbf.Neuron;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: GodDamnItNappa!
 * Date: 29.06.13
 * Time: 20:58
 * To change this template use File | Settings | File Templates.
 */
public class NeuronComparator implements Comparator<Neuron> {

    Neuron patternNeuron = null;

    NeuronComparator(Neuron patternNeuron) {
        this.patternNeuron = patternNeuron;
    }

    public static double countDistance(Neuron n1, Neuron n2) {
        if(n1.getWeights().length!=n2.getWeights().length) {
            return -1;
        } else {
            double resual = 0;
            for(int i=0; i<weights1.length; i++) {
                resual += Math.pow(n1.getWeights()[i]-n2.getWeights()[i],2);
            }
            return Math.sqrt(resual);
        }
    }

    @Override
    public int compare(Neuron n1, Neuron n2) {
        if(countDistance(patternNeuron, n1) > countDistance(patternNeuron, n2)) return 1;
        else if(countDistance(patternNeuron, n1) == countDistance(patternNeuron, n2)) return 0;
        else return -1;
    }

}
