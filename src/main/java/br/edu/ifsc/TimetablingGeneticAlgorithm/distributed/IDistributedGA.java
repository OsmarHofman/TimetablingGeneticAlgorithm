package br.edu.ifsc.TimetablingGeneticAlgorithm.distributed;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTODistributedData;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Classe que faz o processamento distribuído
 */
public interface IDistributedGA extends Remote {
    /**
     * Realiza o processamento de um conjunto.
     *
     * @param data {@link DTODistributedData} que contém os dados dos conjuntos e configurações.
     * @return {@link Chromosome} que representa a solução de um conjunto.
     * @throws InterruptedException   Caso a conexão seja interrompida.
     * @throws ClassNotFoundException Caso alguma classe do domínio não seja encontrada.
     * @throws RemoteException        Caso haja algum tipo de erro na execução distribuída
     */
    Chromosome process(DTODistributedData data)
            throws InterruptedException, ClassNotFoundException, RemoteException;
}
