package domain.ifsc;

import java.util.Arrays;

public class Classroom {
    private int roomId;
    private String name;
    private int capacity;

    public Classroom() {
    }

    public Classroom(int roomId,String name, int capacity) {
        this.roomId = roomId;
        this.name = name;
        this.capacity = capacity;
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
                ", capacity=" + capacity +
                '}';
    }
}
