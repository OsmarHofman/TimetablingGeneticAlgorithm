package br.edu.ifsc.TimetablingGeneticAlgorithm.util;

import br.edu.ifsc.TimetablingGeneticAlgorithm.datapreview.classes.CourseRelation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.datapreview.classes.ProfessorCourse;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Course;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Lesson;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Room;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.UnavailabilityConstraint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe DTO((Data Transfer Object) que representa o modelo adaptado (ITC)
 */
public class DTOITC {

    private Course[] courses;
    private Lesson[] lessons;
    private Room[] rooms;
    private UnavailabilityConstraint[] constraints;

    public DTOITC() {
    }

    public DTOITC(List<Course> courses, List<Lesson> lessons, List<Room> rooms, List<UnavailabilityConstraint> constraints) {
        this.courses = new Course[courses.size()];
        this.lessons = new Lesson[lessons.size()];
        this.rooms = new Room[rooms.size()];
        this.constraints = new UnavailabilityConstraint[constraints.size()];
        this.courses = new Course[courses.size()];
        this.courses = courses.toArray(this.courses);
        this.lessons = lessons.toArray(this.lessons);
        this.rooms = rooms.toArray(this.rooms);
        this.constraints = constraints.toArray(this.constraints);

    }

    public DTOITC(Course[] courses, Lesson[] lessons, Room[] rooms, UnavailabilityConstraint[] constraints) {
        this.courses = courses;
        this.lessons = lessons;
        this.rooms = rooms;
        this.constraints = constraints;
    }

    public Course[] getCourses() {
        return courses;
    }

    public void setCourses(Course[] courses) {
        this.courses = courses;
    }

    public Lesson[] getLessons() {
        return lessons;
    }

    public void setLessons(Lesson[] lessons) {
        this.lessons = lessons;
    }

    public Room[] getRooms() {
        return rooms;
    }

    public void setRooms(Room[] rooms) {
        this.rooms = rooms;
    }

    public UnavailabilityConstraint[] getConstraints() {
        return constraints;
    }

    public void setConstraints(UnavailabilityConstraint[] constraints) {
        this.constraints = constraints;
    }

    /**
     * Obtém o professor pelo identificador da {@link Lesson}.
     *
     * @param index índice que será verificado com os valores das {@link Lesson}s.
     * @return Vetor de {@link String} com os Ids dos professores.
     * @throws ClassNotFoundException Erro quando o professor não é encontrado
     */
    public String[] getProfessorByLessonId(int index) throws ClassNotFoundException {
        for (Lesson lesson : this.lessons) {
            if (lesson.getLessonId().equals(String.valueOf(index))) {
                return lesson.getProfessorId();
            }
        }
        throw new ClassNotFoundException("Professor não encontrado");
    }

    /**
     * Obtém a matéria pelo identificador da {@link Lesson}.
     *
     * @param index índice que será verificado com os valores das {@link Lesson}s.
     * @return {@link Lesson} com o Id correspondente.
     * @throws ClassNotFoundException Erro quando a matéria não é encontrado
     */
    public Lesson getLessonById(int index) throws ClassNotFoundException {
        for (Lesson lesson : this.lessons) {
            if (lesson.getLessonId().equals(String.valueOf(index))) {
                return lesson;
            }
        }
        throw new ClassNotFoundException("Lesson não encontrada");
    }

    /**
     * Obtém o turno de uma turma pelo identificador do {@link Course}.
     *
     * @param courseId índice que será verificado com os valores das {@link Course}s.
     * @return {@link Lesson} com o Id correspondente.
     * @throws ClassNotFoundException Erro quando a matéria não é encontrado
     */
    public Shift getShiftByCourseId(String courseId) throws ClassNotFoundException {
        for (Course course : this.courses) {
            if (course.getCourseId().equals(courseId)) {
                return course.getShift();
            }
        }
        throw new ClassNotFoundException("Curso não encontrado");
    }

    /**
     * Obtém o índice que está um item da {@link Lesson}.
     *
     * @param lessonId valor que será verificado para obter o índice.
     * @return {@link Integer} do índice do {@code lessonId}.
     * @throws ClassNotFoundException Erro quando a Lesson não é encontrada.
     */
    public int getLessonPosition(String lessonId) throws ClassNotFoundException {
        for (int i = 0; i < lessons.length; i++) {
            if (lessons[i].getLessonId().equals(lessonId))
                return i;
        }
        throw new ClassNotFoundException("Lesson não encontrada");
    }

    @Override
    public String toString() {
        return "DTOITC{" +
                "courses=" + Arrays.toString(courses) +
                ", lessons=" + Arrays.toString(lessons) +
                ", rooms=" + Arrays.toString(rooms) +
                ", constraints=" + Arrays.toString(constraints) +
                '}';
    }

    public void convertCourseRelationToITC(List<CourseRelation> courseRelations) {
        String[] names;
        List<Integer> indexes = new ArrayList<>();
        for (CourseRelation courseRelation : courseRelations) {
            int position = -1;
            if (courseRelation.getId().contains("-")) {
                names = courseRelation.getId().split("-");
                for (int i = 0; i < names.length; i++) {
                    for (int j = 0; j < courses.length; j++) {
                        if (names[i].equals(courses[j].getCourseId())) {
                            if (position == -1) {
                                position = i;
                                courses[j].setCourseId(courseRelation.getId());
                            } else {
                                indexes.add(j);
                            }
                        }
                    }
                    int totalLessonsNumber = 0;
                    for (Lesson lesson : lessons) {
                        if (lesson.getCourseId().equals(names[i])) {
                            lesson.setCourseId(courseRelation.getId());
                            totalLessonsNumber++;
                        }
                    }
                    courses[position].setLessonsNumber(totalLessonsNumber);
                }
            }
        }
        indexes.sort(null);
        List<Course> courseArrayList = new ArrayList<>(Arrays.asList(courses));
        ListOperationUtil.removeItemsOnIndexes(indexes,courseArrayList);
        courses = new Course[courseArrayList.size()];
        courses = courseArrayList.toArray(courses);
    }


}
