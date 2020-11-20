package br.edu.ifsc.TimetablingGeneticAlgorithm;

import br.edu.ifsc.TimetablingGeneticAlgorithm.tests.ConfigTest;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class TimetablingGeneticAlgorithmApplication {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        //SpringApplication.run(TimetablingGeneticAlgorithmApplication.class, args);
        ConfigTest.execute();
    }

}
