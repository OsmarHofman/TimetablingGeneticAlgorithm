package br.edu.ifsc.TimetablingGeneticAlgorithm.dtos;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Classe DTO((Data Transfer Object) que representa o modelo adaptado (ITC)
 */
public class DTOITC implements Serializable {

    private Course[] courses;
    private Lesson[] lessons;
    private Room[] rooms;
    private UnavailabilityConstraint[] constraints;

    public DTOITC() {
    }

    public DTOITC(Course[] courses, Lesson[] lessons) {
        this.courses = courses;
        this.lessons = lessons;
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
     * @param id índice que será verificado com os valores das {@link Lesson}s.
     * @return Vetor de {@link String} com os Ids dos professores.
     * @throws ClassNotFoundException Erro quando o professor não é encontrado
     */
    public int[] getProfessorByLessonId(int id) throws ClassNotFoundException {
        for (Lesson lesson : this.lessons) {
            if (lesson.getLessonId() == id) {
                return lesson.getProfessorId();
            }
        }
        throw new ClassNotFoundException("Professor não encontrado");
    }

    /**
     * Obtém a matéria pelo identificador da {@link Lesson}.
     *
     * @param id índice que será verificado com os valores das {@link Lesson}s.
     * @return {@link Lesson} com o Id correspondente.
     * @throws ClassNotFoundException Erro quando a matéria não é encontrado
     */
    public Lesson getLessonById(int id) throws ClassNotFoundException {
        for (Lesson lesson : this.lessons) {
            if (lesson.getLessonId() == id) {
                return lesson;
            }
        }
        throw new ClassNotFoundException("Lesson não encontrada");
    }

    /**
     * Obtém o turno de uma turma pelo identificador do {@link Course}.
     *
     * @param id índice que será verificado com os valores das {@link Course}s.
     * @return {@link Lesson} com o Id correspondente.
     * @throws ClassNotFoundException Erro quando a matéria não é encontrado
     */
    public Shift getShiftByCourseId(int id) throws ClassNotFoundException {
        for (Course course : this.courses) {
            if (course.getCourseId() == id) {
                return course.getShift();
            }
        }
        throw new ClassNotFoundException("Curso não encontrado");
    }

    /**
     * Obtém o turno de uma turma pelo identificador do {@link Lesson}.
     *
     * @param id índice que será verificado com os valores das {@link Lesson}s.
     * @return {@link Shift} com o Id correspondente.
     * @throws ClassNotFoundException Erro quando a matéria não é encontrado
     */
    public Shift getShiftByLessonId(int id) throws ClassNotFoundException {
        for (Lesson lesson : this.lessons) {
            if (lesson.getLessonId() == id) {
                return this.getShiftByCourseId(lesson.getCourseId());
            }
        }
        throw new ClassNotFoundException("Lesson não encontrado");
    }


    /**
     * Obtém o índice que está um item da {@link Lesson}.
     *
     * @param id valor que será verificado para obter o índice.
     * @return {@link Integer} do índice do {@code lessonId}.
     * @throws ClassNotFoundException Erro quando a Lesson não é encontrada.
     */
    public int getLessonPosition(int id) throws ClassNotFoundException {
        for (int i = 0; i < lessons.length; i++) {
            if (lessons[i].getLessonId() == id)
                return i;
        }
        throw new ClassNotFoundException("Lesson não encontrada");
    }

    /**
     * Obtém o curso pelo identificador do {@link Lesson}.
     *
     * @param id índice que será verificado com os valores das {@link Lesson}s.
     * @return {@link Course} com o Id correspondente.
     * @throws ClassNotFoundException Erro quando a matéria não é encontrado
     */
    public int getCourseByLessonId(int id) throws ClassNotFoundException {
        for (Lesson lesson : this.lessons) {
            if (lesson.getLessonId() == id) {
                return lesson.getCourseId();
            }
        }
        throw new ClassNotFoundException("Lesson não encontrado");
    }

    public boolean isLessonInCourse(int lessonId, int courseId) {
        for (Lesson lesson : this.lessons) {
            if (lesson.getCourseId() == courseId && lesson.getLessonId() == lessonId) {
                return true;
            }
        }
        return false;
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


}
