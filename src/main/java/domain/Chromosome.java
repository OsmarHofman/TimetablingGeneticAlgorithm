package domain;

import domain.itc.Course;
import domain.itc.Lesson;

import java.util.Arrays;
import java.util.Random;

public class Chromosome {
    private int[] genes;
    private int avaliation;
    private boolean hasViolatedHardConstraint;



    public Chromosome(int size) {
        this.genes = new int[size];
        this.avaliation = 0;
        this.hasViolatedHardConstraint = false;
    }

    public Chromosome(int size, int classSize, Lesson[] lessons, Course[] courses) {
        this.genes = new int[size * classSize];
        this.avaliation = 0;
        this.hasViolatedHardConstraint = false;
        generateRandom(lessons, courses, classSize);
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

    private void generateRandom(Lesson[] lessons, Course[] courses, int classSize) {
        Random random = new Random();
        for (int i = 0; i < courses.length; i++) {
            int courseIndex = i * classSize;
            Lesson[] coursesLesson = new Lesson[courses[i].getCoursesNumber()];
            int count = 0;
            for (Lesson lesson : lessons) {
                if (lesson.getCourseId().equals(String.valueOf(courses[i].getCourseId()))) {
                    coursesLesson[count] = lesson;
                    count++;
                }
            }
            count = 0;
            for (Lesson lesson : coursesLesson) {
                for (int k = 0; k < lesson.getMinWorkingDays() * lesson.getLecturesNumber(); k++) {
                    genes[count + courseIndex] = Integer.parseInt(lesson.getLessonId());
                    count++;
                }
            }
            for (int k = 0; k < count / 2; k++) {
                int p1 = random.nextInt(count) + courseIndex;
                int p2 = random.nextInt(count) + courseIndex;
                if (p1 != p2) {
                    int aux = genes[p2];
                    genes[p2] = genes[p1];
                    genes[p1] = aux;
                }
            }

        }
    }

    public static Chromosome getBestChromosome(Chromosome[] chromosomes) {
        Chromosome best = null;
        for (int i = 0; i < chromosomes.length; i++) {
            if (i == 0) {
                best = chromosomes[0];
            } else {
                if (chromosomes[i].getAvaliation() > best.getAvaliation())
                    best = chromosomes[i];

            }
        }

        return best;
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
