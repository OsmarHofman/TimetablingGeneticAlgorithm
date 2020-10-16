package br.edu.ifsc.TimetablingGeneticAlgorithm;

import br.edu.ifsc.TimetablingGeneticAlgorithm.geneticalgorithm.GeneticAlgorithm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class TimetablingGeneticAlgorithmApplication {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        SpringApplication.run(TimetablingGeneticAlgorithmApplication.class, args);
//		GeneticAlgorithm ga = new GeneticAlgorithm();
//		ga.process("src//assets//configuracoes.txt");
    }

}
