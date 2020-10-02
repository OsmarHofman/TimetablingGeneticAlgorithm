package br.edu.ifsc.TimetablingGeneticAlgorithm.genetics;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.DTOITC;

import java.util.Random;

public class Crossover {

    /**
     * Realiza o cruzamento por recombinação ordenada.
     *
     * @param chromosomes     vetor de {@link Chromosome} que representa a população de cromossomos
     * @param classSize       valor que representa a quantidade de aulas semanais de todos os cursos.
     * @param crossPercentage porcentagem de cruzamento, ou seja, 10%, 20%, etc.
     * @return nova população de {@link Chromosome}.
     */
    public static Chromosome[] cross(Chromosome[] chromosomes, int classSize, int crossPercentage, DTOITC dtoitc) throws ClassNotFoundException {
        Random random = new Random();
        Chromosome[] newGeneration = new Chromosome[chromosomes.length];
        int size = chromosomes[0].getGenes().length;

        for (int i = 0; i < chromosomes.length; i += 2) {
            int crossChance = random.nextInt(100);
            Chromosome p1 = new Chromosome(chromosomes[i].getGenes(), 0);
            Chromosome p2 = new Chromosome(chromosomes[i + 1].getGenes(), 0);

            //se cruzar
            if (crossChance < crossPercentage) {
                int group = random.nextInt(size - classSize) / classSize;
                //variável que indica a metade inferior do número de matérias. Isso para garantir que o primeiro
                //valor aleatório para ponte de corte, será menor que o segundo.
                int cutPoint1;
                int cutPoint2;
                //TODO alterar método para validar os cutpoints para que não seja cortado no meio o conjunto;
                do {
                    cutPoint1 = group * classSize;

                } while (chromosomes[i].geneIsPartOfGroup(dtoitc, cutPoint1));
                do {
                    cutPoint2 = (random.nextInt(size - cutPoint1) + 10) / classSize * classSize + cutPoint1;
                } while (chromosomes[i].geneIsPartOfGroup(dtoitc, cutPoint2));

                if (cutPoint1 == 0 && cutPoint2 == 300) {
                    newGeneration[i] = p1;
                    newGeneration[i + 1] = p2;
                } else {

                    Chromosome c1 = new Chromosome(size);
                    Chromosome c2 = new Chromosome(size);
                    for (int j = cutPoint1; j < cutPoint2; j++) {
                        c1.getGenes()[j] = p1.getGenes()[j];
                        c2.getGenes()[j] = p2.getGenes()[j];
                    }

                    //passa os gênes dos pais para seus dois filhos
                    transfer(c1, cutPoint1, cutPoint2, p1.getGenes(), p2.getGenes(), size);
                    transfer(c2, cutPoint1, cutPoint2, p2.getGenes(), p1.getGenes(), size);

                    newGeneration[i] = c1;
                    newGeneration[i + 1] = c2;
                }
            } else {
                newGeneration[i] = p1;
                newGeneration[i + 1] = p2;
            }
        }
        return newGeneration;
    }

    /**
     * Faz a transferência dos genes dos pais {@code p1 e p2} para seu filho {@code child}.
     *
     * @param child     {@link Chromosome} que representa o cromossomo que está sendo tracado entre si.
     * @param cutPoint1 valor numérico que representa o primeiro ponto de corte.
     * @param cutPoint2 valor numérico que representa o segundo ponto de corte.
     * @param p1        vetor de int que representa os gênes do primeiro pai.
     * @param p2        vetor de int que representa os gênes do segundo pai.
     * @param size      quantidade total de gênes presentes em um cromossomo
     */
    private static void transfer(Chromosome child, int cutPoint1, int cutPoint2, int[] p1, int[] p2, int size) {
        int parentIterator = cutPoint2;
        for (int j = 0; j < size; j++) {
            if (parentIterator == size)
                parentIterator = 0;
            if (isNotRepeated(p1, cutPoint1, cutPoint2, p2[parentIterator])) {
                if (j < cutPoint1 || j >= cutPoint2) {
                    for (int i = j; i < j + 10; i++) {
                        child.getGenes()[i] = p2[parentIterator];
                        parentIterator++;
                    }
                    j += 9;
                }
            } else {
                parentIterator += 10;
                j--;
            }
        }
    }

    /**
     * Verifica se um {@code gene} em específico, é igual a algum do {@code p1}.
     *
     * @param p1        gênes do primeiro pai.
     * @param cutPoint1 valor numérico que representa o primeiro ponto de corte.
     * @param cutPoint2 valor numérico que representa o segundo ponto de corte.
     * @param gene      gêne a ser verificado se já está contido no {@code p1}.
     * @return true caso o {@code gene} já esteja no {@code p1}, e else caso contrário.
     */
    private static boolean isNotRepeated(int[] p1, int cutPoint1, int cutPoint2, int gene) {
        for (int i = cutPoint1; i < cutPoint2; i++) {
            if (p1[i] == gene && p1[i] != 0)
                return false;
        }
        return true;
    }
}
