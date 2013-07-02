package org.karolgurecki.rbf;

import org.karolgurecki.perceptron.funkcje.IdentityFunction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: GodDamnItNappa!
 * Date: 02.07.13
 * Time: 20:37
 * To change this template use File | Settings | File Templates.
 */
public final class Reader {
    private Reader(){

    }
    public static void readApproxData(boolean col, String filePath,
                                       List<Neuron> tab) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            while (line != null) {
                String[] xy = line.split(" ");
                if (!col) {
                    tab.add(new Neuron(new IdentityFunction(), Double
                            .parseDouble(xy[0])));
                } else {
                    tab.add(new Neuron(new IdentityFunction(), Double
                            .parseDouble(xy[1])));
                }

                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readClassifyData(String filePath, List<Neuron> in, List<Neuron> out, int[] inputs) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            double[] weights;
            while (line != null) {
                weights = new double[inputs.length];
                String[] entry = line.split(" ");
                for (int i = 0; i < inputs.length; i++) {
                    weights[i] = Double.parseDouble(entry[inputs[i]]);
                }
                in.add(new Neuron(new IdentityFunction(), weights.clone()));
                switch (Integer.parseInt(entry[entry.length - 1])) {
                    case 1:
                        out.add(new Neuron(new IdentityFunction(), 1.0, .0, .0));
                        break;
                    case 2:
                        out.add(new Neuron(new IdentityFunction(), .0, 1.0, .0));
                        break;
                    case 3:
                        out.add(new Neuron(new IdentityFunction(), .0, .0, 1.0));
                        break;
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
