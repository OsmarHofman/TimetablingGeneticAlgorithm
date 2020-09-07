package br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model;

import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.classes.CourseRelation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.classes.Intersection;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.classes.Professor_Course;

import java.util.*;

/**
 * Classe que representa as relações entre os cursos e os professores
 */
public class EntitySchedule {

    private List<CourseRelation> courseRelationList;
    private List<Professor_Course> professorRelation;


    public EntitySchedule(ProfessorsScheduleCreation professorsScheduleCreation) {
        this.professorRelation = professorsScheduleCreation.getProfessorsList();
        this.courseRelationList = professorsScheduleCreation.getCourseRelationList();
    }

    public List[] createSet(int percentage) throws ClassNotFoundException {
        String reportData = this.courseRelationList.toString();
        List<String> splitSetName;
        String setName;

        boolean hasJoined = true;
        while (hasJoined) {
            CourseRelation lastCourse = null;
            splitSetName = new ArrayList<>();
            for (CourseRelation iterationCS : this.courseRelationList) {
                hasJoined = iterationCS.joinIntersections(percentage, this.courseRelationList);

                if (hasJoined) {
                    lastCourse = this.courseRelationList.get(this.courseRelationList.size() - 1);
                    splitSetName = this.adjustSetName(lastCourse.getName(), splitSetName);
                    break;
                }
            }
            if (splitSetName.size() != 0) {
                lastCourse.setName(lastCourse.getName().replace("--", "-"));
                setName = lastCourse.getName();

                List<Integer> toRemoveIndexes = new ArrayList<>();
                List<Intersection> intersectionList = new ArrayList<>();
                //seleciona os cursos iguais e junta suas intersecções
                List<Intersection> innerIntersections = this.selectCoursesToRemove(splitSetName, intersectionList, toRemoveIndexes);

                //renomea o nome dos cursos de cada professor, para substituirem pelo conjunto formado
                this.renameProfessorsCourses(splitSetName, lastCourse.getName());

                //agora passa o setName, que é o nome inteiro do conjunto sem split, ja que foi substituido antes
                this.verifyExclusiveProfessor(innerIntersections, setName);

                //efetivamente retira os cursos que compoem um conjunto
                this.removeItemsOnIndexes(toRemoveIndexes, this.courseRelationList);

                //renomeia todos os cursos que tenham uma intersecção com o conjunto criado
                this.renameIntersection(splitSetName, setName);

                //junta as intersecções do conjunto que tem nome igual aos cursos foram usados para formá-lo
                this.mergeIntersections(setName);

                //recalcula o total de professores do conjunto
                lastCourse.sumTotalProfessors();

                reportData = this.courseRelationList.toString();
            }
        }
        String newReportData = reportData.replace("[", "").replace("]", "");
        String[] splitNewReportData = newReportData.split(",");
        List[] formattedDataList = new ArrayList[splitNewReportData.length];
        for (int i = 0; i < formattedDataList.length; i++)
            formattedDataList[i] = new ArrayList<>(Arrays.asList(splitNewReportData[i].split("-")));


        return formattedDataList;
    }

    private List<String> adjustSetName(String setName, List<String> splitSetName) {
        if (setName.contains("--")) {
            splitSetName.addAll(Arrays.asList(setName.split("--")));
            List<String> nameCourses = new ArrayList<>();
            List<Integer> indexes = new ArrayList<>();
            for (String iteratorSplitName : splitSetName) {
                if (iteratorSplitName.contains("-")) {
                    if (ListOperationUtil.itemIsNotInList(iteratorSplitName, this.courseRelationList)) {
                        nameCourses.addAll(Arrays.asList(iteratorSplitName.split("-")));
                        indexes.add(splitSetName.indexOf(iteratorSplitName));
                    }
                }
            }
            this.removeItemsOnIndexes(indexes, splitSetName);
            splitSetName.addAll(nameCourses);

        } else {
            splitSetName = Arrays.asList(setName.split("-"));
        }
        return splitSetName;
    }

    private List<Intersection> selectCoursesToRemove(List<String> splitname, List<Intersection> intersectionList, List<Integer> toRemoveIndexes) {
        /*lista que vai receber os intersections removidos que tem o nome do conjunto. Ex.: Conjunto TECINFO-CC, vai tirar
        as intersecções TECINFO e CC, e adicionar a essa lista*/
        List<Intersection> removedInnerIntersection = new ArrayList<>();
        for (int i = 0; i < this.courseRelationList.size(); i++) {
            CourseRelation iterationCourse = this.courseRelationList.get(i);
            for (int j = 0; j < splitname.size(); j++) {
                if (iterationCourse.getName().equals(splitname.get(j))) {
                    if (intersectionList.isEmpty())
                        intersectionList.addAll(iterationCourse.getIntersection());
                    else
                        this.joinIntersections(iterationCourse.getIntersection(), intersectionList);

                    this.courseRelationList.get(this.courseRelationList.size() - 1).setIntersection(intersectionList);
                    //remove as intersections e as retorna, como explicado acima
                    removedInnerIntersection = removeInnerDuplicatedCourses(splitname, this.courseRelationList.get(this.courseRelationList.size() - 1).getIntersection(), removedInnerIntersection);

                    //não efetivamente tira os cursos da lista, mas guarda seus indices em uma lista, para remove-los depois
                    toRemoveIndexes.add(i);
                }
            }
        }
        return removedInnerIntersection;
    }

