package br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc;

import java.util.Arrays;

public class Lesson {
    private int id;
    private int subjectId;
    private int classesId;
    private int[] teacherId;
    private int periodsPerWeek;
    private int durationPeriods;

    public Lesson() {
    }

    public Lesson(int id, int subjectId, int classesId, int[] teacherId, int periodsPerWeek, int durationPeriods) {
        this.id = id;
        this.subjectId = subjectId;
        this.classesId = classesId;
        this.teacherId = teacherId;
        this.periodsPerWeek = periodsPerWeek;
        this.durationPeriods = durationPeriods;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getClassesId() {
        return classesId;
    }

    public void setClassesId(int classesId) {
        this.classesId = classesId;
    }

    public int[] getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int[] teacherId) {
        this.teacherId = teacherId;
    }

    public int getPeriodsPerWeek() {
        return periodsPerWeek;
    }

    public void setPeriodsPerWeek(int periodsPerWeek) {
        this.periodsPerWeek = periodsPerWeek;
    }

    public int getDurationPeriods() {
        return durationPeriods;
    }

    public void setDurationPeriods(int durationPeriods) {
        this.durationPeriods = durationPeriods;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + id +
                ", subjectId=" + subjectId +
                ", classesId=" + classesId +
                ", teacherId=" + Arrays.toString(teacherId) +
                ", periodsPerWeek=" + periodsPerWeek +
                ", durationPeriods=" + durationPeriods +
                '}';
    }
}
