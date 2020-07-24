package main.java;

import domain.Chromosome;
import domain.ifsc.Classes;
import domain.ifsc.Lesson;
import domain.itc.UnavailabilityConstraint;
import genetics.Avaliation;
import genetics.Crossover;
import genetics.Mutation;
import genetics.Selection;
import preprocessing.dataaccess.FileHandler;
import preprocessing.dataaccess.RetrieveIFSCData;
import preprocessing.interfaces.IFileHandler;
import preprocessing.model.EntitySchedule;
import preprocessing.model.ProfessorsScheduleCreation;
import util.ConvertFactory;
import util.DTOIFSC;
import util.DTOITC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {

        final int populationSize = 100;
        final int classSize = 20;
        final byte elitismPercentage = 10;
        final int joinSetPercentage = 60;
        final int crossPercentage = 60;
        final int mutationPercentage = 10;


        RetrieveIFSCData retrieveIFSCData = new RetrieveIFSCData();
        DTOIFSC dtoifsc = retrieveIFSCData.getAllData();
        ProfessorsScheduleCreation psc = new ProfessorsScheduleCreation(dtoifsc);


        EntitySchedule entitySchedule = new EntitySchedule(psc);

        //Lista que cada posição é uma lista de cursos
        List[] coursesSet = entitySchedule.createSet(joinSetPercentage);
        IFileHandler fileHandler = new FileHandler();
       // fileHandler.createReport(coursesSet, joinSetPercentage + "%");


        DTOITC fromIfSC = ConvertFactory.convertIFSCtoITC(dtoifsc);

        //Matriz de relação dos horarios
        boolean[][] scheduleRelation = new boolean[fromIfSC.getLessons().length][60];
        for (int i = 0; i < fromIfSC.getLessons().length; i++) {
            for (UnavailabilityConstraint iterationConstraints : fromIfSC.getLessons()[i].getConstraints()) {
                scheduleRelation[i][12 * iterationConstraints.getDay() + iterationConstraints.getDayPeriod()] = true;
            }
        }
        int iterationLimit = 0;
        // A partir daqui realizar processamento pensando em objetos distribuidos


        Chromosome[] population = new Chromosome[populationSize];
        //Inicializando população
        Arrays.setAll(population, i -> new Chromosome(fromIfSC.getCourses().length, classSize, fromIfSC.getLessons(), fromIfSC.getCourses()));


       // checkCourses(dtoifsc);
        Chromosome best = Chromosome.getBestChromosome(population);

        while (iterationLimit < 1000 && ((best.getAvaliation() < 3000) || best.isHasViolatedHardConstraint())) { // fazer verificação baseado no BOOLEAN do cromossomo, além das outras condições


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
            byte proportion = populationSize / elitismPercentage;
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


            best = Chromosome.getBestChromosome(population);


            System.out.println("\nIteração: " + iterationLimit);
            System.out.println("Avaliação: " + best.getAvaliation());
            System.out.println("Violou? " + best.isHasViolatedHardConstraint());
        }
        System.out.println(best.toString());
    }
    
    private static void checkCourses(DTOIFSC ifsc){
        List<Integer> morningCourses = new ArrayList<>();
        List<Integer> afternoonCourses = new ArrayList<>();
        List<Integer> nightCourses = new ArrayList<>();
        for (Classes classe: ifsc.getClasses()) {
            byte shift = convertTimeoffToShift(classe.getTimeoff());
            int count = 0;
            for (Lesson lesson :ifsc.getLessons()) {
                if (lesson.getClassesId() == classe.getId()){
                    count += lesson.getPeriodsPerWeek();
                }
            }
            if (shift == 0 && count > 20)
                morningCourses.add(classe.getId());
            else if(shift == 1 && count > 16)
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

}
