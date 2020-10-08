package br.edu.ifsc.TimetablingGeneticAlgorithm.genetics;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;

import java.util.Random;

public class Mutation {

    /**
     * Realiza a mutação de dois genes dos {@link Chromosome}s da população.
     *
     * @param chromosomes        população que contém todos os {@link Chromosome}.
     * @param classSize          {@link Integer} que representa quantas posições cada turma ocupa no {@link Chromosome}.
     * @param mutationPercentage {@link Integer} que representa a chance dessa etapa ocorrer.
     */
    public static void swapMutation(Chromosome[] chromosomes, int classSize, int mutationPercentage) {
        Random random = new Random();

        for (Chromosome chromosome : chromosomes) {
            int mutationChance = random.nextInt(100);

            //Verifica se o cromossomo atual será mutado
            if (mutationChance < mutationPercentage) {
                //identifica o "grupo", ou seja, qual o limite inferior (a primeira posição) da turma atual
                int group = random.nextInt(chromosomes[0].getGenes().length) / classSize;

                //limite superior (última posição) da turma
                int infLimit = group * classSize;

                //obtém-se os dois pontos de troca, isso é necessário para manter o chromossomo sem valores repetidos.
                //Então ao invés de ser atribuído um valor aleatório, serão trocados dois valores em posições aleatórias

                int swapPoint1;
                do {
                    swapPoint1 = random.nextInt(classSize / 2) + infLimit;
                } while (chromosome.getGenes()[swapPoint1] == 0);

                int swapPoint2;
                do {
                    swapPoint2 = random.nextInt(classSize / 2) + infLimit + (classSize / 2);
                } while (chromosome.getGenes()[swapPoint2] == 0);


                int aux = chromosome.getGenes()[swapPoint1];

                //faz a troca dos dois genes
                chromosome.getGenes()[swapPoint1] = chromosome.getGenes()[swapPoint2];
                chromosome.getGenes()[swapPoint2] = aux;
                chromosome.setAvaliation(0);
            }
        }
    }
}
