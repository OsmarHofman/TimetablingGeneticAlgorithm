package preprocessing.model;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import preprocessing.classes.CourseRelation;
import preprocessing.classes.Intersection;
import preprocessing.classes.ProfessorCourseStatus;
import preprocessing.classes.Professor_Course;
import preprocessing.dataaccess.RetrieveSpreadSheetData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProfessorsScheduleCreation {

    private List<String> coursesList;
    private List<Professor_Course> professorsList;
    private List<CourseRelation> courseRelationList;

    public ProfessorsScheduleCreation(String pathname) throws IOException {
        this.getCoursesAndProfessorsByFile(RetrieveSpreadSheetData.getWorkBook(pathname));
        if (this.coursesList != null && this.professorsList != null) {
            this.createCourseRelation();
        } else {
            throw new NullPointerException("A lista de cursos ou de professores está vazia.");
        }
    }

    public List<String> getCoursesList() {
        return coursesList;
    }

    public List<Professor_Course> getProfessorsList() {
        return professorsList;
    }

    public List<CourseRelation> getCourseRelationList() {
        return courseRelationList;
    }

    private void getCoursesAndProfessorsByFile(XSSFWorkbook wb) {
        List<String> professorCoursesList;
        String profName = "";
        this.coursesList = new ArrayList<>();
        this.professorsList = new ArrayList<>();
        XSSFSheet sheet = wb.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.rowIterator();
        XSSFRow row;
        XSSFCell cell;
        System.out.println("Lendo informações do arquivo .xlsx e atribuindo as classes respectivas...\n");
        while (rowIterator.hasNext()) {
            row = (XSSFRow) rowIterator.next();

            Iterator<Cell> cellIterator = row.cellIterator();

            professorCoursesList = new ArrayList<>();

            while (cellIterator.hasNext()) {
                cell = (XSSFCell) cellIterator.next();

                if (row.getRowNum() == 0 && cell.getColumnIndex() != 0) {
                    this.coursesList.add(cell.getStringCellValue());
                } else {

                    if (cell.getColumnIndex() == 0)
                        profName = cell.getStringCellValue();
                    else {
                        professorCoursesList.add(this.coursesList.get(cell.getColumnIndex() - 1));
                    }

                }
            }
            if (row.getRowNum() != 0)
                this.professorsList.add(new Professor_Course(profName, professorCoursesList));
        }
        System.out.println("Classes modeladas e preenchidas com sucesso!\n");
    }

    private void createCourseRelation() {
        courseRelationList = new ArrayList<>();

        System.out.println("Criando a relação entre os professores e os cursos...\n");
        for (String courseName : this.coursesList) {
            CourseRelation iterationCourseRelation = new CourseRelation(courseName);
            for (Professor_Course professor : this.professorsList) {
                String[] result = professor.verifyCourse(courseName);
                if (result[0].equals(ProfessorCourseStatus.EXCLUSIVE.toString())) {
                    iterationCourseRelation.incrementExclusiveProfessorCount();
                    iterationCourseRelation.incrementTotalProfessorCount();
                } else if (result[0].equals(ProfessorCourseStatus.SHARED.toString())) {
                    iterationCourseRelation.checkListIntersection(result[1], professor.getCourse());
                    iterationCourseRelation.incrementTotalProfessorCount();
                }
            }
            this.courseRelationList.add(iterationCourseRelation);
        }

        //link professors
        for (CourseRelation courseRelation : this.courseRelationList) {
            for (Intersection intersection : courseRelation.getIntersection()) {
                for (Professor_Course professor_course : professorsList) {
                    int relatedCourseNumber = 0;
                    for (String iterationCourse : professor_course.getCourse()) {
                        if (iterationCourse.equals(courseRelation.getName()) || iterationCourse.equals(intersection.getIntersectionCourse())) {
                            relatedCourseNumber++;
                        }
                        if (relatedCourseNumber == 2) {
                            intersection.getProfessorsNameList().add(professor_course.getProfessor());
                            break;
                        }
                    }
                }
            }
        }
        System.out.println("Relação entre professores e cursos criada!\n");
    }


}
