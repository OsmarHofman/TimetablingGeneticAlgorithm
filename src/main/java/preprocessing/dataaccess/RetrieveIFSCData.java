package preprocessing.dataaccess;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import domain.Classes;
import domain.Lesson;
import domain.Subject;
import dto.Professor;
import dto.Room;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import util.DTOServerData;


public class RetrieveIFSCData {

    private static DTOServerData serverData;

    public static DTOServerData getAllData() {
        setUp();
        try {
            File fXmlFile = new File("src/Datasets/IFSCFiles/dados.xml");
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

        return serverData;

    }

    private static void getData(NodeList nList, int column) {
        try {
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (column == 0) {// Classes
                        int idClass = Integer.parseInt(eElement.getAttribute("id"));
                        String nameClass = eElement.getAttribute("name");
                        String shortNameClass = eElement.getAttribute("short");
                        int teacherIdClass = Integer.parseInt(eElement.getAttribute("teacherid"));
                        String timeoffClass = eElement.getAttribute("timeoff");
                        serverData.getClasses()
                                .add(new Classes(idClass, nameClass, shortNameClass, teacherIdClass, timeoffClass));
                    } else if (column == 1) {// Lesson
                        int idLesson = Integer.parseInt(eElement.getAttribute("id"));
                        int subjectId = Integer.parseInt(eElement.getAttribute("subjectid"));
                        int classesId = Integer.parseInt(eElement.getAttribute("classid"));
                        int teacherIdLesson;
                        if (eElement.getAttribute("teacherid").equals("")) {
                            teacherIdLesson = -1;
                        } else {
                            teacherIdLesson = Integer.parseInt(eElement.getAttribute("teacherid"));
                        }

                        int periodsPerWeek = Integer.parseInt(eElement.getAttribute("periodsperweek"));
                        serverData.getLessons()
                                .add(new Lesson(idLesson, subjectId, classesId, teacherIdLesson, periodsPerWeek));
                    } else if (column == 2) {// Subject
                        int idSubject = Integer.parseInt(eElement.getAttribute("id"));
                        String nameSubject = eElement.getAttribute("name");
                        String shortNameSubject = eElement.getAttribute("short");
                        serverData.getSubjects().add(new Subject(idSubject, nameSubject, shortNameSubject));
                    } else if (column == 3) {// Teacher
                        String idTeacher = eElement.getAttribute("id");
                        String nameTeacher = eElement.getAttribute("name");
                        String timeoffTeacher = eElement.getAttribute("timeoff");
                        serverData.getProfessors()
                                .add(new Professor(idTeacher, nameTeacher, timeoffTeacher));
                    } else if (column == 4) {// Room
                        String idRoom = eElement.getAttribute("id");
                        String nameRoom = eElement.getAttribute("name");
                        serverData.getRooms()
                                .add(new Room(idRoom, nameRoom));
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

    private static void setUp() {
        serverData = new DTOServerData();
        serverData.setClasses(new ArrayList<>());
        serverData.setLessons(new ArrayList<>());
        serverData.setSubjects(new ArrayList<>());
        serverData.setProfessors(new ArrayList<>());
    }
}

