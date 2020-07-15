package domain;

import domain.itc.Lesson;

import java.util.Arrays;
import java.util.Random;

public class Chromosome {
    private int[] genes;
    private int avaliation;
    private boolean hasViolatedHardConstraint;

    public Chromosome() {
    }

    public Chromosome(int size, Lesson[] lessons) {
        this.genes = new int[size * 20] ;
        this.avaliation = 5000;
        this.hasViolatedHardConstraint = false;
        generateRandom(lessons);
    }

    public Chromosome(int[] genes, int avaliation) {
        this.genes = genes;
        this.avaliation = avaliation;
        this.hasViolatedHardConstraint = false;
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

    public boolean isHasViolatedHardConstraint() {
        return hasViolatedHardConstraint;
    }

    public void setHasViolatedHardConstraint(boolean hasViolatedHardConstraint) {
        this.hasViolatedHardConstraint = hasViolatedHardConstraint;
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
                ", hasViolatedHardConstraint=" + hasViolatedHardConstraint +
                '}';
    }

}
