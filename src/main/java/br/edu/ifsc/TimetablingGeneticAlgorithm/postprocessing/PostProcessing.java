package br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOIFSC;
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

    public Chromosome depthSearchTree(Stack<Chromosome> chromosomeStack, List<ViolatedConstraint> violatedConstraints, DTOITC dtoitc, DTOIFSC dtoifsc, int violatedConstraintsIndex, int perfectResult) throws ClassNotFoundException {
        Chromosome chromosome = chromosomeStack.peek();
        if (chromosome.getAvaliation() == perfectResult)
            return chromosome;
        ViolatedConstraint currentViolatedConstraint = violatedConstraints.get(violatedConstraintsIndex);
        List<Integer> sortedConflicts = currentViolatedConstraint.getConflictedClassWithGreaterAvailableTime(dtoifsc, chromosome);
        for (Integer courseWithConflict : sortedConflicts) {
            for (int j = 0; j < chromosome.getGenes().length; j += 10) {
                if (dtoitc.isLessonInCourse(chromosome.getGenes()[j], courseWithConflict)) {
                    Chromosome child = generateChild(chromosome, j, currentViolatedConstraint, dtoitc, perfectResult);
                    //FIXME verificar para passas o J como parametro no recursivo para gerar um filho em uma posição diferente do qual ja tentou
                    if (child == null) {
                        chromosomeStack.pop();
                        return depthSearchTree(chromosomeStack, violatedConstraints, dtoitc, dtoifsc, violatedConstraintsIndex, perfectResult);
                    } else {
                        chromosomeStack.push(child);
                        return depthSearchTree(chromosomeStack, violatedConstraints, dtoitc, dtoifsc, ++violatedConstraintsIndex, perfectResult);
                    }
                }
            }
        }
        return chromosome;
    }

    private Chromosome generateChild(Chromosome chromosome, int classInitialPosition, ViolatedConstraint violatedConstraint, DTOITC dtoitc, int perfectResult) throws ClassNotFoundException {
        int conflictPosition = violatedConstraint.getChromossomePositionByDayPeriod() + classInitialPosition;
        for (int i = classInitialPosition; i < classInitialPosition + 10; i++) {
            if (i != conflictPosition && chromosome.getGenes()[i] != 0) {
                Chromosome possibleChild = new Chromosome(chromosome.getGenes(), chromosome.getAvaliation());
                int aux = possibleChild.getGenes()[conflictPosition];
                possibleChild.getGenes()[conflictPosition] = possibleChild.getGenes()[i];
                possibleChild.getGenes()[i] = aux;
                if (Avaliation.rateConflicts(possibleChild, dtoitc, new int[]{conflictPosition, i})) {
                    Avaliation.rate(possibleChild, dtoitc, perfectResult, false);
                    return possibleChild;
                }
            }
        }
        return null;
    }
}
