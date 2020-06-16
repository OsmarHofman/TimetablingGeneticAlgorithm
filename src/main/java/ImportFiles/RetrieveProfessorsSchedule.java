package ImportFiles;

import ImportFiles.preProcessing.CourseRelation;
import ImportFiles.preProcessing.Intersection;
import ImportFiles.preProcessing.Professor_Curso;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RetrieveProfessorsSchedule {

    List<Professor_Curso> listaProfessores = new ArrayList<>();


    public XSSFWorkbook pegarArquivo() throws IOException {
        try {
            // procura o arquivo a partir de seu caminho
            FileInputStream arquivo = new FileInputStream(new File("src/Datasets/IFSCFiles/Dados_ifsc_2019.xlsx"));
            return new XSSFWorkbook(arquivo);
        } catch (IOException e) {
            System.out.println("Erro ao tentar ler o arquivo Excel");
            e.printStackTrace();
        }
        throw new IOException("Erro ao pegar arquivo");
    }

    public List<CourseRelation> converterWB(XSSFWorkbook wb) throws IOException {
        XSSFSheet planilha = wb.getSheetAt(0);
        XSSFRow linha;
        XSSFCell celula;


        List<String> listaCursos = new ArrayList<>();
        List<String> listaCursosProfessor = new ArrayList<>();

        Iterator<Row> linhas = planilha.rowIterator();
        String profNome = "";
        while (linhas.hasNext()) {
            linha = (XSSFRow) linhas.next();

            Iterator<Cell> celulas = linha.cellIterator();

            listaCursosProfessor = new ArrayList<>();

            while (celulas.hasNext()) {
                celula = (XSSFCell) celulas.next();

                if (linha.getRowNum() == 0 && celula.getColumnIndex() != 0) {
                    listaCursos.add(celula.getStringCellValue());
                } else {

                    if (celula.getColumnIndex() == 0)
                        profNome = celula.getStringCellValue();
                    else {
                        listaCursosProfessor.add(listaCursos.get(celula.getColumnIndex() - 1));
                    }

                }
            }
            if (linha.getRowNum() != 0)
                listaProfessores.add(new Professor_Curso(profNome, listaCursosProfessor));
        }
        return this.contagemCurso(listaCursos, listaProfessores);
    }

    private List<CourseRelation> contagemCurso(List<String> listaCursos, List<Professor_Curso> listaCursosProfessor) {
        List<CourseRelation> courseRelation = new ArrayList<>();


        for (String curso : listaCursos) {
            CourseRelation iterationCourse = new CourseRelation(curso);
            for (Professor_Curso professor : listaCursosProfessor) {
                String result[] = professor.verifyCourse(curso);
                if (result[0].equals("0")) {
                    iterationCourse.setExclusiveProfessorCount(iterationCourse.getExclusiveProfessorCount() + 1);
                    iterationCourse.setTotalProfessors(iterationCourse.getTotalProfessors() + 1);
                } else if (result[0].equals("1")) {
                    iterationCourse.checkListIntersection(result[1], professor.getCourse());
                    iterationCourse.setTotalProfessors(iterationCourse.getTotalProfessors() + 1);
                }
            }
            courseRelation.add(iterationCourse);

        }
        linkProfessors(courseRelation);

        return courseRelation;
    }

    public void linkProfessors(List<CourseRelation> listaCurso) {
        for (CourseRelation iterationCR : listaCurso) {
            for (Intersection iterationIntersec : iterationCR.getIntersection()) {
                for (Professor_Curso iteratorPC : listaProfessores) {
                    int count = 0;
                    for (String iterationCourse : iteratorPC.getCourse()) {
                        if (iterationCourse.equals(iterationCR.getName()) || iterationCourse.equals(iterationIntersec.getIntersectionCourse())) {
                            count++;
                        }
                        if (count == 2) {
                            iterationIntersec.getProfessorsNameList().add(iteratorPC.getProfessor());
                            break;
                        }
                    }
                }
            }
        }
    }




}
