package br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model;

import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.classes.CourseRelation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.classes.Intersection;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.classes.Professor_Course;

import java.util.List;

public class ListOperationUtil {


    public static boolean itemIsNotInList(String pattern, List<?> list) {
        if (!list.isEmpty()) {
            for (Object listItem : list) {
                String itemPattern = listItem.toString();
                if (listItem instanceof Intersection)
                    itemPattern = ((Intersection) listItem).getIntersectionCourse();

                if (listItem instanceof CourseRelation)
                    itemPattern = ((CourseRelation) listItem).getName();

                if (itemPattern.equals(pattern))
                    return false;
            }
        }
        return true;
    }

    public static Professor_Course getProfessorByName(String professorName, List<Professor_Course> professors) throws ClassNotFoundException {
        for (Professor_Course iteratorProfessor : professors) {
            if (iteratorProfessor.getProfessor().equals(professorName)) {
                return iteratorProfessor;
            }
        }
        throw new ClassNotFoundException(Professor_Course.class.toString() + " não encontrado");
    }


}
