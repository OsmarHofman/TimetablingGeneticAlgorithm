package br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Classes;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Teacher;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Course;
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
        this.getCoursesAndProfessorsByFile(dtoifsc);
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

    private void getCoursesAndProfessorsByFile(DTOIFSC dtoifsc) {
        this.coursesList = new ArrayList<>();
        this.professorsList = new ArrayList<>();
        this.joinCourses(dtoifsc.getClasses());
        this.getProfessors(dtoifsc);

    }

    private void joinCourses(List<Classes> courses) {
        for (Classes course : courses) {
            String courseName = course.getShortName().substring(0, course.getShortName().length() - 1);
            if (this.coursesList.isEmpty()) {
                this.coursesList.add(new CourseGroup(courseName, new ArrayList<>(Collections.singletonList(course.getId()))));
            } else {
                boolean hasAdded = false;
                for (CourseGroup courseGroup : this.coursesList) {
                    if (courseGroup.getGroupName().equals(courseName)) {
                        if (!courseGroup.getCourses().contains(course.getId())) {
                            courseGroup.getCourses().add(course.getId());
                            hasAdded = true;
                        }
                    }
                }
                if (!hasAdded) {
                    this.coursesList.add(new CourseGroup(courseName, new ArrayList<>(Collections.singletonList(course.getId()))));
                }
            }
        }
    }

    private void getProfessors(DTOIFSC dtoifsc) {
        for (CourseGroup cg : this.coursesList) {
            for (int ids : cg.getCourses())
                for (Lesson iterationLesson : dtoifsc.getLessons()) {
                    if (iterationLesson.getClassesId() == ids) {
                        for (Teacher iterationTeacher : dtoifsc.getProfessors()) {
                            for (int i = 0; i < iterationLesson.getTeacherId().length; i++) {
                                if (iterationTeacher.getId() == iterationLesson.getTeacherId()[i]) {
                                    if (this.professorsList.isEmpty()) {
                                        this.professorsList.add(new ProfessorCourse(iterationTeacher.getName(), new ArrayList<>(Collections.singletonList(cg.getGroupName()))));
                                    } else {
                                        boolean hasAdded = false;
                                        for (ProfessorCourse iterationPC : professorsList) {
                                            if (iterationPC.getProfessor().equals(iterationTeacher.getName())) {
                                                if (!iterationPC.getCourse().contains(cg.getGroupName())) {
                                                    iterationPC.getCourse().add(cg.getGroupName());
                                                }
                                                hasAdded = true;
                                            }
                                        }
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


    private void createCourseRelation() {
        courseRelationList = new ArrayList<>();

        System.out.println("Criando a relação entre os professores e os cursos...\n");
        for (CourseGroup course : this.coursesList) {
            String courseName = course.getGroupName();
            CourseRelation iterationCourseRelation = new CourseRelation(courseName);
            for (ProfessorCourse professor : this.professorsList) {
                String[] result = professor.verifyCourse(courseName);
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

        //link professors
        for (CourseRelation courseRelation : this.courseRelationList) {
            for (Intersection intersection : courseRelation.getIntersection()) {
                for (ProfessorCourse professor_course : professorsList) {
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