    private void joinIntersections(List<Intersection> firstCourseIntersections, List<Intersection> secondCourseIntersections) {
        List<Intersection> newIntersections = new ArrayList<>();
        for (Intersection secondCourseIntersection : secondCourseIntersections) {
            for (Intersection firstCourseIntersection : firstCourseIntersections) {

                if (secondCourseIntersection.getIntersectionCourse().equals(firstCourseIntersection.getIntersectionCourse()))
                    secondCourseIntersection.addProfessorsToIntersection(firstCourseIntersection.getProfessorsNameList());

                if (ListOperationUtil.itemIsNotInList(firstCourseIntersection.getIntersectionCourse(), secondCourseIntersections) && !newIntersections.contains(firstCourseIntersection))
                    newIntersections.add(firstCourseIntersection);
            }
        }
        //nessa lista que estará as interseções corretas
        secondCourseIntersections.addAll(newIntersections);
    }

    private List<Intersection> removeInnerDuplicatedCourses(List<String> splitName, List<Intersection> intersectionList, List<Intersection> removedInnerIntersection) {
        for (int i = 0; i < intersectionList.size(); i++) {
            for (int j = 0; j < splitName.size(); j++) {
                if (intersectionList.get(i).getIntersectionCourse().equals(splitName.get(j))) {
                    //essa lista é onde vai ser colocada as intersections removidas
                    removedInnerIntersection.add(intersectionList.remove(i));
                    return removeInnerDuplicatedCourses(splitName, intersectionList, removedInnerIntersection);
                }
            }
        }
        return removedInnerIntersection;
    }

    private void renameProfessorsCourses(List<String> splitSetName, String courseRelation) {
        for (Professor_Course iterationPC : this.professorRelation) {
            List<Integer> indexes = new ArrayList<>();
            for (String iterationCourse : iterationPC.getCourse()) {
                for (String iterationCourseName : splitSetName) {
                    if (iterationCourse.equals(iterationCourseName))
                        indexes.add(iterationPC.getCourse().indexOf(iterationCourse));
                }
            }
            this.removeItemsOnIndexes(indexes, iterationPC.getCourse());
            if (!indexes.isEmpty()) {
                iterationPC.getCourse().add(courseRelation);
            }
        }
    }

    private void verifyExclusiveProfessor(List<Intersection> innerIntersections, String setName) throws ClassNotFoundException {
        CourseRelation lastCourse = this.courseRelationList.get(this.courseRelationList.size() - 1);
        List<String> professorBlackList = new ArrayList<>();
        for (Intersection iterationIntersection : innerIntersections) {
            for (String iterationProfessor : iterationIntersection.getProfessorsNameList()) {
                Professor_Course professor_course = ListOperationUtil.getProfessorByName(iterationProfessor, this.professorRelation);
                if (!professorBlackList.contains(professor_course.getProfessor()) && professor_course.checkExclusivity(setName)) {
                    lastCourse.incrementExclusiveProfessorCount();
                    professorBlackList.add(professor_course.getProfessor());
                }
            }

        }
    }

    private void renameIntersection(List<String> splitName, String setName) {
        for (CourseRelation iterationCourse : this.courseRelationList) {
            for (Intersection iterationIntersec : iterationCourse.getIntersection()) {
                for (String iterationSplitName : splitName) {
                    if (iterationIntersec.getIntersectionCourse().equals(iterationSplitName)) {
                        iterationIntersec.setIntersectionCourse(setName);
                    }
                }
            }
        }
    }

    private void mergeIntersections(String setName) {
        for (CourseRelation iterationCourseRelation : this.courseRelationList) {
            List<Integer> indexes = new ArrayList<>();
            Intersection listSameName = new Intersection();
            int intersecProfessorIndex = 0;
            for (Intersection iterationIntersec : iterationCourseRelation.getIntersection()) {

                if (iterationIntersec.getIntersectionCourse().equals(setName)) {

                    if (listSameName.getProfessorsNameList().isEmpty()) {
                        listSameName = iterationIntersec;
                        intersecProfessorIndex = iterationCourseRelation.getIntersection().indexOf(iterationIntersec);
                    } else {
                        iterationCourseRelation.getIntersection().get(intersecProfessorIndex).addProfessorsToIntersection(iterationIntersec.getProfessorsNameList());
                        indexes.add(iterationCourseRelation.getIntersection().indexOf(iterationIntersec));
                    }
                }
                iterationIntersec.adjustProfessorsCount();
            }
            listSameName.adjustProfessorsCount();
            this.removeItemsOnIndexes(indexes, iterationCourseRelation.getIntersection());
        }
    }

    private void removeItemsOnIndexes(List<Integer> indexes, List<?> list) {
        Collections.reverse(indexes);
        for (int index : indexes) {
            list.remove(index);
        }
    }
}
