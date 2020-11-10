package br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc;

/**
 * Classe que representa uma sala ou laboratório do IFSC
 */
public class Classroom {
    private final int roomId;
    private String name;

    public Classroom(int roomId, String name) {
        this.roomId = roomId;
        this.name = name;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "roomId=" + roomId +
                ", name='" + name + '\'' +
                '}';
    }
}
