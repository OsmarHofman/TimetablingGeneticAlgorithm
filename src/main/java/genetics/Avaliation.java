package genetics;

import domain.Chromosome;
import domain.itc.Lesson;
import util.DTOITC;

public class Avaliation {


    public static void rate(Chromosome chromosome, DTOITC dtoitc) throws ClassNotFoundException {

        int avaliation = 0;

        avaliation = scheduleConflicts(chromosome, avaliation, dtoitc);

        avaliation =  professorsUnavailabilities(chromosome,avaliation,dtoitc);

    }

    private static int scheduleConflicts(Chromosome chromosome, int avaliation, DTOITC dtoitc) throws ClassNotFoundException {
        for (int i = 0; i < chromosome.getGenes().length; i++) {
            String currentProfessor = dtoitc.getProfessorByLessonId(chromosome.getGenes()[i]);
            for (int j = i + 20; j < chromosome.getGenes().length; j += 20) {
                String iterationProfessor = dtoitc.getProfessorByLessonId(chromosome.getGenes()[j]);
                if (currentProfessor.equals(iterationProfessor)) {
                    avaliation += 50;
                }
            }
        }
        return avaliation;
    }

    private static int professorsUnavailabilities(Chromosome chromosome, int avaliation, DTOITC dtoitc) throws ClassNotFoundException {
        for (int i = 0; i < chromosome.getGenes().length; i++) {
            Lesson  lesson = dtoitc.getLessonById(chromosome.getGenes()[i]);
            //TODO verificar lesson referente a posicao no cromossomo e avaliar cada constraint a partir do day e dayPeriod.
            // Cada posicao do vetor de cromossomo pode representar manha/tarde/noite é preciso fazer essa validação
        }
        return avaliation;
    }


}
