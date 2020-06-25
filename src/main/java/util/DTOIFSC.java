package util;

import domain.ifsc.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DTOIFSC {
    private List<Classes> classes;
    private List<Lesson> lessons;
    private List<Subject> subjects;
    private List<Teacher> professors;
    private List<Classroom> rooms;


    public DTOIFSC() {
        this.classes = new ArrayList<>();
        this.lessons = new ArrayList<>();
        this.subjects = new ArrayList<>();
        this.professors = new ArrayList<>();
        this.rooms = new ArrayList<>();
    }

    public DTOIFSC(List<Classes> classes, List<Lesson> lessons, List<Subject> subjects, List<Teacher> professors, List<Classroom> rooms) {
        this.classes = classes;
        this.lessons = lessons;
        this.subjects = subjects;
        this.professors = professors;
        this.rooms = rooms;
    }

    public List<Classes> getClasses() {
        return classes;
    }

    public void setClasses(List<Classes> classes) {
        this.classes = classes;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public List<Teacher> getProfessors() {
        return professors;
    }

    public void setProfessors(List<Teacher> professors) {
        this.professors = professors;
    }

    public List<Classroom> getRooms() {
        return rooms;
    }

    public void setRooms(List<Classroom> rooms) {
        this.rooms = rooms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DTOIFSC that = (DTOIFSC) o;
        return classes.equals(that.classes) &&
                lessons.equals(that.lessons) &&
                subjects.equals(that.subjects) &&
                professors.equals(that.professors) &&
                rooms.equals(that.rooms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classes, lessons, subjects, professors, rooms);
    }

    @Override
    public String toString() {
        return "DTOServerData{" +
                "classes=" + classes +
                ", lessons=" + lessons +
                ", subjects=" + subjects +
                ", professors=" + professors +
                ", rooms=" + rooms +
                '}';
    }
}
