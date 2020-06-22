package domain;

import java.util.Arrays;

public class Lesson {
    private int id;
    private String name;
    private int days;
    private int dayPeriod;
    private Room[] rooms;
    private Course[] course;

    public Lesson(int id, String name, int days, int dayPeriod, Room[] rooms, String course) {
        this.id = id;
        this.name = name;
        this.days = days;
        this.dayPeriod = dayPeriod;
        this.rooms = rooms;
        this.course = new Course[1];
        this.course[0].setId(course);
    }

    public Lesson(String[] s) {
        this.id = Integer.parseInt(s[0]);
        this.name = s[0];
        this.days = 0;
        this.dayPeriod = 0;
        this.rooms = null;
        this.course = new Course[s.length - 2];
        for (int i = 0; i < s.length - 2; i++) {
            this.course[i].setName(s[i + 2]);
        }
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

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getDayPeriod() {
        return dayPeriod;
    }

    public void setDayPeriod(int dayPeriod) {
        this.dayPeriod = dayPeriod;
    }

    public Room[] getRooms() {
        return rooms;
    }

    public void setRooms(Room[] rooms) {
        this.rooms = rooms;
    }

    public Course[] getCourse() {
        return course;
    }

    public void setCourse(Course[] course) {
        this.course = course;
    }

    @Override
    public String toString() {
        return "Curriculas{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", days=" + days +
                ", dayPeriod=" + dayPeriod +
                ", rooms=" + Arrays.toString(rooms) +
                ", courses=" + Arrays.toString(course) +
                '}';
    }
}
