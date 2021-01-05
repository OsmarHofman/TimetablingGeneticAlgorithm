package br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;

import java.util.*;

public class PostProcessing {
    private Chromosome chromosome;

    public PostProcessing() {
    }


    public PostProcessing(Chromosome chromosome, DTOITC dtoitc, int initialAvaliation) throws ClassNotFoundException {
        this.chromosome = chromosome;
        Avaliation.rate(chromosome, dtoitc, initialAvaliation, false);
    }

    public boolean hasConflicts(int initialAvaliation) {
        return chromosome.getAvaliation() != initialAvaliation;
    }

    public Queue<Chromosome> resolveConflicts(Queue<Chromosome> chromosomeQueue, List<Integer> coursesWithConflicts, DTOITC dtoitc, ViolatedConstraint violatedConstraint, int perfectResult) throws ClassNotFoundException {
        int queueSize = chromosomeQueue.size();
        for (int i = 0; i < queueSize; i++) {
            this.chromosome = chromosomeQueue.peek();
            for (Integer courseWithConflict : coursesWithConflicts) {
                for (int j = 0; j < chromosome.getGenes().length; j += 10) {
                    if (dtoitc.isLessonInCourse(chromosome.getGenes()[j], courseWithConflict)) {
                        List<Chromosome> children = this.generateChildren(chromosome, j, violatedConstraint, dtoitc, perfectResult);
                        chromosomeQueue.addAll(children);
                    }
                }
            }
            chromosomeQueue.poll();
            Optional<Chromosome> finals = chromosomeQueue.stream().filter(x -> x.getAvaliation() == perfectResult).findFirst();
            if (finals.isPresent()) {
                Queue<Chromosome> bestChromosome = new LinkedList<>();
                bestChromosome.add(finals.get());
                return bestChromosome;
            }
        }


        return chromosomeQueue;
    }

    private List<Chromosome> generateChildren(Chromosome chromosome, int classInitialPosition, ViolatedConstraint violatedConstraint, DTOITC dtoitc, int perfectResult) throws ClassNotFoundException {
        List<Chromosome> children = new ArrayList<>();
        int conflictPosition = violatedConstraint.getChromossomePositionByDayPeriod() + classInitialPosition;
        for (int i = classInitialPosition; i < classInitialPosition + 10; i++) {
            if (i != conflictPosition && chromosome.getGenes()[i] != 0) {
                Chromosome possibleChild = new Chromosome(chromosome.getGenes(), chromosome.getAvaliation());
                int aux = possibleChild.getGenes()[conflictPosition];
                possibleChild.getGenes()[conflictPosition] = possibleChild.getGenes()[i];
                possibleChild.getGenes()[i] = aux;
                if (Avaliation.rateConflicts(possibleChild, dtoitc, new int[]{conflictPosition, i})) {
                    Avaliation.rate(possibleChild, dtoitc, perfectResult, false);
                    children.add(possibleChild);
                }
            }
        }
        return children;
    }


}
