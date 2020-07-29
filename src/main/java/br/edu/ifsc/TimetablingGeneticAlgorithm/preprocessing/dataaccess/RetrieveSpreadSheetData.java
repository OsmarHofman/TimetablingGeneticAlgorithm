package br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.dataaccess;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class RetrieveSpreadSheetData {

    public static XSSFWorkbook getWorkBook(String pathname) throws IOException {
        System.out.println("Lendo arquivo " + pathname + " ...\n");
        try {
            // procura o arquivo a partir de seu caminho
            FileInputStream arquivo = new FileInputStream(new File(pathname));
            System.out.println("Arquivo " + pathname + " lido com sucesso...\n");
            return new XSSFWorkbook(arquivo);
        } catch (
                IOException e) {
            System.out.println("Erro ao tentar ler o arquivo Excel.");
            e.printStackTrace();
        }
        throw new IOException("Erro ao pegar arquivo.");
    }
}
