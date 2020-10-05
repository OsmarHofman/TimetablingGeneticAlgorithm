package br.edu.ifsc.TimetablingGeneticAlgorithm.genetics;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.DTOITC;

import java.util.Random;

public class Mutation {

    /**
     * Realiza a mutação de dois genes dos {@link Chromosome}s da população.
     *
     * @param chromosomes        população que contém todos os {@link Chromosome}.
     * @param classSize          {@link Integer} que representa quantas posições cada turma ocupa no {@link Chromosome}.
     * @param mutationPercentage {@link Integer} que representa a chance dessa etapa ocorrer.
     */

    //TODO Testar metodo
    public static void swapMutation(Chromosome[] chromosomes, int classSize, int mutationPercentage, DTOITC dtoitc) throws ClassNotFoundException {
        Random random = new Random();

        for (Chromosome chromosome : chromosomes) {
            int mutationChance = random.nextInt(100);

            //Verifica se o cromossomo atual será mutado
            if (mutationChance < mutationPercentage) {
                //identifica qual o limite inferior (a primeira posição) da turma atual
                int courseFirstPosition;
                do {
                    courseFirstPosition = random.nextInt(chromosomes[0].getGenes().length) / classSize;
                } while (chromosome.isGenePartOfGroup(dtoitc, courseFirstPosition));

                classSize = chromosome.getCourseSizeByGene(chromosome.getGenes()[courseFirstPosition], dtoitc);

                //limite superior (última posição) da turma
                int courseLastPosition = courseFirstPosition * classSize;

                //obtém-se os dois pontos de troca, isso é necessário para manter o chromossomo sem valores repetidos.
                //Então ao invés de ser atribuído um valor aleatório, serão trocados dois valores em posições aleatórias

                int swapPoint1;
                do{
                    swapPoint1 = random.nextInt(classSize / 2) + courseLastPosition;
                } while(chromosome.getGenes()[swapPoint1] == 0);

                int swapPoint2;
                do{
                    swapPoint2 = random.nextInt(classSize / 2) + courseLastPosition + (classSize / 2);
                }while(chromosome.getGenes()[swapPoint2] == 0);

                int aux = chromosome.getGenes()[swapPoint1];

                //faz a troca dos dois genes
                chromosome.getGenes()[swapPoint1] = chromosome.getGenes()[swapPoint2];
                chromosome.getGenes()[swapPoint2] = aux;
                chromosome.setAvaliation(0);
            }
        }
    }
}
