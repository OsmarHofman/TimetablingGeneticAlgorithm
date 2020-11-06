package br.edu.ifsc.TimetablingGeneticAlgorithm;

import br.edu.ifsc.TimetablingGeneticAlgorithm.distributed.DistributedGA;
import br.edu.ifsc.TimetablingGeneticAlgorithm.distributed.IDistributedGA;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class TimetablingGeneticAlgorithmApplication {

    public static void main(String[] args) {
        System.out.println("Iniciando servidor do AG");

        try {
            // Inicia o gerenciador de segurança
            System.out.println("\tIniciando o gerenciador de segurança...");
            System.setSecurityManager(new SecurityManager());

            // Instancia o objeto localmente
            System.out.println("\tInstanciado o objeto localmente...");
            IDistributedGA ga = new DistributedGA();

            // Registra o objeto para acesso remoto
            System.out.println("\tRegistrando o objeto para acesso remoto...");

            LocateRegistry.createRegistry(1099);

            Naming.rebind("rmi://localhost:1099/ga", ga);

            // Aguardando requisições
            System.out.println("\tAguardando requisições...");

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            System.exit(1);
        }
    }
}
