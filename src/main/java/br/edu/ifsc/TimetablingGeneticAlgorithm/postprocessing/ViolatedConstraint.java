package br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Teacher;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOIFSC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing.ScheduleTime.Day;
import br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing.ScheduleTime.Period;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.ConvertFactory;

import java.util.*;

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

    public void setAvailableTime(DTOIFSC dtoifsc, Chromosome chromosome) {
        for (Integer conflictClass : conflictedClasses) {
            this.availableTime += sumClassTotalAvailableTime(dtoifsc, conflictClass, chromosome);
        }
    }

    public int sumClassTotalAvailableTime(DTOIFSC dtoifsc, int conflictClass, Chromosome chromosome) {
        int totalAvailableTime = 0;
        List<Teacher> teachers = dtoifsc.getAllTeachersInClass(conflictClass);
        for (Teacher teacher : teachers) {
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
            totalAvailableTime += weekWorkload;
        }
        return totalAvailableTime;
    }

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

    public int getChromossomePositionByDayPeriod() {
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
