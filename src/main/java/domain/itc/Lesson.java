package domain.itc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lesson {

    private String lessonId;
    private String professorName;
    private int lecturesNumber; //lecturesNumber = durationPeriods do IFSC
    private int minWorkingDays; //minWorkingDays = calculo do periodPerWeek do IFSC
    private int studentsNumber;
    private UnavailabilityConstraint[] constraints;
    private Room room;

    public Lesson() {
    }

    public Lesson(String[]parameters) {
        this.lessonId = parameters[0];
        this.professorName = parameters[1];
        this.lecturesNumber = Integer.parseInt(parameters[2]);
        this.minWorkingDays = Integer.parseInt(parameters[3]);
        this.studentsNumber = Integer.parseInt(parameters[4]);
    }



    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public int getLecturesNumber() {
        return lecturesNumber;
    }

    public void setLecturesNumber(int lecturesNumber) {
        this.lecturesNumber = lecturesNumber;
    }

    public int getMinWorkingDays() {
        return minWorkingDays;
    }

    public void setMinWorkingDays(int minWorkingDays) {
        this.minWorkingDays = minWorkingDays;
    }

    public int getStudentsNumber() {
        return studentsNumber;
    }

    public void setStudentsNumber(int studentsNumber) {
        this.studentsNumber = studentsNumber;
    }

    public UnavailabilityConstraint[] getConstraints() {
        return constraints;
    }

    public void setConstraints(UnavailabilityConstraint[] constraints) {
        this.constraints = constraints;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "lessonId='" + lessonId + '\'' +
                ", professorName='" + professorName + '\'' +
                ", lecturesNumber=" + lecturesNumber +
                ", minWorkingDays=" + minWorkingDays +
                ", studentsNumber=" + studentsNumber +
                ", constraints=" + Arrays.toString(constraints) +
                ", room=" + room +
                '}';
    }

    public static Lesson getLessonById(List<Lesson> allLessons, String id) throws ClassNotFoundException {
        for (Lesson iteratorLesson:allLessons) {
            if (iteratorLesson.getLessonId().equals(id)){
                return iteratorLesson;
            }
        }
        throw new ClassNotFoundException("Lesson não encontrada");
    }

    public void retrieveConstraints(List<UnavailabilityConstraint> constraints){
        List<UnavailabilityConstraint>constraintList = new ArrayList<>();
        for (UnavailabilityConstraint iterationConstraints:constraints) {
            if (iterationConstraints.getLessonId().equals(this.lessonId)){
                constraintList.add(iterationConstraints);
            }
        }
        this.constraints = new UnavailabilityConstraint[constraintList.size()];
        this.constraints = constraintList.toArray(this.constraints);

    }
}
