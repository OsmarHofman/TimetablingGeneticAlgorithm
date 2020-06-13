package main.java;

import ImportFiles.RetrieveProfessorsSchedule;
import ImportFiles.preProcessing.CourseRelation;

import java.io.*;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        //TODO Alterar atributos do domain e corrigir possiveis conflitos

        RetrieveProfessorsSchedule rts = new RetrieveProfessorsSchedule();
        List<CourseRelation> cs = rts.converterWB(rts.pegarArquivo());
        createReport(cs);

    }


    private static void createReport(List<CourseRelation> cs) throws IOException {
        File file = new File("relaçoesProfessores.txt");
        if (file.createNewFile()) {
            try {
                FileWriter arq = new FileWriter(file);
                PrintWriter gravarArq = new PrintWriter(arq);

                gravarArq.println(cs.toString());
                arq.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }else{
            System.out.println("Arquivo já existe");
        }
    }
}
