package org.karolgurecki.rbf;

import org.apache.log4j.Logger;
import org.karolgurecki.som.SOM;
import org.karolgurecki.som.impl.Kohonen;
import org.karolgurecki.som.impl.NeuralGas;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: GodDamnItNappa!
 * Date: 02.07.13
 * Time: 20:42
 * To change this template use File | Settings | File Templates.
 */
public class Loader {
    private final static Logger LOGGER = Logger.getLogger(Loader.class);

    private static void load(String fileName) {
        Properties properties = new Properties();
        SOM som = null;
        try {
            properties.load(new FileReader(new File(fileName)));
            int somNeuronCount = Integer.parseInt(properties.getProperty("SOM.neurons").trim());
            String somType = properties.getProperty("SOM.type").trim().toLowerCase();
            switch (somType) {
                case "gas":
                case "neuralgas":
                    som = new NeuralGas(somNeuronCount);
                    break;
                case "kohonen":
                    som = new Kohonen(somNeuronCount);
                    break;
                default:
                    LOGGER.fatal(String.format("SOM type %s is not supported", somType));
                    System.exit(-1);
                    break;
            }
            String learningType=properties.getProperty("LEARNING.Type").trim();
            som.setLambda(Double.parseDouble(properties.getProperty("SOM.lambda").trim()));
            som.setLambdaMax(Double.parseDouble(properties.getProperty("SOM.lambda").trim()));
            som.setLambdaMin(Double.parseDouble(properties.getProperty("SOM.lambdaMin").trim()));
            som.setLearnFactor(Double.parseDouble(properties.getProperty("SOM.learnFactor").trim()));
            som.setLearnFactorMax(Double.parseDouble(properties.getProperty("SOM.learnFactor").trim()));
            som.setLearnFactorMin(Double.parseDouble(properties.getProperty("SOM.learnFactorMin").trim()));
            double perceptronAlpha=Double.parseDouble(properties.getProperty("PERCEPTRON.learnFactor").trim());
            double perceptronMomentum=Double.parseDouble(properties.getProperty("PERCEPTRON.momentum").trim());
            String[] classificationInputsString=properties.getProperty("CLASSIFICATION.inputs").trim().split(",");
            int[] classificationInputs=new int[classificationInputsString.length];
            for(int i=0;i<classificationInputs.length;i++){
                classificationInputs[i]=Integer.parseInt(classificationInputsString[i]);
            }
            String trainFile=properties.getProperty("FILE.train").trim();
            String testFile=properties.getProperty("FILE.test").trim();
            String ext=properties.getProperty("EXERCISE.type").trim();
            int somEras=Integer.parseInt(properties.getProperty("SOM.eras").trim());
            int perceptronEras= Integer.parseInt(properties.getProperty("PERCEPTRON.eras").trim());
            switch(ext.toLowerCase()){
                case "approximation":
                case "approx":
                    if(learningType.toLowerCase().equals("split")){
                        RBFMain.approxSplit(som,perceptronAlpha,perceptronMomentum,trainFile,testFile,
                                String.format("Split learning from file %s",trainFile),somEras,perceptronEras);
                    }else if(learningType.toLowerCase().equals("together")){
                            RBFMain.approxTogether(som,perceptronAlpha,perceptronMomentum,trainFile,testFile,
                                    String.format("Together learning from file %s",trainFile),perceptronEras);
                    }else{
                        LOGGER.fatal(String.format("Learning type %s is not supported",learningType));
                        System.exit(-1);
                    }
                    break;
                case "classification":
                    if(learningType.toLowerCase().equals("split")){
                        RBFMain.classifySplit(som,perceptronAlpha,perceptronMomentum,trainFile,testFile,
                                classificationInputs,somEras,perceptronEras);
                    }else if(learningType.toLowerCase().equals("together")){
                        RBFMain.classifyTogether(som,perceptronAlpha,perceptronMomentum,trainFile,testFile,
                                classificationInputs,perceptronEras);
                    }else{
                        LOGGER.fatal(String.format("Learning type %s is not supported",learningType));
                        System.exit(-1);
                    }
                    break;
                default:
                    LOGGER.fatal(String.format("This exercise type (%s) is not supported",ext));
                    System.exit(-1);
                    break;
            }
        }catch (NullPointerException e){
            LOGGER.fatal("Missing one or more properties",e);
            System.exit(-1);
        }catch (NumberFormatException e){
            LOGGER.fatal("Some numbers are in incorrect format",e);
            System.exit(-1);
        } catch (IOException e) {
            LOGGER.fatal("Can't find or open properties file", e);
            System.exit(-1);
        }
    }
    public static void main(String[] args){
        if(args.length>0)
            load(args[0]);
        else
            load(String.format("src%smain%sresources%snetwork.properties",File.separator,File.separator,File.separator));
    }
}
