package ru.rsmu.tempoLW.utils.builder;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.rsmu.tempoLW.entities.*;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author leonid.
 */
public class TreeQuestionBuilder extends QuestionBuilder {
    protected TreeQuestionBuilder() {
    }

    @Override
    public int parse( Sheet sheet, int rowN ) {
        QuestionTree question = new QuestionTree();
        question.setCorrespondenceVariants( new ArrayList<>() );

        Row row = sheet.getRow( rowN );
        question.setText( getCellValue( row, COLUMN_TEXT ) );

        UploadedImage uploadedImage = checkUploadedImage( row );
        if ( uploadedImage != null ) {
            question.setImage( uploadedImage );
        }
        CorrespondenceVariant currentVariant = null;
        do {
            row = sheet.getRow( ++rowN );
            if ( row == null || row.getCell( COLUMN_ROW_TYPE ) == null ) {
                break; // empty row
            }
            String rowType = getCellValue( row, COLUMN_ROW_TYPE );
            if ( CORRESPONDENCE_ROW.equalsIgnoreCase( rowType ) ) {
                CorrespondenceVariant variant = new CorrespondenceVariant();
                variant.setCorrectAnswers( new LinkedList<>() );
                variant.setText( getCellValue( row, COLUMN_TEXT ) );
                variant.setQuestion( question );
                question.getCorrespondenceVariants().add( variant );
                uploadedImage = checkUploadedImage( row );
                if ( uploadedImage != null ) {
                    variant.setImage( uploadedImage );
                }

                currentVariant = variant;
            }
            else if ( ANSWER_ROW.equalsIgnoreCase( rowType ) ) {
                AnswerVariant answerVariant = new AnswerVariant();
                answerVariant.setText( getCellValue( row, COLUMN_TEXT ) );
                answerVariant.setQuestion( question );

                uploadedImage = checkUploadedImage( row );
                if ( uploadedImage != null ) {
                    answerVariant.setImage( uploadedImage );
                }

                answerVariant.setCorrect( getCellNumber( row, COLUMN_RIGHTNESS ) != null );

                if ( currentVariant != null ) {
                    currentVariant.getCorrectAnswers().add( answerVariant );
                }

            }
            else {
                break;
            }

        } while ( true );

        this.result = question;
        return rowN;
    }

    @Override
    public int write( Sheet sheet, int rowN, Question question ) {
        Row row = sheet.createRow( rowN++ );
        writeQuestionInfo( row, question, TREE_TYPE );

        Cell cell;
        for ( CorrespondenceVariant variant : ((QuestionCorrespondence)question).getCorrespondenceVariants() ) {
            row = sheet.createRow( rowN++ );

            cell = row.createCell( COLUMN_ROW_TYPE );
            cell.setCellValue( CORRESPONDENCE_ROW );


            cell = row.createCell( COLUMN_TEXT );
            cell.setCellValue( variant.getText() );

            if ( variant.getImage() != null ) {
                cell = row.createCell( COLUMN_IMAGE );
                cell.setCellValue( variant.getImage().getSourceName() );
            }
            rowN = writeAnswers( sheet, rowN, variant.getCorrectAnswers() );

        }

        return rowN;
    }
}
