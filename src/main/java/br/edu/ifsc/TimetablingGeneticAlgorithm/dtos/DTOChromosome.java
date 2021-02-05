package br.edu.ifsc.TimetablingGeneticAlgorithm.dtos;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;

import java.io.Serializable;

public class DTOChromosome implements Serializable {
    private Chromosome chromosome;
    private long totalTime;
    private int totalExecution;
    private byte totalGeneration;

    public DTOChromosome() {
    }

    public DTOChromosome(Chromosome chromosome, long totalTime, int totalExecution, byte totalGeneration) {
        this.chromosome = chromosome;
        this.totalTime = totalTime;
        this.totalExecution = totalExecution;
        this.totalGeneration = totalGeneration;
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

    public int getTotalExecution() {
        return totalExecution;
    }

    public void setTotalExecution(int totalExecution) {
        this.totalExecution = totalExecution;
    }

    public byte getTotalGeneration() {
        return totalGeneration;
    }

    public void setTotalGeneration(byte totalGeneration) {
        this.totalGeneration = totalGeneration;
    }
}
