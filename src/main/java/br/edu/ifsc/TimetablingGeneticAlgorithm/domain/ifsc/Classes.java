package br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc;

/**
 * Classe que representa uma turma do IFSC
 */
public class Classes {
    private int id;
    private String name;
    private final String shortName;
    private final int teacherId;
    private final String timeoff;

    public Classes(int id, String name, String shortName, int teacherId, String timeoff) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.teacherId = teacherId;
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

    public String getShortName() {
        return shortName;
    }

    public String getTimeoff() {
        return timeoff;
    }

    @Override
    public String toString() {
        return "Classes [id=" + id + ", name=" + name + ", shortName=" + shortName + ", teacherId=" + teacherId
                + ", timeoff=" + timeoff + "]";
    }
}
