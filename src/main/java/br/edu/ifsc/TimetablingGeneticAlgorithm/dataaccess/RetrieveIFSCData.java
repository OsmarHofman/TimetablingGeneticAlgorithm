package br.edu.ifsc.TimetablingGeneticAlgorithm.dataaccess;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.*;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOIFSC;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe que obtém os dados diretamente do XML gerado pelo IFSC
 */
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

    /**
     * Obtém todos os dados do XML.
     *
     * @return {@link DTOIFSC} que contém a representação em classes do que está no XML.
     */
    public DTOIFSC getAllData() {
        try {
            File fXmlFile = new File("/home/alunoremoto/TCCWilson/TimetablingGeneticAlgorithm/src/assets/Datasets/IFSCFiles/dados.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();
            //Obtém as classes do XML de acordo com suas tags

            NodeList classe = doc.getElementsByTagName("class");
            this.getData(classe, 0);

            NodeList lesson = doc.getElementsByTagName("lesson");
            this.getData(lesson, 1);

            NodeList subject = doc.getElementsByTagName("subject");
            this.getData(subject, 2);

            NodeList teacher = doc.getElementsByTagName("teacher");
            this.getData(teacher, 3);

            NodeList room = doc.getElementsByTagName("classroom");
            this.getData(room, 4);

        } catch (Exception e) {
            System.err.println("Erro ao tentar puxar dados do xml: " + e.getMessage());
            System.exit(1);
        }

        return dtoifsc;

    }

    /**
     * Obtém todos os dados de uma lista de acordo com a sua coluna.
     *
     * @param nList  {@link NodeList} que representa todos os dados dentro de uma das tags do XML.
     * @param column Número que representa qual tag está sendo analisada.
     */
    private void getData(NodeList nList, int column) {
        try {
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (column == 0) {
                        // Classes
                        int idClass = Integer.parseInt(eElement.getAttribute("id"));
                        String shortNameClass = eElement.getAttribute("short");

                        //Se começar com "G" ou "T" significa que é uma turma de graduação ou técnico
                        if (shortNameClass.toUpperCase().startsWith("G") || shortNameClass.toUpperCase().startsWith("T")) {
                            String nameClass = eElement.getAttribute("name");
                            int teacherIdClass = Integer.parseInt(eElement.getAttribute("teacherid"));
                            String timeoffClass = eElement.getAttribute("timeoff");
                            dtoifsc.getClasses()
                                    .add(new Classes(idClass, nameClass, shortNameClass, teacherIdClass, timeoffClass));
                        } else {
                            coursesBlacklist.add(idClass);
                        }
                    } else if (column == 1) {
                        // Lesson
                        int classesId = Integer.parseInt(eElement.getAttribute("classid"));

                        //Verifica se essa Class já foi inserida
                        if (!coursesBlacklist.contains(classesId)) {
                            int idLesson = Integer.parseInt(eElement.getAttribute("id"));
                            int subjectId = Integer.parseInt(eElement.getAttribute("subjectid"));
                            int[] teacherIdLesson = this.getTeacherId(eElement.getAttribute("teacherids"));
                            int periodsPerWeek = Integer.parseInt(eElement.getAttribute("periodsperweek"));
                            int durationPeriods = Integer.parseInt(eElement.getAttribute("durationperiods"));
                            dtoifsc.getLessons()
                                    .add(new Lesson(idLesson, subjectId, classesId, teacherIdLesson, periodsPerWeek, durationPeriods));
                        }
                    } else if (column == 2) {

                        // Subject
                        int idSubject = Integer.parseInt(eElement.getAttribute("id"));
                        for (Lesson iteratorLesson : dtoifsc.getLessons()) {
                            if (iteratorLesson.getSubjectId() == idSubject && !coursesBlacklist.contains(iteratorLesson.getClassesId())) {
                                String nameSubject = eElement.getAttribute("name");
                                String shortNameSubject = eElement.getAttribute("short");
                                dtoifsc.getSubjects().add(new Subject(idSubject, nameSubject, shortNameSubject));
                            }
                        }
                    } else if (column == 3) {

                        // Teacher
                        String idTeacher = eElement.getAttribute("id");
                        String nameTeacher = eElement.getAttribute("name");
                        String timeoffTeacher = eElement.getAttribute("timeoff");
                        dtoifsc.getProfessors()
                                .add(new Teacher(Integer.parseInt(idTeacher), nameTeacher, timeoffTeacher));
                    } else if (column == 4) {

                        // Classroom
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

    /**
     * Obtém os identificadores dos professores de acordo com o atributo obtido do XML
     *
     * @param element Valor do registro obtido do XML
     * @return Vetor que contém os identificadores dos professores
     * @throws ClassNotFoundException Quando o {@code Teacher} não é encontrado.
     */
    private int[] getTeacherId(String element) throws ClassNotFoundException {
        if (element.equals("")) {
            return null;
        }
        String newElement = element;

        /*No XML os professores que lecionam uma matéria são divididos por virgula, mas como não há um padrão onde essa
         * virgula aparece, é necessário fazer uma verificação*/
        if (newElement.startsWith(","))
            newElement = newElement.replaceFirst(",", "");
        String[] teacher = newElement.split(",");

        int[] professors = new int[teacher.length];

        for (int i = 0; i < professors.length; i++) {
            professors[i] = (Integer.parseInt(teacher[i]));
        }

        if (professors.length != 0) {
            return professors;
        }

        throw new ClassNotFoundException("Teacher não encontrado");
    }
}

