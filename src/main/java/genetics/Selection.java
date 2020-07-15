package genetics;

import domain.Chromosome;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Selection {

    public static Chromosome[] roulleteWheel(Chromosome[] chromosomes, int[] ratingHandler, int faA) throws ClassNotFoundException {
        Chromosome[] parents = new Chromosome[chromosomes.length * 2];
        for (int i = 0; i < parents.length; i++) {
            Random random = new Random();
            int parentRNG = random.nextInt(faA) + 1;
            parents[i] = generateParent(chromosomes, ratingHandler, parentRNG);
        }
        return parents;
    }

    private static Chromosome generateParent(Chromosome[] chromosomes, int[] ratingHandler, int parent) throws ClassNotFoundException {
        for (int i = 0; i < ratingHandler.length; i++) {
            if (i == 0) {
                if (parent <= ratingHandler[i])
                    return chromosomes[i];
            } else {
                if (parent > ratingHandler[i - 1] && parent <= ratingHandler[i])
                    return chromosomes[i];
            }
        }
        throw new ClassNotFoundException("Erro ao gerar cromossomo");
    }

    public static Chromosome[] elitism(Chromosome[] chromosomes, byte proportion) {
        Chromosome[] chosenChromosomes = new Chromosome[proportion];
        Chromosome[] eliteChromosomes = new Chromosome[chromosomes.length];
        for (int i = 0; i < chromosomes.length; i++) {
            eliteChromosomes[i] = new Chromosome(chromosomes[i].getGenes(), chromosomes[i].getAvaliation());
        }
        Arrays.sort(eliteChromosomes, Comparator.comparing(Chromosome::getAvaliation).reversed());
        for (int i = 0; i < proportion; i++) {
            chosenChromosomes[i] = eliteChromosomes[i];
        }
        return chosenChromosomes;
    }
}
