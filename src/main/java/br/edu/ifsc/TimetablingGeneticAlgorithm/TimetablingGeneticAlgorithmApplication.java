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
import java.util.Stack;

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

        Chromosome chromosome = new Chromosome(new int[]{272, 777, 777, 272, 459, 273, 777, 459, 0, 0, 879, 415, 417, 416, 879, 416, 415, 879, 0, 0, 615, 880, 615, 618, 620, 620, 621, 617, 0, 0, 884, 882, 883, 885, 886, 882, 0, 0, 0, 0, 1026, 1028, 1027, 1031, 1026, 1030, 1029, 1029, 1030, 1027, 1032, 1033, 1034, 1033, 1034, 1032, 1035, 1035, 1036, 1036, 1038, 1041, 1038, 1037, 1025, 1037, 1041, 1039, 1039, 1025, 1044, 1042, 1043, 1044, 1045, 1044, 1044, 1043, 1045, 1042, 1009, 1009, 1011, 1011, 1017, 1012, 1014, 0, 0, 0, 1019, 1022, 1018, 1018, 1024, 1021, 1022, 345, 1023, 1020, 343, 343, 344, 740, 344, 347, 346, 342, 0, 0, 536, 532, 535, 537, 533, 532, 555, 859, 0, 0, 713, 713, 718, 742, 714, 716, 715, 717, 717, 0, 860, 866, 861, 863, 861, 862, 864, 865, 862, 0, 726, 727, 531, 724, 726, 727, 531, 724, 725, 725, 732, 728, 730, 729, 730, 728, 731, 729, 733, 732, 738, 738, 734, 735, 737, 737, 736, 734, 735, 739, 1002, 999, 998, 1001, 998, 999, 997, 996, 1001, 996, 990, 988, 989, 987, 987, 992, 989, 988, 987, 994, 659, 572, 573, 578, 578, 575, 571, 0, 0, 0, 649, 583, 649, 658, 583, 650, 584, 584, 0, 0, 587, 586, 589, 589, 586, 587, 0, 0, 0, 0, 590, 590, 591, 593, 593, 592, 591, 592, 0, 0, 570, 568, 563, 564, 569, 568, 566, 567, 565, 604, 980, 979, 981, 977, 984, 983, 982, 983, 978, 982, 697, 695, 697, 698, 667, 695, 693, 691, 0, 0, 699, 967, 702, 703, 705, 706, 702, 1047, 0, 0, 675, 768, 673, 672, 669, 670, 670, 673, 673, 669, 677, 680, 771, 682, 681, 772, 746, 770, 676, 746, 774, 685, 745, 684, 689, 683, 745, 773, 690, 689}, initialAvaliation);

        List<ViolatedConstraint> violatedConstraints = chromosome.checkScheduleConflicts(dtoitc, dtoifsc);

        Stack<Chromosome> stack = new Stack<>();

        stack.add(chromosome);

        PostProcessing postProcessing = new PostProcessing();

        Avaliation.rate(chromosome, dtoitc, initialAvaliation, true);

        long initialTime = System.currentTimeMillis();

        try {

            chromosome = postProcessing.depthSearchTree(stack, violatedConstraints, dtoitc, dtoifsc, 0, initialAvaliation);

        } catch (Exception e) {

            System.err.println(e.getMessage());

        } finally {

            System.out.println("\nNumero de filhos na fila: " + stack.size());

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


            System.out.println("---------------------------------Avaliação Pós pós-processamento------------------------------------------");
            System.out.println("Indisponibilidade:");
            chromosome.checkProfessorsUnavailabilities(dtoitc, dtoifsc, scheduleRelation);
            System.out.println("Conflitos:");
            chromosome.checkScheduleConflicts(dtoitc, dtoifsc);
        }
    }
}
