package ru.rsmu.tempoLW.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * @author leonid.
 */
public abstract class ExcelReader {
    protected String getCellValue( Row row, short cellN ) {
        Cell cell = row.getCell( cellN );
        if ( cell != null ) {
            String value;
            switch ( cell.getCellType() ) {
                case STRING:
                    value = cell.getRichStringCellValue().getString().trim();
                    break;
                case NUMERIC:
                    double cellDouble = Math.abs( cell.getNumericCellValue() );
                    double floorDouble = Math.floor( cellDouble );
                    if ( cellDouble == floorDouble ) {
                        value = Long.toString( Math.round( cell.getNumericCellValue() ) );
                    }
                    else {
                        value = Double.toString( cell.getNumericCellValue() );
                    }
                    break;
                case FORMULA:
                    try {
                        value = cell.getRichStringCellValue().toString().trim();
                    } catch (Exception e) { // if formula does not return string we get exception
                        try {               // lets try number
                            value = Double.toString( cell.getNumericCellValue() );
                        } catch (Exception e1) {
                            value = "";
                        }
                    }
                    break;
                default:
                    return null;
            }
            return value;
        }
        return null;
    }

    protected Long getCellNumber( Row row, short cellN ) {
        Cell cell = row.getCell( cellN );
        if ( cell != null ) {
            Long value;
            switch ( cell.getCellType() ) {
                case NUMERIC:
                    value = Math.round( cell.getNumericCellValue() );
                    break;
                default:
                    return null;
            }
            return value;
        }
        return null;
    }
}
