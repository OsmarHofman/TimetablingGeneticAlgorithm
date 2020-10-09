package br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.entities;

import java.util.List;

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
