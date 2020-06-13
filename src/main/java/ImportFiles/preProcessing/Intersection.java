package ImportFiles.preProcessing;

import java.io.Serializable;

public class Intersection implements Serializable {
    private int intersectionProfessorsCount;
    private String intersectionCourse;

    public Intersection() {
        intersectionProfessorsCount = 0;
    }

    public Intersection(int intersectionProfessorsCount, String intersectionCourse) {
        this.intersectionProfessorsCount = intersectionProfessorsCount;
        this.intersectionCourse = intersectionCourse;
    }

    public int getIntersectionProfessorsCount() {
        return intersectionProfessorsCount;
    }

    public void setIntersectionProfessorsCount(int intersectionProfessorsCount) {
        this.intersectionProfessorsCount = intersectionProfessorsCount;
    }

    public String getIntersectionCourse() {
        return intersectionCourse;
    }

    public void setIntersectionCourse(String intersectionCourse) {
        this.intersectionCourse = intersectionCourse;
    }

    @Override
    public String toString() {
        return "\n\n\t\tCurso relacionado: " + intersectionCourse +
                "\n\t\tNúmero de professores compartilhados: " + intersectionProfessorsCount;

    }
}
