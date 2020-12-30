package br.edu.ifsc.TimetablingGeneticAlgorithm;

import br.edu.ifsc.TimetablingGeneticAlgorithm.dataaccess.RetrieveIFSCData;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOIFSC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing.PostProcessing;
import br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing.ViolatedConstraint;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model.PreProcessing;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model.ProfessorsScheduleCreation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.ConvertFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class TimetablingGeneticAlgorithmApplication {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

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
        Chromosome chromosome = new Chromosome(new int[]{459, 272, 459, 272, 777, 273, 777, 777, 0, 0, 879, 879, 879, 415, 417, 416, 415, 416, 0, 0, 615, 620, 615, 618, 880, 620, 617, 621, 0, 0, 883, 882, 885, 886, 882, 884, 0, 0, 0, 0, 1030, 1026, 1027, 1029, 1026, 1028, 1029, 1027, 1031, 1030, 1033, 1032, 1034, 1036, 1033, 1036, 1035, 1035, 1032, 1034, 1039, 1041, 1037, 1037, 1038, 1038, 1025, 1041, 1039, 1025, 1043, 1042, 1042, 1044, 1044, 1045, 1044, 1044, 1045, 1043, 1009, 1011, 1011, 1014, 1017, 1012, 1009, 0, 0, 0, 1023, 1022, 1020, 1024, 1022, 345, 1018, 1019, 1021, 1018, 343, 346, 740, 343, 342, 344, 347, 344, 0, 0, 533, 537, 532, 535, 532, 536, 859, 555, 0, 0, 713, 717, 714, 715, 742, 713, 717, 718, 716, 0, 864, 862, 863, 861, 860, 862, 861, 865, 866, 0, 724, 724, 725, 726, 726, 725, 531, 531, 727, 727, 731, 728, 729, 730, 732, 729, 730, 733, 732, 728, 734, 737, 735, 737, 736, 735, 734, 739, 738, 738, 999, 999, 997, 998, 996, 1002, 996, 1001, 1001, 998, 994, 988, 992, 987, 989, 989, 987, 990, 987, 988, 571, 575, 573, 659, 578, 578, 572, 0, 0, 0, 650, 649, 649, 584, 583, 583, 584, 658, 0, 0, 589, 587, 587, 589, 586, 586, 0, 0, 0, 0, 591, 591, 590, 593, 590, 592, 593, 592, 0, 0, 570, 568, 567, 566, 563, 564, 604, 569, 568, 565, 979, 980, 977, 978, 981, 983, 982, 984, 983, 982, 691, 695, 667, 697, 697, 695, 698, 693, 0, 0, 702, 967, 702, 703, 705, 706, 699, 1047, 0, 0, 768, 675, 672, 673, 669, 673, 669, 670, 670, 673, 680, 677, 771, 676, 682, 681, 772, 746, 746, 770, 773, 684, 683, 774, 689, 690, 745, 745, 685, 689}, 0);
        List<ViolatedConstraint> violatedConstraints = chromosome.checkScheduleConflicts(dtoitc, dtoifsc);
        PostProcessing postProcessing = new PostProcessing();
        for (ViolatedConstraint violatedConstraint : violatedConstraints) {
            List<Integer> sortedConflicts = violatedConstraint.getConflictedClassWithGreaterAvailableTime(dtoifsc, chromosome);
            chromosome = postProcessing.resolveConflicts(chromosome, sortedConflicts, dtoitc, violatedConstraint);

        }

    }

}
