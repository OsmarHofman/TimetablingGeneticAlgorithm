package ImportFiles.preProcessing;

import java.util.ArrayList;
import java.util.List;

public class CourseRelation {
    private String name;
    private int exclusiveProfessorCount;
    private List<Intersection> intersection;

    public CourseRelation(String name) {
        this.name = name;
        exclusiveProfessorCount = 0;
        intersection = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExclusiveProfessorCount() {
        return exclusiveProfessorCount;
    }

    public void setExclusiveProfessorCount(int exclusiveProfessorCount) {
        this.exclusiveProfessorCount = exclusiveProfessorCount;
    }

    public List<Intersection> getIntersection() {
        return intersection;
    }

    public void setIntersection(List<Intersection> intersection) {
        this.intersection = intersection;
    }

    @Override
    public String toString() {
        return "\nCourseRelation{\n" +
                "name='" + name + '\'' +
                ", exclusiveProfessorCount=" + exclusiveProfessorCount +
                ", intersection=\n" + intersection +
                "}\n";
    }

    public void checkListIntersection(String course, List<String> professorCourses) {
        for (String profCourse : professorCourses) {
            for (Intersection intersec : intersection) {
                if (intersec.getIntersectionCourse().equals(profCourse)) {
                    intersec.setIntersectionProfessorsCount(intersec.getIntersectionProfessorsCount() + 1);
                }
            }
        }

        for (String profCourse : professorCourses) {
            if (!profCourse.equals(course)) {
                intersection.add(new Intersection(1, profCourse));
            }

        }
    }

}
