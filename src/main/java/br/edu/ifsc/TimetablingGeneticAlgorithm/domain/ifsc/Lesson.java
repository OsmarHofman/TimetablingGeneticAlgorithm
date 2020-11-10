package br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc;

import java.util.Arrays;

/**
 * Classe que representa a tabela de relação entre as classes: {@link Subject}, {@link Classes},{@link Teacher} e
 * {@link Classroom}
 */
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

    public int getClassesId() {
        return classesId;
    }

    public int[] getTeacherId() {
        return teacherId;
    }

    public int getPeriodsPerWeek() {
        return periodsPerWeek;
    }

    public int getDurationPeriods() {
        return durationPeriods;
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
