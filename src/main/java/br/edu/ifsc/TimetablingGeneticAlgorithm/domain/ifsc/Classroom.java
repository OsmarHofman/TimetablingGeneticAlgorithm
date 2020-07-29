package br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc;

public class Classroom {
    private int roomId;
    private String name;

    public Classroom() {
    }

    public Classroom(int roomId,String name) {
        this.roomId = roomId;
        this.name = name;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
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
