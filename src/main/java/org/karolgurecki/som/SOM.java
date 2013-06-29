package org.karolgurecki.som;

import org.karolgurecki.rbf.Neuron;

import java.io.IOException;
import java.util.List;


public interface SOM {
    void setPattern(List<Neuron> pattern);
    void teach(int epochs) throws IOException;
    List<Neuron> getNeurons();
}

