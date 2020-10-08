package br.edu.ifsc.TimetablingGeneticAlgorithm.genetics;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Lesson;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Shift;

public class Avaliation {


    /**
     * Realiza a avaliação de um cromossomo.
     *
     * @param chromosome     {@link Chromosome} que será avaliado.
     * @param dtoitc         {@link DTOITC} que contém todas informações relativas a matérias, cursos, salas,
     *                       professores e restrições.
     * @param relationMatrix matriz que representa as indisponibilidades dos professores.
     * @return int que representa a avaliação do cromossomo.
     * @throws ClassNotFoundException quando não acha um professor ou matéria dentro do {@code dtoitc}.
     */
    public static int rate(Chromosome chromosome, DTOITC dtoitc, boolean[][] relationMatrix) throws ClassNotFoundException {

        int avaliation = 5000;

        avaliation -= scheduleConflicts(chromosome, dtoitc);

        avaliation -= professorsUnavailabilities(chromosome, dtoitc, relationMatrix);

        //avaliation -= curriculumCompactness(chromosome);

        return avaliation;

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
                //obtém o vetor dos professores
                String[] currentProfessors = dtoitc.getProfessorByLessonId(chromosome.getGenes()[i]);

                //vai de 10 em 10 posições, ou seja, de turma em turma
                for (int j = i + 10; j < chromosome.getGenes().length; j += 10) {
                    for (String currentProfessor : currentProfessors) {

                        //Caso possa ser dado aula nesse dia. Dias não disponíveis tem valor 0.
                        if (chromosome.getGenes()[j] != 0) {

                            //obtém o vetor dos professores a serem comparados
                            String[] iterationProfessors = dtoitc.getProfessorByLessonId(chromosome.getGenes()[j]);

                            for (String iterationProfessor : iterationProfessors) {

                                //caso o mesmo professor esteja dando aula em duas turmas ao mesmo tempo
                                if (currentProfessor.equals(iterationProfessor)) {
                                    avaliation += 10;
                                    chromosome.setHasViolatedHardConstraint(true);
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
        //valor que representa o deslocamento do dia, ou seja, são duas aulas por dia, então varia entre 0 e 1.
        byte periodOffset = 0;

        //valor que representa o deslocamento da semana, ou seja, são dez aulas por semana, então varia entre 0 e 9.
        byte weekOffset = 0;

        for (int i = 0; i < chromosome.getGenes().length; i++) {
            if (periodOffset > 1)
                periodOffset = 0;
            if (weekOffset > 9)
                weekOffset = 0;

            //Caso possa ser dado aula nesse dia. Dias não disponíveis tem valor 0.
            if (chromosome.getGenes()[i] != 0) {
                Lesson lesson = dtoitc.getLessonById(chromosome.getGenes()[i]);

                //por conta da matriz de relação, é preciso obter qual a posição do Lesson atual
                int lessonPosition = dtoitc.getLessonPosition(lesson.getLessonId());

                //obtém o turno do Lesson
                Shift shift = dtoitc.getShiftByCourseId(lesson.getCourseId());

                //cálculo para obter o boolean que representa a disponibilidade do professor na matriz. Sendo que
                // os valores "2" representam o número de aulas por dia, e o "6", as aulas com seus turnos.

                if (relationMatrix[lessonPosition][((shift.ordinal() * 2 + periodOffset) + (6 * Math.floorDiv(weekOffset, 2)))]) {
                    avaliation += 5;
                }
                periodOffset++;
                weekOffset++;
            }
        }
        return avaliation;
    }

    /**
     * Avaliação de compacidade de um curso, ou seja, se as matérias iguais em um dia estão em sequência uma da outra.
     *
     * @param chromosome {@link Chromosome} que será avaliado.
     * @return a penalização do cromossomo, ou seja, o valor numérico que representa a quantidade de vezes que essa
     * restrição foi violada, multiplicado pelo peso da violação. Cada violação dessa restrição tem peso 2.
     */
    private static int curriculumCompactness(Chromosome chromosome) {
        //Esse método foi feito quando a modelagem tinha 4 aulas por dia.
        int avaliation = 0;

        //só até quatro pois verifica só de um dia.
        for (int i = 0; i < chromosome.getGenes().length; i += 4) {
            //só é necessário fazer três comparações, não é preciso verificar a última aula para ver se tem alguma
            //em sequência a ela.
            for (int j = i; j < i + 3; j++) {
                if (chromosome.getGenes()[j] != 0) {
                    //pega as aulas seguintes
                    for (int k = j + 1; k < i + 4; k++) {
                        //Se tiver alguma igual e que não está em sequência
                        if (chromosome.getGenes()[j] == chromosome.getGenes()[k] && k - j > 1) {
                            avaliation += 2;
                        } else if (chromosome.getGenes()[j] == chromosome.getGenes()[k] && k - j == 1) {
                            break;
                        }
                    }
                }
            }

        }
        return avaliation;
    }

}
