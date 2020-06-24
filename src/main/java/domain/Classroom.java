package domain;

import java.util.Arrays;

public class Classroom {
    private int[] classroomSubjects;

    public Classroom() {
        classroomSubjects = new int[20];
    }

    public int[] getClassroomSubjects() {
        return this.classroomSubjects;
    }

    public void setClassroomSubjects(int[] classroomSubjects) {
        this.classroomSubjects = classroomSubjects;
    }

    public void setClassroomSubjectInIndex(int index, int value) {
        classroomSubjects[index] = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Classroom classroom = (Classroom) o;
        return Arrays.equals(classroomSubjects, classroom.classroomSubjects);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(classroomSubjects);
    }

    @Override
    public String toString() {
        return "\nClassroom\n{" + "classroomSubjects=" + Arrays.toString(classroomSubjects) + "}\n";
    }
}
