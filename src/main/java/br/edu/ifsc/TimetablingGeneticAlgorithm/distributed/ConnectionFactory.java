package br.edu.ifsc.TimetablingGeneticAlgorithm.distributed;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTODistributedData;


import java.rmi.Naming;

import java.util.concurrent.CountDownLatch;


public class ConnectionFactory {

    private Chromosome[] finalChromosomes;

    public ConnectionFactory(int size) {
        this.finalChromosomes = new Chromosome[size];
    }

    public void sendSet(String ip, DTODistributedData data, int index, CountDownLatch latch) {
        new Thread(() -> {
            try {
                IDistributedGA distributedGA;
                distributedGA = (IDistributedGA) Naming.lookup(ip);
                finalChromosomes[index] = distributedGA.process(data);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }).start();
    }

    public Chromosome[] getFinalChromosomes() {
        return finalChromosomes;
    }

    public void setFinalChromosomes(Chromosome[] finalChromosomes) {
        this.finalChromosomes = finalChromosomes;
    }
}
