package br.edu.ifsc.TimetablingGeneticAlgorithm.tests;


import br.edu.ifsc.TimetablingGeneticAlgorithm.geneticalgorithm.GeneticAlgorithm;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.ConfigReader;

import java.io.IOException;

public class ConfigTest {

    public static void execute() throws InterruptedException, IOException, ClassNotFoundException {
        final int[] qtdChromosomes = new int[]{100};
        final int[] crossPercentages = new int[]{30, 50, 60, 70, 90};
        final int[] mutationPercentages = new int[]{5, 10, 20, 30, 50};
        final int[] elitismPercentages = new int[]{4, 8, 20, 30, 40};

        ConfigReader.createCSV();

        GeneticAlgorithm ga = new GeneticAlgorithm();
        for (int qtdChromosome : qtdChromosomes) {
            for (int mutationPercentage : mutationPercentages) {
                for (int crossPercentage : crossPercentages) {
                    for (int elitismPercentage : elitismPercentages) {
                        for (int i = 1; i <= 10; i++) {
                            ConfigReader.setConfiguration(new int[]{qtdChromosome, 10, elitismPercentage, crossPercentage,
                                    mutationPercentage, 0, 20000, 1000});
                            ga.process("src//assets//configuracoes.txt", i);
                        }
                    }
                }
            }
        }
    }
}
