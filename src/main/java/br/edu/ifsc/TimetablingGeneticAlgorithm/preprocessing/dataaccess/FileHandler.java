package br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.dataaccess;

import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.interfaces.IFileHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileHandler implements IFileHandler {

    @Override
    public void createReport(String text, String fileName) throws IOException {
        String pathname = fileName + ".txt";
        System.out.println("Criando arquivo " + pathname + " ...\n");
        File file = new File("out/" + pathname);
        if (file.createNewFile()) {
            try {
                FileWriter fileWriter = new FileWriter(file, true);
                PrintWriter printWriter = new PrintWriter(fileWriter);

                printWriter.println(text);

                printWriter.close();
                fileWriter.close();
                System.out.println("Arquivo " + pathname + " criado com sucesso!\n");
            } catch (IOException ex) {
                System.err.println("Erro ao tentar criar o relatório do curso com os professores.");
                ex.printStackTrace();
            }
        } else {
            throw new IOException("Arquivo " + file.getName() + " já existe!");
        }

    }
}
