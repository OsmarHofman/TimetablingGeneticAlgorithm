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

public class GeneticAlgorithm {


    /**
     * Execução do Algoritmo Genetico
     *
     * @param path caminho do arquivo de configuração que contém os parâmetros necessários ao AG
     * @return {@link List} de {@link DTOSchedule} que representa os cursos e suas matérias
     * @throws IOException            Erro ao tentar obter os dados do arquivo de configuração
     * @throws ClassNotFoundException Erro ao obter alguma informação de alguma das classes
     */
    public List<DTOSchedule> process(String path) throws IOException, ClassNotFoundException {

        //Obtém as configurações do arquivo
        int[] config = ConfigReader.readConfiguration(path);
        final int populationSize = config[0];
        final int classSize = config[1];
        final int elitismPercentage = config[2];
        final int crossPercentage = config[3];
        final int mutationPercentage = config[4];
        final int joinSetPercentage = config[5];
        final int geracoes = config[6];


        //Obtem os dados do arquivo XML
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

        Chromosome[] globalBests = new Chromosome[sets.length];

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < sets.length; i++) {

            DTOITC set = sets[i];

            int coursesSize = preProcessing.getCourseRelationList().get(i).getName().split("-").length;

            int initialAvaliation = Avaliation.getInitialAvaliation(coursesSize);

            /*Matriz de relação dos horarios
            Sendo que 30 é o número de períodos no dia * dias na semana, ou seja, 6 * 5 = 30
            */
            boolean[][] scheduleRelation = new boolean[set.getLessons().length][30];
            for (int j = 0; j < set.getLessons().length; j++) {
                for (UnavailabilityConstraint iterationConstraints : set.getLessons()[j].getConstraints()) {
                    scheduleRelation[j][6 * iterationConstraints.getDay() + iterationConstraints.getDayPeriod()] = true;
                }
            }


            //A partir daqui realizar processamento pensando em objetos distribuidos

            //Inicializando população
            Chromosome[] population = new Chromosome[populationSize];
            Arrays.setAll(population, x -> new Chromosome(set.getCourses().length, classSize, set.getLessons(), set.getCourses(), dtoifsc));

            //Avaliando a primeira geração
            for (Chromosome chromosome : population) {
                chromosome.setHasViolatedHardConstraint(false);
                chromosome.setAvaliation(Avaliation.rate(chromosome, set, scheduleRelation, initialAvaliation,set,dtoifsc,scheduleRelation));
            }

            //Obtendo o melhor cromossomo da primeira geração
            Chromosome localBest = Chromosome.getBestChromosome(population);

            Chromosome globalBestChromosome = localBest;


            //Número de execuções do AG
            int iteration = 0;

            long startLocalTime = System.currentTimeMillis();

            while (iteration < geracoes && ((localBest.getAvaliation() < initialAvaliation) || localBest.isHasViolatedHardConstraint())) {

                //Seleção por elitismo
                byte proportion = (byte) (populationSize / elitismPercentage);
                Chromosome[] eliteChromosomes = Selection.elitism(population, proportion);

                //Função de avaliacao acumulada
                int[] ratingHandler = new int[populationSize];
                int faA = 0;
                for (int j = 0; j < population.length; j++) {
                    faA += population[j].getAvaliation();
                    ratingHandler[j] = faA;
                }

                //Seleção por roleta
                Chromosome[] newCouples = Selection.rouletteWheel(population, ratingHandler, faA, proportion);

                //Crossover
                Chromosome[] crossedChromosomes = Crossover.cross(newCouples, classSize, crossPercentage);

                //Unindo as Subpopulações geradas por elitismo e roleta
                Chromosome[] newGeneration = new Chromosome[populationSize];
                System.arraycopy(crossedChromosomes, 0, newGeneration, 0, crossedChromosomes.length);

                System.arraycopy(eliteChromosomes, 0, newGeneration, crossedChromosomes.length, eliteChromosomes.length);

                //Mutação
                Mutation.swapMutation(newGeneration, classSize, mutationPercentage);

                population = newGeneration;

                //Avaliando a nova geração
//                for (Chromosome chromosome : population) {
//                    chromosome.setHasViolatedHardConstraint(false);
//                    chromosome.setAvaliation(Avaliation.rate(chromosome, set, scheduleRelation, initialAvaliation));
//                }

                //Obtendo o melhor cromossomo da geração atual
                localBest = Chromosome.getBestChromosome(population);

                if (globalBestChromosome.getAvaliation() < localBest.getAvaliation())
                    globalBestChromosome = localBest;


                iteration++;

                System.out.println("\nIteração: " + iteration);
//            System.out.println("Avaliação: " + localBest.getAvaliation());
//            System.out.println("Violou: " + localBest.isHasViolatedHardConstraint());
            }

            long endLocalTime = System.currentTimeMillis();

            System.out.println("Tempo Local Final: " + (endLocalTime - startLocalTime));

            globalBests[i] = globalBestChromosome;

            System.out.println("Cromossomo: " + globalBests[i].toString());
            System.out.println("Avaliação=" + globalBests[i].getAvaliation() + ", ViolouHardConstraint=" + globalBests[i].isHasViolatedHardConstraint());
            System.out.println("\nConflitos de Horário:\n");
            globalBests[i].checkScheduleConflicts(set, dtoifsc);
            System.out.println("Indisponibilidade dos Professores:\n");
            globalBests[i].checkProfessorsUnavailabilities(set, dtoifsc, scheduleRelation);

        }
        long endTime = System.currentTimeMillis();

        System.out.println("Tempo Total Final: " + (endTime - startTime));

        return DTOSchedule.convertChromosome(globalBests, dtoifsc, dtoitc);
    }

}
