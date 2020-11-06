package br.edu.ifsc.TimetablingGeneticAlgorithm.distributed;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Subject;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTODistributedData;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.entities.CourseRelation;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IDistributedGA extends Remote {
    Chromosome process(DTODistributedData data)
            throws InterruptedException, ClassNotFoundException, RemoteException;
}
