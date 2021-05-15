package br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Lesson;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Shift;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.UnavailabilityConstraint;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;

public class Avaliation {

    /**
     * Realiza a avaliação de um cromossomo.
     *
     * @param chromosome {@link Chromosome} que será avaliado.
     * @param dtoitc     {@link DTOITC} que contém todas informações relativas a matérias, cursos, salas,
     *                   professores e restrições.
     */
    public static void rate(Chromosome chromosome, DTOITC dtoitc, int initialAvaliation, boolean hasToRateUnavailabilities) throws ClassNotFoundException {
        int avaliation = initialAvaliation;

        chromosome.setHasViolatedHardConstraint(false);

        avaliation -= scheduleConflicts(chromosome, dtoitc);

        if (hasToRateUnavailabilities) {
        /*Matriz de relação dos horarios
        Sendo que 30 é o número de períodos no dia * dias na semana, ou seja, 6 * 5 = 30
        */
            boolean[][] scheduleRelation = new boolean[dtoitc.getLessons().length][30];
            for (int j = 0; j < dtoitc.getLessons().length; j++) {
                for (UnavailabilityConstraint iterationConstraints : dtoitc.getLessons()[j].getConstraints()) {
                    scheduleRelation[j][6 * iterationConstraints.getDay() + iterationConstraints.getDayPeriod()] = true;
                }
            }

            avaliation -= professorsUnavailabilities(chromosome, dtoitc, scheduleRelation);
        }

        chromosome.setAvaliation(avaliation);
    }


