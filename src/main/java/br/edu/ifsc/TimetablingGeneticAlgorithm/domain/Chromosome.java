package br.edu.ifsc.TimetablingGeneticAlgorithm.domain;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Subject;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Course;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Lesson;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.DTOIFSC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.DTOITC;

import java.util.*;

public class Chromosome {
    private int[] genes;
    private int avaliation;
    private boolean hasViolatedHardConstraint;


    public Chromosome(int size) {
        this.genes = new int[size];
        this.avaliation = 0;
        this.hasViolatedHardConstraint = false;
    }

    public Chromosome(int size, int classSize, Lesson[] lessons, Course[] courses, DTOIFSC dtoIfsc) {
        this.genes = new int[size * classSize];
        this.avaliation = 0;
        this.hasViolatedHardConstraint = false;
        this.generateRandom(lessons, courses, classSize, dtoIfsc);
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
    private void generateRandom(Lesson[] lessons, Course[] courses, int classSize, DTOIFSC dtoifsc) {
        Random random = new Random();
        for (int i = 0; i < courses.length; i++) {
            Lesson[] coursesLesson = new Lesson[courses[i].getLessonsNumber()];

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

            //transforma as matérias com aulas impares (1 ou 3 créditos) em pares (2 ou 4 créditos)
            joinOddLessons(coursesLesson, dtoifsc.getSubjects());

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
     * Realiza a junção das matérias com créditos ({@code lecturesNumber}) ímpares em pares, sendo que caso os créditos
     * sejam:
     *
     * <ul>
     *     <li>
     *         3: procura-se uma outra matéria com 1 crédito, e cria-se uma matéria que é a junção das duas, mantendo
     *         ainda a inicial;
     *         <ul>
     *             <li>
     *             Ex.: M1 com 3 créditos, M2 com 1 crédito --> M1 com 2 créditos, M1 - M2 com 2 créditos.
     *             </li>
     *         </ul>
     *     </li>
     *     <li>
     *         1: procura-se uma outra matéria com 1 crédito, e cria-se uma matéria que é a junção das duas, não
     *         mantendo a inicial.
     *         <ul>
     *             <li>
     *             Ex.: M1 com 1 crédito, M2 com 1 crédito --> M1 - M2 com 2 créditos.
     *             </li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * @param coursesLesson Lista de {@link Lesson} que contém as matérias de uma turma.
     * @param ifscSubjects  {@link List} de {@link Subject} que contém os nomes das matérias.
     */
    private void joinOddLessons(Lesson[] coursesLesson, List<Subject> ifscSubjects) {
        for (Lesson courseLesson : coursesLesson) {
            if (courseLesson.getLecturesNumber() == 3) {
                for (Lesson innerCourseLesson : coursesLesson) {

                    //caso encontrar uma matéria com 3, deve haver outra com 1
                    if (innerCourseLesson.getLecturesNumber() == 1) {
                        courseLesson.setLecturesNumber(2);
                        this.joinName(ifscSubjects, courseLesson.getLessonId(), innerCourseLesson.getLessonId());
                        innerCourseLesson.setLecturesNumber(2);
                    }
                }
            }
        }

        for (Lesson courseLesson : coursesLesson) {
            if (courseLesson.getLecturesNumber() == 1) {
                for (Lesson innerCourseLesson : coursesLesson) {

                    //caso encontrar uma matéria com 1, deve haver outra com 1 que não seja ela mesma
                    if (innerCourseLesson.getLecturesNumber() == 1 && !innerCourseLesson.equals(courseLesson)) {
                        courseLesson.setLecturesNumber(2);

                        //obtém qual o índice da segunda matéria para retirá-la
                        int index = this.joinName(ifscSubjects, courseLesson.getLessonId(), innerCourseLesson.getLessonId());
                        ifscSubjects.remove(index);
                    }
                }
            }
        }

    }


    /**
     * Realiza a junção dos nomes de duas matérias.
     *
     * @param ifscSubjects  {@link List} de {@link Subject} que contém os nomes das matérias.
     * @param courseId      Identificador do primeiro {@link Course}.
     * @param innerCourseId Identificador do segundo {@link Course}.
     * @return o índice do {@code innerCourseId}.
     */
    private int joinName(List<Subject> ifscSubjects, String courseId, String innerCourseId) {
        for (Subject subject : ifscSubjects) {
            if (subject.getId() == Integer.parseInt(innerCourseId)) {
                for (Subject innerSubject : ifscSubjects) {
                    if (innerSubject.getId() == Integer.parseInt(courseId)) {
                        subject.setName(subject.getName() + " - " + innerSubject.getName());
                        return ifscSubjects.indexOf(innerSubject);
                    }
                }
            }
        }
        return -1;
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

    public int geneIsPartOfGroup(int gene, DTOITC dtoitc) throws ClassNotFoundException {
        String id = dtoitc.getLessonById(gene).getCourseId();
        if (id.contains("-")){
            return id.split("-").length * 10;
        }
        return 10;
    }

    public boolean geneIsPartOfGroup(DTOITC dtoitc, int position) throws ClassNotFoundException {
        int gene = getGenes()[position];
        String id = dtoitc.getLessonById(gene).getCourseId();
        return id.contains("-");
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
