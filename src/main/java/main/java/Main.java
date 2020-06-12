package main.java;

import ImportFiles.RetrieveProfessorsSchedule;

public class Main {
    public static void main(String[] args) {
        //TODO Alterar atributos do domain e corrigir possiveis conflitos

        RetrieveProfessorsSchedule rts = new RetrieveProfessorsSchedule();

        rts.pegarArquivo();

    }
}
