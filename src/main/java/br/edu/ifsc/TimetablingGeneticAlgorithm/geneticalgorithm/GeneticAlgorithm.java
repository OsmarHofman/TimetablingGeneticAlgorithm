package br.edu.ifsc.TimetablingGeneticAlgorithm.geneticalgorithm;

import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOIFSC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOSchedule;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model.PreProcessing;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model.ProfessorsScheduleCreation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.UnavailabilityConstraint;
import br.edu.ifsc.TimetablingGeneticAlgorithm.genetics.Avaliation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.genetics.Crossover;
import br.edu.ifsc.TimetablingGeneticAlgorithm.genetics.Mutation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.genetics.Selection;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dataaccess.RetrieveIFSCData;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GeneticAlgorithm {
    /**
     * Execução do Algoritmo Genetico
     *
     * @param path caminho do arquivo de configuração que contém os parâmetros necessários ao AG
     * @return {@link List} de {@link DTOSchedule} que representa os cursos e suas matérias
     * @throws IOException            Erro ao tentar obter os dados do arquivo de configuração
     * @throws ClassNotFoundException Erro ao obter alguma informação de alguma das classes
     */
    public List<DTOSchedule> process(String path) throws IOException, ClassNotFoundException {

        int[] config = ConfigReader.readConfiguration(path);
        final int populationSize = config[0];
        final int classSize = config[1];
        final int elitismPercentage = config[2];
        final int crossPercentage = config[3];
        final int mutationPercentage = config[4];
        final int joinSetPercentage = config[5];
        final int geracoes = config[6];


        RetrieveIFSCData retrieveIFSCData = new RetrieveIFSCData();
        DTOIFSC dtoifsc = retrieveIFSCData.getAllData();

        ProfessorsScheduleCreation psc = new ProfessorsScheduleCreation(dtoifsc);

        PreProcessing preProcessing = new PreProcessing(psc);
        //Lista que cada posição é uma lista de cursos
        preProcessing.createSet(joinSetPercentage);

        //Criando Modelagem do ITC
        DTOITC dtoitc = ConvertFactory.convertIFSCtoITC(dtoifsc);

        DTOITC[] sets = preProcessing.splitSet(dtoitc);

        /*Matriz de relação dos horarios
        Sendo que 30 é o número de períodos no dia * dias na semana, ou seja, 6 * 5 = 30
        */
        boolean[][] scheduleRelation = new boolean[dtoitc.getLessons().length][30];
        for (int i = 0; i < dtoitc.getLessons().length; i++) {
            for (UnavailabilityConstraint iterationConstraints : dtoitc.getLessons()[i].getConstraints()) {
                scheduleRelation[i][6 * iterationConstraints.getDay() + iterationConstraints.getDayPeriod()] = true;
            }
        }


        int iterationLimit = 0;

        // A partir daqui realizar processamento pensando em objetos distribuidos

        Chromosome[] population = new Chromosome[populationSize];
        //Inicializando população
        Arrays.setAll(population, i -> new Chromosome(dtoitc.getCourses().length, classSize, dtoitc.getLessons(), dtoitc.getCourses(), dtoifsc));

        for (Chromosome chromosome : population) {
            chromosome.setHasViolatedHardConstraint(false);
            chromosome.setAvaliation(Avaliation.rate(chromosome, dtoitc, scheduleRelation));
        }

        // checkCourses(dtoifsc);
        Chromosome localBest = Chromosome.getBestChromosome(population);
        Chromosome globalBestChromosome = localBest;
        long startTime = System.currentTimeMillis();

        while (iterationLimit < geracoes && ((localBest.getAvaliation() < 4700) || localBest.isHasViolatedHardConstraint())) {


            //Seleção por elitismo
            byte proportion = (byte) (populationSize / elitismPercentage);
            Chromosome[] eliteChromosomes = Selection.elitism(population, proportion);

            //função de avaliacao acumulada
            int[] ratingHandler = new int[populationSize];
            int faA = 0;
            for (int i = 0; i < population.length; i++) {
                faA += population[i].getAvaliation();
                ratingHandler[i] = faA;
            }

            //Seleção por roleta
            Chromosome[] newCouples = Selection.rouletteWheel(population, ratingHandler, faA, proportion);

            //Crossover
            Chromosome[] crossedChromosomes = Crossover.cross(newCouples, classSize, crossPercentage);

            //Unindo as Subpopulações geradas por elitismo e roleta
            Chromosome[] newGeneration = new Chromosome[populationSize];
            System.arraycopy(crossedChromosomes, 0, newGeneration, 0, crossedChromosomes.length);

            System.arraycopy(eliteChromosomes, 0, newGeneration, crossedChromosomes.length, eliteChromosomes.length);

            //Mutação
            Mutation.swapMutation(newGeneration, classSize, mutationPercentage);

            population = newGeneration;

            for (Chromosome chromosome : population) {
                chromosome.setHasViolatedHardConstraint(false);
                chromosome.setAvaliation(Avaliation.rate(chromosome, dtoitc, scheduleRelation));
            }

            localBest = Chromosome.getBestChromosome(population);
            if (globalBestChromosome.getAvaliation() < localBest.getAvaliation())
                globalBestChromosome = localBest;

            iterationLimit++;

            System.out.println("\nIteração: " + iterationLimit);
//            System.out.println("Avaliação: " + localBest.getAvaliation());
//            System.out.println("Violou: " + localBest.isHasViolatedHardConstraint());
        }
        long endTime = System.currentTimeMillis();
        System.out.println(globalBestChromosome.toString());
        System.out.println("tempo Final: " + (endTime - startTime));
        System.out.println("iteração: " + iterationLimit);
        return DTOSchedule.convertChromosome(globalBestChromosome, dtoifsc, dtoitc);
    }

}
