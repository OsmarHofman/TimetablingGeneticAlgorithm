package br.edu.ifsc.TimetablingGeneticAlgorithm.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class ConfigReader {

    /**
     * A partir de um arquivo, lê as parâmetros necessários para o AG executar.
     *
     * @param path        caminho do arquivo com as configurações.
     * @param itemsNumber número de itens que serão lidos do arquivo.
     * @return Vetor com cada um dos parâmetros em cada posição.
     * @throws IOException Erro ao tentar ler o arquivo.
     */
    public static int[] readConfiguration(String path, int itemsNumber) throws IOException {
        int[] config = new int[itemsNumber];
        File myObj = new File(path);
        Scanner myReader;

        try {
            myReader = new Scanner(myObj);
            String linha;
            for (int i = 0; i < config.length; i++) {
                linha = myReader.nextLine();
                config[i] = Integer.parseInt(linha.split("=")[1]);
            }

            return config;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        throw new IOException("Erro ao encontrar configurações no arquivo: " + path);
    }

    public static String[] readConfiguration(int itemsNumber, String path) throws IOException {
        String[] config = new String[itemsNumber];
        File myObj = new File(path);
        Scanner myReader;

        try {
            myReader = new Scanner(myObj);
            String linha;
            for (int i = 0; i < config.length; i++) {
                linha = myReader.nextLine();
                config[i] = linha.split("=")[1];
            }

            return config;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        throw new IOException("Erro ao encontrar configurações no arquivo: " + path);
    }
}
