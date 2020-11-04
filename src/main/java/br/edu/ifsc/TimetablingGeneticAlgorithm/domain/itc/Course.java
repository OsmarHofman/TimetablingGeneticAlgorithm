package br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Classes;

import java.io.Serializable;
import java.util.List;

/**
 * Classe que representa um curso
 */
public class Course implements Serializable {
    private int courseId;
    private int lessonsNumber;
    private Shift shift;

    public Course() {
    }

    public Course(String[] parameters) {
        this.courseId = Integer.parseInt(parameters[0]);
        this.lessonsNumber = Integer.parseInt(parameters[1]);
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getLessonsNumber() {
        return lessonsNumber;
    }

    public void setLessonsNumber(int lessonsNumber) {
        this.lessonsNumber = lessonsNumber;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", lessonsNumber=" + lessonsNumber +
                ", shift=" + shift +
                '}';
    }
}
