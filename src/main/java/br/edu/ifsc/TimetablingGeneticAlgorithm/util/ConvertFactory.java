package br.edu.ifsc.TimetablingGeneticAlgorithm.util;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Teacher;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.*;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOIFSC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;

import java.util.ArrayList;
import java.util.List;


public class ConvertFactory {

    /**
     * Converte os dados vindos do XML do IFSC para a modelagem do ITC.
     *
     * @param dtoifsc dados do XML do IFSC.
     * @return {@link DTOITC} convertido.
     * @throws ClassNotFoundException Erro quando um professor, lesson ou lectures number não é encontrado.
     */
    public static DTOITC convertIFSCtoITC(DTOIFSC dtoifsc) throws ClassNotFoundException {
        DTOITC dtoitc = new DTOITC();

        //ROOMS
        int roomSize = dtoifsc.getRooms().size();
        Room[] room = new Room[roomSize];
        for (int i = 0; i < roomSize; i++) {
            room[i] = new Room();
            room[i].setRoomId(dtoifsc.getRooms().get(i).getRoomId());

            //Não existe capacidade no IFSC, por isso é assumido esse valor
            room[i].setCapacity(Integer.MAX_VALUE);
        }
        dtoitc.setRooms(room);

        //LESSONS
        int lessonSize = dtoifsc.getSubjects().size();
        Lesson[] lessons = new Lesson[lessonSize];
        for (int i = 0; i < lessonSize; i++) {
            lessons[i] = new Lesson();
            int lessonId = dtoifsc.getSubjects().get(i).getId();
            lessons[i].setCourseId(retrieveLessonsCourse(lessonId, dtoifsc.getLessons()));
            lessons[i].setLessonId(lessonId);
            lessons[i].setProfessorId(retrieveProfessorsId(lessonId, dtoifsc.getLessons(), dtoifsc.getProfessors()));
            lessons[i].setLecturesNumber(retrieveLecturesNumber(lessonId, dtoifsc.getLessons()));
            lessons[i].setMinWorkingDays(retrievePeriodsPerWeek(lessonId, dtoifsc.getLessons()));
            lessons[i].setStudentsNumber(0);

        }
        dtoitc.setLessons(lessons);


        //CONSTRAINTS
        for (Lesson lesson : lessons) {
            int[] professorIds = lesson.getProfessorId();
            List<UnavailabilityConstraint> constraintList = new ArrayList<>();
            for (int professorId : professorIds) {
                for (Teacher iterationTeacher : dtoifsc.getProfessors()) {
                    if (iterationTeacher.getId() == professorId) {

                        //Adiciona as constraits as lessons
                        constraintList.addAll(convertTimeoffToUnavailability(iterationTeacher.getTimeoff(), iterationTeacher.getId()));

                    }
                }
            }
            lesson.setConstraints(new UnavailabilityConstraint[constraintList.size()]);
            lesson.setConstraints(constraintList.toArray(lesson.getConstraints()));
        }

        //COURSES
        int courseSize = dtoifsc.getClasses().size();
        Course[] courses = new Course[courseSize];
        for (int i = 0; i < courseSize; i++) {
            List<Lesson> lessonList = retrieveCoursesLesson(dtoifsc.getClasses().get(i).getId(), dtoifsc.getLessons(), lessons);
            int size = lessonList.size();
            courses[i] = new Course();
            courses[i].setCourseId(dtoifsc.getClasses().get(i).getId());
            courses[i].setLessonsNumber(size);
            courses[i].setShift(convertTimeoffToShift(String.valueOf(dtoifsc.getClasses().get(i).getTimeoff())));
        }

        dtoitc.setCourses(courses);

        return dtoitc;
    }

    /**
     * Obtém o Id do curso através do Id da lesson.
     *
     * @param lessonId Id do curso a ser obtido.
     * @param lessons  lista com as {@link br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson}s.
     * @return Id do curso respectivo a lesson.
     * @throws ClassNotFoundException Erro quando a {@link br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson} não é encontrada.
     */
    private static int retrieveLessonsCourse(int lessonId, List<br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson> lessons) throws ClassNotFoundException {
        for (br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson iterationLesson : lessons) {
            if (iterationLesson.getSubjectId() == lessonId)
                return iterationLesson.getClassesId();
        }
        throw new ClassNotFoundException("Lesson não atribuida à um curso");
    }

