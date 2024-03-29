package br.edu.ifsc.TimetablingGeneticAlgorithm.genetics;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Lesson;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.UnavailabilityConstraint;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Shift;

import java.util.concurrent.CountDownLatch;

public class Avaliation {


    /**
     * Avaliação de uma população feita com {@link Thread}.
     *
     * @param populationSize    inteiro que representa o tamanho da população.
     * @param coresNumber       inteiro que representa o quanto núcleos do computador disponíveis.
     * @param population        array de {@link Chromosome} que representa a população de cromossomos.
     * @param set               {@link DTOITC} que contém os dados dos cromossomos.
     * @param relationMatrix    array de booleanos que contém a matriz com todas as restrições dos professores.
     * @param initialAvaliation inteiro que representa a avaliação inicial para a função de avaliação.
     * @throws InterruptedException Erro quando alguma thread é interrompida de alguma forma.
     */
    public static void threadRate(int populationSize, int coresNumber, Chromosome[] population, DTOITC set, boolean[][] relationMatrix, int initialAvaliation) throws InterruptedException {

        //Valor que representa quantos cromossomos cada thread irá processar
        int range = (int) Math.ceil(populationSize / (double) coresNumber);

        /*A última thread irá ter um número de cromossomos diferentes para caso a divisão de cromossomos entre
         * as threads não seja igual. Ex.: população de 100 cromossomos para 6 threads - ficaria 17 cromossomos para
         * as 5 primeiras threads, ou seja, um total de 85 cromossomos, então a última thread ficaria com 15 ao invés
         * de 17 */
        int lastCoreRange = populationSize - (range * (coresNumber - 1));

        //Classe que controla o término das threads
        CountDownLatch latch = new CountDownLatch(coresNumber);

        for (int j = 0; j < coresNumber; j++) {

            //Verificação da execução da última thread
            int innerRange = (j == coresNumber - 1) ? lastCoreRange : range;

            //Variável para poder obter o limite inferior, ou seja, a partir de qual cromossomo irá começar a processar
            int infLimit = j * range;

            //Porção da população que a thread irá processar, com seu tamanho correto
            Chromosome[] populationSlice = new Chromosome[innerRange];

            //Copia os cromossomos da população original para a porção que será processada
            System.arraycopy(population, infLimit, populationSlice, 0, populationSlice.length);

            //Avaliando a porção de cromossomos
            Avaliation.rate(populationSlice, set, relationMatrix, initialAvaliation, latch);

        }

        //Aguarda todas as threads concluírem para continuar o programa
        latch.await();
    }


    /**
     * Realiza a avaliação de um cromossomo.
     *
     * @param population     População de {@link Chromosome}s que será avaliado.
     * @param dtoitc         {@link DTOITC} que contém todas informações relativas a matérias, cursos, salas,
     *                       professores e restrições.
     * @param relationMatrix matriz que representa as indisponibilidades dos professores.
     */
    private static void rate(Chromosome[] population, DTOITC dtoitc, boolean[][] relationMatrix, int initialAvaliation, CountDownLatch latch) {
        new Thread(() -> {
            int avaliation;

            for (Chromosome chromosome : population) {
                avaliation = initialAvaliation;

                chromosome.setHasViolatedHardConstraint(false);

                try {
                    avaliation -= scheduleConflicts(chromosome, dtoitc);
                } catch (ClassNotFoundException e) {
                    System.err.println("Erro na avaliação de choque de horários!");
                    e.printStackTrace();
                }

                try {
                    avaliation -= professorsUnavailabilities(chromosome, dtoitc, relationMatrix);
                } catch (ClassNotFoundException e) {
                    System.err.println("Erro na avaliação de indisponibilidade de professores!");
                    e.printStackTrace();
                }

                chromosome.setAvaliation(avaliation);

            }

            //Contador interno para identificar se a thread já foi concluída
            latch.countDown();
        }).start();
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

                                //Fazendo o cálculo para obter o dia do relationIndex
                                int day = Math.floorDiv(relationIndex, 6);
                                if (day == constraint.getDay()) {

                                    //Fazendo o cálculo para obter o turno
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
     * Avaliação de compacidade de um curso, ou seja, se as matérias iguais em um dia estão em sequência uma da outra.
     *
     * @param chromosome {@link Chromosome} que será avaliado.
     * @return a penalização do cromossomo, ou seja, o valor numérico que representa a quantidade de vezes que essa
     * restrição foi violada, multiplicado pelo peso da violação. Cada violação dessa restrição tem peso 2.
     */
    private static int curriculumCompactness(Chromosome chromosome) {

        //Esse método foi feito quando a modelagem tinha 4 aulas por dia.
        int avaliation = 0;

        //Só até quatro pois verifica só de um dia.
        for (int i = 0; i < chromosome.getGenes().length; i += 4) {

            /*Só é necessário fazer três comparações, não é preciso verificar a última aula para ver se tem alguma
             * em sequência a ela.*/
            for (int j = i; j < i + 3; j++) {
                if (chromosome.getGenes()[j] != 0) {

                    //Obtém as aulas seguintes
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

    /**
     * Obtém o valor inicial da avaliação de acordo com o número de cursos do conjunto.
     *
     * @param coursesSize inteiro que representa o número de cursos presentes em um conjunto.
     * @return inteiro que representa a avaliação inicial utilizada para a função de avaliação.
     */
    public static int getInitialAvaliation(int coursesSize) {

        //Valores definidos pelo professor
        if (coursesSize <= 3) return 500;
        else if (coursesSize <= 6) return 1000;
        else if (coursesSize <= 10) return 1500;
        return 200 * coursesSize;
    }
}
