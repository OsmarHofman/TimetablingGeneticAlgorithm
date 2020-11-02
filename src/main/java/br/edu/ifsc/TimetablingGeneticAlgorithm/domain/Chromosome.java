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
        this.genes = new int[genes.length];
        System.arraycopy(genes, 0, this.genes, 0, genes.length);
        this.avaliation = avaliation;
        this.hasViolatedHardConstraint = false;
    }

    public Chromosome(int[] genes, int avaliation, boolean hasViolatedHardConstraint) {
        this.genes = new int[genes.length];
        System.arraycopy(genes, 0, this.genes, 0, genes.length);
        this.avaliation = avaliation;
        this.hasViolatedHardConstraint = hasViolatedHardConstraint;
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

            /*Contador que representa a posição em que será inserida. É necessário pois é feito um foreach, então serve
             * como o índice*/
            int count = 0;

            //Obtém todas as matérias de dado curso
            for (Lesson lesson : lessons) {
                if (lesson.getCourseId() == courses[i].getCourseId()) {
                    coursesLesson[count] = lesson;
                    count++;
                }
            }
            //Indica a posição do primeiro horário de um curso dentro do cromossomo
            int courseIndex = i * classSize;

            count = 0;

            //Transforma as matérias com aulas impares (1 ou 3 créditos) em pares (2 ou 4 créditos)
            coursesLesson = this.joinOddLessons(coursesLesson, dtoifsc.getSubjects());

            for (Lesson lesson : coursesLesson) {
                //Esse cálculo representa quantas vezes por semana uma matéria deve estar em um curso
                for (int k = 0; k < lesson.getMinWorkingDays() * (lesson.getLecturesNumber() / 2); k++) {
                    genes[count + courseIndex] = lesson.getLessonId();
                    count++;
                }
            }

            //Para randomizar as matérias, obtém-se duas materias aleatórias, e elas são trocadas entre si
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

                    //Caso encontrar uma matéria com 3, deve haver outra com 1
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

                    //Caso encontrar uma matéria com 1, deve haver outra com 1 que não seja ela mesma
                    if (innerCourseLesson.getLecturesNumber() == 1 && !innerCourseLesson.equals(courseLesson)) {
                        innerCourseLesson.setLecturesNumber(2);

                        //Junta os professores das duas matérias
                        int[] newProfessor = new int[innerCourseLesson.getProfessorId().length + courseLesson.getProfessorId().length];
                        System.arraycopy(innerCourseLesson.getProfessorId(), 0, newProfessor, 0, innerCourseLesson.getProfessorId().length);
                        System.arraycopy(courseLesson.getProfessorId(), 0, newProfessor, innerCourseLesson.getProfessorId().length, courseLesson.getProfessorId().length);

                        innerCourseLesson.setProfessorId(newProfessor);

                        //Obtém qual o índice da primeira matéria para retirá-la
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
    private int joinName(List<Subject> ifscSubjects, int courseId, int innerCourseId) {
        for (Subject subject : ifscSubjects) {
            if (subject.getId() == innerCourseId) {
                for (Subject innerSubject : ifscSubjects) {
                    if (innerSubject.getId() == courseId) {
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

    /**
     * Checa a avaliação de conflito de horários, para identificar exatamente qual os professores, turmas,
     * e o dia que aconteceu a violação.
     *
     * @param dtoitc  {@link DTOITC} que contém os dados presentes no cromossomo para identificar a violação.
     * @param dtoifsc {@link DTOIFSC} que contém os dados dos nomes dos professores e turmas.
     * @throws ClassNotFoundException Erro caso não seja encontrado o {@link Lesson}, o professor da {@link Lesson},
     *                                ou o {@link Course}.
     */
    public void checkScheduleConflicts(DTOITC dtoitc, DTOIFSC dtoifsc) throws ClassNotFoundException {
        for (int i = 0; i < this.getGenes().length; i++) {
            if (this.getGenes()[i] != 0) {

                Shift currentShift = dtoitc.getShiftByLessonId(this.getGenes()[i]);

                //Obtém o vetor dos professores
                int[] currentProfessors = dtoitc.getProfessorByLessonId(this.getGenes()[i]);

                //Vai de 10 em 10 posições, ou seja, de turma em turma
                for (int j = i + 10; j < this.getGenes().length; j += 10) {

                    //Caso possa ser dado aula nesse dia. Dias não disponíveis tem valor 0.
                    if (this.getGenes()[j] != 0) {

                        Shift innerShift = dtoitc.getShiftByLessonId(this.getGenes()[j]);
                        if (currentShift.equals(innerShift)) {

                            for (int currentProfessor : currentProfessors) {

                                //Obtém o vetor dos professores a serem comparados
                                int[] innerProfessors = dtoitc.getProfessorByLessonId(this.getGenes()[j]);

                                for (int innerProfessor : innerProfessors) {

                                    //Caso o mesmo professor esteja dando aula em duas turmas ao mesmo tempo
                                    if (currentProfessor == innerProfessor) {

                                        //Obtém a primeira turma relacionada a violação
                                        int courseId = dtoitc.getCourseByLessonId(this.getGenes()[i]);
                                        String courseName = dtoifsc.getCourseNameById(courseId);

                                        //Obtém a segunda turma relacionada a violação
                                        int conflictCourseId = dtoitc.getCourseByLessonId(this.getGenes()[j]);
                                        String conflictCourseName = dtoifsc.getCourseNameById(conflictCourseId);


                                        //Obtém o professor relacionado a violação
                                        String professorName = dtoifsc.getProfessorNameById(currentProfessor);

                                        //Obtém o turno que aconteceu a violação
                                        Shift shift = dtoitc.getShiftByCourseId(courseId);

                                        /*Essa operação é feita para obter somente a parte de unidade do número ,
                                         * ou seja um valor de 0 a 9. Isso é necessário para saber exatamente qual
                                         * o dia e horário da violação */
                                        int normalizedIndex = i % 10;

                                        /*Obtém o dia da violação a partir do índice de 0-9 no qual é dividido por 2
                                         * para poder identificar o dia da semana (segunda-feira, terça-feira, etc.)*/
                                        Optional<Dia> dia = Dia.valueOf(normalizedIndex / 2);

                                        /*Obtém o horário da violação, no qual se for um valor par é uma violação no
                                         * primeiro horário, e se for ímpar é no segundo horário*/
                                        Optional<Horario> horario = Horario.valueOf((normalizedIndex % 2 == 0) ? 0 : 1);

                                        System.out.print("\nProfessor:" + professorName + "\nTurmas conflitantes:" +
                                                courseName + ", " + conflictCourseName + "\nDia da semana:");

                                        //Caso o valor passado no valueOf esteja dentro de uma das possibilidades do enum
                                        if (dia.isPresent() && horario.isPresent())
                                            System.out.print(dia.get() + " " + horario.get() + " ");

                                        System.out.println(shift + "\n\n");

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Checa a avaliação de indisponibilidade de professores, para identificar exatamente qual o professor, curso,
     * matéria e o dia que aconteceu a violação.
     *
     * @param dtoitc  {@link DTOITC} que contém os dados presentes no cromossomo para identificar a violação.
     * @param dtoifsc {@link DTOIFSC} que contém os dados dos nomes dos professores e turmas.
     * @throws ClassNotFoundException Erro caso não seja encontrado o {@link Lesson}.
     */
    public void checkProfessorsUnavailabilities(DTOITC dtoitc, DTOIFSC dtoifsc, boolean[][] relationMatrix) throws
            ClassNotFoundException {

        //Valor que representa o deslocamento do dia, ou seja, são duas aulas por dia, então varia entre 0 e 1.
        byte periodOffset = 0;

        //Valor que representa o deslocamento da semana, ou seja, são dez aulas por semana, então varia entre 0 e 9.
        byte weekOffset = 0;

        for (int i = 0; i < this.getGenes().length; i++) {

            //Maior que 1 pois há duas aulas por dia
            if (periodOffset > 1) {
                periodOffset = 0;
                weekOffset++;
            }
            //Maior que 9 pois uma turma está contida em dez posições
            if (weekOffset > 4)
                weekOffset = 0;

            //Caso possa ser dado aula nesse dia. Dias não disponíveis tem valor 0.
            if (this.getGenes()[i] != 0) {
                Lesson lesson = dtoitc.getLessonById(this.getGenes()[i]);

                //Por conta da matriz de relação, é preciso obter qual a posição do Lesson atual
                int lessonPosition = dtoitc.getLessonPosition(lesson.getLessonId());

                //Obtém o turno do Lesson
                Shift shift = dtoitc.getShiftByCourseId(lesson.getCourseId());

                /*Cálculo para obter o boolean que representa a disponibilidade do professor na matriz. Sendo que
                 * os valores "2" representam o número de aulas por dia, e o "6", as aulas com seus turnos.*/

                int matrixPosition = (shift.ordinal() * 2 + periodOffset) + (6 * weekOffset);

                if (relationMatrix[lessonPosition][matrixPosition]) {

                    for (int professor : lesson.getProfessorId()) {
                        for (UnavailabilityConstraint constraint : lesson.getConstraints()) {

                            //Caso o professor atual seja o que violou
                            if (constraint.getId() == professor) {

                                //É dividido por 6 para poder obter o dia que aconteceu a violação
                                int day = Math.floorDiv(matrixPosition, 6);

                                //Se o dia que aconteceu a violação é o mesmo que o professor tem indisponibilidade
                                if (day == constraint.getDay()) {

                                    /*Cálculo para poder obter o período do dia, que será de 0-9, onde os valores
                                     * pares são do primeiro período e os ímpares do segundo*/
                                    int dayPeriod = shift.ordinal() * 2 + periodOffset;
                                    if (dayPeriod == constraint.getDayPeriod()) {

                                        //Obtém o nome da matéria que aconteceu a violação
                                        String lessonName = dtoifsc.getLessonNameById(lesson.getLessonId());

                                        //Obtém o nome do curso que aconteceu a violação
                                        String courseName = dtoifsc.getCourseNameById(lesson.getCourseId());

                                        /*Obtém o dia da semana (segunda-feira, terça-feira, etc.) da violação a partir
                                         * do índice de 0-9 */
                                        Optional<Horario> horario = Horario.valueOf(periodOffset);

                                        /*Obtém o horário da violação, no qual se for um valor par é uma violação no
                                         * primeiro horário, e se for ímpar é no segundo horário*/
                                        Optional<Dia> dia = Dia.valueOf(weekOffset);


                                        //Obtém o nome do professor envolvido na violação
                                        String professorName = dtoifsc.getProfessorNameById(professor);

                                        System.out.print("Professor:" + professorName + "\nCurso:" +
                                                courseName + "\nMatéria: " + lessonName + "\nDia da semana:");

                                        //Caso o valor passado no valueOf esteja dentro de uma das possibilidades do enum
                                        if (dia.isPresent() && horario.isPresent())
                                            System.out.print(dia.get() + " " + horario.get() + " ");

                                        System.out.println(shift + "\n\n");
                                    }

                                }
                            }
                        }

                    }
                }
            }
            periodOffset++;
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


    /**
     * Representação dos dias da semana, sendo eles:
     * <ul>
     *     <li>Segunda-feira</li>
     *     <li>Terça-feira</li>
     *     <li>Quarta-feira</li>
     *     <li>Quinta-feira</li>
     *     <li>Sexta-feira</li>
     * </ul>
     */
    private enum Dia {
        SEGUNDA_FEIRA(0),
        TERCA_FEIRA(1),
        QUARTA_FEIRA(2),
        QUINTA_FEIRA(3),
        SEXTA_FEIRA(4);


        private final int value;

        Dia(int value) {
            this.value = value;
        }

        /**
         * Obtém qual a representação em {@link String} de um Enum a partir do seu valor.
         *
         * @param value inteiro que representa o valor da representação.
         * @return {@link String} que contém a representação do {@code value}.
         */
        public static Optional<Dia> valueOf(int value) {
            return Arrays.stream(values())
                    .filter(horario -> horario.value == value)
                    .findFirst();
        }
    }

    /**
     * Representação dos horários que uma turma pode ter, sendo eles:
     * <ul>
     *     <li>Primeiro Horário</li>
     *     <li>Segundo Horário</li>
     * </ul>
     */
    private enum Horario {
        PRIMEIRO_HORARIO(0),
        SEGUNDO_HORARIO(1);


        private final int value;

        Horario(int value) {
            this.value = value;
        }

        /**
         * Obtém qual a representação em {@link String} de um Enum a partir do seu valor.
         *
         * @param value inteiro que representa o valor da representação.
         * @return {@link String} que contém a representação do {@code value}.
         */
        public static Optional<Horario> valueOf(int value) {
            return Arrays.stream(values())
                    .filter(horario -> horario.value == value)
                    .findFirst();
        }
    }
}
