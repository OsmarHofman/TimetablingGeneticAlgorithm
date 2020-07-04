package util;

import domain.ifsc.Teacher;
import domain.itc.Course;
import domain.itc.Lesson;
import domain.itc.Room;
import domain.itc.UnavailabilityConstraint;

import java.util.ArrayList;
import java.util.List;


public class ConvertFactory {

    public static DTOITC convertIFSCtoITC(DTOIFSC dtoifsc) throws ClassNotFoundException {
        DTOITC dtoitc = new DTOITC();

        //ROOMS
        int roomSize = dtoifsc.getRooms().size();
        Room[] room = new Room[roomSize];
        for (int i = 0; i < roomSize; i++) {
            room[i] = new Room();
            room[i].setRoomId(String.valueOf(dtoifsc.getRooms().get(i).getRoomId()));
            room[i].setCapacity(Integer.MAX_VALUE);
        }
        dtoitc.setRooms(room);

        //LESSONS


        int lessonSize = dtoifsc.getSubjects().size();
        Lesson[] lessons = new Lesson[lessonSize];
        for (int i = 0; i < lessonSize; i++) {
            lessons[i] = new Lesson();
            int lessonId = dtoifsc.getSubjects().get(i).getId();
            lessons[i].setLessonId(String.valueOf(lessonId));
            lessons[i].setProfessorId(retrieveProfessorId(lessonId, dtoifsc.getLessons(), dtoifsc.getProfessors()));
            lessons[i].setLecturesNumber(retrieveLecturesNumber(lessonId, dtoifsc.getLessons()));
            lessons[i].setMinWorkingDays(retrievePeriodsPerWeek(lessonId, dtoifsc.getLessons()));
            lessons[i].setStudentsNumber(0);

        }
        dtoitc.setLessons(lessons);

        //CONSTRAINTS
        for (int i = 0; i < lessons.length; i++) {
            String professorId = lessons[i].getProfessorId();
            for (Teacher iterationTeacher : dtoifsc.getProfessors()) {
                if (String.valueOf(iterationTeacher.getId()).equals(professorId)) {
                    List<UnavailabilityConstraint> constraintList = convertTimeoff(iterationTeacher.getTimeoff(), String.valueOf(iterationTeacher.getId()));
                    lessons[i].setConstraints(new UnavailabilityConstraint[constraintList.size()]);
                    lessons[i].setConstraints(constraintList.toArray(lessons[i].getConstraints()));
                }
            }
        }

        //COURSES
        int courseSize = dtoifsc.getClasses().size();
        Course[] courses = new Course[courseSize];
        for (int i = 0; i < courseSize; i++) {
            List<Lesson> lessonList = retrieveCoursesLesson(dtoifsc.getClasses().get(i).getId(),dtoifsc.getLessons(),lessons);
            int size = lessonList.size();
            courses[i] = new Course(size);
            courses[i].setCourseId(String.valueOf(dtoifsc.getClasses().get(i).getId()));
            courses[i].setLessons(lessonList.toArray(courses[i].getLessons()));
            courses[i].setCoursesNumber(size);

            for (Lesson iterationLesson:courses[i].getLessons()) {
                List<UnavailabilityConstraint>courseConstraints = convertTimeoff(String.valueOf(dtoifsc.getClasses().get(i).getTimeoff()),String.valueOf(dtoifsc.getClasses().get(i).getId()));
                iterationLesson.mergeCourseLessonConstraints(courseConstraints);
            }
        }



        dtoitc.setCourses(courses);

        return dtoitc;
    }

    private static String retrieveProfessorId(int id, List<domain.ifsc.Lesson> lessons, List<Teacher> teachers) throws ClassNotFoundException {
        for (domain.ifsc.Lesson iterationLesson : lessons) {
            if (iterationLesson.getSubjectId() == id) {
                int professorId = iterationLesson.getTeacherId();
                for (Teacher iterationTeacher : teachers) {
                    if (iterationTeacher.getId() == professorId) {
                        return String.valueOf(iterationTeacher.getId());
                    }
                }
            }
        }
        throw new ClassNotFoundException("Teacher ou Lesson não encontrado");
    }

    private static int retrieveLecturesNumber(int id, List<domain.ifsc.Lesson> lessons) throws ClassNotFoundException {
        for (domain.ifsc.Lesson iterationLesson : lessons) {
            if (iterationLesson.getSubjectId() == id) {
                return iterationLesson.getDurationPeriods();
            }
        }
        throw new ClassNotFoundException("LecturesNumber não encontrado");
    }

    private static int retrievePeriodsPerWeek(int id, List<domain.ifsc.Lesson> lessons) throws ClassNotFoundException {
        for (domain.ifsc.Lesson iterationLesson : lessons) {
            if (iterationLesson.getSubjectId() == id) {
                return iterationLesson.getPeriodsPerWeek() / iterationLesson.getDurationPeriods();
            }
        }
        throw new ClassNotFoundException("LecturesNumber não encontrado");
    }

    private static List<UnavailabilityConstraint> convertTimeoff(String timeoff, String lessonId) {
        List<UnavailabilityConstraint> constraintList = new ArrayList<>();
        String[] days = timeoff.replace(".", "").split(",");
        for (int i = 0; i < days.length - 1; i++) {
            for (int j = 0; j < 12; j++) {
                if (days[i].charAt(j) == '0') {
                    constraintList.add(new UnavailabilityConstraint(lessonId, i, j));
                }
            }
        }
        return constraintList;
    }

    private static List<Lesson> retrieveCoursesLesson(int courseId, List<domain.ifsc.Lesson> IFSCLessons, Lesson[] ITCLesson){
        List<Lesson>lessonList = new ArrayList<>();
        for (domain.ifsc.Lesson iterationLesson: IFSCLessons) {
            if (iterationLesson.getClassesId() == courseId){
                for (Lesson iterationITCLesson: ITCLesson) {
                   if (iterationITCLesson.getLessonId().equals(String.valueOf(iterationLesson.getSubjectId()))){
                       lessonList.add(iterationITCLesson);
                   }
                }
            }
        }
        return lessonList;
    }
}
