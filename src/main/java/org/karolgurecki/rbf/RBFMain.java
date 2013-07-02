package org.karolgurecki.rbf;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.karolgurecki.perceptron.NeuralLayer;
import org.karolgurecki.perceptron.NeuralNetwork;
import org.karolgurecki.perceptron.funkcje.IdentityFunction;
import org.karolgurecki.perceptron.funkcje.LinearFunction;
import org.karolgurecki.som.SOM;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: GodDamnItNappa!
 * Date: 30.06.13
 * Time: 17:06
 * To change this template use File | Settings | File Templates.
 */
public class RBFMain {
    private final static Logger LOGGER = Logger.getLogger(RBFMain.class);

    private static double getNearestDist(int i, List<RBFNeuron> neurons) {
        RBFNeuron tmp = neurons.get(i);
        double ret = Double.MAX_VALUE;
        double dist;
        for (int j = 0; j < neurons.size(); j++) {
            if (j == i)
                continue;
            dist = tmp.distance(neurons.get(j));
            if (dist < ret)
                ret = dist;
        }
        return ret;
    }

    private static double countSigma(List<RBFNeuron> neurons) {
        double ret = .0;
        for (int i = 0; i < neurons.size(); i++)
            ret += getNearestDist(i, neurons);

        return ret / neurons.size();
    }

    private static List<RBFNeuron> toRBFNeurons(List<Neuron> neurons) {
        List<RBFNeuron> result = new ArrayList<>();
        for (Neuron n : neurons) {
            result.add(new RBFNeuron(n.getWeights()));
        }
        return result;
    }

    public static double[] getResults(Point x, List<RBFNeuron> neurons) {
        int neuronsCount = neurons.size();
        double[] ret = new double[neuronsCount];
        int i = 0;
        for (RBFNeuron n : neurons)
            ret[i++] = n.getOut(x);
        return ret;
    }

    public static void approxSplit(SOM som, double networkAlpha,
                                   double networkMomentum, String approxTrainFile, String approxTestFile, String chartTitle, int somEras, int perceptronEras)
            throws IOException {

        ArrayList<Neuron> in = new ArrayList<>();
        ArrayList<Neuron> out = new ArrayList<>();

        Reader.readApproxData(false, approxTrainFile, in);
        Reader.readApproxData(true, approxTrainFile, out);

        som.setLearnPattern(in);
        som.teach(somEras);

        // Utworzenie sieci
        NeuralLayer layer = new NeuralLayer(1, som.getNeurons().size());
        layer.setActivateFunction(new IdentityFunction());
        layer.setBiasEnabled(true);

        Neuron.setAlpha(networkAlpha);
        Neuron.setMomentum(networkMomentum);

        NeuralNetwork network = new NeuralNetwork();
        network.initWeights(-1d, 1d);
        network.addLayer(layer);

        // Warstwa z sigma

        List<RBFNeuron> neurons = toRBFNeurons(som.getNeurons());
        double sigma = countSigma(neurons);
        for (RBFNeuron n : neurons) {
            n.setSigma(sigma);
        }

        // nauka sieci


        int pointCount = in.size();
        Random rnd = new Random();
        Point random;
        boolean[] tab = new boolean[pointCount];
        int chose;

        for (int i = 0; i < perceptronEras; i++) {
            Arrays.fill(tab, false);
            for (int k = 0; k < pointCount; k++) {
                do {
                    chose = rnd.nextInt(pointCount);
                } while (tab[chose]);
                tab[chose] = true;
                random = new Point(in.get(chose).getWeights());

                network.setExpectedOutput(out.get(chose).getWeights());
                network.initSignals(getResults(random, neurons));

                network.getOutput();
                network.teach();
            }
        }
        double error=0;
            for (int h = 0; h < network.getOutput().length; h++)
                error += Math.pow(network.getOutput()[h] - out.get(out.size()-1).getWeights()[h], 2);

        Reader.readApproxData(false, approxTestFile, in);
        Reader.readApproxData(true, approxTestFile, out);
        error/=network.getOutput().length;
        // test

        XYSeries series = new XYSeries("Line");
        List<double[]> points = new ArrayList<>();
        for (int i = 0; i < in.size(); i++) {
            network.initSignals(getResults(new Point(in.get(i).getWeights()),
                    neurons));
            network.setExpectedOutput(new double[]{out.get(i).getWeights()[0]});

            series.add(in.get(i).getWeights()[0], network.getOutput()[0]);
            points.add(new double[]{in.get(i).getWeights()[0], network.getOutput()[0]});
        }

        XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(series);
        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "x",
                "y", data, PlotOrientation.VERTICAL, true, true, false);

