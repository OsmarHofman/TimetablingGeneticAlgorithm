package br.edu.ifsc.TimetablingGeneticAlgorithm.genetics;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;

import java.util.Random;

public class Mutation {

    public static void swapMutation(Chromosome[] chromosomes, int classSize, int mutationPercentage) {
        Random random = new Random();

        for (Chromosome chromosome : chromosomes) {
            int mutationChance = random.nextInt(100);
            if (mutationChance < mutationPercentage) {
                int group = random.nextInt(chromosomes[0].getGenes().length) / classSize;
                int infLimit = group * classSize;
                int swapPoint1 = random.nextInt(classSize / 2) + infLimit;
                int swapPoint2 = random.nextInt(classSize / 2) + infLimit + (classSize / 2);
                int aux = chromosome.getGenes()[swapPoint1];
                chromosome.getGenes()[swapPoint1] = chromosome.getGenes()[swapPoint2];
                chromosome.getGenes()[swapPoint2] = aux;
                chromosome.setAvaliation(0);
            }
        }
    }
}
