package preprocessing.model;

import preprocessing.classes.CourseRelation;
import preprocessing.classes.Intersection;
import preprocessing.classes.Professor_Course;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EntitySchedule {

    private List<CourseRelation> courseRelationList;
    private List<Professor_Course> professorRelation;
    private List<String> professorBlackList = new ArrayList<>();


    public EntitySchedule(ProfessorsScheduleCreation professorsScheduleCreation) {
        this.professorRelation = professorsScheduleCreation.getProfessorsList();
        this.courseRelationList = professorsScheduleCreation.getCourseRelationList();
    }

    public void createSet(int percentage) throws ClassNotFoundException {
        List<String> splitedSetName;
        String setName = "";
        boolean hasJoined = true;

        while (hasJoined) {

            splitedSetName = new ArrayList<>();
            for (CourseRelation iterationCS : courseRelationList) {
                hasJoined = iterationCS.joinIntersections(percentage, courseRelationList);

                if (hasJoined) {
                    setName = courseRelationList.get(courseRelationList.size() - 1).getName();
                    if (setName.contains("--")) {
                        splitedSetName.addAll(Arrays.asList(setName.split("--")));
                        List<String> nameCourses = new ArrayList<>();
                        List<Integer> indexes = new ArrayList<>();
                        for (String iteratorSplitName : splitedSetName) {
                            if (iteratorSplitName.contains("-")) {
                                if (!Intersection.hasCourseRelationInList(iteratorSplitName, this.courseRelationList)) {
                                    nameCourses.addAll(Arrays.asList(iteratorSplitName.split("-")));
                                    indexes.add(splitedSetName.indexOf(iteratorSplitName));
                                }
                            }
                        }
                        Collections.reverse(indexes);
                        for (int iteratorInt : indexes) {
                            splitedSetName.remove(iteratorInt);
                        }
                        splitedSetName.addAll(nameCourses);

                    } else {
                        splitedSetName = Arrays.asList(setName.split("-"));
                    }
                    break;
                }
            }
            if (splitedSetName.size() != 0) {
                List<Intersection> intersectionList = new ArrayList<>();
                courseRelationList.get(courseRelationList.size() - 1).setName(courseRelationList.get(courseRelationList.size() - 1).getName().replace("--", "-"));
                setName = courseRelationList.get(courseRelationList.size() - 1).getName();
                removeCourses(courseRelationList, splitedSetName, intersectionList);
                renameIntersection(courseRelationList, splitedSetName, setName);
                mergeIntersections(courseRelationList, setName);
                renameProfessorsCourses(splitedSetName, courseRelationList.get(courseRelationList.size() - 1).getName());
                //FIXME recalcular soma total dos professores (verificar professores exclusivos)
                sumTotalProfessors(courseRelationList.get(courseRelationList.size() - 1));

            }
        }
        System.out.println(courseRelationList.toString());

    }

    private void renameProfessorsCourses(List<String> splitedSetName, String courseRelation) {
        for (Professor_Course iteratorPC : this.professorRelation) {
            List<Integer> indexes = new ArrayList<>();
            for (String iteratorCourses : iteratorPC.getCourse()) {
                for (String iteratorCourseName : splitedSetName) {
                    if (iteratorCourses.equals(iteratorCourseName))
                        indexes.add(iteratorPC.getCourse().indexOf(iteratorCourses));
                }
            }
            Collections.reverse(indexes);
            for (int iteratorIndex : indexes) {
                iteratorPC.getCourse().remove(iteratorIndex);
            }
            if (!indexes.isEmpty()) {
                iteratorPC.getCourse().add(courseRelation);
            }
        }
    }

    private void verifyExclusiveProfessor(CourseRelation course, List<String> splitedSetName) throws ClassNotFoundException {
        for (Intersection iteratorIntersection : course.getIntersection()) {
            for (String iteratorProfessor : iteratorIntersection.getProfessorsNameList()) {
                Professor_Course professor_course = Intersection.getProfessorByName(iteratorProfessor, this.professorRelation);
                if (!professorBlackList.contains(professor_course.getProfessor()) && professor_course.checkExclusivity(splitedSetName)) {
                    course.setExclusiveProfessorCount(course.getExclusiveProfessorCount() + 1);
                    professorBlackList.add(professor_course.getProfessor());
                }
            }

        }
    }


    private void sumTotalProfessors(CourseRelation courseRelation) {
        List<String> professors = new ArrayList<>();
        for (Intersection iteratorIntersection : courseRelation.getIntersection()) {
            if (professors.isEmpty()) {
                professors.addAll(iteratorIntersection.getProfessorsNameList());
            } else {
                for (String iteratorProfessors : iteratorIntersection.getProfessorsNameList()) {
                    if (!professors.contains(iteratorProfessors)) {
                        professors.add(iteratorProfessors);
                    }
                }
            }

        }
        courseRelation.setTotalProfessors(professors.size() + courseRelation.getExclusiveProfessorCount());
    }

    private boolean removeCourses(List<CourseRelation> cs, List<String> nomeSeparado, List<Intersection> intersectionList) throws ClassNotFoundException {
        for (int i = 0; i < cs.size(); i++) {
            for (int j = 0; j < nomeSeparado.size(); j++) {
                if (cs.get(i).getName().equals(nomeSeparado.get(j))) {
                    if (intersectionList.isEmpty())
                        intersectionList.addAll(cs.get(i).getIntersection());
                    else {
                        this.joinIntersections(cs.get(i).getIntersection(), intersectionList);
                    }
                    verifyExclusiveProfessor(this.courseRelationList.get(this.courseRelationList.size() - 1), nomeSeparado);
                    cs.get(cs.size() - 1).setIntersection(intersectionList);
                    removeCourses(nomeSeparado, cs.get(cs.size() - 1).getIntersection());
                    cs.remove(i);
                    return removeCourses(cs, nomeSeparado, intersectionList);
                }
            }
        }
        return true;
    }

    private boolean removeCourses(List<String> nomeSeparado, List<Intersection> intersectionList) {
        for (int i = 0; i < intersectionList.size(); i++) {
            for (int j = 0; j < nomeSeparado.size(); j++) {
                if (intersectionList.get(i).getIntersectionCourse().equals(nomeSeparado.get(j))) {
                    intersectionList.remove(i);
                    return removeCourses(nomeSeparado, intersectionList);
                }
            }
        }
        return true;
    }

    private void joinIntersections(List<Intersection> csIntersections, List<Intersection> intersectionList) {
        List<Intersection> newIntersections = new ArrayList<>();
        for (Intersection iil : intersectionList) {
            for (Intersection iteratorCS : csIntersections) {
                if (iil.getIntersectionCourse().equals(iteratorCS.getIntersectionCourse())) {
                    List<String> auxList = new ArrayList<>();
                    for (String iteratorStringCS : iteratorCS.getProfessorsNameList()) {
                        if (!Intersection.hasProfessorInList(iteratorStringCS, iil.getProfessorsNameList()) && !auxList.contains(iteratorStringCS)) {
                            auxList.add(iteratorStringCS);
                        }
                    }
                    iil.getProfessorsNameList().addAll(auxList);
                }
                if (!Intersection.hasIntersectionInList(iteratorCS.getIntersectionCourse(), intersectionList) && !newIntersections.contains(iteratorCS)) {
                    newIntersections.add(iteratorCS);
                }

            }

        }
        intersectionList.addAll(newIntersections);
    }

    private void renameIntersection(List<CourseRelation> cs, List<String> nomeSeparado, String nomeTudoJunto) {
        for (CourseRelation iterationCR : cs) {
            for (Intersection iterationIntersec : iterationCR.getIntersection()) {
                for (String iterationName : nomeSeparado) {
                    if (iterationIntersec.getIntersectionCourse().equals(iterationName)) {
                        iterationIntersec.setIntersectionCourse(nomeTudoJunto);
                    }
                }
            }

        }
    }

    private static void mergeIntersections(List<CourseRelation> cs, String nomeTudoJunto) {
        for (CourseRelation iteratorCS : cs) {
            List<Integer> indexes = new ArrayList<>();
            Intersection listSameName = new Intersection();
            int intersecProfessorIndex = 0;
            for (Intersection iteratorIntersec : iteratorCS.getIntersection()) {
                if (iteratorIntersec.getIntersectionCourse().equals(nomeTudoJunto)) {
                    if (listSameName.getProfessorsNameList().isEmpty()) {
                        listSameName = iteratorIntersec;
                        intersecProfessorIndex = iteratorCS.getIntersection().indexOf(iteratorIntersec);
                    } else {
                        List<String> professors = new ArrayList<>();
                        for (String iteratorProfessorsCS : iteratorIntersec.getProfessorsNameList()) {
                            if (!Intersection.hasProfessorInList(iteratorProfessorsCS, listSameName.getProfessorsNameList()) && !professors.contains(iteratorProfessorsCS)) {
                                professors.add(iteratorProfessorsCS);
                            }
                        }
                        iteratorCS.getIntersection().get(intersecProfessorIndex).getProfessorsNameList().addAll(professors);
                        indexes.add(iteratorCS.getIntersection().indexOf(iteratorIntersec));
                    }
                }
                iteratorIntersec.setIntersectionProfessorsCount(iteratorIntersec.getProfessorsNameList().size());
            }
            listSameName.setIntersectionProfessorsCount(listSameName.getProfessorsNameList().size());
            Collections.reverse(indexes);
            for (int indexInt : indexes) {
                iteratorCS.getIntersection().remove(indexInt);
            }
        }
    }


}
