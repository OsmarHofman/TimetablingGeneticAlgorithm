package br.edu.ifsc.TimetablingGeneticAlgorithm.distributed;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOChromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTODistributedData;


import java.rmi.Naming;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;


public class ConnectionFactory {

    private DTOChromosome[] finalChromosomes;

    public ConnectionFactory(int size) {
        this.finalChromosomes = new DTOChromosome[size];
    }

    public void sendSet(String ip, DTODistributedData data, int index, CountDownLatch latch) {
        new Thread(() -> {
            try {
                IDistributedGA distributedGA = (IDistributedGA) Naming.lookup("rmi://" + ip + ":1099/ga");
                finalChromosomes[index] = distributedGA.process(data);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }).start();
    }

    public Chromosome[] getFinalChromosomes() {
        Chromosome[] chromosomes = new Chromosome[finalChromosomes.length];

        for (int i = 0; i < chromosomes.length; i++) {
            chromosomes[i] = finalChromosomes[i].getChromosome();
        }

        return chromosomes;
    }


    public long getAverageTime() {
        return (long) Arrays.stream(finalChromosomes).mapToLong(DTOChromosome::getTotalTime).average().orElse(0);
    }

}
