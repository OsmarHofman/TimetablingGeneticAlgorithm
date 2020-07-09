package main.java;

import domain.Chromosome;
import domain.itc.Lesson;
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

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
//
        //TODO pre processar dados vindos do XML e tratar o retorno do pre processamento com a quantidade de conjuntos e quais cursos em cada conjunto

        //FIXME atributo id na classe Constraints pode ser removido e tbm o atributo Constraints na classe DTOITC
//        ProfessorsScheduleCreation psc = new ProfessorsScheduleCreation("src/Datasets/IFSCFiles/Dados_ifsc_2019.xlsx");
//
//        final int percentage = 60;
//        EntitySchedule entitySchedule = new EntitySchedule(psc);
//        String text = entitySchedule.createSet(percentage);
//        IFileHandler fileHandler = new FileHandler();
//        fileHandler.createReport(text, percentage + "%");

        RetrieveIFSCData retrieveIFSCData = new RetrieveIFSCData();
        DTOIFSC dtoifsc = retrieveIFSCData.getAllData();
        RetrieveITCData rid = new RetrieveITCData();
        DTOITC dtoitc = rid.getFromITC();
        DTOITC fromIfSC = ConvertFactory.convertIFSCtoITC(dtoifsc);


        boolean[][] scheduleRelation = new boolean[fromIfSC.getLessons().length][60];
        for (int i = 0; i < fromIfSC.getLessons().length; i++) {
            for (UnavailabilityConstraint iterationConstraints : fromIfSC.getLessons()[i].getConstraints()) {
                scheduleRelation[i][12 * iterationConstraints.getDay() + iterationConstraints.getDayPeriod()] = true;
            }
        }


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
