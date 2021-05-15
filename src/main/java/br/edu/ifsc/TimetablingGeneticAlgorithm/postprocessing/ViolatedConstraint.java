package br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Teacher;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOIFSC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing.ScheduleTime.Day;
import br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing.ScheduleTime.Period;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.ConvertFactory;

import java.util.Collections;
import java.util.List;

/**
 * Classe que representa uma restrição violada
 */
public class ViolatedConstraint {

    int professorId;
    String professorName;
    Day day;
    Period period;
    List<Integer> conflictedClasses;
    int availableTime;

    public ViolatedConstraint() {
    }

    public ViolatedConstraint(int professorId, String professorName, Day day, Period period, List<Integer> conflictedClasses) {
        this.professorId = professorId;
        this.professorName = professorName;
        this.day = day;
        this.period = period;
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

    public Day getDia() {
        return day;
    }

    public void setDia(Day day) {
        this.day = day;
    }

    public Period getHorario() {
        return period;
    }

    public void setHorario(Period period) {
        this.period = period;
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

    /**
     * Atribui o tempo disponível de uma turma a partir dos tempos de folgas dos professores
     *
     * @param dtoifsc    {@link DTOIFSC} com os dados dos professores e turmas.
     * @param chromosome {@link Chromosome} que serão verificados os genes para obter os professores.
     */
    public void setAvailableTime(DTOIFSC dtoifsc, Chromosome chromosome) {
        for (Integer conflictClass : conflictedClasses) {
            this.availableTime += sumClassTotalAvailableTime(dtoifsc, conflictClass, chromosome);
        }
    }

    /**
     * Obtém a soma de todos os horários de folga de um professor de uma turma
     *
     * @param dtoifsc       {@link DTOIFSC} com os dados dos professores e turma.
     * @param conflictClass posição que indica a turma com o conflito.
     * @param chromosome    {@link Chromosome} que serão verificados os genes para obter os professores.
     * @return inteiro que representa a soma total do tempo disponível de uma turma.
     */
    public int sumClassTotalAvailableTime(DTOIFSC dtoifsc, int conflictClass, Chromosome chromosome) {
        int totalAvailableTime = 0;
        //Pega todos os professores de uma turma
        List<Teacher> teachers = dtoifsc.getAllTeachersInClass(conflictClass);
        for (Teacher teacher : teachers) {
            //Essa variável controla quanto de carga de trabalho um professor tem em uma semana
            int weekWorkload = ConvertFactory.convertTimeoffToAvailableTime(teacher.getTimeoff());
            for (int gene : chromosome.getGenes()) {
                for (Lesson lesson : dtoifsc.getLessons()) {
                    if (lesson.getSubjectId() == gene) {
                        for (int teacherId : lesson.getTeacherId()) {
                            if (teacherId == professorId) {
                                /*Cada vez que é identificado que o professor leciona em uma turma,
                                 é decrementado seu tempo disponível na semana
                                 */
                                weekWorkload--;
                            }
                        }
                    }
                }
            }
            totalAvailableTime += weekWorkload;
        }
        return totalAvailableTime;
    }

    /**
     * Gera uma lista organizado pela turma com maior tempo disponível.
     *
     * @param dtoifsc    {@link DTOIFSC} com todos os dados das turmas.
     * @param chromosome {@link Chromosome} que será verificado a tempo disponível das turmas.
     * @return {@link List} de inteiros com as turmas organizadas pelo tempo disponível dos professores.
     */
    public List<Integer> getConflictedClassWithGreaterAvailableTime(DTOIFSC dtoifsc, Chromosome chromosome) {
        int[] conflictedClassesAvailableTime = new int[2];
        for (int i = 0; i < this.conflictedClasses.size(); i++) {
            conflictedClassesAvailableTime[i] = sumClassTotalAvailableTime(dtoifsc, this.conflictedClasses.get(i), chromosome);
        }
        if (conflictedClassesAvailableTime[0] > conflictedClassesAvailableTime[1])
            return conflictedClasses;
        Collections.reverse(conflictedClasses);
        return conflictedClasses;
    }

    /**
     * Obtém uma posição do cromossomo de acordo com o dia.
     *
     * @return posição do cromossomo.
     */
    public int getChromosomePositionByDayPeriod() {
        return this.day.ordinal() * 2 + this.period.ordinal();
    }

    @Override
    public String toString() {
        return "ViolatedConstraint{" +
                "professorName='" + professorName + '\'' +
                ", day=" + day +
                ", period=" + period +
                ", conflictedClasses=" + conflictedClasses +
                '}';
    }
}
