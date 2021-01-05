package br.edu.ifsc.TimetablingGeneticAlgorithm;

import br.edu.ifsc.TimetablingGeneticAlgorithm.dataaccess.RetrieveIFSCData;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.UnavailabilityConstraint;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOIFSC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing.Avaliation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing.PostProcessing;
import br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing.ViolatedConstraint;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model.PreProcessing;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model.ProfessorsScheduleCreation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.ConvertFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@SpringBootApplication
public class TimetablingGeneticAlgorithmApplication {

    public static void main(String[] args) throws ClassNotFoundException {

        //SpringApplication.run(TimetablingGeneticAlgorithmApplication.class, args);
//        GeneticAlgorithm ga = new GeneticAlgorithm();
//        ga.process("src//assets//configuracoes.txt");

        RetrieveIFSCData retrieveIFSCData = new RetrieveIFSCData();
        DTOIFSC dtoifsc = retrieveIFSCData.getAllData();

        //Cria os conjuntos do pré-processamento
        ProfessorsScheduleCreation psc = new ProfessorsScheduleCreation(dtoifsc);
        PreProcessing preProcessing = new PreProcessing(psc);
        preProcessing.createSet(0);


        //Criando Modelagem do ITC
        DTOITC dtoitc = ConvertFactory.convertIFSCtoITC(dtoifsc);
        //FIXME alterar initialavaliation para pegar do arquivo de configuração
        int initialAvaliation = 1500;
        Chromosome chromosome = new Chromosome(new int[]{272, 272, 459, 777, 273, 777, 777, 459, 0, 0, 879, 879, 416, 415, 879, 416, 415, 417, 0, 0, 880, 617, 620, 615, 620, 621, 615, 618, 0, 0, 885, 883, 886, 882, 884, 882, 0, 0, 0, 0, 1030, 1028, 1027, 1030, 1026, 1026, 1027, 1029, 1031, 1029, 1033, 1036, 1034, 1032, 1035, 1035, 1032, 1036, 1034, 1033, 1041, 1041, 1038, 1037, 1038, 1037, 1039, 1039, 1025, 1025, 1044, 1042, 1043, 1044, 1044, 1044, 1043, 1045, 1045, 1042, 1011, 1011, 1009, 1009, 1017, 1012, 1014, 0, 0, 0, 345, 1020, 1018, 1018, 1024, 1019, 1023, 1021, 1022, 1022, 740, 346, 347, 343, 344, 342, 343, 344, 0, 0, 532, 533, 555, 859, 536, 532, 537, 535, 0, 0, 713, 713, 714, 717, 716, 742, 715, 717, 718, 0, 860, 865, 864, 862, 866, 861, 861, 862, 863, 0, 725, 725, 724, 726, 531, 726, 531, 724, 727, 727, 728, 730, 729, 729, 731, 733, 730, 728, 732, 732, 735, 738, 734, 735, 739, 734, 738, 737, 736, 737, 997, 999, 999, 996, 998, 998, 1002, 1001, 1001, 996, 988, 992, 987, 988, 994, 987, 990, 989, 989, 987, 659, 572, 573, 578, 578, 575, 571, 0, 0, 0, 649, 649, 658, 584, 583, 650, 584, 583, 0, 0, 589, 586, 587, 589, 586, 587, 0, 0, 0, 0, 591, 592, 590, 591, 593, 590, 593, 592, 0, 0, 570, 564, 565, 566, 568, 569, 604, 567, 563, 568, 979, 980, 978, 983, 981, 977, 982, 984, 982, 983, 697, 695, 667, 693, 695, 697, 691, 698, 0, 0, 967, 702, 703, 702, 705, 706, 699, 1047, 0, 0, 768, 675, 672, 670, 673, 673, 670, 669, 673, 669, 746, 677, 771, 680, 676, 770, 682, 681, 746, 772, 774, 684, 689, 689, 683, 745, 690, 745, 773, 685}, initialAvaliation);
        List<ViolatedConstraint> violatedConstraints = chromosome.checkScheduleConflicts(dtoitc, dtoifsc);
        Queue<Chromosome> queue = new LinkedList<>();
        queue.add(chromosome);
        PostProcessing postProcessing = new PostProcessing();
        Avaliation.rate(chromosome, dtoitc, initialAvaliation, true);
        long initialTime = System.currentTimeMillis();
        for (ViolatedConstraint violatedConstraint : violatedConstraints) {
            System.out.printf("\nResolvendo constraint: " + violatedConstraint.toString());
            List<Integer> sortedConflicts = violatedConstraint.getConflictedClassWithGreaterAvailableTime(dtoifsc, queue.peek());

            queue = postProcessing.resolveConflicts(queue, sortedConflicts, dtoitc, violatedConstraint, initialAvaliation);

            //TODO verificar pq esta caindo no break na primeira constraint resolvida
            if (queue.size() == 1 && queue.peek().getAvaliation() == initialAvaliation)
                break;

        }
        long endTime = System.currentTimeMillis();

        System.out.println("Tempo final:" + (endTime - initialTime));
        /*Matriz de relação dos horarios
        Sendo que 30 é o número de períodos no dia * dias na semana, ou seja, 6 * 5 = 30
        */
        boolean[][] scheduleRelation = new boolean[dtoitc.getLessons().length][30];
        for (int j = 0; j < dtoitc.getLessons().length; j++) {
            for (UnavailabilityConstraint iterationConstraints : dtoitc.getLessons()[j].getConstraints()) {
                scheduleRelation[j][6 * iterationConstraints.getDay() + iterationConstraints.getDayPeriod()] = true;
            }
        }


        chromosome = queue.peek();
        System.out.println("---------------------------------Avaliação Pós pós-processamento------------------------------------------");
        System.out.println("Indisponibilidade:");
        chromosome.checkProfessorsUnavailabilities(dtoitc, dtoifsc, scheduleRelation);
        System.out.println("Conflitos:");
        chromosome.checkScheduleConflicts(dtoitc, dtoifsc);
    }

}
