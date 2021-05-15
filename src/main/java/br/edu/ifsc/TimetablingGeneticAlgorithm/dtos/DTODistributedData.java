package br.edu.ifsc.TimetablingGeneticAlgorithm.dtos;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Subject;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.entities.CourseRelation;

import java.io.Serializable;
import java.util.List;

/**
 * DTO que contém todos os dados necessários para execução distribuída
 */
public class DTODistributedData implements Serializable {
    DTOITC[] sets;
    int[] config;
    List<Subject> dtoIfscSubjects;
    List<CourseRelation> courseRelations;

    public DTODistributedData() {
    }

    public DTODistributedData(DTOITC[] sets, int[] config, List<Subject> dtoIfscSubjects, List<CourseRelation> courseRelations) {
        this.sets = sets;
        this.config = config;
        this.dtoIfscSubjects = dtoIfscSubjects;
        this.courseRelations = courseRelations;
    }

    public DTOITC[] getSets() {
        return sets;
    }

    public void setSets(DTOITC[] sets) {
        this.sets = sets;
    }

    public int[] getConfig() {
        return config;
    }

    public void setConfig(int[] config) {
        this.config = config;
    }

    public List<Subject> getDtoIfscSubjects() {
        return dtoIfscSubjects;
    }

    public void setDtoIfscSubjects(List<Subject> dtoIfscSubjects) {
        this.dtoIfscSubjects = dtoIfscSubjects;
    }

    public List<CourseRelation> getCourseRelations() {
        return courseRelations;
    }

    public void setCourseRelations(List<CourseRelation> courseRelations) {
        this.courseRelations = courseRelations;
    }
}
