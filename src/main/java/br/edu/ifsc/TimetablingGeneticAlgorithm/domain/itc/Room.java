package br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc;

public class Room {
    private String roomId;
    private int capacity;

    public Room() {
    }

    public Room(String[] parameters) {
        this.roomId = parameters[0];
        this.capacity = Integer.parseInt(parameters[1]);
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomId='" + roomId + '\'' +
                ", capacity=" + capacity +
                '}';
    }
}
