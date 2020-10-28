package br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Course;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Lesson;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.entities.CourseGroup;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.entities.CourseRelation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.entities.Intersection;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.entities.ProfessorCourse;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.ListOperationUtil;

import java.util.*;

/**
 * Classe que representa a etapa de pré-processamento
 */
public class PreProcessing {

    private final List<CourseRelation> courseRelationList;
    private final List<ProfessorCourse> professorRelation;
    private final List<CourseGroup> courseGroupList;


    public PreProcessing(ProfessorsScheduleCreation professorsScheduleCreation) {
        this.courseGroupList = professorsScheduleCreation.getCoursesList();
        this.professorRelation = professorsScheduleCreation.getProfessorsList();
        this.courseRelationList = professorsScheduleCreation.getCourseRelationList();
    }

    public List<CourseRelation> getCourseRelationList() {
        return courseRelationList;
    }

    //<editor-fold desc="Create Set">

    /**
     * Cria os conjuntos
     *
     * @param percentage {@link Integer} que representa a chance de agrupar os conjuntos, sendo 0% o agrupamento
     *                   de todos cursos (por consequência, todas as turmas juntas) e 100% nenhum agrupamento
     *                   de curso (cada curso só irá ter as suas turmas).
     * @throws ClassNotFoundException Erro caso não encontre alguma turma na lista.
     */
    public void createSet(int percentage) throws ClassNotFoundException {

        //Nome do conjunto dividido entre os seus cursos
        List<String> splitSetName;
        String setName;

        //Variável que identifica se um conjunto foi formado ou não
        boolean hasJoined = true;
        while (hasJoined) {
            CourseRelation lastCourse = null;
            splitSetName = new ArrayList<>();

            //Verifica cada um dos cursos, e caso um conjunto for formado, sai do laço
            for (CourseRelation iterationCS : this.courseRelationList) {

                hasJoined = iterationCS.joinIntersections(percentage, this.courseRelationList);

                if (hasJoined) {
                    lastCourse = this.courseRelationList.get(this.courseRelationList.size() - 1);
                    splitSetName = this.adjustSetName(lastCourse.getName(), splitSetName);
                    break;
                }
            }

            //Se formou um conjunto
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
                ListOperationUtil.removeItemsOnIndexes(toRemoveIndexes, this.courseRelationList);

                //renomeia todos os cursos que tenham uma intersecção com o conjunto criado
                this.renameIntersection(splitSetName, setName);

                //junta as intersecções do conjunto que tem nome igual aos cursos foram usados para formá-lo
                this.mergeIntersections(setName);

                //recalcula o total de professores do conjunto
                lastCourse.sumTotalProfessors();
            }
        }
    }

    /**
     * Ajusta o nome do conjunto formado.
     *
     * @param setName      {@link String} que contém o nome do conjunto no formato "Curso1-Curso2".
     * @param splitSetName {@link List} de {@link String} que contém cada um dos cursos que formam o conjunto.
     * @return um {@link List} de {@link String} que contém os nomes dos cursos ajustados
     */
    private List<String> adjustSetName(String setName, List<String> splitSetName) {
        //Se for um conjunto e um curso ou outro conjunto, no formato "Curso1-Curso2--Curso3".
        if (setName.contains("--")) {
            //Splita pelo "--" e coloca na lista os cursos separados
            splitSetName.addAll(Arrays.asList(setName.split("--")));

            //Lista que terá os nomes corretos dos cursos
            List<String> nameCourses = new ArrayList<>();

            //Lista com os índices dos cursos que serão retirados da lista
            List<Integer> indexes = new ArrayList<>();
            for (String iteratorSplitName : splitSetName) {
                //conjunto interno, ou seja, "Curso1-Curso2"
                if (iteratorSplitName.contains("-")) {
                    if (ListOperationUtil.itemIsNotInList(iteratorSplitName, this.courseRelationList)) {
                        nameCourses.addAll(Arrays.asList(iteratorSplitName.split("-")));
                        indexes.add(splitSetName.indexOf(iteratorSplitName));
                    }
                }
            }
            //Remove os cursos desnecessários e coloca os nomes corretos
            ListOperationUtil.removeItemsOnIndexes(indexes, splitSetName);
            splitSetName.addAll(nameCourses);

        } else {
            //Se for a junção de um curso com outro curso
            splitSetName = Arrays.asList(setName.split("-"));
        }
        return splitSetName;
    }

    /**
     * Seleciona os cursos e suas intersecções a serem removidas, pois formaram um conjunto.
     *
     * @param splitname        {@link List} de {@link String} que representa o nome do conjunto separado.
     * @param intersectionList {@link List} de {@link Intersection} com as intersecções do conjunto.
     * @param toRemoveIndexes  {@link List} de {@link Intersection} que contém os indices do cursos a serem removidos
     * @return {@link List} de {@link Intersection} que serão removidas.
     */
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

    /**
     * Junta duas intersecções
     *
     * @param firstCourseIntersections  primeira {@link List} de {@link Intersection} a ser agrupada.
     * @param secondCourseIntersections segunda {@link List} de {@link Intersection} a ser agrupada.
     */
    private void joinIntersections(List<Intersection> firstCourseIntersections, List<Intersection> secondCourseIntersections) {
        List<Intersection> newIntersections = new ArrayList<>();
        for (Intersection secondCourseIntersection : secondCourseIntersections) {
            for (Intersection firstCourseIntersection : firstCourseIntersections) {

                if (secondCourseIntersection.getIntersectionCourse().equals(firstCourseIntersection.getIntersectionCourse()))
                    secondCourseIntersection.addProfessorsToIntersection(firstCourseIntersection.getProfessorsList());

                if (ListOperationUtil.itemIsNotInList(firstCourseIntersection.getIntersectionCourse(), secondCourseIntersections) && !newIntersections.contains(firstCourseIntersection))
                    newIntersections.add(firstCourseIntersection);
            }
        }
        //nessa lista que estará as interseções corretas
        secondCourseIntersections.addAll(newIntersections);
    }

    /**
     * Remove os cursos duplicados que estão no conjunto.
     *
     * @param splitName                {@link List} de {@link String} que representa o nome do conjunto separado.
     * @param intersectionList         {@link List} de {@link Intersection} com as intersecções do conjunto.
     * @param removedInnerIntersection {@link List} de {@link Intersection} com as intersecções removidas.
     * @return {@link List} de {@link Intersection} que foram removidos.
     */
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

    /**
     * Renomeia os cursos que os professores lecionam, para o novo conjunto formado.
     *
     * @param splitSetName   {@link List} de {@link String} que representa o nome do conjunto separado.
     * @param courseRelation {@link String} que representa o nome do conjunto.
     */
    private void renameProfessorsCourses(List<String> splitSetName, String courseRelation) {
        for (ProfessorCourse iterationPC : this.professorRelation) {
            List<Integer> indexes = new ArrayList<>();
            //Obtém os cursos com o nome antigo
            for (String iterationCourse : iterationPC.getCourse()) {
                for (String iterationCourseName : splitSetName) {
                    if (iterationCourse.equals(iterationCourseName))
                        indexes.add(iterationPC.getCourse().indexOf(iterationCourse));
                }
            }

            //Remove os cursos selecionados anteriormente
            ListOperationUtil.removeItemsOnIndexes(indexes, iterationPC.getCourse());

            //Adiciona o conjunto aos cursos que o professor leciona
            if (!indexes.isEmpty()) {
                iterationPC.getCourse().add(courseRelation);
            }
        }
    }

    /**
     * Verifica se um professor leciona exclusivamente para um curso.
     *
     * @param innerIntersections {@link List} de {@link Intersection} com as intersecções do curso.
     * @param name               {@link String} que representa o nome do conjunto.
     * @throws ClassNotFoundException Erro caso não encontre o professor na lista.
     */
    private void verifyExclusiveProfessor(List<Intersection> innerIntersections, String name) throws ClassNotFoundException {
        //Obtém o conjunto
        CourseRelation lastCourse = this.courseRelationList.get(this.courseRelationList.size() - 1);
        //Lista que estará os professores já analisados
        List<String> professorBlackList = new ArrayList<>();
        for (Intersection iterationIntersection : innerIntersections) {
            for (String iterationProfessor : iterationIntersection.getProfessorsList()) {
                ProfessorCourse professor_course = ListOperationUtil.getProfessorById(iterationProfessor, this.professorRelation);

                //Se não foi analisado e é exclusivo
                if (!professorBlackList.contains(professor_course.getProfessor()) && professor_course.checkExclusivity(name)) {
                    lastCourse.incrementExclusiveProfessorCount();
                    professorBlackList.add(professor_course.getProfessor());
                }
            }

        }
    }

    /**
     * Renomeia as intersecções para o nome do conjunto.
     *
     * @param splitName {@link List} de {@link String} que representa o nome do conjunto separado.
     * @param setName   {@link String} que representa o nome do conjunto.
     */
    private void renameIntersection(List<String> splitName, String setName) {
        //Verifica todos os cursos de todas as intersecções, e se tiver um curso que pertence ao conjunto, renomeia
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

    /**
     * Agrupa as intersecções dos cursos utilizados para construir o conjunto.
     *
     * @param setName {@link String} que representa o conjunto.
     */
    private void mergeIntersections(String setName) {
        for (CourseRelation iterationCourseRelation : this.courseRelationList) {
            //Lista de indices das intersecções que serão retiradas
            List<Integer> indexes = new ArrayList<>();

            //Intersecção do conjunto, que serão adicionados as outras intersecções dos cursos que formam o conjunto
            Intersection setIntersection = new Intersection();
            int intersecProfessorIndex = 0;
            for (Intersection iterationIntersec : iterationCourseRelation.getIntersection()) {

                if (iterationIntersec.getIntersectionCourse().equals(setName)) {

                    if (setIntersection.getProfessorsList().isEmpty()) {
                        setIntersection = iterationIntersec;
                        intersecProfessorIndex = iterationCourseRelation.getIntersection().indexOf(iterationIntersec);
                    } else {
                        iterationCourseRelation.getIntersection().get(intersecProfessorIndex).addProfessorsToIntersection(iterationIntersec.getProfessorsList());
                        indexes.add(iterationCourseRelation.getIntersection().indexOf(iterationIntersec));
                    }
                }
                iterationIntersec.adjustProfessorsCount();
            }
            setIntersection.adjustProfessorsCount();
            ListOperationUtil.removeItemsOnIndexes(indexes, iterationCourseRelation.getIntersection());
        }
    }

    //</editor-fold>

    /**
     * A partir dos dados no {@code originalDTOITC}, cria um DTOITC para cada um dos conjuntos formados.
     *
     * @param originalDTOITC {@link DTOITC} com os dados originais de todos os cursos, professores,etc.
     * @return Array de {@link DTOITC} que cada posição é contém os dados de cada conjunto.
     */
    public DTOITC[] splitSet(DTOITC originalDTOITC) {
        DTOITC[] dtoitcs = new DTOITC[courseRelationList.size()];

        //Identifica em qual posição será inserido o DTOITC do conjunto respectivo
        int setIndex = 0;

        List<Course> courseList;
        List<Lesson> lessonList;
        for (CourseRelation courseRelation : this.courseRelationList) {

            //Listas de courses e lessons de cada um dos conjuntos
            courseList = new ArrayList<>();
            lessonList = new ArrayList<>();

            String[] splitSetNames = courseRelation.getName().split("-");

            //Para cada um dos cursos dos conjuntos, pega as turmas e disciplicas respectivas
            for (String setName : splitSetNames) {
                for (CourseGroup courseGroup : courseGroupList) {
                    if (setName.equals(courseGroup.getGroupName())) {
                        for (int courseId : courseGroup.getCourses()) {

                            //Pega todas as turmas do conjunto
                            for (Course course : originalDTOITC.getCourses()) {
                                if (courseId == course.getCourseId()) {
                                    courseList.add(course);
                                }
                            }

                            //Pega todas as matérias do conjunto
                            for (Lesson lesson : originalDTOITC.getLessons()) {
                                if (courseId == lesson.getCourseId()) {
                                    lessonList.add(lesson);
                                }
                            }
                        }
                    }
                }
            }

            Course[] courseArray = new Course[courseList.size()];
            courseArray = courseList.toArray(courseArray);

            Lesson[] lessonArray = new Lesson[lessonList.size()];
            lessonArray = lessonList.toArray(lessonArray);

            //Coloca as turmas e matérias no DTOITC respectivo ao conjunto
            dtoitcs[setIndex] = new DTOITC(courseArray, lessonArray);

            setIndex++;
        }
        return dtoitcs;
    }
}
