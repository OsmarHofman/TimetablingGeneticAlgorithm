package br.edu.ifsc.TimetablingGeneticAlgorithm.geneticalgorithm;

import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOIFSC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOSchedule;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model.PreProcessing;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model.ProfessorsScheduleCreation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.UnavailabilityConstraint;
import br.edu.ifsc.TimetablingGeneticAlgorithm.genetics.Avaliation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.genetics.Crossover;
import br.edu.ifsc.TimetablingGeneticAlgorithm.genetics.Mutation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.genetics.Selection;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dataaccess.RetrieveIFSCData;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class GeneticAlgorithm {


    /**
     * Execução do Algoritmo Genetico
     *
     * @param path caminho do arquivo de configuração que contém os parâmetros necessários ao AG
     * @return {@link List} de {@link DTOSchedule} que representa os cursos e suas matérias
     * @throws IOException            Erro ao tentar obter os dados do arquivo de configuração
     * @throws ClassNotFoundException Erro ao obter alguma informação de alguma das classes
     */
    public List<DTOSchedule> process(String path) throws IOException, ClassNotFoundException, InterruptedException {

        //Obtém as configurações do arquivo
        int[] config = ConfigReader.readConfiguration(path, 7);
        final int populationSize = config[0];
        final int classSize = config[1];
        final int elitismPercentage = config[2];
        final int crossPercentage = config[3];
        final int mutationPercentage = config[4];
        final int joinSetPercentage = config[5];
        final int geracoes = config[6];
        int coresNumber = Runtime.getRuntime().availableProcessors();

        //Obtém os dados do arquivo XML
        RetrieveIFSCData retrieveIFSCData = new RetrieveIFSCData();
        DTOIFSC dtoifsc = retrieveIFSCData.getAllData();

        //Cria os conjuntos do pré-processamento
        ProfessorsScheduleCreation psc = new ProfessorsScheduleCreation(dtoifsc);
        PreProcessing preProcessing = new PreProcessing(psc);
        preProcessing.createSet(joinSetPercentage);

        //Criando Modelagem do ITC
        DTOITC dtoitc = ConvertFactory.convertIFSCtoITC(dtoifsc);

        //Ajusta os cursos que foram pré-processados para a modelagem do DTOITC
        DTOITC[] sets = preProcessing.splitSet(dtoitc);

        //Armazena os melhores cromossomos e todas as gerações
        Chromosome[] globalBests = new Chromosome[sets.length];

        long startTime = System.currentTimeMillis();

        int availablePCs = 3;
        if (sets.length < availablePCs)
            availablePCs = sets.length;

        int[] PCsIPs = ConfigReader.readConfiguration("src//assets//ips.txt", availablePCs);

        int[] numberSetsForPCs = new int[availablePCs];

        int numberIndex = 0;
        for (int i = 0; i < sets.length; i++) {
            numberSetsForPCs[numberIndex]++;
            numberIndex++;
            if (numberIndex == availablePCs)
                numberIndex = 0;
        }

        int count = 0;
        for (int i = 0; i < availablePCs; i++) {
            DTOITC[] setDTO = new DTOITC[numberSetsForPCs[i]];
            for (int j = 0; j < numberSetsForPCs[i]; j++) {
                setDTO[j] = sets[count];
                count++;
            }

            //TODO Configurar RMI para conexão distribuida com os servidores

        }


        //Apresenta os valores relativos ao tempo de execução total
        long endTime = System.currentTimeMillis();

        long totalFinalTime = (endTime - startTime);

        System.out.println("Tempo Total Final: " + totalFinalTime / 1000 + "." + totalFinalTime % 1000 + " segundos");

        return DTOSchedule.convertChromosome(globalBests, dtoifsc, dtoitc);
    }

}
