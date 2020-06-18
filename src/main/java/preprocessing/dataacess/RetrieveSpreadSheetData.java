package preprocessing.dataacess;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class RetrieveSpreadSheetData {

    public static XSSFWorkbook getWorkBook(String pathname) throws IOException {
        try {
            // procura o arquivo a partir de seu caminho
            FileInputStream arquivo = new FileInputStream(new File(pathname));
            return new XSSFWorkbook(arquivo);
        } catch (
                IOException e) {
            System.out.println("Erro ao tentar ler o arquivo Excel.");
            e.printStackTrace();
        }
        throw new IOException("Erro ao pegar arquivo.");
    }
}
