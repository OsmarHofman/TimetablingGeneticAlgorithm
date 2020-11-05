package br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Classes;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Teacher;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.entities.*;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOIFSC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Cria as listas de professores com os cursos que leciona, e a relação entre os cursos
 */
public class ProfessorsScheduleCreation {

    private List<CourseGroup> coursesList;
    private List<ProfessorCourse> professorsList;
    private List<CourseRelation> courseRelationList;

    public ProfessorsScheduleCreation(DTOIFSC dtoifsc) {

        //Adiciona as listas de coursesList e professorsList
        this.getCoursesAndProfessors(dtoifsc);
        if (this.coursesList != null && this.professorsList != null) {
            this.createCourseRelation();
        } else {
            throw new NullPointerException("A lista de cursos ou de professores está vazia.");
        }
    }

    public List<CourseGroup> getCoursesList() {
        return coursesList;
    }

    public List<ProfessorCourse> getProfessorsList() {
        return professorsList;
    }

    public List<CourseRelation> getCourseRelationList() {
        return courseRelationList;
    }

    /**
     * Obtém os cursos com suas turmas e professores com os cursos que leciona.
     *
     * @param dtoifsc {@link DTOIFSC} que contém todos os dados de turmas, professores e etc.
     */
    private void getCoursesAndProfessors(DTOIFSC dtoifsc) {
        this.coursesList = new ArrayList<>();
        this.professorsList = new ArrayList<>();
        this.joinCourses(dtoifsc.getClasses());
        this.getProfessors(dtoifsc);
    }

    /**
     * Agrupa as turmas em cursos.
     *
     * @param courses {@link List} de {@link Classes} que contém todas as turmas.
     */
    private void joinCourses(List<Classes> courses) {
        for (Classes course : courses) {
            //A identificação do nome do curso vem pelo atributo short do XML
            String courseName = course.getShortName().substring(0, course.getShortName().length() - 1);
            if (this.coursesList.isEmpty()) {
                this.coursesList.add(new CourseGroup(courseName, new ArrayList<>(Collections.singletonList(course.getId()))));
            } else {

                //Variável para identificar se um curso já foi inserido ou não na lista
                boolean hasAdded = false;

                //Compara com todos os cursos na lista e insere se não estiver na lista
                for (CourseGroup courseGroup : this.coursesList) {
                    if (courseGroup.getGroupName().equals(courseName)) {
                        if (!courseGroup.getCourses().contains(course.getId())) {
                            courseGroup.getCourses().add(course.getId());
                            hasAdded = true;
                        }
                    }
                }

                //Se não inseriu na lista anteriormente
                if (!hasAdded) {
                    this.coursesList.add(new CourseGroup(courseName, new ArrayList<>(Collections.singletonList(course.getId()))));
                }
            }
        }
    }

    /**
     * Obtém os professores e os cursos que leciona.
     *
     * @param dtoifsc {@link DTOIFSC} que contém todos os dados de turmas, professores e etc.
     */
    private void getProfessors(DTOIFSC dtoifsc) {

        //Para cada um dos cursos
        for (CourseGroup cg : this.coursesList) {
            for (int courseId : cg.getCourses())
                for (Lesson iterationLesson : dtoifsc.getLessons()) {

                    //Encontra o curso da iteração
                    if (iterationLesson.getClassesId() == courseId) {
                        for (Teacher iterationTeacher : dtoifsc.getProfessors()) {

                            //Para cada um dos professores
                            for (int i = 0; i < iterationLesson.getTeacherId().length; i++) {
                                if (iterationTeacher.getId() == iterationLesson.getTeacherId()[i]) {
                                    if (this.professorsList.isEmpty()) {
                                        this.professorsList.add(new ProfessorCourse(iterationTeacher.getName(), new ArrayList<>(Collections.singletonList(cg.getGroupName()))));
                                    } else {

                                        //Variável para identificar se um professor já foi inserido ou não na lista
                                        boolean hasAdded = false;

                                        //Compara com todos os professores na lista e insere se não estiver na lista
                                        for (ProfessorCourse iterationPC : professorsList) {
                                            if (iterationPC.getProfessor().equals(iterationTeacher.getName())) {
                                                if (!iterationPC.getCourse().contains(cg.getGroupName())) {
                                                    iterationPC.getCourse().add(cg.getGroupName());
                                                }
                                                hasAdded = true;
                                            }
                                        }

                                        //Se não inseriu na lista anteriormente
                                        if (!hasAdded) {
                                            this.professorsList.add(new ProfessorCourse(iterationTeacher.getName(), new ArrayList<>(Collections.singletonList(cg.getGroupName()))));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

        }
    }


    /**
     * Centraliza as informações sobre os cursos e os professores.
     */
    private void createCourseRelation() {
        courseRelationList = new ArrayList<>();

        System.out.println("Criando a relação entre os professores e os cursos...\n");
        for (CourseGroup course : this.coursesList) {

            //Cria o curso
            String courseName = course.getGroupName();
            CourseRelation iterationCourseRelation = new CourseRelation(courseName);

            //Adiciona os professores ao curso
            for (ProfessorCourse professor : this.professorsList) {
                String[] result = professor.verifyCourse(courseName);

                /*Diferencia se o professor leciona exclusivamente para aquele curso, leciona para aquele curso e outros
                 *ao mesmo tempo, ou se ele não tem qualquer relação com aquele curso */
                if (result[0].equals(ProfessorCourseStatus.EXCLUSIVE.toString())) {
                    iterationCourseRelation.incrementExclusiveProfessorCount();
                    iterationCourseRelation.incrementTotalProfessorCount();

                } else if (result[0].equals(ProfessorCourseStatus.SHARED.toString())) {
                    iterationCourseRelation.checkListIntersection(result[1], professor.getCourse());
                    iterationCourseRelation.incrementTotalProfessorCount();
                }
            }
            this.courseRelationList.add(iterationCourseRelation);
        }

        //Faz a ligação com os professores
        for (CourseRelation courseRelation : this.courseRelationList) {
            for (Intersection intersection : courseRelation.getIntersection()) {
                for (ProfessorCourse professor_course : professorsList) {

                    /*Contagem do número de professores compartilhados, sendo que quando chega a 2, significa que
                     * ele leciona para múltiplos cursos, então é compartilhado, ou seja, está na intersecção*/
                    int relatedCourseNumber = 0;
                    for (String iterationCourse : professor_course.getCourse()) {
                        if (iterationCourse.equals(courseRelation.getName()) || iterationCourse.equals(intersection.getIntersectionCourse())) {
                            relatedCourseNumber++;
                        }
                        if (relatedCourseNumber == 2) {
                            intersection.getProfessorsList().add(professor_course.getProfessor());
                            break;
                        }
                    }
                }
            }
        }
        System.out.println("Relação entre professores e cursos criada!\n");
    }
}
