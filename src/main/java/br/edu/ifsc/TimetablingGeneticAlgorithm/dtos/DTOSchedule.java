package br.edu.ifsc.TimetablingGeneticAlgorithm.dtos;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Classes;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Subject;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Teacher;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Lesson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe que representa um curso e suas matérias para ser tranferido para o frontend
 */
public class DTOSchedule implements Serializable {
    private static final long serialVersionUID = 1L;

    private String courseName;
    private List<ScheduleSubject> subjects;

    public DTOSchedule(String name, List<ScheduleSubject> subjects) {
        this.courseName = name;
        this.subjects = subjects;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public List<ScheduleSubject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<ScheduleSubject> subjects) {
        this.subjects = subjects;
    }

    /**
     * Converte um {@link Chromosome} em uma {@link List} de {@link DTOSchedule}.
     *
     * @param chromosome {@link Chromosome} a ser convertido.
     * @param dtoifsc    {@link DTOIFSC} de onde serão obtidos os {@link String} (nomes) de todos os dados.
     * @param dtoitc     {@link DTOITC} de onde serão obtidos os {@link Integer} (Ids) de todos os dados.
     * @return {@link List} de {@link DTOSchedule} que representa um cromossomo.
     * @throws ClassNotFoundException Erro ao não encontrar o nome de um curso
     */
    public static List<DTOSchedule> convertChromosome(Chromosome chromosome, DTOIFSC dtoifsc, DTOITC dtoitc) throws ClassNotFoundException {
        List<DTOSchedule> schedules = new ArrayList<>();
        for (int i = 0; i < dtoitc.getCourses().length; i++) {
            int courseId = dtoitc.getCourses()[i].getCourseId();
            String courseName = getCourseName(courseId, dtoifsc.getClasses());
            schedules.add(new DTOSchedule(courseName, retrieveScheduleSubjects(dtoitc.getLessons(), dtoifsc, chromosome, courseId)));
        }
        return schedules;
    }

    /**
     * Obtém o nome de um curso através de seu Id.
     *
     * @param id      Id do curso (presente nos dados do DTOITC) a ser obtido o nome.
     * @param classes {@link List} de {@link Classes} de onde será buscado o nome (presente nos dados do DTOIFSC).
     * @return {@link String} que representa o nome do curso.
     * @throws ClassNotFoundException Erro ao não encontrar o nome do curso.
     */
    private static String getCourseName(int id, List<Classes> classes) throws ClassNotFoundException {
        for (Classes classe : classes) {
            int courseId = classe.getId();
            if (courseId == id)
                return classe.getName();
        }
        throw new ClassNotFoundException("Erro ao encontrar nome do curso");
    }

    /**
     * Obtém uma lista de matérias a partir de todos os dados.
     *
     * @param lessons    Vetor de {@link Lesson} de onde serão obtidos os ids dos dados.
     * @param dtoifsc    {@link DTOIFSC} de onde serão obtidos os {@link String} (nomes) de todos os dados.
     * @param chromosome {@link Chromosome} que será analisado e convertido.
     * @param courseId   {@link Integer} que representa o Id do curso a ser convertido.
     * @return {@link List} de {@link ScheduleSubject} convertido de todos os dados presentes no {@link Chromosome}.
     */
    private static List<ScheduleSubject> retrieveScheduleSubjects(Lesson[] lessons, DTOIFSC dtoifsc, Chromosome chromosome, int courseId) {
        List<ScheduleSubject> subjects = new ArrayList<>();
        byte weekOffset = 0;
        byte periodOffset = 0;


        for (int i = 0; i < chromosome.getGenes().length; i++) {
            //Duas matérias por dia (0-1)
            if (periodOffset > 1)
                periodOffset = 0;

            //Dez posicoes para uma semana (0-9)
            if (weekOffset > 9)
                weekOffset = 0;

            for (Lesson lesson : lessons) {
                //Encontra a matéria
                if (lesson.getCourseId() == courseId) {

                    int[] professors = lesson.getProfessorId();
                    int lessonId = lesson.getLessonId();

                    //Encontra a matéria (id) dentro do cromossomo
                    if (lessonId == chromosome.getGenes()[i]) {
                        String lessonName = "";

                        for (Subject subject : dtoifsc.getSubjects()) {
                            if (lessonId == subject.getId()) {
                                //Obtém o nome da matéria
                                lessonName = subject.getName();
                                break;
                            }
                        }

                        //Obtém os professores que lecionam essa matéria, separando por virgula
                        StringBuilder professorName = new StringBuilder();
                        for (int professor : professors) {
                            for (Teacher teacher : dtoifsc.getProfessors()) {
                                if (professor == teacher.getId()) {
                                    if (professorName.toString().isEmpty()) {
                                        professorName = new StringBuilder(teacher.getName());
                                    } else {
                                        professorName.append(", ").append(teacher.getName());
                                    }
                                }
                            }
                        }

                        //Identifica qual o dia da semana que está sendo lecionada a matéria
                        int weekDay = weekOffset / 2;
                        subjects.add(new ScheduleSubject(lessonName, professorName.toString(), weekDay, periodOffset));
                    }
                }
                periodOffset++;
                weekOffset++;
            }
        }
        return subjects;
    }


    /**
     * Classe interna para visualização de todos os dados relacionados a uma matéria
     */
    private static class ScheduleSubject implements Serializable {
        private static final long serialVersionUID = 1L;

        private String subjectName;
        private String professorsName;
        private int weekDay;
        private int subjectPeriod;

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
