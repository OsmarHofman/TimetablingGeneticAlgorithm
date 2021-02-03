package br.edu.ifsc.TimetablingGeneticAlgorithm.util;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;

import java.io.*;
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


    public static void setConfiguration(int[] items) {
        File file = new File("/home/alunoremoto/TCCWilson/TimetablingGeneticAlgorithm/src/assets/configuracoes.txt");
        try {

            FileWriter fileWriter = new FileWriter(file, false);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            String[] labels = new String[]{"tamanhoPopulacao", "tamanhoTurma", "porcentagemElitismo", "porcentagemCruzamento",
                    "porcentagemMutacao", "porcentagemJuncaoCurso", "totalGeracoes", "intervaloVerificacao", "numeroComputadores"};
            for (int i = 0; i < items.length; i++) {
                printWriter.println(labels[i] + "=" + items[i]);
            }

            printWriter.close();
            fileWriter.close();
        } catch (IOException ex) {
            System.err.println("Erro ao tentar reescrever o arquivo de configuração");
            ex.printStackTrace();
        }
    }


    /*TODO Pegar do servidor: gerações, n de resets ou soma dos dois;
                              pegar o tempo individual de cada AG ou a soma deles.*/
    public static void buildCSV(Chromosome chromosome, int[] configs, int idTest, int faMax, long preProcessingTime,
                                long processingTime, long gaAverageProcessingTime, long posProcessingTime, long finalTime, int geracoes) {

        String[] times = new String[5];

        times[0] = preProcessingTime / 1000 + "." + preProcessingTime % 1000;
        times[1] = processingTime / 1000 + "." + processingTime % 1000;
        times[2] = posProcessingTime / 1000 + "." + posProcessingTime % 1000;
        times[3] = finalTime / 1000 + "." + finalTime % 1000;
        times[4] = gaAverageProcessingTime / 1000 + "." + gaAverageProcessingTime % 1000;
        File file = new File("/home/alunoremoto/TCCWilson/TimetablingGeneticAlgorithm/src/assets/tests.csv");
        try {

            FileWriter fileWriter = new FileWriter(file, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.println(configs[0] + "," + configs[4] + "," + configs[3] + "," + configs[2] + "," +
                    configs[5] + "," + idTest + "," + faMax + "," + chromosome.getAvaliation() + "," + times[0]
                    + "," + times[1] + "," + times[4] + "," + times[2] + "," + times[3] + "," + geracoes);

            printWriter.close();
            fileWriter.close();
        } catch (IOException ex) {
            System.err.println("Erro ao tentar reescrever o arquivo de configuração");
            ex.printStackTrace();
        }
    }


    public static void createCSV() {
        File file = new File("/home/alunoremoto/TCCWilson/TimetablingGeneticAlgorithm/src/assets/tests.csv");
        try {
            FileWriter fileWriter = new FileWriter(file, false);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            //TODO Ajustar para colocar cabeçalho correto
            printWriter.println("População,Mutação,Cruzamento,Elitismo,Intersecção,Id Testes," +
                    "FA Max,Resultado,Violou Hard,Tempo (s),Gerações,Execuções");

            printWriter.close();
            fileWriter.close();
        } catch (IOException ex) {
            System.err.println("Erro ao tentar reescrever o arquivo de configuração");
            ex.printStackTrace();
        }
    }

}
