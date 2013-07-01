package org.karolgurecki.perceptron;

import org.apache.log4j.Logger;
import org.karolgurecki.perceptron.funkcje.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: GodDamnItNappa!
 * Date: 30.06.13
 * Time: 17:04
 * To change this template use File | Settings | File Templates.
 */
public class NeuralNetwork {
    private static final Logger LOGGER = Logger.getLogger(NeuralNetwork.class.getSimpleName());
    private List<NeuralLayer> layers = new ArrayList<>();
    private double[] expectedOutput;

    public NeuralNetwork() {

    }

    public void setExpectedOutput(double[] expOut) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Setting expected output");

        this.expectedOutput = expOut.clone();
    }


    public void addLayer(NeuralLayer layer) {
        layers.add(layer);
    }


    public void initWeights(double lowerBound, double upperBound) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Initializing network weights");

        int i = 1;
        for (NeuralLayer layer : layers) {
            if (LOGGER.isDebugEnabled())
            LOGGER.debug("Layer " + (i++));
            layer.initWeights(lowerBound, upperBound);
        }
    }

    public void initSignals(double[] signals) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Initializing network signals");

        layers.get(0).initSignals(signals);
    }


    public double[] getOutput() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Counting network output.");

        for (int i = 0; i < layers.size() - 1; i++) {
            double[] layerOut = layers.get(i).output();

            for (int j = 0; j < layers.get(i + 1).getNeurons().size(); j++) {
                layers.get(i + 1).getNeurons().get(j).setSignals(layerOut);
            }
        }

        return layers.get(layers.size() - 1).output();
    }

    public void teach() {

        for (int i = 0; i < layers.get(layers.size() - 1).getNeurons().size(); i++) {
            double layerOut = layers.get(layers.size() - 1).getNeurons().get(i)
                    .getResult();
            Function activateFunction = layers.get(layers.size() - 1)
                    .getNeurons().get(i).getActivateFunction();
            layers.get(layers.size() - 1)
                    .getNeurons()
                    .get(i)
                    .setDelta(
                            (activateFunction.calculate(layerOut) - expectedOutput[i])
                                    * activateFunction.derivative(layerOut));
        }

        for (int i = layers.size() - 2; i >= 0; i--) {
            layers.get(i).teach();
        }

        for(NeuralLayer layer : layers) {
            layer.updateWeights();
        }
    }
}
