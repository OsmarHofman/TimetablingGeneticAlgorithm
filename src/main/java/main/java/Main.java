package main.java;

import domain.Chromosome;
import domain.itc.UnavailabilityConstraint;
import genetics.Avaliation;
import genetics.Crossover;
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
        RetrieveIFSCData retrieveIFSCData = new RetrieveIFSCData();
        DTOIFSC dtoifsc = retrieveIFSCData.getAllData();
        ProfessorsScheduleCreation psc = new ProfessorsScheduleCreation(dtoifsc);


        final int percentage = 60;
        EntitySchedule entitySchedule = new EntitySchedule(psc);
        //Lista que cada posição é uma lista de cursos
        List[] coursesSet = entitySchedule.createSet(percentage);
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

        // A partir daqui realizar processamento pensando em objetos distribuidos
        while (true) { // fazer verificar baseado no BOOLEAN do cromossomo, além das outras condições


            Chromosome[] population = new Chromosome[populationSize];
            //Inicializando população
            Arrays.setAll(population, i -> {
                return new Chromosome(fromIfSC.getCourses().length, fromIfSC.getLessons());
            });


            for (int i = 0; i < population.length; i++) {
                population[i].setAvaliation(population[i].getAvaliation() - Avaliation.rate(population[i], fromIfSC, scheduleRelation));
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
            final byte elitismPercentage = 10;
            byte proportion = populationSize / elitismPercentage;
            Chromosome[] eliteChromosomes = Selection.elitism(population, proportion);


            //Seleção por roleta
            Chromosome[] newCouples = Selection.roulleteWheel(population, ratingHandler, faA, proportion);


            //Crossover
            Chromosome[] crossedChromosomes = Crossover.cross(newCouples);

            //Unindo elitismo com selecao por roleta
            Chromosome[] newGeneration = new Chromosome[populationSize];
            for (int i = 0; i < crossedChromosomes.length; i++) {
                newGeneration[i] = crossedChromosomes[i];
            }

            for (int i = 0; i < eliteChromosomes.length; i++) {
                newGeneration[i + crossedChromosomes.length] = eliteChromosomes[i];
            }
        }
    }


}
