package br.edu.ifsc.TimetablingGeneticAlgorithm.geneticalgorithm;

import br.edu.ifsc.TimetablingGeneticAlgorithm.distributed.ConnectionFactory;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTODistributedData;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOIFSC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOSchedule;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model.PreProcessing;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model.ProfessorsScheduleCreation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dataaccess.RetrieveIFSCData;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.*;

import java.io.IOException;
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
        int[] config = ConfigReader.readConfiguration(path, 8);
        final int joinSetPercentage = config[5];

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

        long startTime = System.currentTimeMillis();

        int availablePCs = 2;
        if (sets.length < availablePCs)
            availablePCs = sets.length;

        String[] PCsIPs = ConfigReader.readConfiguration(availablePCs, "/home/alunoremoto/TCCWilson/TimetablingGeneticAlgorithm/src/assets/ips.txt");

        int[] numberSetsForPCs = new int[availablePCs];

        int numberIndex = 0;
        for (int i = 0; i < sets.length; i++) {
            numberSetsForPCs[numberIndex]++;
            numberIndex++;
            if (numberIndex == availablePCs)
                numberIndex = 0;
        }

        int count = 0;
        CountDownLatch latch = new CountDownLatch(availablePCs);
        ConnectionFactory connectionFactory = new ConnectionFactory(availablePCs);
        for (int i = 0; i < availablePCs; i++) {
            DTOITC[] setDTO = new DTOITC[numberSetsForPCs[i]];
            for (int j = 0; j < numberSetsForPCs[i]; j++) {
                setDTO[j] = sets[count];
                count++;
            }
            DTODistributedData data = new DTODistributedData(setDTO, config, dtoifsc.getSubjects(), preProcessing.getCourseRelationList());
            connectionFactory.sendSet(PCsIPs[i], data, i, latch);
        }

        latch.await();

        Chromosome globalBests = Chromosome.groupSets(connectionFactory.getFinalChromosomes());


        //Apresenta os valores relativos ao tempo de execução total

        long endTime = System.currentTimeMillis();

        long totalFinalTime = (endTime - startTime);

        System.out.println(globalBests.toString());

        System.out.println("Tempo Total Final: " + totalFinalTime / 1000 + "." + totalFinalTime % 1000 + " segundos");

        return DTOSchedule.convertChromosome(globalBests, dtoifsc, dtoitc);
    }

}
