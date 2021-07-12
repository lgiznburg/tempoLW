package ru.rsmu.tempoLW.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * @author leonid.
 */
public class TesteeLoader extends ExcelReader {

    private static final short CASE_NUMBER_CELL = 0;
    private static final short FULL_NAME_CELL = 1;
    private static final short FIRST_NAME_CELL = 2;
    private static final short MIDDLE_NAME_CELL = 3;
    private static final short EMAIL_CELL = 4;
    private static final short GOSUSLUGI_CELL = 5;


    private final TesteeDao testeeDao;

    //private List<String> warning;

    public TesteeLoader( TesteeDao testeeDao ) {
        this.testeeDao = testeeDao;
    }


    public List<Testee> loadTestee( InputStream input, boolean excel2007 ) throws IOException {
        return doLoadTestee( input, excel2007, true );
    }

    public List<Testee> findTestee( InputStream input, boolean excel2007 ) throws IOException {
        return doLoadTestee( input, excel2007, false );
    }


    private List<Testee> doLoadTestee( InputStream input, boolean excel2007, boolean createNewTestee ) throws IOException {
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

            String email = getCellValue( row, EMAIL_CELL );
            if ( testee == null ) {
                testee = new Testee();
                testee.setCaseNumber( caseNumber );
                testee.setLastName( getCellValue( row, FULL_NAME_CELL ) );
                testee.setFirstName( getCellValue( row, FIRST_NAME_CELL ) );
                testee.setMiddleName( getCellValue( row, MIDDLE_NAME_CELL ) );
                String gosuslugi = getCellValue( row, GOSUSLUGI_CELL );
                testee.setGosuslugi( "да".equalsIgnoreCase( gosuslugi ) );
                if ( StringUtils.isNoneBlank( email ) ) {
                    testee.setEmail( email );
                }

                testee.setLogin( createLogin( caseNumber ) );
                if ( createNewTestee ) {
                    testeeDao.save( testee );
                }
            }
            else if ( StringUtils.isBlank( testee.getEmail()) &&
                    StringUtils.isNoneBlank( email ) ) {
                testee.setEmail( email );   // update email address
                testeeDao.save( testee );
            }

            testees.add( testee );
            rowN++;
        } while ( true );

        return testees;
    }

    public String createLogin( String caseNumber ) {
        long number = Long.parseLong( caseNumber.substring( 4 ) );  // skip first 4 digits (year number)
        int yearSinceBegin = Calendar.getInstance().get( Calendar.YEAR ) - 2019;
        number += 100000 * yearSinceBegin;
        String numberCode = Long.toHexString( number );
        String random = RandomStringUtils.randomAlphabetic( 4 ).toLowerCase();
        random = random
                .replace( 'l', 'k' )
                .replace( 'O', 'W' )
                .replace( 'o', 'w' )
                .replace( 'I', 'U' );
        return "rsmu_" + numberCode.substring( 0,2 ) +
                random.substring( 0, 2 ) + numberCode.substring( 2 ) +
                random.substring( 2 );
    }

    public void loadTesteeEmails( InputStream input, boolean excel2007 ) throws IOException {
        Workbook wb;
        if ( excel2007 ) {
            wb = new HSSFWorkbook( input );
        }
        else {
            wb = new XSSFWorkbook( input );
        }
        Sheet sheet = wb.getSheetAt( 0 );  // main page
        Iterator<Row> rowIterator = sheet.rowIterator();
        if ( rowIterator.hasNext() ) rowIterator.next(); // skip first line / header
        while ( rowIterator.hasNext() ) {
            Row row = rowIterator.next();
            String caseNumber = getCellValue( row, CASE_NUMBER_CELL );

            if ( caseNumber != null && caseNumber.matches( "\\d{9}" ) ) {
                Testee testee = testeeDao.findByCaseNumber( caseNumber );

                if ( testee == null ) {
                    testee = new Testee();
                    testee.setCaseNumber( caseNumber );
                    testee.setLogin( createLogin( caseNumber ) );
                }
                testee.setLastName( getCellValue( row, FULL_NAME_CELL ) );
                testee.setFirstName( getCellValue( row, FIRST_NAME_CELL ) );
                testee.setMiddleName( getCellValue( row, MIDDLE_NAME_CELL ) );
                testee.setEmail( getCellValue( row, EMAIL_CELL ) );
                //if ( createNewTestee ) {
                testeeDao.save( testee );
                //}

            }
        }
    }
}
