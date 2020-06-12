package ImportFiles;

import java.util.ArrayList;
import java.util.List;

public class Professor_Curso {
    private List<String> professor;
    private List<String> curso;

    public Professor_Curso() {
        this.curso = new ArrayList<String>();
        this.professor = new ArrayList<String>();
    }

    public List<String> getProfessor() {
        return professor;
    }

    public void setProfessor(List<String> professor) {
        this.professor = professor;
    }

    public List<String> getCurso() {
        return curso;
    }

    public void setCurso(List<String> curso) {
        this.curso = curso;
    }
}
