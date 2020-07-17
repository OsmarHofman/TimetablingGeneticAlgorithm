package main.java;

import domain.Chromosome;
import domain.itc.UnavailabilityConstraint;
import genetics.Avaliation;
import genetics.Crossover;
import genetics.Mutation;
import genetics.Selection;
import preprocessing.dataaccess.FileHandler;
import preprocessing.dataaccess.RetrieveIFSCData;
import preprocessing.dataaccess.RetrieveITCData;
import preprocessing.interfaces.IFileHandler;
import preprocessing.model.EntitySchedule;
import preprocessing.model.ProfessorsScheduleCreation;
import util.ConvertFactory;
import util.DTOIFSC;
import util.DTOITC;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        final int populationSize = 40;
        final int classSize = 20;
        final byte elitismPercentage = 10;
        final int joinSetPercentage = 60;
        final int crossPercentage = 60;
        final int mutationPercentage = 10;


        RetrieveIFSCData retrieveIFSCData = new RetrieveIFSCData();
        DTOIFSC dtoifsc = retrieveIFSCData.getAllData();
        ProfessorsScheduleCreation psc = new ProfessorsScheduleCreation(dtoifsc);


        EntitySchedule entitySchedule = new EntitySchedule(psc);
        //Lista que cada posição é uma lista de cursos
        List[] coursesSet = entitySchedule.createSet(joinSetPercentage);
        //IFileHandler fileHandler = new FileHandler();
        // fileHandler.createReport(text, percentage + "%");


        DTOITC fromIfSC = ConvertFactory.convertIFSCtoITC(dtoifsc);

        //Matriz de relação dos horarios
        boolean[][] scheduleRelation = new boolean[fromIfSC.getLessons().length][60];
        for (int i = 0; i < fromIfSC.getLessons().length; i++) {
            for (UnavailabilityConstraint iterationConstraints : fromIfSC.getLessons()[i].getConstraints()) {
                scheduleRelation[i][12 * iterationConstraints.getDay() + iterationConstraints.getDayPeriod()] = true;
            }
        }
        int iterationLimit = 0;
        // A partir daqui realizar processamento pensando em objetos distribuidos


        Chromosome[] population = new Chromosome[populationSize];
        //Inicializando população
        Arrays.setAll(population, i -> new Chromosome(fromIfSC.getCourses().length, classSize, fromIfSC.getLessons()));


        Chromosome best = Chromosome.getBestChromosome(population);

        while (iterationLimit < 1000 && ((best.getAvaliation() < 3800) || best.isHasViolatedHardConstraint()) || iterationLimit == 0) { // fazer verificação baseado no BOOLEAN do cromossomo, além das outras condições

            for (Chromosome chromosome : population) {
                chromosome.setAvaliation(chromosome.getAvaliation() - Avaliation.rate(chromosome, fromIfSC, scheduleRelation));
            }

            //função de avaliacao acumulada
            System.out.println("\nCalculando FaA...");
            int[] ratingHandler = new int[populationSize];
            int faA = 0;
            for (int i = 0; i < population.length; i++) {
                faA += population[i].getAvaliation();
                ratingHandler[i] = faA;
            }

            //Seleção por elitismo
            byte proportion = populationSize / elitismPercentage;
            Chromosome[] eliteChromosomes = Selection.elitism(population, proportion);


            //Seleção por roleta
            Chromosome[] newCouples = Selection.roulleteWheel(population, ratingHandler, faA, proportion);


            //FIXME verificar chance de cruzar
            //Crossover
            Chromosome[] crossedChromosomes = Crossover.cross(newCouples, classSize, crossPercentage);

            //Unindo elitismo com selecao por roleta
            Chromosome[] newGeneration = new Chromosome[populationSize];
            System.arraycopy(crossedChromosomes, 0, newGeneration, 0, crossedChromosomes.length);

            System.arraycopy(eliteChromosomes, 0, newGeneration, crossedChromosomes.length, eliteChromosomes.length);

            //Mutação
            Mutation.swapMutation(newGeneration, classSize, mutationPercentage);

            iterationLimit++;


            //TODO verificar desempenho dos métodos abaixo, após realizar a inicialização correta
            long startTime = System.currentTimeMillis();
            best = Chromosome.getBestChromosome(population);
            long endTime = System.currentTimeMillis();

            System.out.println("Resultado collections: " + (endTime-startTime));

            startTime = System.currentTimeMillis();
            Chromosome best2 = Chromosome.getBest2(population);
            endTime = System.currentTimeMillis();

            System.out.println("Resultado manual: " + (endTime-startTime));
        }
    }
}