    /**
     * Avaliação de conflito de horário de um professor, ou seja, quando está lecionando em dois cursos ao mesmo tempo.
     *
     * @param chromosome {@link Chromosome} que será avaliado.
     * @param dtoitc     {@link DTOITC} que contém todas informações relativas a matérias, cursos, salas,
     *                   professores e restrições.
     * @return a penalização do cromossomo, ou seja, o valor numérico que representa a quantidade de vezes que essa
     * restrição foi violada, multiplicado pelo peso da violação. Cada violação dessa restrição tem peso 10.
     * @throws ClassNotFoundException quando não é encontrado um professor no {@code dtoitc}.
     */
    private static int scheduleConflicts(Chromosome chromosome, DTOITC dtoitc) throws ClassNotFoundException {
        int avaliation = 0;
        for (int i = 0; i < chromosome.getGenes().length; i++) {
            if (chromosome.getGenes()[i] != 0) {

                Shift currentShift = dtoitc.getShiftByLessonId(chromosome.getGenes()[i]);

                //Obtém o vetor dos professores
                int[] currentProfessors = dtoitc.getProfessorByLessonId(chromosome.getGenes()[i]);

                //Vai de 10 em 10 posições, ou seja, de turma em turma
                for (int j = i + 10; j < chromosome.getGenes().length; j += 10) {

                    //Caso possa ser dado aula nesse dia. Dias não disponíveis tem valor 0.
                    if (chromosome.getGenes()[j] != 0) {

                        Shift innerShift = dtoitc.getShiftByLessonId(chromosome.getGenes()[j]);
                        if (currentShift.equals(innerShift)) {

                            for (int currentProfessor : currentProfessors) {

                                //Obtém o vetor dos professores a serem comparados
                                int[] innerProfessors = dtoitc.getProfessorByLessonId(chromosome.getGenes()[j]);

                                for (int innerProfessor : innerProfessors) {

                                    //Caso o mesmo professor esteja dando aula em duas turmas ao mesmo tempo
                                    if (currentProfessor == innerProfessor) {
                                        avaliation += 10;
                                        chromosome.setHasViolatedHardConstraint(true);
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

    /**
     * Avaliação de indisponibilidade de horário de um professor, ou seja, quando está lecionando no seu dia de folga.
     *
     * @param chromosome     {@link Chromosome} que será avaliado.
     * @param dtoitc         {@link DTOITC} que contém todas informações relativas a matérias, cursos, salas,
     *                       professores e restrições.
     * @param relationMatrix matriz que representa as indisponibilidades dos professores.
     * @return a penalização do cromossomo, ou seja, o valor numérico que representa a quantidade de vezes que essa
     * restrição foi violada, multiplicado pelo peso da violação. Cada violação dessa restrição tem peso 5.
     * @throws ClassNotFoundException quando não é encontrado um professor no {@code dtoitc}.
     */
    private static int professorsUnavailabilities(Chromosome chromosome, DTOITC dtoitc, boolean[][] relationMatrix) throws ClassNotFoundException {
        int avaliation = 0;
        //Valor que representa o deslocamento do dia, ou seja, são duas aulas por dia, então varia entre 0 e 1.
        byte periodOffset = 0;

        //Valor que representa o deslocamento da semana, ou seja, são dez aulas por semana, então varia entre 0 e 9.
        byte weekOffset = 0;

        for (int i = 0; i < chromosome.getGenes().length; i++) {

            //Maior que 1 pois há duas aulas por dia
            if (periodOffset > 1) {
                periodOffset = 0;
                weekOffset++;
            }
            //Maior que 9 pois uma turma está contida em dez posições
            if (weekOffset > 4)
                weekOffset = 0;

            //Caso possa ser dado aula nesse dia. Dias não disponíveis tem valor 0.
            if (chromosome.getGenes()[i] != 0) {
                Lesson lesson = dtoitc.getLessonById(chromosome.getGenes()[i]);

                //Por conta da matriz de relação, é preciso obter qual a posição do Lesson atual
                int lessonPosition = dtoitc.getLessonPosition(lesson.getLessonId());

                //Obtém o turno do Lesson
                Shift shift = dtoitc.getShiftByCourseId(lesson.getCourseId());

                /*Cálculo para obter o boolean que representa a disponibilidade do professor na matriz. Sendo que
                 * os valores "2" representam o número de aulas por dia, e o "6", as aulas com seus turnos.*/

                int relationIndex = (shift.ordinal() * 2 + periodOffset) + (6 * weekOffset);

                if (relationMatrix[lessonPosition][relationIndex]) {

                    for (int professor : lesson.getProfessorId()) {
                        for (UnavailabilityConstraint constraint : lesson.getConstraints()) {
                            if (constraint.getId() == professor) {

                                int day = Math.floorDiv(relationIndex, 6);
                                if (day == constraint.getDay()) {

                                    int dayPeriod = shift.ordinal() * 2 + periodOffset;
                                    if (dayPeriod == constraint.getDayPeriod()) {
                                        avaliation += 3;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            periodOffset++;
        }
        return avaliation;
    }

    /**
     * Avalia se o cromossomo ainda tem um conflito de horários em específico.
     *
     * @param possibleChild      {@link Chromosome} gerado pela árvore e que será verificado.
     * @param dtoitc             {@link DTOITC} que contém os dados.
     * @param conflictsPositions vetor de inteiros de duas posições sendo o primeiro inteiro a posição no cromossomo
     *                           que está o conflito, e o segundo uma outra posição a ser verificada, já que o conflito
     *                           de horários está entre duas turmas diferentes.
     * @return true caso o conflito já foi resolvido e false caso contrário.
     * @throws ClassNotFoundException Caso uma entidade não seja encontrada.
     */
    public static boolean rateConflicts(Chromosome possibleChild, DTOITC dtoitc, int[] conflictsPositions) throws ClassNotFoundException {
        for (int conflictsPosition : conflictsPositions) {

            //Obtém o turno e professores envolvidos no conflito
            Shift currentShift = dtoitc.getShiftByLessonId(possibleChild.getGenes()[conflictsPosition]);

            int[] currentProfessors = dtoitc.getProfessorByLessonId(possibleChild.getGenes()[conflictsPosition]);

            //Vai de 10 em 10 para verificar as outras turmas que podem ter conflito
            for (int i = conflictsPosition % 10; i < possibleChild.getGenes().length; i += 10) {

                if (possibleChild.getGenes()[i] != 0 && i != conflictsPosition) {

                    Shift innerShift = dtoitc.getShiftByLessonId(possibleChild.getGenes()[i]);

                    if (currentShift.equals(innerShift)) {

                        for (int currentProfessor : currentProfessors) {

                            //Obtém o vetor dos professores a serem comparados
                            int[] innerProfessors = dtoitc.getProfessorByLessonId(possibleChild.getGenes()[i]);

                            for (int innerProfessor : innerProfessors) {

                                //Caso o mesmo professor esteja dando aula em duas turmas ao mesmo tempo
                                if (currentProfessor == innerProfessor) {

                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

}
