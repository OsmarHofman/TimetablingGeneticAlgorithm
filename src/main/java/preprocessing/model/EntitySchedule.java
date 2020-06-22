package preprocessing.model;

import preprocessing.classes.CourseRelation;
import preprocessing.classes.Intersection;
import preprocessing.classes.Professor_Course;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EntitySchedule {

    private List<CourseRelation> courseRelationList;
    private List<Professor_Course> professorRelation;


    public EntitySchedule(ProfessorsScheduleCreation professorsScheduleCreation) {
        this.professorRelation = professorsScheduleCreation.getProfessorsList();
        this.courseRelationList = professorsScheduleCreation.getCourseRelationList();
    }

    public void createSet(int percentage) throws ClassNotFoundException, IOException {
        FileWriter report = this.generateReport(percentage);
        String reportData = "";
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
                    setName = lastCourse.getName();
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
                        Collections.reverse(indexes);
                        for (int iteratorInt : indexes) {
                            splitSetName.remove(iteratorInt);
                        }
                        splitSetName.addAll(nameCourses);

                    } else {
                        splitSetName = Arrays.asList(setName.split("-"));
                    }
                    break;
                }
            }
            if (splitSetName.size() != 0) {
                List<Intersection> intersectionList = new ArrayList<>();
                lastCourse.setName(lastCourse.getName().replace("--", "-"));
                setName = lastCourse.getName();

                List<Integer> toRemoveIndexes = new ArrayList<>();
                //antigo remove courses
                List<Intersection> innerIntersections = selectCoursesToRemove(splitSetName, intersectionList, toRemoveIndexes);

                //renomea o nome dos cursos de cada professor, para substituirem pelo conjunto formado
                renameProfessorsCourses(splitSetName, lastCourse.getName());

                //agora passa o setName, que é o nome inteiro do conjunto sem split, ja que foi substituido antes
                verifyExclusiveProfessor(innerIntersections, setName);

                //efetivamente retira os cursos que compoem um conjunto
                Collections.reverse(toRemoveIndexes);
                for (int iteratorIndexes : toRemoveIndexes) {
                    this.courseRelationList.remove(iteratorIndexes);
                }

                renameIntersection(splitSetName, setName);
                mergeIntersections(setName);
                lastCourse.sumTotalProfessors();


                report.append("\n\n\n--------------------------Nova Geração--------------------------\nNovo conjunto: " + setName + "\n\n");

                report.append(courseRelationList.toString());
            }

        }
        System.out.println(reportData);
        PrintWriter gravarArq = new PrintWriter(report);
        report.close();
        gravarArq.close();
    }

    private void renameProfessorsCourses(List<String> splitSetName, String courseRelation) {
        for (Professor_Course iteratorPC : this.professorRelation) {
            List<Integer> indexes = new ArrayList<>();
            for (String iteratorCourses : iteratorPC.getCourse()) {
                for (String iteratorCourseName : splitSetName) {
                    if (iteratorCourses.equals(iteratorCourseName))
                        indexes.add(iteratorPC.getCourse().indexOf(iteratorCourses));
                }
            }
            this.removeItemsOnIndexes(indexes, iteratorPC.getCourse());
            if (!indexes.isEmpty()) {
                iteratorPC.getCourse().add(courseRelation);
            }
        }
    }

    private void verifyExclusiveProfessor(List<Intersection> innerIntersections, String setName) throws ClassNotFoundException {
        CourseRelation lastCourse = this.courseRelationList.get(this.courseRelationList.size() - 1);
        List<String> professorBlackList = new ArrayList<>();
        for (Intersection iteratorIntersection : innerIntersections) {
            for (String iteratorProfessor : iteratorIntersection.getProfessorsNameList()) {
                Professor_Course professor_course = ListOperationUtil.getProfessorByName(iteratorProfessor, this.professorRelation);
                if (!professorBlackList.contains(professor_course.getProfessor()) && professor_course.checkExclusivity(setName)) {
                    lastCourse.incrementExclusiveProfessorCount();
                    professorBlackList.add(professor_course.getProfessor());
                }
            }

        }
    }

    //TODO refatorar daqui para baixo, e o metodo createSet
    private List<Intersection> selectCoursesToRemove(List<String> nomeSeparado, List<Intersection> intersectionList, List<Integer> toRemoveIndexes) {
        /*lista que vai receber os intersections removidos que tem o nome do conjunto. Ex.: Conjunto TECINFO-CC, vai tirar
        as intersecções TECINFO e CC, e adicionar a essa lista*/
        List<Intersection> removedInnerIntersection = new ArrayList<>();
        for (int i = 0; i < this.courseRelationList.size(); i++) {
            for (int j = 0; j < nomeSeparado.size(); j++) {
                if (this.courseRelationList.get(i).getName().equals(nomeSeparado.get(j))) {
                    if (intersectionList.isEmpty())
                        intersectionList.addAll(this.courseRelationList.get(i).getIntersection());
                    else {
                        this.joinIntersections(this.courseRelationList.get(i).getIntersection(), intersectionList);
                    }
                    this.courseRelationList.get(this.courseRelationList.size() - 1).setIntersection(intersectionList);
                    //antigo removeCourses sobrecarregado. Método que remove as intersections e as retorna, como explicado acima
                    removedInnerIntersection = removeInnerDuplicatedCourses(nomeSeparado, this.courseRelationList.get(this.courseRelationList.size() - 1).getIntersection(), removedInnerIntersection);

                    //não efetivamente tira os cursos da lista, mas guarda seus indices em uma lista, para remove-los depois
                    toRemoveIndexes.add(i);
                }
            }
        }
        return removedInnerIntersection;
    }

    private List<Intersection> removeInnerDuplicatedCourses(List<String> nomeSeparado, List<Intersection> intersectionList, List<Intersection> removedInnerIntersection) {
        for (int i = 0; i < intersectionList.size(); i++) {
            for (int j = 0; j < nomeSeparado.size(); j++) {
                if (intersectionList.get(i).getIntersectionCourse().equals(nomeSeparado.get(j))) {
                    //essa lista é onde vai ser colocada as intersections removidas
                    removedInnerIntersection.add(intersectionList.remove(i));
                    return removeInnerDuplicatedCourses(nomeSeparado, intersectionList, removedInnerIntersection);
                }
            }
        }
        return removedInnerIntersection;
    }

    private void joinIntersections(List<Intersection> csIntersections, List<Intersection> intersectionList) {
        List<Intersection> newIntersections = new ArrayList<>();
        for (Intersection iil : intersectionList) {
            for (Intersection iteratorCS : csIntersections) {
                if (iil.getIntersectionCourse().equals(iteratorCS.getIntersectionCourse())) {
                    List<String> auxList = new ArrayList<>();
                    for (String iteratorStringCS : iteratorCS.getProfessorsNameList()) {
                        if (ListOperationUtil.itemIsNotInList(iteratorStringCS, iil.getProfessorsNameList()) && !auxList.contains(iteratorStringCS)) {
                            auxList.add(iteratorStringCS);
                        }
                    }
                    iil.getProfessorsNameList().addAll(auxList);
                }
                if (ListOperationUtil.itemIsNotInList(iteratorCS.getIntersectionCourse(), intersectionList) && !newIntersections.contains(iteratorCS)) {
                    newIntersections.add(iteratorCS);
                }

            }

        }
        intersectionList.addAll(newIntersections);
    }

    private void renameIntersection(List<String> nomeSeparado, String nomeTudoJunto) {
        for (CourseRelation iterationCR : this.courseRelationList) {
            for (Intersection iterationIntersec : iterationCR.getIntersection()) {
                for (String iterationName : nomeSeparado) {
                    if (iterationIntersec.getIntersectionCourse().equals(iterationName)) {
                        iterationIntersec.setIntersectionCourse(nomeTudoJunto);
                    }
                }
            }

        }
    }

    private void mergeIntersections(String nomeTudoJunto) {
        for (CourseRelation iteratorCS : this.courseRelationList) {
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
                            if (ListOperationUtil.itemIsNotInList(iteratorProfessorsCS, listSameName.getProfessorsNameList()) && !professors.contains(iteratorProfessorsCS)) {
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
            this.removeItemsOnIndexes(indexes, iteratorCS.getIntersection());
        }
    }

    private FileWriter generateReport(int percentage) throws IOException {
        String pathname = percentage + "%.txt";
        System.out.println("Criando arquivo " + pathname + " ...\n");
        File file = new File("out/" + pathname);
        if (file.createNewFile()) {
            try {
                FileWriter arq = new FileWriter(file, true);
                PrintWriter gravarArq = new PrintWriter(arq);

                gravarArq.println(this.courseRelationList.toString());
                System.out.println("Arquivo " + pathname + " criado com sucesso!\n");
                return arq;
            } catch (IOException ex) {
                System.err.println("Erro ao tentar criar o relatório do curso com os professores.");
                ex.printStackTrace();
            }
        }
        throw new IOException("Arquivo " + file.getName() + " já existe!");
    }

    private void removeItemsOnIndexes(List<Integer> indexes, List<?> list) {
        Collections.reverse(indexes);
        for (int index : indexes) {
            list.remove(index);
        }

    }


}
