package br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Teacher;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOIFSC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing.ScheduleTime.Dia;
import br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing.ScheduleTime.Horario;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.ConvertFactory;

import java.util.List;

public class ViolatedConstraint {

    int professorId;
    String professorName;
    Dia dia;
    Horario horario;
    List<Integer> conflictedClasses;
    int availableTime;

    public ViolatedConstraint() {
    }

    public ViolatedConstraint(int professorId, String professorName, Dia dia, Horario horario, List<Integer> conflictedClasses) {
        this.professorId = professorId;
        this.professorName = professorName;
        this.dia = dia;
        this.horario = horario;
        this.conflictedClasses = conflictedClasses;
    }


    public int getProfessorId() {
        return professorId;
    }

    public void setProfessorId(int professorId) {
        this.professorId = professorId;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public Dia getDia() {
        return dia;
    }

    public void setDia(Dia dia) {
        this.dia = dia;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public List<Integer> getConflictedClasses() {
        return conflictedClasses;
    }

    public void setConflictedClasses(List<Integer> conflictedClasses) {
        this.conflictedClasses = conflictedClasses;
    }

    public int getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(DTOIFSC dtoifsc, Chromosome chromosome) {
        Teacher teacher = dtoifsc.getProfessorById(professorId);
        int weekWorkload = ConvertFactory.convertTimeoffToAvailableTime(teacher.getTimeoff());
        for (int gene : chromosome.getGenes()) {
            for (Lesson lesson : dtoifsc.getLessons()) {
                if (lesson.getSubjectId() == gene) {
                    for (int teacherId : lesson.getTeacherId()) {
                        if (teacherId == professorId) {
                            weekWorkload--;
                        }
                    }
                }
            }
        }
        availableTime = weekWorkload;
    }
}
