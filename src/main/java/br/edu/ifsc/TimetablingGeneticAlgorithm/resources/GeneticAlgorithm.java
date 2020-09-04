package br.edu.ifsc.TimetablingGeneticAlgorithm.resources;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Classes;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Teacher;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.UnavailabilityConstraint;
import br.edu.ifsc.TimetablingGeneticAlgorithm.genetics.Avaliation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.genetics.Crossover;
import br.edu.ifsc.TimetablingGeneticAlgorithm.genetics.Mutation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.genetics.Selection;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.dataaccess.RetrieveIFSCData;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model.EntitySchedule;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model.ProfessorsScheduleCreation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeneticAlgorithm {

    public List<DTOSchedule> process(String path) throws IOException, ClassNotFoundException {

        int[] config = ConfigReader.readConfiguration(path);
        final int populationSize = config[0];
        final int classSize = config[1];
        final int elitismPercentage = config[2];
        final int crossPercentage = config[3];
        final int mutationPercentage = config[4];
        final int joinSetPercentage = config[5];
        final int geracoes = config[6];


        RetrieveIFSCData retrieveIFSCData = new RetrieveIFSCData();
        DTOIFSC dtoifsc = retrieveIFSCData.getAllData();
        //TODO pre-processar para não ter turmas com numero de aulas impar

        ProfessorsScheduleCreation psc = new ProfessorsScheduleCreation(dtoifsc);

        //slaves(dtoifsc);
        EntitySchedule entitySchedule = new EntitySchedule(psc);

        //Lista que cada posição é uma lista de cursos
        List[] coursesSet = entitySchedule.createSet(joinSetPercentage);
        // IFileHandler fileHandler = new FileHandler();
        // fileHandler.createReport(coursesSet, joinSetPercentage + "%");


        DTOITC fromIfSC = ConvertFactory.convertIFSCtoITC(dtoifsc);

        /*Matriz de relação dos horarios
        Sendo que 30 é o número de períodos no dia * dias na semana, ou seja, 6 * 5 = 30
        */

        //TODO verificar onde colocamos os constraints do curso
        boolean[][] scheduleRelation = new boolean[fromIfSC.getLessons().length][30];
        for (int i = 0; i < fromIfSC.getLessons().length; i++) {
            for (UnavailabilityConstraint iterationConstraints : fromIfSC.getLessons()[i].getConstraints()) {
                scheduleRelation[i][6 * iterationConstraints.getDay() + iterationConstraints.getDayPeriod()] = true;
            }
        }
        int iterationLimit = 0;

        // A partir daqui realizar processamento pensando em objetos distribuidos

        Chromosome[] population = new Chromosome[populationSize];
        //Inicializando população
        Arrays.setAll(population, i -> new Chromosome(fromIfSC.getCourses().length, classSize, fromIfSC.getLessons(), fromIfSC.getCourses(),dtoifsc));


        // checkCourses(dtoifsc);
        Chromosome localBest = Chromosome.getBestChromosome(population);
        Chromosome globalBestChromosome = localBest;
        long startTime = System.currentTimeMillis();

        //FIXME alterar 2 para "geracoes"
        while (iterationLimit < 2 && ((localBest.getAvaliation() < 3800) || localBest.isHasViolatedHardConstraint())) { // fazer verificação baseado no BOOLEAN do cromossomo, além das outras condições


            for (Chromosome chromosome : population) {
                chromosome.setHasViolatedHardConstraint(false);
                chromosome.setAvaliation(Avaliation.rate(chromosome, fromIfSC, scheduleRelation));
            }

            //função de avaliacao acumulada
            //System.out.println("\nCalculando FaA...");
            int[] ratingHandler = new int[populationSize];
            int faA = 0;
            for (int i = 0; i < population.length; i++) {
                faA += population[i].getAvaliation();
                ratingHandler[i] = faA;
            }

            //Seleção por elitismo
            byte proportion = (byte) (populationSize / elitismPercentage);
            Chromosome[] eliteChromosomes = Selection.elitism(population, proportion);


            //Seleção por roleta
            Chromosome[] newCouples = Selection.roulleteWheel(population, ratingHandler, faA, proportion);


            //Crossover
            Chromosome[] crossedChromosomes = Crossover.cross(newCouples, classSize, crossPercentage);

            //Unindo elitismo com selecao por roleta
            Chromosome[] newGeneration = new Chromosome[populationSize];
            System.arraycopy(crossedChromosomes, 0, newGeneration, 0, crossedChromosomes.length);

            System.arraycopy(eliteChromosomes, 0, newGeneration, crossedChromosomes.length, eliteChromosomes.length);

            //Mutação
            Mutation.swapMutation(newGeneration, classSize, mutationPercentage);

            iterationLimit++;


            localBest = Chromosome.getBestChromosome(population);
            if (globalBestChromosome.getAvaliation() < localBest.getAvaliation())
                globalBestChromosome = localBest;


//            System.out.println("\nIteração: " + iterationLimit);
//            System.out.println("Avaliação: " + localBest.getAvaliation());
//            System.out.println("Violou: " + localBest.isHasViolatedHardConstraint());
        }
        long endTime = System.currentTimeMillis();
        System.out.println(globalBestChromosome.toString());
        System.out.println("tempo Final: " + (endTime - startTime));
        System.out.println("iteração: " + iterationLimit);
        return DTOSchedule.convertChromosome(globalBestChromosome, dtoifsc, fromIfSC);
    }

    private static void checkCourses(DTOIFSC ifsc) {
        List<Integer> morningCourses = new ArrayList<>();
        List<Integer> afternoonCourses = new ArrayList<>();
        List<Integer> nightCourses = new ArrayList<>();
        for (Classes classe : ifsc.getClasses()) {
            byte shift = convertTimeoffToShift(classe.getTimeoff());
            int count = 0;
            for (Lesson lesson : ifsc.getLessons()) {
                if (lesson.getClassesId() == classe.getId()) {
                    count += lesson.getPeriodsPerWeek();
                }
            }
            if (shift == 0 && count > 20)
                morningCourses.add(classe.getId());
            else if (shift == 1 && count > 16)
                afternoonCourses.add(classe.getId());
            else if (shift == 2 && count > 20)
                nightCourses.add(classe.getId());
        }

        System.out.println("manha: " + morningCourses.toString());
        System.out.println("tarde: " + afternoonCourses.toString());
        System.out.println("noite: " + nightCourses.toString());
    }


    private static byte convertTimeoffToShift(String timeoff) {
        String[] days = timeoff.replace(".", "").split(",");
        if (days[0].charAt(0) == '1')
            return 0;
        else if (days[0].charAt(4) == '1')
            return 1;
        return 2;

    }

    private static void slaves(DTOIFSC dtoifsc) {
        List<Teacher> slaves = new ArrayList<>();
        for (Teacher teacher : dtoifsc.getProfessors()) {
            int count = 0;
            for (Lesson lesson : dtoifsc.getLessons()) {
                for (int i = 0; i < lesson.getTeacherId().length; i++) {
                    if (lesson.getTeacherId()[i] == teacher.getId())
                        count++;
                }
            }
            if (count > 20) {
                slaves.add(teacher);
            }
        }
        System.out.println(slaves.toString());
    }

}
