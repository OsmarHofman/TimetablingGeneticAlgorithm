package br.edu.ifsc.TimetablingGeneticAlgorithm;

import br.edu.ifsc.TimetablingGeneticAlgorithm.tests.ConfigTest;

import java.io.IOException;

public class TimetablingGeneticAlgorithmApplication {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

//        GeneticAlgorithm ga = new GeneticAlgorithm();
//        ga.process("/home/alunoremoto/TCCWilson/TimetablingGeneticAlgorithm/src/assets/configuracoes.txt");
        ConfigTest.execute();
    }

}