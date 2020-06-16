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
                        nomeSeparado.set(1,nomeSeparado.get(1) + '-');
                    } else {
                        nomeSeparado = Arrays.asList(nomeTudoJunto.split("-"));
                        //TODO remover ultimo "-" do nome do curso
                    }
                    break;
                }
            }
            if (nomeSeparado.size() != 0) {
                removeCourses(cs, nomeSeparado);
                //FIXME verificar intersecções com nomes repetidos
                renameIntersection(cs, nomeSeparado, nomeTudoJunto);

            }
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

    private static boolean removeCourses(List<CourseRelation> cs, List<String> nomeSeparado) {
        for (int i = 0; i < cs.size(); i++) {
            for (int j = 0; j < nomeSeparado.size(); j++) {
                if (cs.get(i).getName().equals(nomeSeparado.get(j))) {
                    cs.remove(i);
                    return removeCourses(cs, nomeSeparado);
                }
            }
        }
        return true;
    }


}
