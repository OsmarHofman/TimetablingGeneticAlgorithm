package br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc;

/**
 * Classe que representa um professor do IFSC
 */
public class Teacher {
    private int id;
    private String name;
    private final String timeoff;


    public Teacher(int id, String name, String timeoff) {
        this.id = id;
        this.name = name;
        this.timeoff = timeoff;
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

    public String getTimeoff() {
        return timeoff;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", timeoff='" + timeoff + '\'' +
                '}';
    }
}
