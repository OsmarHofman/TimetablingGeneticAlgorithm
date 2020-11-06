package br.edu.ifsc.TimetablingGeneticAlgorithm;

import br.edu.ifsc.TimetablingGeneticAlgorithm.geneticalgorithm.GeneticAlgorithm;

import java.io.IOException;

public class TimetablingGeneticAlgorithmApplication {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        GeneticAlgorithm ga = new GeneticAlgorithm();
        ga.process("src//assets//configuracoes.txt");
    }

}
