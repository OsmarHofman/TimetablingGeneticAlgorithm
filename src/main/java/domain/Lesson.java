package domain;

import java.util.Objects;

public class Lesson {
    private int id;
    private int subjectId;
    private int classesId;
    private int teacherId;
    private int periodsPerWeek;

    public Lesson() {
    }

    public Lesson(int id, int subjectId, int classesId, int teacherId, int periodsPerWeek) {
        this.id = id;
        this.subjectId = subjectId;
        this.classesId = classesId;
        this.teacherId = teacherId;
        this.periodsPerWeek = periodsPerWeek;
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

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getPeriodsPerWeek() {
        return periodsPerWeek;
    }

    public void setPeriodsPerWeek(int periodsPerWeek) {
        this.periodsPerWeek = periodsPerWeek;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Lesson lesson = (Lesson) o;
        return id == lesson.id && subjectId == lesson.subjectId && classesId == lesson.classesId
                && teacherId == lesson.teacherId && periodsPerWeek == lesson.periodsPerWeek;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subjectId, classesId, teacherId, periodsPerWeek);
    }

    @Override
    public String toString() {
        return "Lesson{" + "id=" + id + ", subjectId=" + subjectId + ", classesId=" + classesId + ", teacherId="
                + teacherId + ", periodsPerWeek=" + periodsPerWeek + '}';
    }
}
