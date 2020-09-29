package br.edu.ifsc.TimetablingGeneticAlgorithm.datapreview.classes;

import br.edu.ifsc.TimetablingGeneticAlgorithm.util.ListOperationUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe que representa a intersecção de um curso com outros cursos que estão relacionados
 */
public class Intersection implements Serializable {
    private int intersectionProfessorsCount;
    private String intersectionCourse;
    private List<String> professorsList;

    public Intersection() {
        intersectionProfessorsCount = 0;
        professorsList = new ArrayList<>();
    }

    public Intersection(int intersectionProfessorsCount, String intersectionCourse) {
        this.intersectionProfessorsCount = intersectionProfessorsCount;
        this.intersectionCourse = intersectionCourse;
        this.professorsList = new ArrayList<>();
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

    public List<String> getProfessorsList() {
        return professorsList;
    }

    public void setProfessorsList(List<String> professorsList) {
        this.professorsList = professorsList;
    }

    /**
     * Adiciona professores a intersecção
     *
     * @param professorsList {@link List} de {@link String} com os nomes dos professores
     */
    public void addProfessorsToIntersection(List<String> professorsList) {
        List<String> newProfessorsList = new ArrayList<>();
        for (String professorName : professorsList) {
            //se o professor já não estiver na intersecção
            if (ListOperationUtil.itemIsNotInList(professorName, this.professorsList) && !newProfessorsList.contains(professorName)) {
                newProfessorsList.add(professorName);
            }
        }
        this.professorsList.addAll(newProfessorsList);
    }

    public void adjustProfessorsCount() {
        this.intersectionProfessorsCount = this.professorsList.size();
    }

    @Override
    public String toString() {
        return "\n\n\t\tCurso relacionado: " + intersectionCourse +
                "\n\t\tNúmero de professores compartilhados: " + intersectionProfessorsCount +
                "\n\t\tProfessores: " + professorsList;


    }
}
