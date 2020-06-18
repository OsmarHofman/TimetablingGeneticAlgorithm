package preprocessing.model;

import preprocessing.classes.CourseRelation;
import preprocessing.classes.Intersection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EntitySchedule {

    private List<CourseRelation> courseRelationList;

    public EntitySchedule(List<CourseRelation> courseRelationList) {
        this.courseRelationList = courseRelationList;
    }

    public void createSet(int percentage) throws ClassNotFoundException {
        List<String> splitedSetName;
        String setName = "";
        boolean hasJoined = true;

        while (hasJoined) {

            splitedSetName = new ArrayList<>();
            for (CourseRelation iterationCS : this.courseRelationList) {
                hasJoined = iterationCS.joinIntersections(percentage, this.courseRelationList);

                if (hasJoined) {
                    setName = courseRelationList.get(courseRelationList.size() - 1).getName();
                    if (setName.contains("--")) {
                        splitedSetName = Arrays.asList(setName.split("--"));
                        splitedSetName.set(1, splitedSetName.get(1) + '-');
                    } else {
                        splitedSetName = Arrays.asList(setName.split("-"));
                    }
                    break;
                }
            }
            if (splitedSetName.size() != 0) {
                List<Intersection> intersectionList = new ArrayList<>();
                removeCourses(courseRelationList, splitedSetName, intersectionList);
                renameIntersection(courseRelationList, splitedSetName, setName);
                mergeIntersections(courseRelationList, setName);

            }
            //FIXME adicionar informacao do professor exclusivo
        }
        System.out.println("alo?" + courseRelationList.toString());
    }

    private boolean removeCourses(List<CourseRelation> cs, List<String> nomeSeparado, List<Intersection> intersectionList) {
        for (int i = 0; i < cs.size(); i++) {
            for (int j = 0; j < nomeSeparado.size(); j++) {
                if (cs.get(i).getName().equals(nomeSeparado.get(j))) {
                    if (intersectionList.isEmpty())
                        intersectionList.addAll(cs.get(i).getIntersection());
                    else {
                        this.joinIntersections(cs.get(i).getIntersection(), intersectionList);
                    }
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
        List<String> auxList = new ArrayList<>();
        for (Intersection iil : intersectionList) {
            for (Intersection iteratorCS : csIntersections) {
                if (iil.getIntersectionCourse().equals(iteratorCS.getIntersectionCourse())) {
                    for (String iteratorStringInt : iil.getProfessorsNameList()) {
                        for (String iteratorStringCS : iteratorCS.getProfessorsNameList()) {
                            if (!iteratorStringInt.equals(iteratorStringCS)) {
                                auxList.add(iteratorStringCS);
                            }
                        }
                    }
                    iil.getProfessorsNameList().addAll(auxList);
                }
            }
        }
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
        List<Integer> indexes = new ArrayList<>();
        for (CourseRelation iteratorCS : cs) {
            Intersection listSameName = new Intersection();
            int intersecProfessorIndex = 0;
            for (Intersection iteratorIntersec : iteratorCS.getIntersection()) {
                if (iteratorIntersec.getIntersectionCourse().equals(nomeTudoJunto)) {
                    if (listSameName.getProfessorsNameList().isEmpty()) {
                        listSameName = iteratorIntersec;
                        intersecProfessorIndex = iteratorCS.getIntersection().indexOf(iteratorIntersec);
                    } else {
                        List<String> professors = new ArrayList<>();
                        int professorSize = listSameName.getProfessorsNameList().size();
                        int count = 0;
                        for (int i = 0; i < professorSize; i++) {
                            for (String iteratorProfessorsCS : iteratorIntersec.getProfessorsNameList()) {
                                if (!listSameName.getProfessorsNameList().get(i).equals(iteratorProfessorsCS)) {
                                    count++;
                                    if (count >= listSameName.getProfessorsNameList().size()) {
                                        professors.add(iteratorProfessorsCS);
                                        listSameName.setIntersectionProfessorsCount(listSameName.getIntersectionProfessorsCount() + 1);
                                    }
                                }
                            }
                        }
                        iteratorCS.getIntersection().get(intersecProfessorIndex).getProfessorsNameList().addAll(professors);
                        indexes.add(iteratorCS.getIntersection().indexOf(iteratorIntersec));
                    }
                }
            }
            Collections.reverse(indexes);
            for (int indexInt : indexes) {
                iteratorCS.getIntersection().remove(indexInt);
            }
        }
    }


}
