package eu.ibisba.hub.backend;

import java.io.File;
import java.io.IOException;
import java.util.*;

import eu.ibisba.hub.backend.ISA.Assay;
import eu.ibisba.hub.backend.ISA.Investigation;
import eu.ibisba.hub.backend.ISA.Study;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

@Service
public class BackendService {

    private List<IBISBAMember> employees;
    private List<Workflow> workflows;

    {
        // Init dummy data

        employees = new ArrayList<>();
        employees.add(new IBISBAMember("Rowena", "Leeming", "rleeming0@bbc.co.uk", "Food Chemist"));
        employees.add(new IBISBAMember("Alvinia", "Delong", "adelong1@altervista.org", "Recruiting Manager"));
        employees.add(new IBISBAMember("Leodora", "Burry", "lburry2@example.com", "Food Chemist"));
        employees.add(new IBISBAMember("Karen", "Oaten", "koaten3@ihg.com", "VP Sales"));
        employees.add(new IBISBAMember("Mariele", "Huke", "mhuke4@washingtonpost.com", "Research Assistant IV"));
        employees.add(new IBISBAMember("Grata", "Widdowes", "gwiddowes5@cargocollective.com", "Actuary"));
        employees.add(new IBISBAMember("Donna", "Roadknight", "droadknight6@apache.org", "Mechanical Systems Engineer"));
        employees.add(new IBISBAMember("Tommi", "Nowland", "tnowland7@biblegateway.com", "Senior Developer"));
        employees.add(new IBISBAMember("Tonya", "Teresia", "tteresia8@boston.com", "Assistant Manager"));
        employees.add(new IBISBAMember("Steffen", "Yon", "syon9@ocn.ne.jp", "Senior Sales Associate"));
        employees.add(new IBISBAMember("Consalve", "Willes", "cwillesa@linkedin.com", "Programmer I"));
        employees.add(new IBISBAMember("Jeanelle", "Lambertz", "jlambertzb@nymag.com", "Operator"));
        employees.add(new IBISBAMember("Odelia", "Loker", "olokerc@gov.uk", "Developer I"));
        employees.add(new IBISBAMember("Briano", "Shawell", "bshawelld@posterous.com", "Research Assistant IV"));
        employees.add(new IBISBAMember("Tarrance", "Mainston", "tmainstone@cmu.edu", "Research Nurse"));
        employees.add(new IBISBAMember("Torrence", "Gehring", "tgehringf@a8.net", "Geological Engineer"));
        employees.add(new IBISBAMember("Augie", "Pionter", "apionterg@ehow.com", "Senior Financial Analyst"));
        employees.add(new IBISBAMember("Marillin", "Aveson", "mavesonh@shop-pro.jp", "Technical Writer"));
        employees.add(new IBISBAMember("Jacquelyn", "Moreby", "jmorebyi@slashdot.org", "Executive Secretary"));
        employees.add(new IBISBAMember("Glenn", "Bangley", "gbangleyj@prlog.org", "Account Executive"));
        employees.add(new IBISBAMember("Isidoro", "Glave", "iglavek@tamu.edu", "Compensation Analyst"));
        employees.add(new IBISBAMember("Cchaddie", "Spatarul", "cspatarull@sun.com", "Business Systems Development Analyst"));

    }

    public List<IBISBAMember> getEmployees() {
        return employees;
    }

    public ArrayList<Investigation> getWorkflows() {
        String SAMPLE_XLSX_FILE_PATH = "tascu_yeast_muconic_acid.xlsx";
        ArrayList<String> isaLookup = new ArrayList<>();
        isaLookup.add("investigation");
        isaLookup.add("study");
        isaLookup.add("assay");
        isaLookup.add("id");
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

                    if (row.getCell(lookup.get("assay")) != null &&
                            row.getCell(lookup.get("assay")).getStringCellValue().trim().length() > 0 &&
                            row.getCell(lookup.get("id")) != null) {

                        String value = row.getCell(lookup.get("assay")).getStringCellValue();
                        // double id = Double.parseDouble(row.getCell(lookup.get("id")).getStringCellValue());
//                        System.err.println(lookup.get("id"));
//                        System.err.println(row.getCell(lookup.get("id")));
//                        System.err.println(row.getCell(lookup.get("id")).getCellTypeEnum());
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
        return investigations;
    }
}
