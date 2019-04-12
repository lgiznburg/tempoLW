package ru.rsmu.tempoLW.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * @author leonid.
 */
public interface ExcelLayout {
    public static String QUESTION_ROW = "В";
    public static String ANSWER_ROW = "О";
    public static String CORRESPONDENCE_ROW = "У";

    public static String SIMPLE_TYPE = "Пр";
    public static String OPEN_TYPE = "Откр";
    public static String CORRESPONDENCE_TYPE = "Соот";
    public static String SIMPLE_ORDER_TYPE = "Посл";
    public static String TREE_TYPE = "Дер";

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
    public static short COLUMN_IMAGE = 10;

}
