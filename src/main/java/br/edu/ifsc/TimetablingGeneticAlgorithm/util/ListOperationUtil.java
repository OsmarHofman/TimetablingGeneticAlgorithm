package br.edu.ifsc.TimetablingGeneticAlgorithm.util;

import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.classes.CourseRelation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.classes.Intersection;
import br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.classes.ProfessorCourse;

import java.util.Collections;
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
     * @param professorId {@link String} com o id do professor a ser buscado.
     * @param professors  {@link List} de {@link ProfessorCourse} de onde será buscado.
     * @return O {@link ProfessorCourse} presente na lista.
     * @throws ClassNotFoundException caso o {@code professorId} não esteja na lista.
     */
    public static ProfessorCourse getProfessorById(String professorId, List<ProfessorCourse> professors) throws ClassNotFoundException {
        for (ProfessorCourse iteratorProfessor : professors) {
            if (iteratorProfessor.getProfessor().equals(professorId)) {
                return iteratorProfessor;
            }
        }
        throw new ClassNotFoundException(ProfessorCourse.class.toString() + " não encontrado");
    }

    //TODO INSIRA AQUI DOCUMENTACAO
    public static void removeItemsOnIndexes(List<Integer> indexes, List<?> list) {
        Collections.reverse(indexes);
        for (int index : indexes) {
            list.remove(index);
        }
    }
}