        ChartFrame frame1 = new ChartFrame("XYArea Chart", chart);
        frame1.setVisible(true);
        frame1.setSize(500, 400);
        StringBuilder builder = new StringBuilder();
        // double[][] points = series.toArray();
        for (int i = 0; i < points.size(); i++) {
            for (int j = 0; j < points.get(i).length; j++)
                builder.append(points.get(i)[j] + " ");
            builder.append("\n");
        }
        new File("results").mkdir();
        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter("wyniki" + File.separator + chartTitle + " " +
                    som.getNeuronCounter() + " neurons split " + networkAlpha + " " + networkMomentum + " error "+error+".txt"
            ));
            outputWriter.write(builder.toString());
            outputWriter.close();
        } catch (IOException e) {
        }
        LOGGER.info(chartTitle + " " +
                som.getNeuronCounter() + " neurons split " + networkAlpha + " " + networkMomentum +" error "+error+ " done");
    }

    public static void approxTogether(SOM som, double networkAlpha,
                                      double networkMomentum, String approxTrainFile, String approxTestFile, String chartTitle, int eras)
            throws IOException {

        List<Neuron> in = new ArrayList<>();
        List<Neuron> out = new ArrayList<>();

        Reader.readApproxData(false, approxTestFile, in);
        Reader.readApproxData(true, approxTestFile, out);

        som.setLearnPattern(in);
        som.teach(1);

        // Utworzenie sieci
        NeuralLayer layer = new NeuralLayer(1, som.getNeurons().size());
        layer.setActivateFunction(new LinearFunction(1d, 0d));
        layer.setBiasEnabled(true);

        Neuron.setAlpha(networkAlpha);
        Neuron.setMomentum(networkMomentum);

        NeuralNetwork network = new NeuralNetwork();
        network.initWeights(-1d, 1d);
        network.addLayer(layer);

        // Warstwa z sigma

        List<RBFNeuron> neurons = toRBFNeurons(som.getNeurons());
        double sigma = countSigma(neurons);
        for (RBFNeuron n : neurons) {
            n.setSigma(sigma);
        }

        // nauka sieci

        int pointCount = in.size();
        Random rnd = new Random();
        Point random;
        boolean[] tab = new boolean[pointCount];
        int chose;

        for (int i = 0; i < eras; i++) {
            som.teach(1);
            Arrays.fill(tab, false);
            for (int k = 0; k < pointCount; k++) {
                do {
                    chose = rnd.nextInt(pointCount);
                } while (tab[chose]);
                tab[chose] = true;
                random = new Point(in.get(chose).getWeights());

                network.setExpectedOutput(out.get(chose).getWeights());
                network.initSignals(getResults(random, neurons));

                network.getOutput();
                network.teach();
            }
        }

        double error=0;
        for (int h = 0; h < network.getOutput().length; h++)
            error += Math.pow(network.getOutput()[h] - out.get(out.size()-1).getWeights()[h], 2);
        error/=network.getOutput().length;
        Reader.readApproxData(false, approxTestFile, in);
        Reader.readApproxData(true, approxTestFile, out);

        // test

        XYSeries series = new XYSeries("Line");
        List<double[]> points = new ArrayList<>();
        for (int i = 0; i < in.size(); i++) {
            network.initSignals(getResults(new Point(in.get(i).getWeights()),
                    neurons));
            network.setExpectedOutput(new double[]{out.get(i).getWeights()[0]});

            series.add(in.get(i).getWeights()[0], network.getOutput()[0]);
            points.add(new double[]{in.get(i).getWeights()[0], network.getOutput()[0]});
        }

        XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(series);
        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "x",
                "y", data, PlotOrientation.VERTICAL, true, true, false);

        ChartFrame frame1 = new ChartFrame("XYArea Chart", chart);
        frame1.setVisible(true);
        frame1.setSize(500, 400);
        StringBuilder builder = new StringBuilder();
        // double[][] points = series.toArray();
        for (int i = 0; i < points.size(); i++) {
            for (int j = 0; j < points.get(i).length; j++)
                builder.append(points.get(i)[j] + " ");
            builder.append("\n");
        }
        new File("results").mkdir();
        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter("results" + File.separator + chartTitle + " " +
                    som.getNeuronCounter() + " neurons together " + networkAlpha + " " + networkMomentum + " error "+error+".txt"
            ));
            outputWriter.write(builder.toString());
            outputWriter.close();
        } catch (IOException e) {
        }
        LOGGER.info(chartTitle + " " +
                som.getNeuronCounter() + " neurons together " + networkAlpha + " " + networkMomentum +" error "+error+ " done");
    }

    public static void classifySplit(SOM som, double networkAlpha,
                                     double networkMomentum, String classificationTrainFile, String classificationTestFile, int[] inputs, int somEras, int perceptronEras)
            throws IOException {

        List<Neuron> in = new ArrayList<Neuron>();
        List<Neuron> out = new ArrayList<Neuron>();

        Reader.readClassifyData(classificationTrainFile, in, out, inputs);
        som.setLearnPattern(in);
        som.teach(somEras);

        // Utworzenie sieci
        NeuralLayer layer = new NeuralLayer(3, som.getNeurons().size());
        layer.setActivateFunction(new IdentityFunction());
        layer.setBiasEnabled(true);

        Neuron.setAlpha(networkAlpha);
        Neuron.setMomentum(networkMomentum);

        NeuralNetwork network = new NeuralNetwork();
        network.initWeights(-1d, 1d);
        network.addLayer(layer);

        // Warstwa z sigma

        List<RBFNeuron> neurons = toRBFNeurons(som.getNeurons());
        double sigma = countSigma(neurons);
        for (RBFNeuron n : neurons) {
            n.setSigma(sigma);
        }

        // nauka sieci


        int pointCount = in.size();
        Random rnd = new Random();
        Point random;
        boolean[] tab = new boolean[pointCount];
        int chose;

        for (int i = 0; i < perceptronEras; i++) {
            Arrays.fill(tab, false);
            for (int k = 0; k < pointCount; k++) {
                do {
                    chose = rnd.nextInt(pointCount);
                } while (tab[chose]);
                tab[chose] = true;
                random = new Point(in.get(chose).getWeights());

                network.setExpectedOutput(out.get(chose).getWeights());
                network.initSignals(getResults(random, neurons));

                network.getOutput();
                network.teach();
            }
        }
        double error=0;
        for (int h = 0; h < network.getOutput().length; h++)
            error += Math.pow(network.getOutput()[h] - out.get(out.size()-1).getWeights()[h], 2);
        error/=network.getOutput().length;
        Reader.readClassifyData(classificationTrainFile, in, out, inputs);

        // test

        double[] testOut;
        int properlyInCount = 0;
        for (int i = 0; i < in.size(); i++) {
            network.initSignals(getResults(new Point(in.get(i).getWeights()),
                    neurons));
            network.setExpectedOutput(out.get(i).getWeights());
            testOut = network.getOutput();
            int maxIndex = 0;
            for (int j = 0; j < testOut.length; j++) {
                if (testOut[j] > testOut[maxIndex]) maxIndex = j;
            }
            if (out.get(i).getWeights()[maxIndex] == 1.0) properlyInCount++;
        }

        double result = ((double) properlyInCount * 100.0 / (double) out.size()) ;
        LOGGER.info(String.format("Percentage properly classify inputs for split teaching %d neurons on %s = %f. Error = %f", som.getNeuronCounter(),
                Arrays.toString(inputs), result,error));


    }

    public static void classifyTogether(SOM som, double networkAlpha,
                                        double networkMomentum, String classificationTrainFile, String classificationTestFile, int[] inputs, int eras)
            throws IOException {

        List<Neuron> in = new ArrayList<Neuron>();
        List<Neuron> out = new ArrayList<Neuron>();

        Reader.readClassifyData(classificationTrainFile, in, out, inputs);

        som.setLearnPattern(in);
        som.teach(1);

        // Utworzenie sieci
        NeuralLayer layer = new NeuralLayer(3, som.getNeurons().size());
        layer.setActivateFunction(new LinearFunction(1d, 0d));
        layer.setBiasEnabled(true);

        Neuron.setAlpha(networkAlpha);
        Neuron.setMomentum(networkMomentum);

        NeuralNetwork network = new NeuralNetwork();
        network.initWeights(-1d, 1d);
        network.addLayer(layer);

        // Warstwa z sigma

        List<RBFNeuron> neurons = toRBFNeurons(som.getNeurons());
        double sigma = countSigma(neurons);
        for (RBFNeuron n : neurons) {
            n.setSigma(sigma);
        }

        // nauka sieci
        int pointCount = in.size();
        Random rnd = new Random();
        Point random;
        boolean[] tab = new boolean[pointCount];
        int chose;

        for (int i = 0; i < eras; i++) {
            som.teach(1);
            Arrays.fill(tab, false);
            for (int k = 0; k < pointCount; k++) {
                do {
                    chose = rnd.nextInt(pointCount);
                } while (tab[chose]);
                tab[chose] = true;
                random = new Point(in.get(chose).getWeights());

                network.setExpectedOutput(out.get(chose).getWeights());
                network.initSignals(getResults(random, neurons));

                network.getOutput();
                network.teach();
            }
        }
        double error=0;
        for (int h = 0; h < network.getOutput().length; h++)
            error += Math.pow(network.getOutput()[h] - out.get(out.size()-1).getWeights()[h], 2);
        error/=network.getOutput().length;
        Reader.readClassifyData(classificationTrainFile, in, out, inputs);

        // test

        double[] testOut;
        int properlyInCount = 0;
        for (int i = 0; i < in.size(); i++) {
            network.initSignals(getResults(new Point(in.get(i).getWeights()),
                    neurons));
            network.setExpectedOutput(out.get(i).getWeights());
            testOut = network.getOutput();
            int maxIndex = 0;
            for (int j = 0; j < testOut.length; j++) {
                if (testOut[j] > testOut[maxIndex]) maxIndex = j;
            }
            if (out.get(i).getWeights()[maxIndex] == 1.0) properlyInCount++;
        }
        double result = ((double) properlyInCount / (double) in.size()) * 100.0;
        LOGGER.info(String.format("Percentage properly classify inputs for teaching together %d neurons on %s = %f. Error = %f", som.getNeuronCounter(),
                Arrays.toString(inputs), result,error));

    }

