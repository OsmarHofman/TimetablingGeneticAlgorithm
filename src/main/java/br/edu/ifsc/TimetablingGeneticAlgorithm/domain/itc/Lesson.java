package br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe que representa a matéria de um {@link Course}
 */
public class Lesson {

    private String lessonId;
    private String courseId;
    private String[] professorId;
    private int lecturesNumber; //lecturesNumber = durationPeriods do IFSC
    private int minWorkingDays; //minWorkingDays = cálculo (periodPerWeek / durationPeriod) do IFSC
    private int studentsNumber;
    private UnavailabilityConstraint[] constraints;
    private Room room;

    public Lesson() {
    }

    public Lesson(String[] parameters) {
        //Dados vindos do arquivo txt do ITC
    }


    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public String[] getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String[] professorId) {
        this.professorId = professorId;
    }

    public int getLecturesNumber() {
        return lecturesNumber;
    }

    public void setLecturesNumber(int lecturesNumber) {
        this.lecturesNumber = lecturesNumber;
    }

    public int getMinWorkingDays() {
        return minWorkingDays;
    }

    public void setMinWorkingDays(int minWorkingDays) {
        this.minWorkingDays = minWorkingDays;
    }

    public int getStudentsNumber() {
        return studentsNumber;
    }

    public void setStudentsNumber(int studentsNumber) {
        this.studentsNumber = studentsNumber;
    }

    public UnavailabilityConstraint[] getConstraints() {
        return constraints;
    }

    public void setConstraints(UnavailabilityConstraint[] constraints) {
        this.constraints = constraints;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * Associa as constraints a lesson.
     *
     * @param constraints {@link List} de {@link UnavailabilityConstraint} a ser associada.
     */
    public void retrieveConstraints(List<UnavailabilityConstraint> constraints) {
        List<UnavailabilityConstraint> constraintList = new ArrayList<>();
        for (UnavailabilityConstraint iterationConstraints : constraints) {
            if (iterationConstraints.getId().equals(this.lessonId)) {
                constraintList.add(iterationConstraints);
            }
        }
        this.constraints = new UnavailabilityConstraint[constraintList.size()];
        this.constraints = constraintList.toArray(this.constraints);
    }


    @Override
    public String toString() {
        return "Lesson{" +
                "lessonId='" + lessonId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", professorId=" + Arrays.toString(professorId) +
                ", lecturesNumber=" + lecturesNumber +
                ", minWorkingDays=" + minWorkingDays +
                ", studentsNumber=" + studentsNumber +
                ", constraints=" + Arrays.toString(constraints) +
                ", room=" + room +
                '}';
    }
}
