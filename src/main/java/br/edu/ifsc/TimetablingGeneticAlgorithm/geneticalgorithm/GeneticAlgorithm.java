package br.edu.ifsc.TimetablingGeneticAlgorithm.geneticalgorithm;

import br.edu.ifsc.TimetablingGeneticAlgorithm.dataaccess.RetrieveIFSCData;
import br.edu.ifsc.TimetablingGeneticAlgorithm.distributed.ConnectionFactory;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.UnavailabilityConstraint;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTODistributedData;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOIFSC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOSchedule;
import br.edu.ifsc.TimetablingGeneticAlgorithm.genetics.Avaliation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing.PostProcessing;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model.PreProcessing;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model.ProfessorsScheduleCreation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.ConfigReader;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.ConvertFactory;

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
        int[] config = ConfigReader.readConfiguration(path, 9);
        final int joinSetPercentage = config[5];
        int availablePCs = config[8];


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

        if (sets.length < availablePCs)
            availablePCs = sets.length;

        String[] PCsIPs = ConfigReader.readConfiguration(availablePCs, "/home/alunoremoto/TCCWilson/TimetablingGeneticAlgorithm/src/assets/ips.txt");

        int[] numberSetsForPCs = new int[availablePCs];

        int numberIndex = 0;
        int totalCourses = 0;
        for (int i = 0; i < sets.length; i++) {
            numberSetsForPCs[numberIndex]++;
            numberIndex++;
            if (numberIndex == availablePCs)
                numberIndex = 0;

            totalCourses+= preProcessing.getCourseRelationList().get(i).getName().split("-").length;
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

        if (sets.length != 1) {
            System.out.println("\n -------------- Pós-processamento -------------- \n");
            int initialAvaliation = Avaliation.getInitialAvaliation(totalCourses);
            PostProcessing postProcessing = new PostProcessing(globalBests, dtoitc, dtoifsc, initialAvaliation);
            if (postProcessing.hasConflicts(globalBests, initialAvaliation)) {
                globalBests = postProcessing.resolveConflicts(globalBests, initialAvaliation);

                //Checa os conflitos de horários
                System.out.println("\n -------------- \nConflitos de Horário:\n");
                globalBests.checkScheduleConflicts(dtoitc, dtoifsc);

                /*Matriz de relação dos horarios
                Sendo que 30 é o número de períodos no dia * dias na semana, ou seja, 6 * 5 = 30
                */
                boolean[][] scheduleRelation = new boolean[dtoitc.getLessons().length][30];
                for (int j = 0; j < dtoitc.getLessons().length; j++) {
                    for (UnavailabilityConstraint iterationConstraints : dtoitc.getLessons()[j].getConstraints()) {
                        scheduleRelation[j][6 * iterationConstraints.getDay() + iterationConstraints.getDayPeriod()] = true;
                    }
                }

                //Checa as indisponibilidades dos professores
                System.out.println("Indisponibilidade dos Professores:\n");
                globalBests.checkProfessorsUnavailabilities(dtoitc, dtoifsc, scheduleRelation);
            }
        }


        //Apresenta os valores relativos ao tempo de execução total

        long endTime = System.currentTimeMillis();

        long totalFinalTime = (endTime - startTime);

        System.out.println(globalBests.toString());

        System.out.println("Tempo Total Final: " + totalFinalTime / 1000 + "." + totalFinalTime % 1000 + " segundos");

        return DTOSchedule.convertChromosome(globalBests, dtoifsc, dtoitc);
    }

}
