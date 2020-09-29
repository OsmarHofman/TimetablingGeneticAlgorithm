package br.edu.ifsc.TimetablingGeneticAlgorithm.datapreview.model;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Classes;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Teacher;
import br.edu.ifsc.TimetablingGeneticAlgorithm.datapreview.classes.CourseRelation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.datapreview.classes.Intersection;
import br.edu.ifsc.TimetablingGeneticAlgorithm.datapreview.classes.ProfessorCourseStatus;
import br.edu.ifsc.TimetablingGeneticAlgorithm.datapreview.classes.ProfessorCourse;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.DTOIFSC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Cria as listas de professores com os cursos que leciona, e a relação entre os cursos
 */
public class ProfessorsScheduleCreation {

    private List<String> coursesList;
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

    public List<String> getCoursesList() {
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
        List<CourseGroup> courseGroupList = this.joinCourses(dtoifsc.getClasses());
        getProfessors(dtoifsc, courseGroupList);

    }

    private List<CourseGroup> joinCourses(List<Classes> courses) {
        List<CourseGroup> courseGroups = new ArrayList<>();
        for (Classes iterationClasses : courses) {
            String courseId = String.valueOf(iterationClasses.getId());
            if (courseGroups.isEmpty()) {
                courseGroups.add(new CourseGroup(courseId, new ArrayList<>(Collections.singletonList(iterationClasses.getId()))));
                this.coursesList.add(courseId);
            } else {
                boolean hasAdded = false;
                for (CourseGroup courseGroup : courseGroups) {
                    if (courseGroup.getId().equals(courseId)) {
                        courseGroup.getBlacklist().add(iterationClasses.getId());
                        hasAdded = true;
                    }
                }
                if (!hasAdded) {
                    courseGroups.add(new CourseGroup(courseId, new ArrayList<>(Collections.singletonList(iterationClasses.getId()))));
                    this.coursesList.add(courseId);
                }
            }
        }
        return courseGroups;
    }

    private void getProfessors(DTOIFSC dtoifsc, List<CourseGroup> courseGroups) {
        for (CourseGroup cg : courseGroups) {
            for (int ids : cg.getBlacklist()) {
                for (Lesson iterationLesson : dtoifsc.getLessons()) {
                    if (iterationLesson.getClassesId() == ids) {
                        for (Teacher iterationTeacher : dtoifsc.getProfessors()) {
                            for (int i = 0; i < iterationLesson.getTeacherId().length; i++) {
                                if (iterationTeacher.getId() == iterationLesson.getTeacherId()[i]) {
                                    if (this.professorsList.isEmpty()) {
                                        this.professorsList.add(new ProfessorCourse(String.valueOf(iterationTeacher.getId()), new ArrayList<>(Collections.singletonList(cg.getId()))));
                                    } else {
                                        boolean hasAdded = false;
                                        for (ProfessorCourse iterationPC : professorsList) {
                                            if (iterationPC.getProfessor().equals(String.valueOf(iterationTeacher.getId()))) {
                                                if (!iterationPC.getCourse().contains(cg.getId())) {
                                                    iterationPC.getCourse().add(cg.getId());
                                                }
                                                hasAdded = true;
                                            }
                                        }
                                        if (!hasAdded) {
                                            this.professorsList.add(new ProfessorCourse(String.valueOf(iterationTeacher.getId()), new ArrayList<>(Collections.singletonList(cg.getId()))));
                                        }
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
        for (String courseName : this.coursesList) {
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
                        if (iterationCourse.equals(courseRelation.getId()) || iterationCourse.equals(intersection.getIntersectionCourse())) {
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

    private static class CourseGroup {
        private String id;
        private List<Integer> blacklist;


        public CourseGroup(String id, List<Integer> coursesIds) {
            this.id = id;
            this.blacklist = coursesIds;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<Integer> getBlacklist() {
            return blacklist;
        }

        public void setBlacklist(List<Integer> blacklist) {
            this.blacklist = blacklist;
        }
    }


}
