package genetics;

import domain.Chromosome;

import java.util.Random;

public class Mutation {

    public static void swapMutation(Chromosome[] chromosomes, int classSize, int mutationPercentage) {
        Random random = new Random();

        for (int i = 0; i < chromosomes.length; i++) {
            int mutationChance = random.nextInt(100);
            if (mutationChance < mutationPercentage) {
                int group = random.nextInt(chromosomes[0].getGenes().length) / classSize;
                int infLimit = group * classSize;
                int swapPoint1 = random.nextInt(classSize / 2) + infLimit;
                int swapPoint2 = random.nextInt(classSize / 2) + infLimit + (classSize / 2);
                int aux = chromosomes[i].getGenes()[swapPoint1];
                chromosomes[i].getGenes()[swapPoint1] = chromosomes[i].getGenes()[swapPoint2];
                chromosomes[i].getGenes()[swapPoint2] = aux;
            }
        }
    }
}
