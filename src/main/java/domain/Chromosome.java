package domain;

import domain.itc.Lesson;
import util.DTOITC;

import java.util.Arrays;
import java.util.Random;

public class Chromosome {
    private int[] genes;
    private int avaliation;

    public Chromosome() {
    }

    public Chromosome(int size, Lesson[] lessons) {
        this.genes = new int[size * 20] ;
        this.avaliation = 5000;
        generateRandom(lessons);
    }

    public int[] getGenes() {
        return genes;
    }

    public void setGenes(int[] genes) {
        this.genes = genes;
    }

    public int getAvaliation() {
        return avaliation;
    }

    public void setAvaliation(int avaliation) {
        this.avaliation = avaliation;
    }


    private void generateRandom(Lesson[] lessons){
        Random random = new Random();
        for (int i = 0; i <this.genes.length; i++) {
            int index= random.nextInt(lessons.length);
            this.genes[i] = Integer.parseInt(lessons[index].getLessonId());
        }
    }


    @Override
    public String toString() {
        return "Chromosome{" +
                "genes=" + Arrays.toString(genes) +
                ", avaliation=" + avaliation +
                '}';
    }

}
