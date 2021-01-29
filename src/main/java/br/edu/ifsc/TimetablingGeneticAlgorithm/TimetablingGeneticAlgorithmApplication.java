package br.edu.ifsc.TimetablingGeneticAlgorithm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class TimetablingGeneticAlgorithmApplication {

    public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
        SpringApplication.run(TimetablingGeneticAlgorithmApplication.class, args);
//        GeneticAlgorithm ga = new GeneticAlgorithm();
//        ga.process("src//assets//configuracoes.txt");
    }
}
