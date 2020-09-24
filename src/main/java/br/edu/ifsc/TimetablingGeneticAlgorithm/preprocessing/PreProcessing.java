package br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing;

import br.edu.ifsc.TimetablingGeneticAlgorithm.datapreview.classes.CourseRelation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.datapreview.classes.Professor_Course;
import br.edu.ifsc.TimetablingGeneticAlgorithm.datapreview.model.ProfessorsScheduleCreation;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Course;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.DTOIFSC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.DTOITC;

import java.util.Arrays;
import java.util.List;

public class PreProcessing {

        private List<CourseRelation> courseRelationList;
        private List<Professor_Course> professorRelation;

    public PreProcessing(ProfessorsScheduleCreation professorsScheduleCreation) {
        this.professorRelation = professorsScheduleCreation.getProfessorsList();
        this.courseRelationList = professorsScheduleCreation.getCourseRelationList();
    }

    public void preProcess(DTOITC dtoitc, DTOIFSC dtoifsc, int percentage){


        boolean hasJoined = true;
        while (hasJoined){
            Course lastCourse, newCourse = null;

            for (Course iterationCourse: dtoitc.getCourses()) {
                hasJoined = iterationCourse.joinIntersection(percentage,dtoifsc.getClasses());

            }
            // TODO criar intersecções dos cursos
            // TODO verificar se os cursos serão agrupados
            // TODO criar intersecções dos cursos

        }

    }
}
