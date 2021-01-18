package br.edu.ifsc.TimetablingGeneticAlgorithm;

import br.edu.ifsc.TimetablingGeneticAlgorithm.dataaccess.RetrieveIFSCData;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.UnavailabilityConstraint;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOIFSC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.geneticalgorithm.GeneticAlgorithm;
import br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing.Avaliation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing.PostProcessing;
import br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing.ViolatedConstraint;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model.PreProcessing;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model.ProfessorsScheduleCreation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.ConvertFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

@SpringBootApplication
public class TimetablingGeneticAlgorithmApplication {

    public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {

        //SpringApplication.run(TimetablingGeneticAlgorithmApplication.class, args);
        GeneticAlgorithm ga = new GeneticAlgorithm();
        ga.process("src//assets//configuracoes.txt");


    }
}
