package br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.entities;

import java.util.List;

/**
 * Representação do professor e em que cursos leciona
 */
public class ProfessorCourse {
    private String professor;
    private List<String> course;

    public ProfessorCourse(String professor, List<String> curso) {
        this.professor = professor;
        this.course = curso;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public List<String> getCourse() {
        return course;
    }

    public void setCourse(List<String> curso) {
        this.course = curso;
    }

    /**
     * Verifica se o professor leciona exclusivamente a um curso.
     *
     * @param courseName {@link String} do nome do curso.
     * @return {@code true} se ele é exclusivo, e {@code false} caso contrário.
     */
    public boolean checkExclusivity(String courseName) {
        //verifica se só tem um item, e esse item é o conjunto
        return this.course.size() == 1 && this.course.get(0).equals(courseName);
    }

    /**
     * Identifica se os professores de um curso são exclusivos, compartilhados ou não relacionados.
     *
     * @param course {@link String} nome do curso a ser analisado.
     * @return Um vetor que cada posição representação a relação dos professores com o curso.
     */
    public String[] verifyCourse(String course) {
        if (this.course.size() == 1 && this.course.get(0).equals(course))
            return new String[]{ProfessorCourseStatus.EXCLUSIVE.toString()};

        for (String iterationCourse : this.course) {
            if (iterationCourse.equals(course)) {
                return new String[]{ProfessorCourseStatus.SHARED.toString(), iterationCourse};
            }
        }
        return new String[]{ProfessorCourseStatus.NOTRELATED.toString()};
    }

    @Override
    public String toString() {
        return "Professor_Course{" +
                "professor='" + professor + '\'' +
                ", course=" + course +
                '}';
    }

}
