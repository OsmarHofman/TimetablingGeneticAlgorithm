package br.edu.ifsc.TimetablingGeneticAlgorithm.genetics;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;

import java.util.Random;

public class Crossover {

    public static Chromosome[] cross(Chromosome[] chromosomes, int classSize, int crossPercentage) {
        Random random = new Random();
        Chromosome[] newGeneration = new Chromosome[chromosomes.length];
        int size = chromosomes[0].getGenes().length;

        for (int i = 0; i < chromosomes.length; i += 2) {
            int crossChance = random.nextInt(100);
            Chromosome p1 = new Chromosome(chromosomes[i].getGenes(), 0);
            Chromosome p2 = new Chromosome(chromosomes[i + 1].getGenes(), 0);
            if (crossChance < crossPercentage) {
                int group = random.nextInt(size) / classSize;
                int infLimit = group * classSize;
                int cutPoint1 = random.nextInt(classSize / 2) + infLimit;
                int cutPoint2 = random.nextInt(classSize / 2) + infLimit + (classSize / 2);
                Chromosome c1 = new Chromosome(size);
                Chromosome c2 = new Chromosome(size);
                for (int j = cutPoint1 + 1; j <= cutPoint2; j++) {
                    c1.getGenes()[j] = p1.getGenes()[j];
                    c2.getGenes()[j] = p2.getGenes()[j];
                }
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

    private static boolean isNotRepeated(int[] p1, int cutPoint1, int cutPoint2, int gene) {
        for (int i = cutPoint1; i <= cutPoint2; i++) {
            if (p1[i] == gene)
                return false;
        }
        return true;
    }

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
}
