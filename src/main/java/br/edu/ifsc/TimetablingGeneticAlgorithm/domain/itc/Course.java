package br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Classes;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.Shift;

import java.util.Arrays;
import java.util.List;

/**
 * Classe que representa um curso
 */
public class Course {
    private String courseId;
    private int lessonsNumber;
    private String shift;

    public Course() {
    }

    public Course(String[] parameters) {
        this.courseId = parameters[0];
        this.lessonsNumber = Integer.parseInt(parameters[1]);
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getLessonsNumber() {
        return lessonsNumber;
    }

    public void setLessonsNumber(int lessonsNumber) {
        this.lessonsNumber = lessonsNumber;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", lessonsNumber=" + lessonsNumber +
                ", shift='" + shift + '\'' +
                '}';
    }
}
