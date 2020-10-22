package ru.rsmu.tempoLW.utils.builder;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.rsmu.tempoLW.entities.Question;
import ru.rsmu.tempoLW.entities.QuestionFree;
import ru.rsmu.tempoLW.entities.UploadedImage;

import java.util.LinkedList;

/**
 * @author leonid.
 */
public class FreeQuestionBuilder extends QuestionBuilder {
    protected FreeQuestionBuilder() {}

    @Override
    public int parse( Sheet sheet, int rowN ) {
        QuestionFree question = new QuestionFree();

        Row row = sheet.getRow( rowN );
        question.setText( getCellValue( row, COLUMN_TEXT ) );

        UploadedImage uploadedImage = checkUploadedImage( row );
        if ( uploadedImage != null ) {
            question.setImage( uploadedImage );
        }
        this.result = question;
        return ++rowN;
    }

    @Override
    public int write( Sheet sheet, int rowN, Question question ) {
        Row row = sheet.createRow( rowN++ );
        writeQuestionInfo( row, question, FREE_TYPE );

        return rowN;
    }
}
