package br.edu.ifsc.TimetablingGeneticAlgorithm.datapreview.interfaces;

import java.io.IOException;

public interface IFileHandler {
    /**
     * cria um arquivo txt com os dados de uma {@link String}.
     *
     * @param text     {@link String} a ser serializada.
     * @param fileName Nome desejado do arquivo a ser criado.
     * @throws IOException Caso não seja possível fazer a operação de escrita.
     */
    void createReport(String text, String fileName) throws IOException;
}
