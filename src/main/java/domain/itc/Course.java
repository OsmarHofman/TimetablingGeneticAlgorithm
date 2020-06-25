package domain.itc;

import java.util.Arrays;
import java.util.List;

public class Course {
    private String courseId;
    private int coursesNumber;
    private Lesson[] lessons;

    public Course() {
    }

    public Course(String[]parameters, List<Lesson> allLessons) throws ClassNotFoundException {
        this.courseId = parameters[0];
        this.coursesNumber = Integer.parseInt(parameters[1]);
        this.lessons = new Lesson[this.coursesNumber];
        for (int i = 2; i <parameters.length ; i++) {
            this.lessons[i-2] = Lesson.getLessonById(allLessons,parameters[i],this.coursesNumber);
        }
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getCoursesNumber() {
        return coursesNumber;
    }

    public void setCoursesNumber(int coursesNumber) {
        this.coursesNumber = coursesNumber;
    }

    public Lesson[] getLessons() {
        return lessons;
    }

    public void setLessons(Lesson[] lessons) {
        this.lessons = lessons;
    }


    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", coursesNumber=" + coursesNumber +
                ", lessons=" + Arrays.toString(lessons) +
                '}';
    }
}
