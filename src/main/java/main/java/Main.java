package main.java;

import ImportFiles.RetrieveProfessorsSchedule;
import ImportFiles.preProcessing.CourseRelation;
import ImportFiles.preProcessing.Intersection;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //TODO Alterar atributos do domain e corrigir possiveis conflitos

        final int percentage = 50;
        RetrieveProfessorsSchedule rts = new RetrieveProfessorsSchedule();
        boolean hasJoined = true;
        List<CourseRelation> cs = rts.converterWB(rts.pegarArquivo());
        List<CourseRelation> newRelations = cs;
        List<String> nomeSeparado;
        String nomeTudoJunto = "";
        createReport(cs);

        while (hasJoined) {

            nomeSeparado = new ArrayList<>();
            for (CourseRelation iterationCS : newRelations) {
                hasJoined = iterationCS.joinIntersections(percentage, cs);
                if (hasJoined) {
                    nomeTudoJunto = cs.get(cs.size() - 1).getName();
                    if (nomeTudoJunto.contains("--")) {
                        nomeSeparado = Arrays.asList(nomeTudoJunto.split("--"));
                        nomeSeparado.set(1, nomeSeparado.get(1) + '-');
                    } else {
                        nomeSeparado = Arrays.asList(nomeTudoJunto.split("-"));
                    }
                    break;
                }
            }
            if (nomeSeparado.size() != 0) {
                List<Intersection> intersectionList = new ArrayList<>();
                removeCourses(cs, nomeSeparado, intersectionList);
                renameIntersection(cs, nomeSeparado, nomeTudoJunto);
                mergeIntersections(cs, nomeTudoJunto);

            }
            //FIXME adicionar informacao do professor exclusivo
        }
        System.out.println("alo?" + cs.toString());
    }

    private static void renameIntersection(List<CourseRelation> cs, List<String> nomeSeparado, String nomeTudoJunto) {
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


    private static void createReport(List<CourseRelation> cs) throws IOException {
        File file = new File("relaçoesProfessores.txt");
        if (file.createNewFile()) {
            try {
                FileWriter arq = new FileWriter(file);
                PrintWriter gravarArq = new PrintWriter(arq);

                gravarArq.println(cs.toString());
                arq.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Arquivo já existe");
        }
    }

    public static CourseRelation getCourseByName(List<CourseRelation> cs, String course) throws ClassNotFoundException {
        for (CourseRelation iterationCS : cs) {
            if (iterationCS.getName().equals(course)) {
                return iterationCS;
            }
        }
        throw new ClassNotFoundException("Curso não encontrado");
    }

    private static boolean removeCourses(List<CourseRelation> cs, List<String> nomeSeparado, List<Intersection> intersectionList) {
        for (int i = 0; i < cs.size(); i++) {
            for (int j = 0; j < nomeSeparado.size(); j++) {
                if (cs.get(i).getName().equals(nomeSeparado.get(j))) {
                    if (intersectionList.isEmpty())
                        intersectionList.addAll(cs.get(i).getIntersection());
                    else {
                        joinIntersections(cs.get(i).getIntersection(), intersectionList);
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

    private static boolean removeCourses(List<String> nomeSeparado, List<Intersection> intersectionList) {
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

    private static void joinIntersections(List<Intersection> csIntersections, List<Intersection> intersectionList) {
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

    private static boolean mergeIntersections(List<CourseRelation> cs, String nomeTudoJunto) {
        List<CourseRelation> newRelation = cs;
        List<Integer> indexes = new ArrayList<>();
        for (CourseRelation iteratorCS : newRelation) {
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
            //FIXME remover de tras para frente
            for (int indexInt : indexes) {
                iteratorCS.getIntersection().remove(indexInt);
            }
        }
        return true;
    }

}
