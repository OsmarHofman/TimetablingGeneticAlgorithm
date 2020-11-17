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
    public List<DTOSchedule> process(String path) throws IOException, ClassNotFoundException, InterruptedException {
        System.out.println("Iniciando Algoritmo Genético...");
        //Obtém as configurações do arquivo
        int[] config = ConfigReader.readConfiguration(path);
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

        for (int i = 0; i < sets.length; i++) {

            //Obtém o DTOITC respectivo ao conjunto que será processado
            DTOITC set = sets[i];

            //Obtém o número de cursos dentro de um conjunto
            int coursesSize = preProcessing.getCourseRelationList().get(i).getName().split("-").length;

            //Obtém a avaliação inicial, ou seja, a que será usada para a função de avaliação desse conjunto
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
            Chromosome globalBestChromosome = new Chromosome(0);
            long startLocalTime = System.currentTimeMillis();

            //Número de execuções do While de fora
            int iterator = -1;

            //Número de execuções do While de dentro
            int innerIterator = 0;

            int restartCount = 0;

            while (globalBestChromosome.getAvaliation() < initialAvaliation && restartCount < 10) {
                iterator = -1;
                innerIterator = 0;
                //Inicializando população
                Chromosome[] population = new Chromosome[populationSize];
                Arrays.setAll(population, x -> new Chromosome(set.getCourses().length, classSize, set.getLessons(), set.getCourses(), dtoifsc));

                //Avaliando a primeira geração com threads
                Avaliation.threadRate(populationSize, coresNumber, population, set, scheduleRelation, initialAvaliation);

                //Obtendo o melhor cromossomo da primeira geração
                Chromosome localBest = Chromosome.getBestChromosome(population);

                //Inicializando o melhor cromossomo global
                globalBestChromosome = localBest;


                //Melhor avaliação
                int avaliation = 0;

                //Laço que controla se as gerações estão melhorando
                while (iterator < geracoes &&
                        ((localBest.getAvaliation() < initialAvaliation) || localBest.isHasViolatedHardConstraint())) {

                    iterator++;
                    innerIterator = 0;

                    //Laço do processamento das gerações
                    while (innerIterator < geracoes &&
                            ((localBest.getAvaliation() < initialAvaliation) || localBest.isHasViolatedHardConstraint())) {

                        //Seleção por elitismo
                        byte proportion = (byte) (populationSize / elitismPercentage);
                        Chromosome[] eliteChromosomes = Selection.elitism(population, proportion);

                        //Função de avaliação acumulada
                        int[] ratingHandler = new int[populationSize];
                        int faA = 0;
                        for (int j = 0; j < population.length; j++) {
                            faA += population[j].getAvaliation();
                            ratingHandler[j] = faA;
                        }

                        //Seleção por roleta
                        Chromosome[] newCouples = Selection.rouletteWheel(population, ratingHandler, faA, proportion);

                        //Cruzamento
                        Chromosome[] crossedChromosomes = Crossover.cross(newCouples, classSize, crossPercentage);

                        //Unindo as Subpopulações geradas por elitismo e roleta
                        Chromosome[] newGeneration = new Chromosome[populationSize];
                        System.arraycopy(crossedChromosomes, 0, newGeneration, 0, crossedChromosomes.length);

                        System.arraycopy(eliteChromosomes, 0, newGeneration, crossedChromosomes.length, eliteChromosomes.length);

                        //Mutação
                        Mutation.swapMutation(newGeneration, classSize, mutationPercentage);

                        //Atribuindo a nova geração
                        population = newGeneration;

                        //Avaliando a nova geraação com threads
                        Avaliation.threadRate(populationSize, coresNumber, population, set, scheduleRelation, initialAvaliation);

                        //Obtendo o melhor cromossomo da geração atual
                        localBest = Chromosome.getBestChromosome(population);

                        //Caso o melhor cromossomo dessa geração seja melhor que o melhor global
                        if (globalBestChromosome.getAvaliation() < localBest.getAvaliation())
                            globalBestChromosome = new Chromosome(localBest.getGenes(), localBest.getAvaliation(), localBest.isHasViolatedHardConstraint());

                        innerIterator++;


//                    System.out.println("Iteração " + (iterator * geracoes + innerIterator));

//            System.out.println("Avaliação: " + localBest.getAvaliation());
//            System.out.println("Violou: " + localBest.isHasViolatedHardConstraint());
                    }

                    //Caso as gerações melhoraram, continua, senão sai dos laços
                    if (globalBestChromosome.getAvaliation() > avaliation)
                        avaliation = globalBestChromosome.getAvaliation();
                    else {
                        break;
                    }

                }
                restartCount++;
            }
            //Apresenta os valores relativos as iterações
            System.out.println("################## CONJUNTO " + psc.getCourseRelationList().get(i).getName() + " ##################");

            System.out.println("Número de resets: " + (restartCount - 1));

            System.out.println("\nNúmero total de iterações: " + (iterator * geracoes + innerIterator));

            //Apresenta os valores relativos ao tempo de execução
            long endLocalTime = System.currentTimeMillis();

            long localFinalTime = (endLocalTime - startLocalTime);

            System.out.println("Tempo Local Final: " + localFinalTime / 1000 + "." + localFinalTime % 1000 + " segundos");

            //Apresenta os valores relativos ao resultado final obtido
            globalBests[i] = globalBestChromosome;

            System.out.println("Cromossomo: " + globalBests[i].toString());

            System.out.println("Avaliação=" + globalBests[i].getAvaliation() + ", ViolouHardConstraint=" + globalBests[i].isHasViolatedHardConstraint());

            //Checa os conflitos de horários
            System.out.println("\n -------------- \nConflitos de Horário:\n");
            globalBests[i].checkScheduleConflicts(set, dtoifsc);

            //Checa as indisponibilidades dos professores
            System.out.println("Indisponibilidade dos Professores:\n");
            globalBests[i].checkProfessorsUnavailabilities(set, dtoifsc, scheduleRelation);

        }

        //Apresenta os valores relativos ao tempo de execução total
        long endTime = System.currentTimeMillis();

        long totalFinalTime = (endTime - startTime);

        System.out.println("Tempo Total Final: " + totalFinalTime / 1000 + "." + totalFinalTime % 1000 + " segundos");

        return DTOSchedule.convertChromosome(globalBests, dtoifsc, dtoitc);
    }

}
