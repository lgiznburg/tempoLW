package ru.rsmu.tempoLW.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * @author leonid.
 */
public interface ExcelLayout {
    String QUESTION_ROW = "В";
    String ANSWER_ROW = "О";
    String CORRESPONDENCE_ROW = "У";

    String SIMPLE_TYPE = "Пр";
    String OPEN_TYPE = "Откр";
    String CORRESPONDENCE_TYPE = "Соот";
    String SIMPLE_ORDER_TYPE = "Посл";
    String TREE_TYPE = "Дер";
    String FREE_TYPE = "Своб";

    short COLUMN_ROW_TYPE = 0;
    short COLUMN_QUESTION_TYPE = 1;
    short COLUMN_TOPIC = 2;
    short COLUMN_COMPLEXITY = 3;
    short COLUMN_MAX_SCORE = 4;
    short COLUMN_NAME = 5;
    short COLUMN_VARIANT = 6;
    short COLUMN_CODE = 7;
    short COLUMN_TEXT = 8;
    short COLUMN_RIGHTNESS = 9;
    short COLUMN_IMAGE = 10;

}
