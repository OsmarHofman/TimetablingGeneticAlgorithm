package br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.interfaces;

import java.io.IOException;

public interface IFileHandler {
    void createReport(String text, String fileName) throws IOException;
}
