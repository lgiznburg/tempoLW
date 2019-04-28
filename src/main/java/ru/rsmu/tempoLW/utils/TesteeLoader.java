package ru.rsmu.tempoLW.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.rsmu.tempoLW.dao.TesteeDao;
import ru.rsmu.tempoLW.entities.Testee;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author leonid.
 */
public class TesteeLoader extends ExcelReader {

    private static final short CASE_NUMBER_CELL = 0;
    private static final short FULL_NAME_CELL = 1;
    private static final short EXAM_DATE_CELL = 4;


    private TesteeDao testeeDao;

    //private List<String> warning;

    public TesteeLoader( TesteeDao testeeDao ) {
        this.testeeDao = testeeDao;
    }


    public List<Testee> loadTestee( InputStream input, boolean excel2007 ) throws IOException {
        Workbook wb;
        if ( excel2007 ) {
            wb = new HSSFWorkbook( input );
        }
        else {
            wb = new XSSFWorkbook( input );
        }
        //warning = new LinkedList<>();

        List<Testee> testees = new ArrayList<>();

        Sheet sheet = wb.getSheetAt( 0 );  // main page
        int rowN = 1;  // skip header
        do {
            Row row = sheet.getRow( rowN );
            // check if row is valid
            if ( row == null || row.getCell( CASE_NUMBER_CELL ) == null ||
                    getCellValue( row, CASE_NUMBER_CELL ) == null ) {
                break;
            }
            String caseNumber = getCellValue( row, CASE_NUMBER_CELL );

            Testee testee = testeeDao.findByCaseNumber( caseNumber );

            if ( testee == null ) {
                testee = new Testee();
                testee.setCaseNumber( caseNumber );
                testee.setLastName( getCellValue( row, FULL_NAME_CELL ) );
                testee.setLogin( createLogin( caseNumber ) );
                testeeDao.save( testee );
            }

            testees.add( testee );
            rowN++;
        } while ( true );

        return testees;
    }

    public String createLogin( String caseNumber ) {
        Long number = Long.parseLong( caseNumber );
        String numberCode = Long.toHexString( number );
        String random = RandomStringUtils.randomAlphanumeric( 4 ).toLowerCase();
        return "rsmu" + numberCode.substring( 0,4 ) + "_" +
                random.substring( 0, 2 ) + numberCode.substring( 4 ) +
                random.substring( 2 );
    }
}
