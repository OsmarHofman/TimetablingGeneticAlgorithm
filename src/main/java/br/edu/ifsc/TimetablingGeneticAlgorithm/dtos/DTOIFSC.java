package br.edu.ifsc.TimetablingGeneticAlgorithm.dtos;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.*;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Shift;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public DTOIFSC(List<Classes> classes, List<Lesson> lessons, List<Subject> subjects, List<Teacher> professors, List<Classroom> rooms) {
        this.classes = classes;
        this.lessons = lessons;
        this.subjects = subjects;
        this.professors = professors;
        this.rooms = rooms;
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
     * Converte um timeoff (formato do XMl do IFSC) para um turno.
     *
     * @param timeoff {@link String} que representa o timeoff a ser convertido.
     * @return {@link Shift} com o turno correspondente.
     */
    private Shift convertTimeoffToShift(String timeoff) {
        String[] days = timeoff.replace(".", "").split(",");
        if (days[0].charAt(0) == '1')
            return Shift.MATUTINO;
        else if (days[0].charAt(4) == '1')
            return Shift.VESPERTINO;
        return Shift.NOTURNO;
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

    /**
     * Obtém todos os professores de uma turma com um conflito.
     *
     * @param conflictClassId id da turma com o conflito
     * @return {@link List} de todos os {@link Teacher}.
     */
    public List<Teacher> getAllTeachersInClass(int conflictClassId) {
        List<Teacher> teachers = new ArrayList<>();
        for (Lesson lesson : lessons) {
            if (lesson.getClassesId() == conflictClassId) {
                for (Integer teacherId : lesson.getTeacherId()) {
                    for (Teacher teacher : professors) {
                        //insere se o professor já não está na lista
                        if (teacher.getId() == teacherId && teachers.stream().noneMatch(x -> x.getId() == teacherId))
                            teachers.add(teacher);
                    }
                }
            }
        }
        return teachers;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DTOIFSC that = (DTOIFSC) o;
        return classes.equals(that.classes) &&
                lessons.equals(that.lessons) &&
                subjects.equals(that.subjects) &&
                professors.equals(that.professors) &&
                rooms.equals(that.rooms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classes, lessons, subjects, professors, rooms);
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
}
