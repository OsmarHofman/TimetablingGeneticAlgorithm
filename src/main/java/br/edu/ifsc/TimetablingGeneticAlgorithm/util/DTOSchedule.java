package br.edu.ifsc.TimetablingGeneticAlgorithm.util;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Classes;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Subject;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Teacher;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Lesson;

import java.util.ArrayList;
import java.util.List;

public class DTOSchedule {

    private String courseName;
    private List<ScheduleSubject> subjects;

    public DTOSchedule(String name, List<ScheduleSubject> subjects) {
        this.courseName = name;
        this.subjects = subjects;
    }

    public static List<DTOSchedule> convertChromosome(Chromosome chromosome, DTOIFSC dtoifsc, DTOITC dtoitc) throws ClassNotFoundException {
        List<DTOSchedule> schedules = new ArrayList<>();
        //FIXME corrigir dtoSchedule
        for (int i = 0; i < dtoitc.getCourses().length; i++) {
            String courseId = dtoitc.getCourses()[i].getCourseId();
            String courseName = getCourseName(courseId, dtoifsc.getClasses());
            schedules.add(new DTOSchedule(courseName, retrieveScheduleSubjects(dtoitc, dtoifsc, chromosome, courseId)));
        }
        return schedules;
    }

    private static String getCourseName(String id, List<Classes> classes) throws ClassNotFoundException {
        for (Classes classe : classes) {
            String courseId = String.valueOf(classe.getId());
            if (courseId.equals(id))
                return courseId;
        }
        throw new ClassNotFoundException("Erro ao encontrar nome do curso");
    }

    private static List<ScheduleSubject> retrieveScheduleSubjects(DTOITC dtoitc, DTOIFSC dtoifsc, Chromosome chromosome, String courseId) {
        List<ScheduleSubject> subjects = new ArrayList<>();
        byte weekOffset = 0;
        byte periodOffset = 0;
        for (int i = 0; i < chromosome.getGenes().length; i++) {
            if (periodOffset > 3)
                periodOffset = 0;
            if (weekOffset > 19)
                weekOffset = 0;
            for (Lesson lesson : dtoitc.getLessons()) {
                if (lesson.getCourseId().equals(courseId)) {
                    String[] professors = lesson.getProfessorId();
                    String lessonId = lesson.getLessonId();
                    if (Integer.parseInt(lessonId) == chromosome.getGenes()[i]) {
                        String lessonName = "";
                        for (Subject subject : dtoifsc.getSubjects()) {
                            if (lessonId.equals(String.valueOf(subject.getId()))) {
                                lessonName = subject.getName();
                                break;
                            }
                        }
                        StringBuilder professorName = new StringBuilder();
                        for (String professor : professors) {
                            for (Teacher teacher : dtoifsc.getProfessors()) {
                                if (professor.equals(String.valueOf(teacher.getId()))) {
                                    if (professorName.toString().isEmpty()) {
                                        professorName = new StringBuilder(teacher.getName());
                                    } else {
                                        professorName.append(",").append(teacher.getName());
                                    }
                                }
                            }
                        }
                        int weekDay = weekOffset / 4;
                        subjects.add(new ScheduleSubject(lessonName, professorName.toString(), weekDay, periodOffset));
                    }
                }
            }
            periodOffset++;
            weekOffset++;
        }
        return subjects;
    }


    private static class ScheduleSubject {
        private String subjectName;
        private String professorsName;
        private int weekDay;
        private int subjectPeriod;

        public ScheduleSubject() {
        }


        public ScheduleSubject(String subjectName, String professorsName, int weekDay, int subjectPeriod) {
            this.subjectName = subjectName;
            this.professorsName = professorsName;
            this.weekDay = weekDay;
            this.subjectPeriod = subjectPeriod;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public String getProfessorsName() {
            return professorsName;
        }

        public void setProfessorsName(String professorsName) {
            this.professorsName = professorsName;
        }

        public int getWeekDay() {
            return weekDay;
        }

        public void setWeekDay(int weekDay) {
            this.weekDay = weekDay;
        }

        public int getSubjectPeriod() {
            return subjectPeriod;
        }

        public void setSubjectPeriod(int subjectPeriod) {
            this.subjectPeriod = subjectPeriod;
        }
    }
}
