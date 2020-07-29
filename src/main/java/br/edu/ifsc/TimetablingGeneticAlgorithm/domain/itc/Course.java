package br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc;

public class Course {
    private String courseId;
    private int coursesNumber;
    private byte shift;

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

    public byte getShift() {
        return shift;
    }

    public void setShift(byte shift) {
        this.shift = shift;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", coursesNumber=" + coursesNumber +
                ", turno=" + shift +
                '}';
    }
}
