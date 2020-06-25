package main.java;

import preprocessing.dataaccess.FileHandler;
import preprocessing.dataaccess.RetrieveIFSCData;
import preprocessing.dataaccess.RetrieveITCData;
import preprocessing.interfaces.IFileHandler;
import preprocessing.model.EntitySchedule;
import preprocessing.model.ProfessorsScheduleCreation;
import util.DTOIFSC;
import util.DTOITC;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //TODO Alterar atributos do domain e corrigir possiveis conflitos

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
        System.out.println("osmar ne");

    }


}
