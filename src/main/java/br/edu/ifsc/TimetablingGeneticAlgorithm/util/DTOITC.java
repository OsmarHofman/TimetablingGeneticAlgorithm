package br.edu.ifsc.TimetablingGeneticAlgorithm.util;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Course;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Lesson;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Room;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.UnavailabilityConstraint;

import java.util.Arrays;
import java.util.List;

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

    public String[] getProfessorByLessonId(int index, Chromosome chromosome) throws ClassNotFoundException {
        for (Lesson iterationLesson : this.lessons) {
            if (iterationLesson.getLessonId().equals(String.valueOf(index))) {
                return iterationLesson.getProfessorId();
            }
        }
        throw new ClassNotFoundException("Professor não encontrado");
    }

    public Lesson getLessonById(int index) throws ClassNotFoundException {
        for (Lesson iterationLesson : this.lessons) {
            if (iterationLesson.getLessonId().equals(String.valueOf(index))) {
                return iterationLesson;
            }
        }
        throw new ClassNotFoundException("Lesson não encontrada");
    }

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

    public byte getShiftByCourseId(String courseId) throws ClassNotFoundException {
        for (Course iterationCourse : this.courses) {
            if (iterationCourse.getCourseId().equals(courseId)) {
                return iterationCourse.getShift();
            }
        }
        throw new ClassNotFoundException("Curso não encontrado");
    }

}
