package br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc;

import java.io.Serializable;

/**
 * Classe que representa as salas e laboratórios
 */
public class Room implements Serializable {
    private int roomId;
    private int capacity;

    public Room() {
    }

    public Room(String[] parameters) {
        this.roomId = Integer.parseInt(parameters[0]);
        this.capacity = Integer.parseInt(parameters[1]);
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
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
