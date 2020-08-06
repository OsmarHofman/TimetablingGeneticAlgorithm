package br.edu.ifsc.TimetablingGeneticAlgorithm.domain;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Course;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Lesson;

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
        this.generateRandom(lessons, courses, classSize);
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

    /**
     * Inicializa o cromossomo aleatóriamente de acordo com {@link Lesson} de cada um dos {@link Course}.
     *
     * @param lessons   vetor de {@link Lesson} que representa todas as matérias dos cursos.
     * @param courses   vetor de {@link Course} que representa todos os cursos.
     * @param classSize valor que representa a quantidade de aulas semanais de todos os cursos.
     */
    private void generateRandom(Lesson[] lessons, Course[] courses, int classSize) {
        Random random = new Random();
        for (int i = 0; i < courses.length; i++) {
            Lesson[] coursesLesson = new Lesson[courses[i].getCoursesNumber()];

            //contador que representa a posição em que será inserida. É necessário pois é feito um foreach, então serve
            //como o índice
            int count = 0;

            //obtém todas as matérias de dado curso
            for (Lesson lesson : lessons) {
                if (lesson.getCourseId().equals(String.valueOf(courses[i].getCourseId()))) {
                    coursesLesson[count] = lesson;
                    count++;
                }
            }
            //indica a posição do primeiro horário de um curso dentro do cromossomo
            int courseIndex = i * classSize;

            count = 0;
            for (Lesson lesson : coursesLesson) {
                //esse cálculo representa quantas vezes por semana uma matéria deve estar em um curso
                for (int k = 0; k < lesson.getMinWorkingDays() * (lesson.getLecturesNumber() / 2); k++) {
                    genes[count + courseIndex] = Integer.parseInt(lesson.getLessonId());
                    count++;
                }
            }

            //para randomizar as matérias, obtém-se duas materias aleatórias, e elas são trocadas entre si
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

    /**
     * Obtém o cromossomo com a melhor avaliação a partir da população.
     *
     * @param chromosomes vetor de {@link Chromosome} que representa a população de cromossomos.
     * @return o {@link Chromosome} com a maior avaliação de toda população.
     */
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
