package br.edu.ifsc.TimetablingGeneticAlgorithm.genetics;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Lesson;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.DTOITC;

public class Avaliation {


    public static int rate(Chromosome chromosome, DTOITC dtoitc, boolean[][] relationMatrix) throws ClassNotFoundException {

        int avaliation = 5000;

        avaliation -= scheduleConflicts(chromosome, dtoitc);

        avaliation -= professorsUnavailabilities(chromosome, dtoitc, relationMatrix);

        avaliation -= curriculumCompactness(chromosome);

        return avaliation;

    }

    private static int scheduleConflicts(Chromosome chromosome, DTOITC dtoitc) throws ClassNotFoundException {
        int avaliation = 0;
        for (int i = 0; i < chromosome.getGenes().length; i++) {
            if (chromosome.getGenes()[i] != 0) {
                String[] currentProfessors = dtoitc.getProfessorByLessonId(chromosome.getGenes()[i], chromosome);
                for (int j = i + 20; j < chromosome.getGenes().length; j += 20) {
                    for (String currentProfessor : currentProfessors) {
                        if (chromosome.getGenes()[j] != 0) {
                            String[] iterationProfessors = dtoitc.getProfessorByLessonId(chromosome.getGenes()[j], chromosome);
                            for (String iterationProfessor : iterationProfessors) {
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

    private static int professorsUnavailabilities(Chromosome chromosome, DTOITC dtoitc, boolean[][] relationMatrix) throws ClassNotFoundException {
        int avaliation = 0;
        byte periodOffset = 0;
        byte weekOffset = 0;
        for (int i = 0; i < chromosome.getGenes().length; i++) {
            if (periodOffset > 3)
                periodOffset = 0;
            if (weekOffset > 19)
                weekOffset = 0;
            if (chromosome.getGenes()[i] != 0) {
                Lesson lesson = dtoitc.getLessonById(chromosome.getGenes()[i]);
                int lessonPosition = dtoitc.getLessonPosition(lesson.getLessonId());
                byte shift = dtoitc.getShiftByCourseId(lesson.getCourseId());
                if (relationMatrix[lessonPosition][((shift * 4 + periodOffset) + (12 * Math.floorDiv(weekOffset, 4)))]) {
                    avaliation += 5;
                }
                periodOffset++;
                weekOffset++;
            }
        }
        return avaliation;
    }

    private static int curriculumCompactness(Chromosome chromosome) {
        int avaliation = 0;
        for (int i = 0; i < chromosome.getGenes().length; i += 4) {
            for (int j = i; j < i + 3; j++) {
                if (chromosome.getGenes()[j] != 0) {
                    for (int k = j + 1; k < i + 4; k++) {
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
