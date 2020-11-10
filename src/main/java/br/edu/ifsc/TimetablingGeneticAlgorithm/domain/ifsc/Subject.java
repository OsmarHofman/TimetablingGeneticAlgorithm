package br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc;

import java.util.Objects;

/**
 * Classe que representa as disciplinas lecionadas no IFSC
 */
public class Subject {
    private int id;
    private String name;
    private final String shortName;

    public Subject(int id, String name, String shortName) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "Subject{" + "id=" + id + ", name='" + name + '\'' + ", shortName='" + shortName + '\'' + '}';
    }
}
