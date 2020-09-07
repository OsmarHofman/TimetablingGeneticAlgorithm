package br.edu.ifsc.TimetablingGeneticAlgorithm.genetics;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;

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
    public static Chromosome[] cross(Chromosome[] chromosomes, int classSize, int crossPercentage) {
        Random random = new Random();
        Chromosome[] newGeneration = new Chromosome[chromosomes.length];
        int size = chromosomes[0].getGenes().length;

        for (int i = 0; i < chromosomes.length; i += 2) {
            int crossChance = random.nextInt(100);
            Chromosome p1 = new Chromosome(chromosomes[i].getGenes(), 0);
            Chromosome p2 = new Chromosome(chromosomes[i + 1].getGenes(), 0);

            //se cruzar
            if (crossChance < crossPercentage) {
                int group = random.nextInt(size) / classSize;
                //variável que indica a metade inferior do número de matérias. Isso para garantir que o primeiro
                //valor aleatório para ponte de corte, será menor que o segundo.
                int infLimit = group * classSize;
                int cutPoint1 = random.nextInt(classSize / 2) + infLimit;
                int cutPoint2 = random.nextInt(classSize / 2) + infLimit + (classSize / 2);
                Chromosome c1 = new Chromosome(size);
                Chromosome c2 = new Chromosome(size);
                for (int j = cutPoint1 + 1; j <= cutPoint2; j++) {
                    c1.getGenes()[j] = p1.getGenes()[j];
                    c2.getGenes()[j] = p2.getGenes()[j];
                }

                //passa os gênes dos pais para seus dois filhos
                transfer(c1, cutPoint1, cutPoint2, p1.getGenes(), p2.getGenes(), size);
                transfer(c2, cutPoint1, cutPoint2, p2.getGenes(), p1.getGenes(), size);

                newGeneration[i] = c1;
                newGeneration[i + 1] = c2;
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
        int aux = cutPoint2 + 1;
        for (int j = 0; j < size; j++) {
            if (aux == size)
                aux = 0;
            if (isNotRepeated(p1, cutPoint1, cutPoint2, p2[aux])) {
                if (j <= cutPoint1 || j > cutPoint2) {
                    child.getGenes()[j] = p2[aux];
                    aux++;
                }
            } else {
                aux++;
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
        for (int i = cutPoint1; i <= cutPoint2; i++) {
            if (p1[i] == gene)
                return false;
        }
        return true;
    }
}