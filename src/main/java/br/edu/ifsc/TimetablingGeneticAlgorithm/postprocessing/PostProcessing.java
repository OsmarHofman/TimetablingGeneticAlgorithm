package br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;

public class PostProcessing {
    private Chromosome chromosome;

    public PostProcessing() {
    }
    //TODO criar metodos para o pos processamento (verificar se tem conflitos e possivelmente arvore)


    public PostProcessing(Chromosome chromosome, DTOITC dtoitc, int initialAvaliation) throws ClassNotFoundException {
        this.chromosome = chromosome;
        Avaliation.rate(chromosome, dtoitc, initialAvaliation);
    }

    public boolean hasConflicts(int initialAvaliation) {
        return chromosome.getAvaliation() != initialAvaliation;
    }


}
