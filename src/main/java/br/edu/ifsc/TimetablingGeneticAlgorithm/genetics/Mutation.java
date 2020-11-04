package br.edu.ifsc.TimetablingGeneticAlgorithm.genetics;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;

import java.io.Serializable;
import java.util.Random;

public class Mutation implements Serializable {

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

                /*Obtém-se os dois pontos de troca, isso é necessário para manter o chromossomo sem valores repetidos.
                 * Então ao invés de ser atribuído um valor aleatório, serão trocados dois valores em posições aleatórias*/

                int swapPoint1;
                do {

                    //O cutpoint1 é totalmente aleatório, e o cutpoint2 será na mesma turma do cutpoint1
                    swapPoint1 = random.nextInt(chromosome.getGenes().length);

                } while (chromosome.getGenes()[swapPoint1] == 0);

                //Limite inferior, ou seja, a primeira posição da turma
                int infLimit = Math.floorDiv(swapPoint1, classSize) * 10;

                int swapPoint2;
                do {
                    swapPoint2 = random.nextInt(classSize) + infLimit;
                } while (chromosome.getGenes()[swapPoint2] == 0);

                //faz a troca dos dois genes
                int aux = chromosome.getGenes()[swapPoint1];
                chromosome.getGenes()[swapPoint1] = chromosome.getGenes()[swapPoint2];
                chromosome.getGenes()[swapPoint2] = aux;
                chromosome.setAvaliation(0);
            }
        }
    }
}
