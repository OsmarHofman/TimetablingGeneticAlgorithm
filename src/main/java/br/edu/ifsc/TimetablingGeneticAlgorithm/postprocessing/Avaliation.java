package br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Shift;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;

import java.util.concurrent.CountDownLatch;

public class Avaliation {

    /**
     * Realiza a avaliação de um cromossomo.
     *
     * @param chromosome {@link Chromosome} que será avaliado.
     * @param dtoitc     {@link DTOITC} que contém todas informações relativas a matérias, cursos, salas,
     *                   professores e restrições.
     */
    public static void rate(Chromosome chromosome, DTOITC dtoitc, int initialAvaliation) throws ClassNotFoundException {
        int avaliation = initialAvaliation;

        chromosome.setHasViolatedHardConstraint(false);

        avaliation -= scheduleConflicts(chromosome, dtoitc);

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

    public static int getInitialAvaliation(int coursesSize) {

        //Valores definidos pelo professor
        if (coursesSize <= 3) return 500;
        else if (coursesSize <= 6) return 1000;
        else if (coursesSize <= 10) return 1500;
        return 200 * coursesSize;
    }
}
