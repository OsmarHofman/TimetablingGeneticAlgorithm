package br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.entities;

import java.util.List;

/**
 * Classe que representa um curso e os ids das turmas pertencentes a ele
 */
public class CourseGroup {
    private String groupName;
    private List<Integer> courses;


    public CourseGroup(String groupName, List<Integer> coursesIds) {
        this.groupName = groupName;
        this.courses = coursesIds;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Integer> getCourses() {
        return courses;
    }

    public void setCourses(List<Integer> courses) {
        this.courses = courses;
    }

}
