package preprocessing.classes;

import preprocessing.model.ListOperationUtil;

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

    public void addProfessorsToIntersection(List<String> professorsList) {
        List<String> newProfessorsList = new ArrayList<>();
        for (String professorName : professorsList) {
            if (ListOperationUtil.itemIsNotInList(professorName, this.professorsNameList) && !newProfessorsList.contains(professorName)) {
                newProfessorsList.add(professorName);
            }
        }
        this.professorsNameList.addAll(newProfessorsList);
    }

    public void adjustProfessorsCount(){
        this.intersectionProfessorsCount = this.professorsNameList.size();
    }

    @Override
    public String toString() {
        return "\n\n\t\tCurso relacionado: " + intersectionCourse +
                "\n\t\tNúmero de professores compartilhados: " + intersectionProfessorsCount +
                "\n\t\tProfessores: " + professorsNameList;


    }
}
