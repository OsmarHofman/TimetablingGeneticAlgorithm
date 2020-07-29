package br.edu.ifsc.TimetablingGeneticAlgorithm.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class ConfigReader {

    public static int[] readConfiguration(String path) throws IOException {
        int[] config = new int[7];
        File myObj = new File(path);
        Scanner myReader;

        try {
            myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String linha;
                //cursos
                if (myReader.hasNext("tamanhoPopulacao=[0-9]+")) {
                   linha  = myReader.nextLine();
                   config[0] = Integer.parseInt(linha.split("=")[1]);
                }else if (myReader.hasNext("tamanhoTurma=[0-9]+")) {
                    linha  = myReader.nextLine();
                    config[1] = Integer.parseInt(linha.split("=")[1]);
                }else if (myReader.hasNext("porcentagemElitismo=[0-9]+")) {
                    linha  = myReader.nextLine();
                    config[2] = Integer.parseInt(linha.split("=")[1]);
                }else if (myReader.hasNext("porcentagemCruzamento=[0-9]+")) {
                    linha  = myReader.nextLine();
                    config[3] = Integer.parseInt(linha.split("=")[1]);
                }else if (myReader.hasNext("porcentagemMutacao=[0-9]+")) {
                    linha  = myReader.nextLine();
                    config[4] = Integer.parseInt(linha.split("=")[1]);
                }else if (myReader.hasNext("porcentagemJuncaoCurso=[0-9]+")) {
                    linha  = myReader.nextLine();
                    config[5] = Integer.parseInt(linha.split("=")[1]);
                }else if (myReader.hasNext("geracoes=[0-9]+")){
                    linha  = myReader.nextLine();
                    config[6] = Integer.parseInt(linha.split("=")[1]);
                }
            }
            return config;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        throw new IOException("Erro ao encontrar configurações gerais do AG");
    }
}
