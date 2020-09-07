package br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.model;

import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.classes.CourseRelation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.classes.Intersection;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.classes.Professor_Course;

import java.util.List;

public class ListOperationUtil {


    /**
     * Verifica se um item está ou não em uma determinada lista. Os tipos de listas tratadas e seus atributos são:
     *
     * <ul>
     *     <li>
     *         {@link Intersection}: intersectionCourse;
     *     </li>
     *     <li>
     *         {@link CourseRelation}: name;
     *     </li>
     *     <li>
     *         {@link String}.
     *     </li>
     * </ul>
     *
     * @param pattern {@link String} padrão a ser identificado se está na lista.
     * @param list    {@link List} a ser verificada.
     * @return {@code true} caso o item esteja na lista, e {@code false} caso contrário.
     */
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

    /**
     * Obtém um professor através do seu nome.
     *
     * @param professorName {@link String} com o nome do professor a ser buscado.
     * @param professors    {@link List} de {@link Professor_Course} de onde será buscado.
     * @return O {@link Professor_Course} presente na lista.
     * @throws ClassNotFoundException caso o {@code professorName} não esteja na lista.
     */
    public static Professor_Course getProfessorByName(String professorName, List<Professor_Course> professors) throws ClassNotFoundException {
        for (Professor_Course iteratorProfessor : professors) {
            if (iteratorProfessor.getProfessor().equals(professorName)) {
                return iteratorProfessor;
            }
        }
        throw new ClassNotFoundException(Professor_Course.class.toString() + " não encontrado");
    }
}
