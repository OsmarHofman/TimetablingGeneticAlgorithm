package br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe que representa o conjunto, com as informações sobre os professores e intersecções
 */
public class CourseRelation implements Serializable {
    private String name;
    private int totalProfessors;
    private int exclusiveProfessorCount;
    private List<Intersection> intersection;

    public CourseRelation(String name) {
        this.name = name;
        totalProfessors = 0;
        exclusiveProfessorCount = 0;
        intersection = new ArrayList<>();
    }

    public CourseRelation() {
        exclusiveProfessorCount = 0;
        intersection = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExclusiveProfessorCount() {
        return exclusiveProfessorCount;
    }

    public void setExclusiveProfessorCount(int exclusiveProfessorCount) {
        this.exclusiveProfessorCount = exclusiveProfessorCount;
    }

    public List<Intersection> getIntersection() {
        return intersection;
    }

    public void setIntersection(List<Intersection> intersection) {
        this.intersection = intersection;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Verifica as intersecções de um curso, para incluir os outros cursos relacionados.
     *
     * @param course           Nome do curso o qual será inserida as intersecções.
     * @param professorCourses {@link List} de todos os cursos que um professor leciona.
     */
    public void checkListIntersection(String course, List<String> professorCourses) {
        //Lista onde serão incluidos os cursos relacionados
        List<String> intersectionsName = new ArrayList<>();
        for (Intersection intersec : intersection) {
            intersectionsName.add(intersec.getIntersectionCourse());
        }
        for (String profCourse : professorCourses) {
            if (!intersectionsName.contains(profCourse) && !profCourse.equals(course)) {
                intersection.add(new Intersection(1, profCourse));
            }
        }
    }

    /**
     * Verifica os cursos com intersecções em comum a partir de uma porcentagem de agrupamento, e cria um grupo
     * caso o número de intersecções seja maior ou igual a porcentagem.
     *
     * @param percentage {@link Integer} que representa a chance de agrupar os conjuntos, sendo 0% o agrupamento
     *                   de todos cursos (por consequência, todas as turmas juntas) e 100% nenhum agrupamento
     *                   de curso (cada curso só irá ter as suas turmas).
     * @param cs         {@link List} de {@link CourseRelation} que representa cada curso com suas intersecções.
     * @return {@code true} caso um conjunto foi formado e {@code false} caso contrário.
     * @throws ClassNotFoundException Erro caso não encontrar o curso pelo nome.
     */
    public boolean joinIntersections(int percentage, List<CourseRelation> cs) throws ClassNotFoundException {
        List<String> listIntersection = new ArrayList<>(Collections.singletonList(this.name));

        //Nome do novo conjunto
        StringBuilder nomeCurso = new StringBuilder(this.name);
        CourseRelation newCourse = new CourseRelation();
        int exclusiveProfessors = this.exclusiveProfessorCount;

        //Cria a proporção para identificar se será feito um conjunto
        int proportion = this.totalProfessors * percentage / 100;

        //Olha todas as intersecções do Curso atual, para ver se cria um conjunto
        for (Intersection iteratorIntersection : intersection) {

            //Cria um conjunto
            if (iteratorIntersection.getIntersectionProfessorsCount() >= proportion) {
                listIntersection.add(iteratorIntersection.getIntersectionCourse());

                //Caso for a junção de um conjunto com outro curso ou conjunto
                if (iteratorIntersection.getIntersectionCourse().contains("-")) {

                    //Insere um novo nome do tipo "Curso1-Curso2--Curso 3", sendo o Curso1-Curso2 um conjunto
                    nomeCurso.insert(0, iteratorIntersection.getIntersectionCourse() + "--");
                } else {
                    //Cria o nome do tipo "Curso1-Curso2"
                    nomeCurso.append("-").append(iteratorIntersection.getIntersectionCourse());
                }
                //Cria um novo CourseRelation para o conjunto
                CourseRelation course = this.getCourseByName(iteratorIntersection.getIntersectionCourse(), cs);

                //Ajusta o número de professores exclusivos
                exclusiveProfessors += course.getExclusiveProfessorCount();
            }
        }

        //Se formou um conjunto
        if (listIntersection.size() > 1) {

            //Troca o nome e adiciona na lista
            newCourse.setName(nomeCurso.toString());
            newCourse.setExclusiveProfessorCount(exclusiveProfessors);
            cs.add(newCourse);
            return true;
        }

        return false;
    }

    /**
     * Obtém um curso a partir de seu nome.
     *
     * @param course {@link String} que contém o curso a ser buscado.
     * @param cs     {@link List} de {@link CourseRelation} que contém todos os cursos.
     * @return {@link CourseRelation} com o nome buscado.
     * @throws ClassNotFoundException Erro caso o curso não esteja na lista.
     */
    public CourseRelation getCourseByName(String course, List<CourseRelation> cs) throws ClassNotFoundException {
        for (CourseRelation iterationCS : cs) {
            if (iterationCS.getName().equals(course)) {
                return iterationCS;
            }
        }
        throw new ClassNotFoundException("Curso não encontrado");
    }

    /**
     * Incrementa o número de professores exclusivos em um.
     */
    public void incrementExclusiveProfessorCount() {
        this.exclusiveProfessorCount++;
    }

    /**
     * Incrementa o número total de professores em um.
     */
    public void incrementTotalProfessorCount() {
        this.totalProfessors++;
    }

    /**
     * Calcula o número total de professores baseado nos exclusivos e os das intersecções.
     */
    public void sumTotalProfessors() {
        List<String> professors = new ArrayList<>();

        //Para cada uma das intersecções, verifica para não contar duas vezes o mesmo professor
        for (Intersection iteratorIntersection : this.getIntersection()) {
            if (professors.isEmpty()) {
                professors.addAll(iteratorIntersection.getProfessorsList());
            } else {
                for (String iteratorProfessors : iteratorIntersection.getProfessorsList()) {
                    if (!professors.contains(iteratorProfessors)) {
                        professors.add(iteratorProfessors);
                    }
                }
            }

        }
        this.totalProfessors = professors.size() + exclusiveProfessorCount;
    }

}
