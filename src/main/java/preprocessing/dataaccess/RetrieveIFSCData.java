package preprocessing.dataaccess;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import domain.ifsc.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import util.DTOIFSC;


public class RetrieveIFSCData {
    private final DTOIFSC dtoifsc;
    private final List<Integer> coursesBlacklist;

    public RetrieveIFSCData() {
        dtoifsc = new DTOIFSC();
        dtoifsc.setClasses(new ArrayList<>());
        dtoifsc.setLessons(new ArrayList<>());
        dtoifsc.setSubjects(new ArrayList<>());
        dtoifsc.setProfessors(new ArrayList<>());
        dtoifsc.setRooms(new ArrayList<>());
        coursesBlacklist = new ArrayList<>();
    }


    public DTOIFSC getAllData() {
        try {
            File fXmlFile = new File("src/assets/Datasets/IFSCFiles/dados.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            NodeList classe = doc.getElementsByTagName("class");
            getData(classe, 0);

            NodeList lesson = doc.getElementsByTagName("lesson");
            getData(lesson, 1);

            NodeList subject = doc.getElementsByTagName("subject");
            getData(subject, 2);

            NodeList teacher = doc.getElementsByTagName("teacher");
            getData(teacher, 3);

            NodeList room = doc.getElementsByTagName("classroom");
            getData(room, 4);

        } catch (Exception e) {
            System.err.println("Erro ao tentar puxar dados do xml: " + e.getMessage());
            System.exit(1);
        }

        return dtoifsc;

    }

    private void getData(NodeList nList, int column) {
        try {
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (column == 0) {// Classes
                        int idClass = Integer.parseInt(eElement.getAttribute("id"));
                        String shortNameClass = eElement.getAttribute("short");
                        if (shortNameClass.toUpperCase().startsWith("G") || shortNameClass.toUpperCase().startsWith("T")) {
                            String nameClass = eElement.getAttribute("name");
                            int teacherIdClass = Integer.parseInt(eElement.getAttribute("teacherid"));
                            String timeoffClass = eElement.getAttribute("timeoff");
                            dtoifsc.getClasses()
                                    .add(new Classes(idClass, nameClass, shortNameClass, teacherIdClass, timeoffClass));
                        } else {
                            coursesBlacklist.add(idClass);
                        }
                    } else if (column == 1) {// Lesson
                        int classesId = Integer.parseInt(eElement.getAttribute("classid"));

                        if (!coursesBlacklist.contains(classesId)) {
                            int idLesson = Integer.parseInt(eElement.getAttribute("id"));
                            int subjectId = Integer.parseInt(eElement.getAttribute("subjectid"));
                            int[] teacherIdLesson = this.getTeacherId(eElement.getAttribute("teacherids"));
                            int periodsPerWeek = Integer.parseInt(eElement.getAttribute("periodsperweek"));
                            int durationPeriods = Integer.parseInt(eElement.getAttribute("durationperiods"));
                            dtoifsc.getLessons()
                                    .add(new Lesson(idLesson, subjectId, classesId, teacherIdLesson, periodsPerWeek, durationPeriods));
                        }
                    } else if (column == 2) {// Subject
                        int idSubject = Integer.parseInt(eElement.getAttribute("id"));
                        for (Lesson iteratorLesson : dtoifsc.getLessons()) {
                            if (iteratorLesson.getSubjectId() == idSubject && !coursesBlacklist.contains(iteratorLesson.getClassesId())) {
                                String nameSubject = eElement.getAttribute("name");
                                String shortNameSubject = eElement.getAttribute("short");
                                dtoifsc.getSubjects().add(new Subject(idSubject, nameSubject, shortNameSubject));
                            }
                        }
                    } else if (column == 3) {// Teacher
                        String idTeacher = eElement.getAttribute("id");
                        String nameTeacher = eElement.getAttribute("name");
                        String timeoffTeacher = eElement.getAttribute("timeoff");
                        dtoifsc.getProfessors()
                                .add(new Teacher(Integer.parseInt(idTeacher), nameTeacher, timeoffTeacher));
                    } else if (column == 4) {// Room
                        String idRoom = eElement.getAttribute("id");
                        String nameRoom = eElement.getAttribute("name");
                        dtoifsc.getRooms()
                                .add(new Classroom(Integer.parseInt(idRoom), nameRoom));
                    } else {
                        System.out.println("Não existente");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao tentar pegar dados específicos: " + e.getMessage());
            System.exit(1);
        }
    }

    private int[] getTeacherId(String element) throws ClassNotFoundException {
        if (element.equals("")) {
            return null;
        }
        String newElement = element;
        if (newElement.startsWith(","))
            newElement = newElement.replaceFirst(",", "");
        String[] teacher = newElement.split(",");

        int[] professors = new int[teacher.length];

        for (int i = 0; i < professors.length; i++) {
            professors[i] = (Integer.parseInt(teacher[i]));
        }

        if (professors.length != 0){
            return professors;
        }

        throw new ClassNotFoundException("Teacher não encontrado");
    }
}

