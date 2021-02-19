package br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOIFSC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;

import java.util.List;
import java.util.Stack;

public class PostProcessing {
    private final DTOITC dtoitc;
    private final DTOIFSC dtoifsc;
    private Chromosome bestChromosome;

    public PostProcessing(Chromosome chromosome, DTOITC dtoitc, DTOIFSC dtoifsc, int initialAvaliation) throws ClassNotFoundException {
        this.dtoitc = dtoitc;
        this.dtoifsc = dtoifsc;
        bestChromosome = new Chromosome(chromosome.getGenes().length);
        Avaliation.rate(chromosome, dtoitc, initialAvaliation, false);
    }

    public boolean hasConflicts(Chromosome chromosome, int initialAvaliation) {
        return chromosome.getAvaliation() != initialAvaliation;
    }

    public Chromosome resolveConflicts(Chromosome chromosome, int initialAvaliation) throws ClassNotFoundException {
        List<ViolatedConstraint> violatedConstraints = chromosome.checkScheduleConflicts(this.dtoifsc, this.dtoitc);

        Stack<Chromosome> stack = new Stack<>();

        stack.add(chromosome);

        Avaliation.rate(chromosome, this.dtoitc, initialAvaliation, true);

        return this.depthSearchTree(stack, violatedConstraints, 0, initialAvaliation, 0);

    }

    public Chromosome depthSearchTree(Stack<Chromosome> chromosomeStack, List<ViolatedConstraint> violatedConstraints, int violatedConstraintsIndex, int perfectResult, int geneIndex) throws ClassNotFoundException {
        if (chromosomeStack.empty()){
            return bestChromosome;
        }

        Chromosome chromosome = chromosomeStack.peek();

        if (chromosome.getAvaliation() > bestChromosome.getAvaliation())
            bestChromosome = new Chromosome(chromosome.getGenes(),chromosome.getAvaliation(),chromosome.isHasViolatedHardConstraint());

        if (chromosome.getAvaliation() == perfectResult)
            return chromosome;

        ViolatedConstraint currentViolatedConstraint = violatedConstraints.get(violatedConstraintsIndex);
        List<Integer> sortedConflicts = currentViolatedConstraint.getConflictedClassWithGreaterAvailableTime(dtoifsc, chromosome);
        for (Integer courseWithConflict : sortedConflicts) {

            for (int j = 0; j < chromosome.getGenes().length; j += 10) {

                if (this.dtoitc.isLessonInCourse(chromosome.getGenes()[j], courseWithConflict)) {

                    Chromosome child = generateChild(chromosome, j, currentViolatedConstraint, perfectResult, geneIndex);

                    if (child == null) {

                        chromosomeStack.pop();

                        return depthSearchTree(chromosomeStack, violatedConstraints, violatedConstraintsIndex, perfectResult, ++geneIndex);

                    } else {

                        chromosomeStack.push(child);

                        return depthSearchTree(chromosomeStack, violatedConstraints, ++violatedConstraintsIndex, perfectResult, 0);

                    }
                }
            }
        }
        return chromosome;
    }

    private Chromosome generateChild(Chromosome chromosome, int classInitialPosition, ViolatedConstraint violatedConstraint, int perfectResult, int geneIndex) throws ClassNotFoundException {
        int conflictPosition = violatedConstraint.getChromossomePositionByDayPeriod() + classInitialPosition;
        int possibleChildIndex = geneIndex + classInitialPosition;

        for (int i = possibleChildIndex; i < classInitialPosition + 10; i++) {

            if (i != conflictPosition && chromosome.getGenes()[i] != 0) {

                Chromosome possibleChild = new Chromosome(chromosome.getGenes(), chromosome.getAvaliation());

                int aux = possibleChild.getGenes()[conflictPosition];
                possibleChild.getGenes()[conflictPosition] = possibleChild.getGenes()[i];
                possibleChild.getGenes()[i] = aux;

                if (Avaliation.rateConflicts(possibleChild, this.dtoitc, new int[]{conflictPosition, i})) {

                    Avaliation.rate(possibleChild, this.dtoitc, perfectResult, false);
                    return possibleChild;
                }
            }
        }
        return null;
    }
}
