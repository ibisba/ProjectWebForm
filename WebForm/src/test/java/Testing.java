import eu.ibisba.hub.backend.ISA.Assay;
import eu.ibisba.hub.backend.ISA.Investigation;
import eu.ibisba.hub.backend.ISA.Study;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Testing{
    @Test
    public void testWorkflows() {
        String SAMPLE_XLSX_FILE_PATH = "tascu_yeast_muconic_acid.xlsx";
        ArrayList<String> isaLookup = new ArrayList<>();
        isaLookup.add("investigation");
        isaLookup.add("study");
        isaLookup.add("assay");
        HashMap<String, Integer> lookup = new HashMap<>();
        ArrayList<Investigation> investigations = new ArrayList<>();

        try {
            Workbook workbook = WorkbookFactory.create(new File(SAMPLE_XLSX_FILE_PATH));
            Sheet sheet = workbook.getSheet("Sheet1");
            Iterator<Row> rowIter = sheet.rowIterator();
            Investigation investigation = null;
            Study study = null;
            Assay assay;
            while (rowIter.hasNext()) {
                Row row = rowIter.next();
                if (row.getRowNum() == 0) {
                    Iterator<Cell> cellIter = row.cellIterator();
                    while (cellIter.hasNext()) {
                        Cell cell = cellIter.next();
                        String cellValue = cell.getStringCellValue().toLowerCase();
                        for (String isa : isaLookup) {
                            if (isa.equals(cellValue)) {
                                lookup.put(isa, cell.getColumnIndex());
                            }
                        }
                    }
                    // Printing results debug
                    lookup.forEach((s, integer) -> System.err.println(s + " " + integer));
                } else {
                    if (row.getCell(lookup.get("investigation")) != null) {
                        String value = row.getCell(lookup.get("investigation")).getStringCellValue();
                        investigation = new Investigation(value);
                        investigations.add(investigation);
                    }

                    if (row.getCell(lookup.get("study")) != null) {
                        String value = row.getCell(lookup.get("study")).getStringCellValue();
                        study = new Study(value);
                        investigation.addStudy(study);
                    }

                    if (row.getCell(lookup.get("assay")) != null) {
                        String value = row.getCell(lookup.get("assay")).getStringCellValue();
                        String id = row.getCell(lookup.get("id")).getStringCellValue();
                        assay = new Assay(value, Integer.parseInt(id));
                        study.addAssay(assay);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        testInvestigation(investigations);
    }

    private void testInvestigation(ArrayList<Investigation> investigations) {
        System.err.println(investigations.get(0).getStudy().get(0).getAssay().get(0).getDescription());
    }
}
