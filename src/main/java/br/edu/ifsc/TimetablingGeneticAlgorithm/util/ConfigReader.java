package br.edu.ifsc.TimetablingGeneticAlgorithm.util;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;

import java.io.*;
import java.util.Scanner;

public class ConfigReader {

    /**
     * A partir de um arquivo, lê as parâmetros necessários para o AG executar.
     *
     * @param path caminho do arquivo com as configurações.
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
        throw new IOException("Erro ao encontrar configurações gerais do AG");
    }

    public static void setConfiguration(int[] items) {
        File file = new File("/home/alunoremoto/TCCWilson/TimetablingGeneticAlgorithm/src/assets/configuracoes.txt");
        try {

            FileWriter fileWriter = new FileWriter(file, false);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            String[] labels = new String[]{"tamanhoPopulacao", "tamanhoTurma", "porcentagemElitismo", "porcentagemCruzamento",
                    "porcentagemMutacao", "porcentagemJuncaoCurso", "totalGeracoes", "intervaloVerificacao"};
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

    public static void buildCSV(Chromosome chromosome, int[] configs, String finalTime,
                                int faAMax, int geracoes, int numExec, int idTest) {
        File file = new File("/home/alunoremoto/TCCWilson/TimetablingGeneticAlgorithm/src/assets/tests.csv");
        try {

            FileWriter fileWriter = new FileWriter(file, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.println(configs[0] + "," + configs[4] + "," + configs[3] + "," + configs[2] + "," +
                    configs[5] + "," + idTest + "," + faAMax + "," + chromosome.getAvaliation() + "," +
                    chromosome.isHasViolatedHardConstraint() + "," + finalTime + "," + geracoes + "," + numExec);

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
