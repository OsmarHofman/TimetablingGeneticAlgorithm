package br.edu.ifsc.TimetablingGeneticAlgorithm.dtos;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;

import java.io.Serializable;

public class DTOChromosome implements Serializable {
    private Chromosome chromosome;
    private long totalTime;

    public DTOChromosome() {
    }

    public DTOChromosome(Chromosome chromosome, long totalTime) {
        this.chromosome = chromosome;
        this.totalTime = totalTime;
    }

    public Chromosome getChromosome() {
        return chromosome;
    }

    public void setChromosome(Chromosome chromosome) {
        this.chromosome = chromosome;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }
}
