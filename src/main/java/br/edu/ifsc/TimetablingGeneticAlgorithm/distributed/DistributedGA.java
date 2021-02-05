package br.edu.ifsc.TimetablingGeneticAlgorithm.distributed;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Subject;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.UnavailabilityConstraint;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOChromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTODistributedData;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.genetics.Avaliation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.genetics.Crossover;
import br.edu.ifsc.TimetablingGeneticAlgorithm.genetics.Mutation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.genetics.Selection;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.entities.CourseRelation;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.List;

public class DistributedGA extends UnicastRemoteObject implements IDistributedGA, Serializable {

    private static final long serialVersionUID = 1L;

    public DistributedGA() throws RemoteException {
    }

    public DTOChromosome process(DTODistributedData data) throws InterruptedException, ClassNotFoundException, RemoteException {
        System.out.println("Iniciando Algoritmo Genético...");

        long startLocalTime = System.currentTimeMillis();
        long localFinalTime = 0;


        int[] config = data.getConfig();
        DTOITC[] sets = data.getSets();
        List<CourseRelation> courseRelations = data.getCourseRelations();
        List<Subject> dtoIfscSubjects = data.getDtoIfscSubjects();


        final int populationSize = config[0];
        final int classSize = config[1];
        final int elitismPercentage = config[2];
        final int crossPercentage = config[3];
        final int mutationPercentage = config[4];
        final int totalIterationNumber = config[6];
        final int verificationInterval = config[7];

        Chromosome[] globalBestChromosomes = new Chromosome[sets.length];

        int restartCount = 0;
        int totalIterations = 0;

        for (int i = 0; i < sets.length; i++) {

            //Obtém o DTOITC respectivo ao conjunto que será processado
            DTOITC set = sets[i];

            //Obtém o número de cursos dentro de um conjunto
            int coursesSize = courseRelations.get(i).getName().split("-").length;


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


            restartCount = 0;

            while (globalBestChromosome.getAvaliation() < initialAvaliation && restartCount < 10) {
                //Número de execuções do While de fora
                int iterator = -1;

                //Número de execuções do While de dentro
                int innerIterator;

                //Inicializando população
                Chromosome[] population = new Chromosome[populationSize];
                Arrays.setAll(population, x -> new Chromosome(set.getCourses().length, classSize, set.getLessons(), set.getCourses(), dtoIfscSubjects));

                int coresNumber = Runtime.getRuntime().availableProcessors();

                //Avaliando a primeira geração com threads
                Avaliation.threadRate(populationSize, coresNumber, population, set, scheduleRelation, initialAvaliation);

                //Obtendo o melhor cromossomo da primeira geração
                Chromosome localBest = Chromosome.getBestChromosome(population);

                //Inicializando o melhor cromossomo global
                globalBestChromosome = localBest;


                //Melhor avaliação
                int avaliation = 0;

                //Laço que controla se as gerações estão melhorando
                while (iterator < totalIterationNumber / verificationInterval &&
                        ((localBest.getAvaliation() < initialAvaliation) || localBest.isHasViolatedHardConstraint())) {

                    iterator++;
                    innerIterator = 0;

                    //Laço do processamento das gerações
                    while (innerIterator < verificationInterval &&
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

                        totalIterations = iterator * verificationInterval + innerIterator;

                        System.out.println("Iteração " + totalIterations);

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
            System.out.println("Conjunto processado");

//        System.out.println("\nNúmero total de iterações: " + (iterator * geracoes + innerIterator));

            //Apresenta os valores relativos ao tempo de execução
            long endLocalTime = System.currentTimeMillis();

            localFinalTime = (endLocalTime - startLocalTime);

            System.out.println("Tempo Local Final: " + localFinalTime / 1000 + "." + localFinalTime % 1000 + " segundos");

            globalBestChromosomes[i] = globalBestChromosome;

        }

        System.out.println("Finalizado Processamento do AG");
        //Apresenta os valores relativos ao resultado final obtido
        return new DTOChromosome(Chromosome.groupSets(globalBestChromosomes), localFinalTime, totalIterations, (byte) restartCount);
    }

}
