package main.java;

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

    }


}
