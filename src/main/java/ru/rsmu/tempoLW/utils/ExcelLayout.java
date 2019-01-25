package ru.rsmu.tempoLW.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * @author leonid.
 */
public abstract class ExcelLayout {
    public static String QUESTION_ROW = "В";
    public static String ANSWER_ROW = "О";
    public static String CORRESPONDENCE_ROW = "У";

    public static String SIMPLE_TYPE = "Пр";
    public static String OPEN_TYPE = "Откр";
    public static String CORRESPONDENCE_TYPE = "Соот";
    public static String SIMPLE_ORDER_TYPE = "Посл";

    public static short COLUMN_ROW_TYPE = 0;
    public static short COLUMN_QUESTION_TYPE = 1;
    public static short COLUMN_TOPIC = 2;
    public static short COLUMN_COMPLEXITY = 3;
    public static short COLUMN_MAX_SCORE = 4;
    public static short COLUMN_NAME = 5;
    public static short COLUMN_VARIANT = 6;
    public static short COLUMN_CODE = 7;
    public static short COLUMN_TEXT = 8;
    public static short COLUMN_RIGHTNESS = 9;

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
