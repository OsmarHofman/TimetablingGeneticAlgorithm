package genetics;

import domain.Chromosome;
import domain.itc.Lesson;
import util.DTOITC;

public class Avaliation {


    public static void rate(Chromosome chromosome, DTOITC dtoitc, boolean[][]relationMatrix) throws ClassNotFoundException {

        int avaliation = 0;

        avaliation = scheduleConflicts(chromosome, avaliation, dtoitc);

        avaliation =  professorsUnavailabilities(chromosome,avaliation,dtoitc,relationMatrix);

        avaliation = missingLessonsInCourse(chromosome,avaliation,dtoitc);

    }

    private static int scheduleConflicts(Chromosome chromosome, int avaliation, DTOITC dtoitc) throws ClassNotFoundException {
        for (int i = 0; i < chromosome.getGenes().length; i++) {
            String currentProfessor = dtoitc.getProfessorByLessonId(chromosome.getGenes()[i]);
            for (int j = i + 20; j < chromosome.getGenes().length; j += 20) {
                String iterationProfessor = dtoitc.getProfessorByLessonId(chromosome.getGenes()[j]);
                if (currentProfessor.equals(iterationProfessor)) {
                    avaliation += 10;
                }
            }
        }
        return avaliation;
    }

    private static int professorsUnavailabilities(Chromosome chromosome, int avaliation, DTOITC dtoitc, boolean[][] relationMatrix) throws ClassNotFoundException {
        byte offset = 0;
        for (int i = 0; i < chromosome.getGenes().length; i++) {
            if (offset > 3)
                offset = 0;
            Lesson lesson = dtoitc.getLessonById(chromosome.getGenes()[i]);
            byte shift = dtoitc.getShiftByCourseId(lesson.getCourseId());
            if (relationMatrix[i][((shift*4 + offset) + (12* Math.floorDiv(i,4)))]){
                avaliation+=5;
            }
            offset++;
        }
        return avaliation;
    }

    private static int missingLessonsInCourse(Chromosome chromosome, int avaliation, DTOITC dtoitc) throws ClassNotFoundException {

        for (int i = 0; i < chromosome.getGenes().length; i++) {
            Lesson lesson = dtoitc.getLessonById(chromosome.getGenes()[i]);
            int qtdPerWeek = lesson.getMinWorkingDays();
            //TODO fazer esse regra após inicializar o cromossomo corretamente
        }
        return avaliation;
    }
}
