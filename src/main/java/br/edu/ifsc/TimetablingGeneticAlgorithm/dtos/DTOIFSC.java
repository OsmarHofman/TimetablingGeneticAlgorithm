package br.edu.ifsc.TimetablingGeneticAlgorithm.dtos;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe DTO (Data Transfer Object) de todas as classes que representam o XML do IFSC
 */
public class DTOIFSC {
    private List<Classes> classes;
    private List<Lesson> lessons;
    private List<Subject> subjects;
    private List<Teacher> professors;
    private List<Classroom> rooms;


    public DTOIFSC() {
        this.classes = new ArrayList<>();
        this.lessons = new ArrayList<>();
        this.subjects = new ArrayList<>();
        this.professors = new ArrayList<>();
        this.rooms = new ArrayList<>();
    }

    public List<Classes> getClasses() {
        return classes;
    }

    public void setClasses(List<Classes> classes) {
        this.classes = classes;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public List<Teacher> getProfessors() {
        return professors;
    }

    public void setProfessors(List<Teacher> professors) {
        this.professors = professors;
    }

    public List<Classroom> getRooms() {
        return rooms;
    }

    public void setRooms(List<Classroom> rooms) {
        this.rooms = rooms;
    }

    /**
     * Obtém o nome do curso pelo seu identificador
     *
     * @param id índice que será verificado com os valores das {@link Classes}.
     * @return {@link String} com o nome do curso pelo Id correspondente.
     */
    public String getCourseNameById(int id) {
        for (Classes classe : this.classes) {
            if (classe.getId() == id)
                return classe.getName();
        }
        return null;
    }

    /**
     * Obtém o nome do professor pelo seu identificador
     *
     * @param id índice que será verificado com os valores dos {@link Teacher}s.
     * @return {@link String} com o nome do professor pelo Id correspondente.
     */
    public String getProfessorNameById(int id) {
        for (Teacher teacher : this.professors) {
            if (teacher.getId() == id)
                return teacher.getName();
        }
        return null;
    }

    /**
     * Obtém o nome da matéria pelo seu identificador
     *
     * @param id índice que será verificado com os valores doa {@link Lesson}s.
     * @return {@link String} com o nome da matéria pelo Id correspondente.
     */
    public String getLessonNameById(int id) {
        for (Subject subject : this.subjects) {
            if (subject.getId() == id)
                return subject.getName();
        }
        return null;
    }

    @Override
    public String toString() {
        return "DTOServerData{" +
                "classes=" + classes +
                ", lessons=" + lessons +
                ", subjects=" + subjects +
                ", professors=" + professors +
                ", rooms=" + rooms +
                '}';
    }

    public List<Teacher> getAllTeachersInClass(int conflictClass) {
        List<Teacher> teachers = new ArrayList<>();
        for (Lesson lesson : lessons) {
            if (lesson.getClassesId() == conflictClass) {
                for (Integer teacherId : lesson.getTeacherId()) {
                    for (Teacher teacher : professors) {
                        if (teacher.getId() == teacherId && teachers.stream().noneMatch(x -> x.getId() == teacherId))
                            teachers.add(teacher);
                    }
                }
            }
        }
        return teachers;
    }
}
