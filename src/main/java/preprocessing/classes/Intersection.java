package preprocessing.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Intersection implements Serializable {
    private int intersectionProfessorsCount;
    private String intersectionCourse;
    private List<String> professorsNameList;

    public Intersection() {
        intersectionProfessorsCount = 0;
        professorsNameList = new ArrayList<>();
    }

    public Intersection(int intersectionProfessorsCount, String intersectionCourse) {
        this.intersectionProfessorsCount = intersectionProfessorsCount;
        this.intersectionCourse = intersectionCourse;
        this.professorsNameList = new ArrayList<>();
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

    public List<String> getProfessorsNameList() {
        return professorsNameList;
    }

    public void setProfessorsNameList(List<String> professorsNameList) {
        this.professorsNameList = professorsNameList;
    }

    //FIXME colocar métodos abaixo em uma classe e sobrecarregar
    public static boolean hasIntersectionInList(String courseName, List<Intersection> intersections){
        for (Intersection iteratorIntersection:intersections) {
            if (iteratorIntersection.getIntersectionCourse().equals(courseName)){
                return true;
            }
        }
        return false;
    }

    public static boolean hasProfessorInList(String professorName, List<String> professors){
        for (String iteratorProfessor:professors) {
            if (iteratorProfessor.equals(professorName)){
                return true;
            }
        }
        return false;
    }

    public static boolean hasCourseRelationInList(String courseName, List<CourseRelation> courses){
        for (CourseRelation iteratorCR:courses) {
            if (iteratorCR.getName().equals(courseName)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "\n\n\t\tCurso relacionado: " + intersectionCourse +
                "\n\t\tNúmero de professores compartilhados: " + intersectionProfessorsCount +
                "\n\t\tProfessores: " + professorsNameList;


    }
}
