package ImportFiles;

import ImportFiles.Professor_Curso;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class RetrieveProfessorsSchedule {

    public void pegarArquivo() {
        try {
            // procura o arquivo a partir de seu caminho
            FileInputStream arquivo = new FileInputStream(new File("src/Datasets/IFSCFiles/Dados_ifsc_2019.xlsx"));
            XSSFWorkbook wb = new XSSFWorkbook(arquivo);
            this.converterWB(wb);
        } catch (IOException e) {
            System.out.println("Erro ao tentar ler o arquivo Excel");
            e.printStackTrace();
        }

    }

    private void converterWB(XSSFWorkbook wb) throws IOException {
        XSSFSheet planilha = wb.getSheetAt(0);
        XSSFRow linha;
        XSSFCell celula;
        Professor_Curso professor_curso = new Professor_Curso();

        Iterator<Row> linhas = planilha.rowIterator();

        while (linhas.hasNext()) {
            linha = (XSSFRow) linhas.next();

            Iterator<Cell> celulas = linha.cellIterator();

            while (celulas.hasNext()) {
                celula = (XSSFCell) celulas.next();

                if (celula.getColumnIndex() == 0) {
                    professor_curso.getProfessor().add(celula.getStringCellValue());
                } else {
                    break;
                }
            }

        }
//
//        linhas = planilha.rowIterator();
//
//        while (linhas.hasNext()) {
//            linha = (XSSFRow) linhas.next();
//
//            if (linha.getRowNum() > 1) {
//
//                Iterator<Cell> celulas = linha.cellIterator();
//                conecta = null;
//
//                while (celulas.hasNext()) {
//                    celula = (XSSFCell) celulas.next();
//
//                    if (celula.getColumnIndex() == 0) {
//                        conecta = grafo.pesquisaVertice(celula.getStringCellValue());
//                    } else {
//                        if (celula.getCellType().toString().equals("NUMERIC")) {
//                            double peso = celula.getNumericCellValue();
//                            grafo.pesquisaVertice(
//                                    planilha.getRow(1).getCell(celula.getColumnIndex()).getStringCellValue())
//                                    .adicionarArco(conecta, peso);
//                        }
//                    }
//                }
//            }
//        }
//
//        planilha = wb.getSheetAt(1);
//        linhas = planilha.rowIterator();
//
//        while (linhas.hasNext()) {
//            linha = (XSSFRow) linhas.next();
//
//            if (linha.getRowNum() > 1) {
//
//                Iterator<Cell> celulas = linha.cellIterator();
//                conecta = null;
//
//                while (celulas.hasNext()) {
//                    celula = (XSSFCell) celulas.next();
//
//                    if (celula.getColumnIndex() == 0) {
//                        conecta = grafo.pesquisaVertice(celula.getStringCellValue());
//                    } else {
//                        if (celula.getCellType().toString().equals("NUMERIC")) {
//                            double peso = celula.getNumericCellValue();
//                            grafo.pesquisaVertice(
//                                    planilha.getRow(1).getCell(celula.getColumnIndex()).getStringCellValue())
//                                    .adicionarArcoHeuristica(conecta, peso);
//                        }
//                    }
//                }
//            }
//        }
//        wb.close();
    }
}
