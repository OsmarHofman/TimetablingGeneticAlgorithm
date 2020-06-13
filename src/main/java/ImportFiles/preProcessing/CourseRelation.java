package ImportFiles.preProcessing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CourseRelation implements Serializable {
    private String name;
    private int totalProfessors;
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

    public int getTotalProfessors() {
        return totalProfessors;
    }

    public void setTotalProfessors(int totalProfessors) {
        this.totalProfessors = totalProfessors;
    }

    @Override
    public String toString() {
        return "\n\nCurso: " + name +
                "\n\t Total de professores= " + totalProfessors +
                "\n\t Número de professores exclusivos=" + exclusiveProfessorCount +
                "\n\t Professores com intersecção=\n\t" + intersection +
                "}";
    }

    public void checkListIntersection(String course, List<String> professorCourses) {
        List<IsRelated> relateds = new ArrayList<>();
        IsRelated isRelated;
        for (String profCourse : professorCourses) {
            for (Intersection intersec : intersection) {
                if (intersec.getIntersectionCourse().equals(profCourse)) {
                    intersec.setIntersectionProfessorsCount(intersec.getIntersectionProfessorsCount() + 1);
                    isRelated = new IsRelated(intersec.getIntersectionCourse(), true);
                } else {
                    isRelated = new IsRelated(intersec.getIntersectionCourse(), false);
                }
                relateds.add(isRelated);
            }
        }

        for (String profCourse : professorCourses) {
            if (!profCourse.equals(course) && !findByName(profCourse,relateds)) {
                intersection.add(new Intersection(1, profCourse));
            }

        }


    }

    private Boolean findByName(String name, List<IsRelated>relateds){
        for (IsRelated related: relateds) {
            if (related.getName().equals(name) && related.isHasAdded())
                return true;
        }
        return false;
    }

}