    /**
     * Obtém os professores de uma determinada disciplina através da lesson.
     *
     * @param id       id da disciplina dos professores a serem buscados.
     * @param lessons  {@link List} de {@link br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson} de onde será
     *                 verificada as disciplinas.
     * @param teachers {@link List} de {@link String} que contém os professores a serem obtidos.
     * @return Vetor de {@link String} que contém os professores que lecionam uma disciplina.
     * @throws ClassNotFoundException Erro quando o professor ou a disciplina não é encontrada.
     */
    private static int[] retrieveProfessorsId(int id, List<br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson> lessons, List<Teacher> teachers) throws ClassNotFoundException {
        int[] professorsList = new int[0];
        int count = 0;
        for (br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson iterationLesson : lessons) {
            if (iterationLesson.getSubjectId() == id) {
                int[] professorId = iterationLesson.getTeacherId();
                professorsList = new int[professorId.length];
                for (int value : professorId) {
                    for (Teacher iterationTeacher : teachers) {
                        if (iterationTeacher.getId() == value) {
                            professorsList[count] = iterationTeacher.getId();
                            count++;
                        }
                    }
                }
            }
        }
        if (professorsList.length != 0) {
            return professorsList;
        }
        throw new ClassNotFoundException("Teacher ou Lesson não encontrado");
    }

    /**
     * Obtém o número de créditos de uma disciplina a partir do seu Id.
     *
     * @param id      Id da disciplina que irá ser buscada.
     * @param lessons {@link List} de {@link br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson} de onde será
     *                verificado o Id.
     * @return o número de créditos da disciplina.
     * @throws ClassNotFoundException Erro ao tentar obter o número de créditos
     */
    private static int retrieveLecturesNumber(int id, List<br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson> lessons) throws ClassNotFoundException {
        for (br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson iterationLesson : lessons) {
            if (iterationLesson.getSubjectId() == id) {
                return iterationLesson.getDurationPeriods();
            }
        }
        throw new ClassNotFoundException("LecturesNumber não encontrado");
    }

    /**
     * Obtém o número de vezes na semana que uma disciplina deve ser lecionada, a partir do seu Id.
     *
     * @param id      Id da disciplina que irá ser buscada.
     * @param lessons {@link List} de {@link br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson} de onde será
     *                verificado o Id.
     * @return o número de vezes na semana que a disciplina deve ser lecionada.
     * @throws ClassNotFoundException Erro ao tentar obter o número de vezes na semana da disciplina.
     */
    private static int retrievePeriodsPerWeek(int id, List<br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson> lessons) throws ClassNotFoundException {
        for (br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson iterationLesson : lessons) {
            if (iterationLesson.getSubjectId() == id) {
                return iterationLesson.getPeriodsPerWeek() / iterationLesson.getDurationPeriods();
            }
        }
        throw new ClassNotFoundException("LecturesNumber não encontrado");
    }

    /**
     * Obtém as disciplinas de um curso a partir do seu Id.
     *
     * @param courseId    Id da disciplina que irá ser buscada.
     * @param IFSCLessons {@link List} de {@link br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson} de onde será
     *                    verificado o Id.
     * @param ITCLesson   todas as disciplinas já convertidas para o modelo do ITC.
     * @return {@link List} de {@link Lesson} do curso desejado.
     */
    private static List<Lesson> retrieveCoursesLesson(int courseId, List<br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson> IFSCLessons, Lesson[] ITCLesson) {
        List<Lesson> lessonList = new ArrayList<>();
        for (br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson iterationLesson : IFSCLessons) {
            if (iterationLesson.getClassesId() == courseId) {
                for (Lesson iterationITCLesson : ITCLesson) {
                    if (iterationITCLesson.getLessonId() == iterationLesson.getSubjectId()) {
                        lessonList.add(iterationITCLesson);
                    }
                }
            }
        }
        return lessonList;
    }


    /**
     * Convert o timeoff (padrão do XML do IFSC) para {@link UnavailabilityConstraint} (padrão ITC).
     *
     * @param timeoff   {@link String} que representa o timeoff.
     * @param teacherId Id do {@link Teacher} pro associada a esse timeoff.
     * @return {@link List} de {@link UnavailabilityConstraint} convertidos.
     */
    private static List<UnavailabilityConstraint> convertTimeoffToUnavailability(String timeoff, int teacherId) {
        List<UnavailabilityConstraint> constraintList = new ArrayList<>();
        String[] days = timeoff.replace(".", "").split(",");
        for (int i = 0; i < days.length - 1; i++) {
            int count = 0;
            //No XML o timeoff é uma String que a cada 12 posições tem-se um dia (4 posições para cada turno)
            for (int j = 0; j < 12; j += 2) {
                if (days[i].charAt(j) == '0') {
                    constraintList.add(new UnavailabilityConstraint(teacherId, i, count));
                } else if (days[i].charAt(j + 1) == '0') {
                    constraintList.add(new UnavailabilityConstraint(teacherId, i, count));
                }
                count++;
            }
        }
        return constraintList;
    }

    /**
     * Obtém o turno (shift) a partir de um timeoff.
     *
     * @param timeoff {@link String} que representa o valor a ser convertido.
     * @return {@link Shift} que indica qual o turno.
     */
    private static Shift convertTimeoffToShift(String timeoff) {
        String[] days = timeoff.replace(".", "").split(",");
        if (days[0].charAt(0) == '1')
            return Shift.MATUTINO;
        else if (days[0].charAt(4) == '1')
            return Shift.VESPERTINO;
        return Shift.NOTURNO;

    }
}
