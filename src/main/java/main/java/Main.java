package main.java;

import domain.Chromosome;
import domain.itc.UnavailabilityConstraint;
import genetics.Avaliation;
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


        RetrieveIFSCData retrieveIFSCData = new RetrieveIFSCData();
        DTOIFSC dtoifsc = retrieveIFSCData.getAllData();
        ProfessorsScheduleCreation psc = new ProfessorsScheduleCreation(dtoifsc);


        final int percentage = 60;
        EntitySchedule entitySchedule = new EntitySchedule(psc);
        //Lista que cada posição é uma lista de cursos
        List[] text = entitySchedule.createSet(percentage);
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


            Chromosome[] population = new Chromosome[100];
            //Inicializando população
            Arrays.setAll(population, i -> new Chromosome(fromIfSC.getCourses().length, fromIfSC.getLessons()));


            //função de avaliacao acumulada
            System.out.println("\nCalculando FaA...");
            int faA = 0;
            for (int i = 0; i < population.length; i++) {
                faA += population[i].getAvaliation();
            }


            for (int i = 0; i < population.length; i++) {
                Avaliation.rate(population[i], fromIfSC);
            }


        }
    }


}
