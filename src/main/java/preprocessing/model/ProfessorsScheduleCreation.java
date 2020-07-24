package preprocessing.model;

import domain.ifsc.Classes;
import domain.ifsc.Lesson;
import domain.ifsc.Teacher;
import preprocessing.classes.CourseRelation;
import preprocessing.classes.Intersection;
import preprocessing.classes.ProfessorCourseStatus;
import preprocessing.classes.Professor_Course;
import util.DTOIFSC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfessorsScheduleCreation {

    private List<String> coursesList;
    private List<Professor_Course> professorsList;
    private List<CourseRelation> courseRelationList;

    public ProfessorsScheduleCreation(DTOIFSC dtoifsc){
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

    public List<Professor_Course> getProfessorsList() {
        return professorsList;
    }

    public List<CourseRelation> getCourseRelationList() {
        return courseRelationList;
    }

    private void getCoursesAndProfessorsByFile(DTOIFSC dtoifsc) {
        this.coursesList = new ArrayList<>();
        this.professorsList = new ArrayList<>();
        List<CourseGroup> courseGroupList = joinCourses(dtoifsc.getClasses());
        getProfessors(dtoifsc, courseGroupList);

    }

    private List<CourseGroup> joinCourses(List<Classes> courses) {
        List<CourseGroup> cg125 = new ArrayList<>();
        for (Classes iterationClasses : courses) {
            String courseName = iterationClasses.getShortName().substring(0, iterationClasses.getShortName().length() - 1);
            if (cg125.isEmpty()) {
                cg125.add(new CourseGroup(courseName, new ArrayList<>(Collections.singletonList(iterationClasses.getId()))));
                this.coursesList.add(courseName);
            } else {
                boolean hasAdded = false;
                for (CourseGroup courseGroup : cg125) {
                    if (courseGroup.getName().equals(courseName)) {
                        courseGroup.getCoursesIds().add(iterationClasses.getId());
                        hasAdded = true;
                    }
                }
                if (!hasAdded) {
                    cg125.add(new CourseGroup(courseName, new ArrayList<>(Collections.singletonList(iterationClasses.getId()))));
                    this.coursesList.add(courseName);
                }
            }
        }
        return cg125;
    }

    private void getProfessors(DTOIFSC dtoifsc, List<CourseGroup> courseGroups) {
        for (CourseGroup cg : courseGroups) {
            for (int ids : cg.getCoursesIds()) {
                for (Lesson iterationLesson : dtoifsc.getLessons()) {
                    if (iterationLesson.getClassesId() == ids) {
                        for (Teacher iterationTeacher : dtoifsc.getProfessors()) {
                            for (int i = 0; i < iterationLesson.getTeacherId().length; i++) {
                                if (iterationTeacher.getId() == iterationLesson.getTeacherId()[i]) {
                                    if (this.professorsList.isEmpty()) {
                                        this.professorsList.add(new Professor_Course(iterationTeacher.getName(), new ArrayList<>(Collections.singletonList(cg.getName()))));
                                    } else {
                                        boolean hasAdded = false;
                                        for (Professor_Course iterationPC : professorsList) {
                                            if (iterationPC.getProfessor().equals(iterationTeacher.getName())) {
                                                if (!iterationPC.getCourse().contains(cg.getName())) {
                                                    iterationPC.getCourse().add(cg.getName());
                                                }
                                                hasAdded = true;
                                            }
                                        }
                                        if (!hasAdded) {
                                            this.professorsList.add(new Professor_Course(iterationTeacher.getName(), new ArrayList<>(Collections.singletonList(cg.getName()))));
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
            for (Professor_Course professor : this.professorsList) {
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
                for (Professor_Course professor_course : professorsList) {
                    int relatedCourseNumber = 0;
                    for (String iterationCourse : professor_course.getCourse()) {
                        if (iterationCourse.equals(courseRelation.getName()) || iterationCourse.equals(intersection.getIntersectionCourse())) {
                            relatedCourseNumber++;
                        }
                        if (relatedCourseNumber == 2) {
                            intersection.getProfessorsNameList().add(professor_course.getProfessor());
                            break;
                        }
                    }
                }
            }
        }
        System.out.println("Relação entre professores e cursos criada!\n");
    }

    private static class CourseGroup {
        private String name;
        private List<Integer> coursesIds;


        public CourseGroup(String name, List<Integer> coursesIds) {
            this.name = name;
            this.coursesIds = coursesIds;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Integer> getCoursesIds() {
            return coursesIds;
        }

        public void setCoursesIds(List<Integer> coursesIds) {
            this.coursesIds = coursesIds;
        }
    }


}
