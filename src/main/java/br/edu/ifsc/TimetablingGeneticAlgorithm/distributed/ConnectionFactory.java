package br.edu.ifsc.TimetablingGeneticAlgorithm.distributed;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTODistributedData;


import java.rmi.Naming;

import java.util.concurrent.CountDownLatch;

/**
 * Construtor da conexão com os servidores
 */
public class ConnectionFactory {

    private Chromosome[] finalChromosomes;

    public ConnectionFactory(int size) {
        this.finalChromosomes = new Chromosome[size];
    }

    /**
     * Envia o conjunto para o computador servidor fazer o processamento.
     *
     * @param ip    {@link String} que representa o ip do computador. Ex.:"10.151.35.22"
     * @param data  {@link DTODistributedData} que contém os dados a serem enviados.
     * @param index inteiro que representa qual o "id" do computador.
     * @param latch {@link CountDownLatch} que faz o gerenciamento dos retornos dos computadores.
     */
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
        return finalChromosomes;
    }

}
