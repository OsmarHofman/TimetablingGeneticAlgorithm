package preprocessing.classes;

import java.util.ArrayList;
import java.util.List;

public class Professor_Course {
    private String professor;
    private List<String> course;

    public Professor_Course() {
        this.course = new ArrayList<>();
    }

    public Professor_Course(String professor, List<String> curso) {
        this.professor = professor;
        this.course = curso;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public List<String> getCourse() {
        return course;
    }

    public void setCourse(List<String> curso) {
        this.course = curso;
    }

    @Override
    public String toString() {
        return "Professor_Course{" +
                "professor='" + professor + '\'' +
                ", course=" + course +
                '}';
    }

    //-1 é quando não acha, 0 é exclusivo, e 1 não é exclusivo
    public String[] verifyCourse(String course) {
        if (this.course.size() == 1 && this.course.get(0).equals(course))
            return new String[]{ProfessorCourseStatus.EXCLUSIVE.toString()};

        for (String iterationCourse : this.course) {
            if (iterationCourse.equals(course)) {
                return new String[]{ProfessorCourseStatus.SHARED.toString(), iterationCourse};
            }
        }
        return new String[]{ProfessorCourseStatus.NOTRELATED.toString()};

    }

}
