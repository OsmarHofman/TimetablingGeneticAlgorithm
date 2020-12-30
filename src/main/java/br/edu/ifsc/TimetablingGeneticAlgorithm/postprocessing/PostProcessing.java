package br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PostProcessing {
    private Chromosome chromosome;

    public PostProcessing() {
    }


    public PostProcessing(Chromosome chromosome, DTOITC dtoitc, int initialAvaliation) throws ClassNotFoundException {
        this.chromosome = chromosome;
        Avaliation.rate(chromosome, dtoitc, initialAvaliation);
    }

    public boolean hasConflicts(int initialAvaliation) {
        return chromosome.getAvaliation() != initialAvaliation;
    }

    public Chromosome resolveConflicts(Chromosome chromosome, List<Integer> coursesWithConflicts, DTOITC dtoitc, ViolatedConstraint violatedConstraint){
        Queue<Chromosome> queue = new LinkedList<>();
        queue.add(chromosome);
        for (Integer courseWithConflict : coursesWithConflicts) {
            for (int i = 0; i < chromosome.getGenes().length; i += 10) {
                if (dtoitc.isLessonInCourse(chromosome.getGenes()[i], courseWithConflict)) {
                    List<Chromosome> children = this.generateChildren(chromosome, i, violatedConstraint, dtoitc);
                    queue.addAll(children);
                }
            }
        }

        return null;
    }

    private List<Chromosome> generateChildren(Chromosome chromosome, int classInitialPosition, ViolatedConstraint violatedConstraint, DTOITC dtoitc) {
        List<Chromosome> children = new ArrayList<>();
        int conflictPosition = violatedConstraint.getChromossomePositionByDayPeriod() + classInitialPosition;
        for (int i = classInitialPosition; i < classInitialPosition + 10; i++) {
            if (i != conflictPosition) {
                Chromosome possibleChild = new Chromosome(chromosome.getGenes(), chromosome.getAvaliation());
                int aux = possibleChild.getGenes()[conflictPosition];
                possibleChild.getGenes()[conflictPosition] = possibleChild.getGenes()[i];
                possibleChild.getGenes()[i] = aux;
                if(Avaliation.rateConflicts(possibleChild, dtoitc))
                    children.add(possibleChild);
            }
        }
        return null;
    }



}
