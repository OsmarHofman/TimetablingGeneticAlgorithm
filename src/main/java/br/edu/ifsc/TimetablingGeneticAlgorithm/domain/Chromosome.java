package br.edu.ifsc.TimetablingGeneticAlgorithm.domain;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Subject;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Course;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Lesson;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Shift;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.UnavailabilityConstraint;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOIFSC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;

import java.util.*;

/**
 * Classe que representa um Cromossomo, ou seja, uma estrutura com a possível solução do problema
 */
public class Chromosome {
    private int[] genes;
    private int avaliation;
    private boolean hasViolatedHardConstraint;

    public List<String> getLogs() {
        return logs;
    }

    private List<String> logs = new ArrayList<>();


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
            coursesLesson = this.joinOddLessons(coursesLesson, dtoifsc.getSubjects());

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
     *             Ex.: 'M1' com 3 créditos, 'M2' com 1 crédito --> 'M1' com 2 créditos, 'M1 - M2' com 2 créditos.
     *             </li>
     *         </ul>
     *     </li>
     *     <li>
     *         1: procura-se uma outra matéria com 1 crédito, e cria-se uma matéria que é a junção das duas, não
     *         mantendo a inicial.
     *         <ul>
     *             <li>
     *             Ex.: 'M1' com 1 crédito, 'M2' com 1 crédito --> 'M1 - M2' com 2 créditos.
     *             </li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * @param coursesLesson Lista de {@link Lesson} que contém as matérias de uma turma.
     * @param ifscSubjects  {@link List} de {@link Subject} que contém os nomes das matérias.
     * @return nova {@code coursesLesson} com as {@link Lesson}s atualizadas.
     */
    private Lesson[] joinOddLessons(Lesson[] coursesLesson, List<Subject> ifscSubjects) {
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

        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < coursesLesson.length; i++) {
            Lesson courseLesson = coursesLesson[i];
            if (courseLesson.getLecturesNumber() == 1) {
                for (Lesson innerCourseLesson : coursesLesson) {

                    //caso encontrar uma matéria com 1, deve haver outra com 1 que não seja ela mesma
                    if (innerCourseLesson.getLecturesNumber() == 1 && !innerCourseLesson.equals(courseLesson)) {
                        innerCourseLesson.setLecturesNumber(2);

                        //Junta os professores das duas matérias
                        String[] newProfessor = new String[innerCourseLesson.getProfessorId().length + courseLesson.getProfessorId().length];
                        System.arraycopy(innerCourseLesson.getProfessorId(), 0, newProfessor, 0, innerCourseLesson.getProfessorId().length);
                        System.arraycopy(courseLesson.getProfessorId(), 0, newProfessor, innerCourseLesson.getProfessorId().length, courseLesson.getProfessorId().length);

                        innerCourseLesson.setProfessorId(newProfessor);

                        //obtém qual o índice da primeira matéria para retirá-la
                        int index = this.joinName(ifscSubjects, courseLesson.getLessonId(), innerCourseLesson.getLessonId());
                        ifscSubjects.remove(index);

                        indexes.add(i);
                    }
                }
            }
        }

        //Remove os itens do Array
        if (indexes.size() > 0) {
            return removeItensOnArray(coursesLesson, indexes);
        }
        return coursesLesson;
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

    /**
     * Obtém o cromossomo com a pior avaliação a partir da população.
     *
     * @param chromosomes vetor de {@link Chromosome} que representa a população de cromossomos.
     * @return o {@link Chromosome} com a menor avaliação de toda população.
     */
    public static Chromosome getWorstChromosome(Chromosome[] chromosomes) {
        Chromosome worst = null;
        for (int i = 0; i < chromosomes.length; i++) {
            if (i == 0) {
                worst = chromosomes[0];
            } else {
                if (chromosomes[i].getAvaliation() < worst.getAvaliation())
                    worst = chromosomes[i];
            }
        }

        return worst;
    }

    /**
     * Remove os itens de um Array de {@link Lesson} e reajusta o seu tamanho.
     *
     * @param coursesLesson Array de {@link Lesson} com os itens a serem removidos.
     * @param indexes       {@link List} de {@link Integer} com os índices a serem removidos do Array.
     * @return nova {@code coursesLesson} com os itens removidos e reajustados.
     */
    private Lesson[] removeItensOnArray(Lesson[] coursesLesson, List<Integer> indexes) {
        for (Integer index : indexes) {
            coursesLesson[index] = null;
        }

        List<Lesson> lessonList = new ArrayList<>();

        for (Lesson lesson : coursesLesson) {
            if (lesson != null) {
                lessonList.add(lesson);
            }
        }

        return lessonList.toArray(new Lesson[0]);
    }