//    public static void main(String[] args) throws IOException {
//        String[][] str = {{"src/main/resources/aprox-train-small.dat", " - file small"}, {"src/main/resources/aprox-train-big.dat", " - file big"}};
//        int[] numberOfNeurons = {5,10, 15};
//        int[][] numberOfIn = {{0, 1, 2, 3}/*, {0, 1, 2}, {1, 2, 3},
//                {0, 2, 3}, {0, 1, 3}, {0, 1}, {0, 2}, {0, 3},
//                {1, 2}, {1, 3}, {2, 3}, {0}, {1}, {2}, {3}*/};
////        for (int i = 0; i < numberOfNeurons.length; i++) {
////            for (int j = 0; j < str.length; j++) {
////                // Kohonen - file small - together
////                approxTogether(new Kohonen(numberOfNeurons[i]), 0.001, 0.1,
////                        str[j][0],
////                        "src/main/resources/aprox-test.dat",
////                        "Kohonen" + str[j][1]);
////
////
////                // NeuralGas - file small - together
////                approxTogether(new NeuralGas(numberOfNeurons[i]), 0.01, 0.1,
////                        str[j][0],
////                        "src/main/resources/aprox-test.dat",
////                        "NeuralGas" + str[j][1]);
////
////                // Kohonen - file small - split
////                approxSplit(new Kohonen(numberOfNeurons[i]), 0.001, 0.1,
////                        str[j][0],
////                        "src/main/resources/aprox-test.dat",
////                        "Kohonen" + str[j][1]);
////
////                // Neural gas - file small - split
////                approxSplit(new NeuralGas(numberOfNeurons[i]), 0.001, 0.1,
////                        str[j][0],
////                        "src/main/resources/aprox-test.dat",
////                        "NeuralGas" + str[j][1]);
////            }
////        }
//        numberOfNeurons = new int[]{ 10};
//        for (int i = 0; i < numberOfNeurons.length; i++) {
//            for (int k = 0; k < numberOfIn.length; k++) {
////                LOGGER.info("Kohonen");
////                // Kohonen - file small - together
//                classifySplit(new Kohonen(numberOfNeurons[i]), 0.1, 0.4,
//                        "src/main/resources/c-train.dat",
//                        "src/main/resources/c-test.dat",
//                        numberOfIn[k]);
//
////                classifyTogether(new Kohonen(numberOfNeurons[i]), 0.001, 0.1,
////                        "src/main/resources/c-train.dat",
////                        "src/main/resources/c-test.dat",
////                        numberOfIn[k]);
//                LOGGER.info("NeuralGas");
////                classifySplit(new NeuralGas(numberOfNeurons[i]), 0.0001, 0.001,
////                        "src/main/resources/c-train.dat",
////                        "src/main/resources/c-test.dat",
////                        numberOfIn[k]);
//
////                classifyTogether(new NeuralGas(numberOfNeurons[i]), 0.001, 0.1,
////                        "src/main/resources/c-train.dat",
////                        "src/main/resources/c-test.dat",
////                        numberOfIn[k]);
//            }
//        }
///* ============================================================= */
//
//    }
}
