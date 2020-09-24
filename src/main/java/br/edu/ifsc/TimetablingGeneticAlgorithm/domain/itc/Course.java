package br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Classes;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.Shift;

import java.util.List;

/**
 * Classe que representa um curso
 */
public class Course {
    private String courseId;
    private int coursesNumber;
    private Shift shift;

    public Course() {
    }

    public Course(String[] parameters) {
        this.courseId = parameters[0];
        this.coursesNumber = Integer.parseInt(parameters[1]);
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getCoursesNumber() {
        return coursesNumber;
    }

    public void setCoursesNumber(int coursesNumber) {
        this.coursesNumber = coursesNumber;
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
                ", coursesNumber=" + coursesNumber +
                ", shift=" + shift +
                '}';
    }

    public boolean joinIntersection(int percentage, List<Classes> classes) {
        return true;
    }
}
