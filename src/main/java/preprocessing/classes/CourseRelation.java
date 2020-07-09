package preprocessing.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourseRelation implements Serializable {
    private String name;
    private int totalProfessors;
    private int exclusiveProfessorCount;
    private List<Intersection> intersection;

    public CourseRelation(String name) {
        this.name = name;
        totalProfessors = 0;
        exclusiveProfessorCount = 0;
        intersection = new ArrayList<>();
    }

    public CourseRelation() {
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
        return name;
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
            if (!profCourse.equals(course) && !findByName(profCourse, relateds)) {
                intersection.add(new Intersection(1, profCourse));
            }

        }


    }

    private Boolean findByName(String name, List<IsRelated> relateds) {
        for (IsRelated related : relateds) {
            if (related.getName().equals(name) && related.isHasAdded())
                return true;
        }
        return false;
    }

    public boolean joinIntersections(int percentage, List<CourseRelation> cs) throws ClassNotFoundException {
        List<String> listIntersection = new ArrayList<>(Collections.singletonList(this.name));
        StringBuilder nomeCurso = new StringBuilder(this.name);
        CourseRelation newCourse = new CourseRelation();
        int exclusiveProfessors = this.exclusiveProfessorCount;
        int proportion = this.totalProfessors * percentage / 100;
        for (Intersection iteratorIntersection : intersection) {
            if (iteratorIntersection.getIntersectionProfessorsCount() >= proportion) {
                listIntersection.add(iteratorIntersection.getIntersectionCourse());
                if (iteratorIntersection.getIntersectionCourse().contains("-")) {
                    nomeCurso.insert(0, iteratorIntersection.getIntersectionCourse() + "--");
                } else {
                    nomeCurso.append("-").append(iteratorIntersection.getIntersectionCourse());
                }
                CourseRelation course = this.getCourseByName(cs, iteratorIntersection.getIntersectionCourse());
                exclusiveProfessors += course.getExclusiveProfessorCount();
            }
        }
        if (listIntersection.size() > 1) {
            newCourse.setName(nomeCurso.toString());
            newCourse.setExclusiveProfessorCount(exclusiveProfessors);
            cs.add(newCourse);
            return true;
        }
        return false;
    }

    public CourseRelation getCourseByName(List<CourseRelation> cs, String course) throws ClassNotFoundException {
        for (CourseRelation iterationCS : cs) {
            if (iterationCS.getName().equals(course)) {
                return iterationCS;
            }
        }
        throw new ClassNotFoundException("Curso não encontrado");
    }

    public void incrementExclusiveProfessorCount() {
        this.exclusiveProfessorCount++;
    }

    public void incrementTotalProfessorCount() {
        this.totalProfessors++;
    }

    public void sumTotalProfessors() {
        List<String> professors = new ArrayList<>();
        for (Intersection iteratorIntersection : this.getIntersection()) {
            if (professors.isEmpty()) {
                professors.addAll(iteratorIntersection.getProfessorsNameList());
            } else {
                for (String iteratorProfessors : iteratorIntersection.getProfessorsNameList()) {
                    if (!professors.contains(iteratorProfessors)) {
                        professors.add(iteratorProfessors);
                    }
                }
            }

        }
        this.totalProfessors = professors.size() + exclusiveProfessorCount;
    }

}
