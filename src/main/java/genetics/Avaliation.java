package genetics;

import domain.Chromosome;
import domain.itc.Lesson;
import util.DTOITC;

public class Avaliation {


    public static int rate(Chromosome chromosome, DTOITC dtoitc, boolean[][]relationMatrix) throws ClassNotFoundException {

        int avaliation = 0;

        avaliation = scheduleConflicts(chromosome, avaliation, dtoitc);

        avaliation =  professorsUnavailabilities(chromosome,avaliation,dtoitc,relationMatrix);

        return avaliation;

    }

    private static int scheduleConflicts(Chromosome chromosome, int avaliation, DTOITC dtoitc) throws ClassNotFoundException {
        for (int i = 0; i < chromosome.getGenes().length; i++) {
            String currentProfessor = dtoitc.getProfessorByLessonId(chromosome.getGenes()[i]);
            for (int j = i + 20; j < chromosome.getGenes().length; j += 20) {
                String iterationProfessor = dtoitc.getProfessorByLessonId(chromosome.getGenes()[j]);
                if (currentProfessor.equals(iterationProfessor)) {
                    avaliation += 10;
                    chromosome.setHasViolatedHardConstraint(true);
                }
            }
        }
        return avaliation;
    }

    private static int professorsUnavailabilities(Chromosome chromosome, int avaliation, DTOITC dtoitc, boolean[][] relationMatrix) throws ClassNotFoundException {
        byte periodOffset = 0;
        byte weekOffset = 0;
        for (int i = 0; i < chromosome.getGenes().length; i++) {
            if (periodOffset > 3)
                periodOffset = 0;
            if (weekOffset > 19)
                weekOffset = 0;
            Lesson lesson = dtoitc.getLessonById(chromosome.getGenes()[i]);
            int lessonPosition = dtoitc.getLessonPosition(lesson.getLessonId());
            byte shift = dtoitc.getShiftByCourseId(lesson.getCourseId());
            if (relationMatrix[lessonPosition][((shift*4 + periodOffset) + (12* Math.floorDiv(weekOffset,4)))]){
                avaliation+=5;
            }
            periodOffset++;
            weekOffset++;
        }
        return avaliation;
    }

}