    public int checkScheduleConflicts(DTOITC dtoitc, DTOIFSC dtoifsc) throws ClassNotFoundException {
        int avaliation = 0;
        for (int i = 0; i < this.getGenes().length; i++) {
            if (this.getGenes()[i] != 0) {

                Shift currentShift = dtoitc.getShiftByLessonId(this.getGenes()[i]);

                //obtém o vetor dos professores
                String[] currentProfessors = dtoitc.getProfessorByLessonId(this.getGenes()[i]);

                //vai de 10 em 10 posições, ou seja, de turma em turma
                for (int j = i + 10; j < this.getGenes().length; j += 10) {

                    //Caso possa ser dado aula nesse dia. Dias não disponíveis tem valor 0.
                    if (this.getGenes()[j] != 0) {

                        Shift iterationShift = dtoitc.getShiftByLessonId(this.getGenes()[j]);
                        if (currentShift.equals(iterationShift)) {

                            for (String currentProfessor : currentProfessors) {

                                //obtém o vetor dos professores a serem comparados
                                String[] iterationProfessors = dtoitc.getProfessorByLessonId(this.getGenes()[j]);

                                for (String iterationProfessor : iterationProfessors) {

                                    //caso o mesmo professor esteja dando aula em duas turmas ao mesmo tempo
                                    if (currentProfessor.equals(iterationProfessor)) {
                                        String courseId = dtoitc.getCourseByLessonId(this.getGenes()[i]);
                                        String courseName = dtoifsc.getCourseNameById(courseId);

                                        String conflictCourseId = dtoitc.getCourseByLessonId(this.getGenes()[j]);
                                        String conflictCourseName = dtoifsc.getCourseNameById(conflictCourseId);

                                        String professorName = dtoifsc.getProfessorNameById(currentProfessor);

                                        Optional<Horario> horario = Horario.valueOf(i % 10);

                                        System.out.println("\nProfessor:" + professorName + "\nTurmas conflitantes:" +
                                                courseName + ", " + conflictCourseName + "\nDia da semana:" +
                                                horario.get() + " " + iterationShift + "\n\n");

                                        avaliation += 10;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return avaliation;
    }


    public int checkProfessorsUnavailabilities(DTOITC dtoitc, DTOIFSC dtoifsc, boolean[][] relationMatrix) throws
            ClassNotFoundException {
        int avaliation = 0;

        //valor que representa o deslocamento do dia, ou seja, são duas aulas por dia, então varia entre 0 e 1.
        byte periodOffset = 0;

        //valor que representa o deslocamento da semana, ou seja, são dez aulas por semana, então varia entre 0 e 9.
        byte weekOffset = 0;

        for (int i = 0; i < this.getGenes().length; i++) {

            //Maior que 1 pois há duas aulas por dia
            if (periodOffset > 1)
                periodOffset = 0;

            //Maior que 9 pois uma turma está contida em dez posições
            if (weekOffset > 9)
                weekOffset = 0;

            //Caso possa ser dado aula nesse dia. Dias não disponíveis tem valor 0.
            if (this.getGenes()[i] != 0) {
                Lesson lesson = dtoitc.getLessonById(this.getGenes()[i]);

                //por conta da matriz de relação, é preciso obter qual a posição do Lesson atual
                int lessonPosition = dtoitc.getLessonPosition(lesson.getLessonId());

                //obtém o turno do Lesson
                Shift shift = dtoitc.getShiftByCourseId(lesson.getCourseId());

                //cálculo para obter o boolean que representa a disponibilidade do professor na matriz. Sendo que
                // os valores "2" representam o número de aulas por dia, e o "6", as aulas com seus turnos.

                int matrixPosition = (shift.ordinal() * 2 + periodOffset) + (6 * Math.floorDiv(weekOffset, 2));
                if (relationMatrix[lessonPosition][((shift.ordinal() * 2 + periodOffset) + (6 * Math.floorDiv(weekOffset, 2)))]) {

                    for (String professor : lesson.getProfessorId()) {
                        for (UnavailabilityConstraint constraint : lesson.getConstraints()) {
                            if (constraint.getId().equals(professor)) {

                                int day = Math.floorDiv(matrixPosition, 6);
                                if (day == constraint.getDay()) {

                                    int dayPeriod = shift.ordinal() * 2 + periodOffset;
                                    if (dayPeriod == constraint.getDayPeriod()) {
                                        String lessonName = dtoifsc.getLessonById(lesson.getLessonId());

                                        String courseName = dtoifsc.getCourseNameById(lesson.getCourseId());

                                        Optional<Horario> horario = Horario.valueOf(weekOffset);

                                        String professorName = dtoifsc.getProfessorNameById(professor);

                                        logs.add("Professor:" + professorName + "\nCurso:" +
                                                courseName + "\nMatéria: " + lessonName + "\nDia da semana:" +
                                                horario.get() + " " + shift + "\n\n");


                                        System.out.println("Professor:" + professorName + "\nCurso:" +
                                                courseName + "\nMatéria: " + lessonName + "\nDia da semana:" +
                                                horario.get() + " " + shift + "\n\n");

                                        avaliation += 3;
                                    }

                                }
                            }
                        }

                    }

                }
                periodOffset++;
                weekOffset++;
            }
        }
        return avaliation;
    }

    @Override
    public String toString() {
        return "Chromosome{" +
                "genes=" + Arrays.toString(genes) +
                ", avaliation=" + avaliation +
                ", hasViolatedHardConstraint=" + hasViolatedHardConstraint +
                '}';
    }


    private enum Horario {
        SEGUNDA_FEIRA_PRIMEIRO_HORARIO(0),
        SEGUNDA_FEIRA_SEGUNDO_HORARIO(1),
        TERCA_FEIRA_PRIMEIRO_HORARIO(2),
        TERCA_FEIRA_SEGUNDO_HORARIO(3),
        QUARTA_FEIRA_PRIMEIRO_HORARIO(4),
        QUARTA_FEIRA_SEGUNDO_HORARIO(5),
        QUINTA_FEIRA_PRIMEIRO_HORARIO(6),
        QUINTA_FEIRA_SEGUNDO_HORARIO(7),
        SEXTA_FEIRA_PRIMEIRO_HORARIO(8),
        SEXTA_FEIRA_SEGUNDO_HORARIO(9);


        private final int value;

        Horario(int value) {
            this.value = value;
        }

        public static Optional<Horario> valueOf(int value) {
            return Arrays.stream(values())
                    .filter(horario -> horario.value == value)
                    .findFirst();
        }
    }
}
