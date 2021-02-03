package br.edu.ifsc.TimetablingGeneticAlgorithm.distributed;

import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOChromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTODistributedData;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IDistributedGA extends Remote {
    DTOChromosome process(DTODistributedData data)
            throws InterruptedException, ClassNotFoundException, RemoteException;
}
