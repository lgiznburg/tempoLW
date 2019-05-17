package ru.rsmu.tempoLW.utils.builder;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.rsmu.tempoLW.entities.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author leonid.
 */
public class CorrespondenceQuestionBuilder extends QuestionBuilder {
    protected CorrespondenceQuestionBuilder() {
    }

    @Override
    public int parse( Sheet sheet, int rowN ) {
        QuestionCorrespondence question = new QuestionCorrespondence();
        question.setAnswerVariants( new LinkedList<>() );
        question.setCorrespondenceVariants( new LinkedList<>() );

        Row row = sheet.getRow( rowN );
        question.setText( getCellValue( row, COLUMN_TEXT ) );

        UploadedImage uploadedImage = checkUploadedImage( row );
        if ( uploadedImage != null ) {
            question.setImage( uploadedImage );
        }

        Map<String, CorrespondenceVariant> variantMap = new HashMap<>();

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
                String code = getCellValue( row, COLUMN_CODE );
                variantMap.put( code, variant );
            }
            else if ( ANSWER_ROW.equalsIgnoreCase( rowType ) ) {
                AnswerVariant answerVariant = new AnswerVariant();
                answerVariant.setText( getCellValue( row, COLUMN_TEXT ) );
                answerVariant.setQuestion( question );
                question.getAnswerVariants().add( answerVariant );

                uploadedImage = checkUploadedImage( row );
                if ( uploadedImage != null ) {
                    answerVariant.setImage( uploadedImage );
                }

                String correct = getCellValue( row, COLUMN_RIGHTNESS );
                if ( correct != null && !correct.isEmpty() ) {
                    String[] codes = correct.split( "," );
                    for ( String code : codes ) {
                        CorrespondenceVariant variant = variantMap.get( code );
                        if ( variant != null ) {
                            variant.getCorrectAnswers().add( answerVariant );
                            answerVariant.setCorrect( true );
                        }
                        else {
                            //error ?
                        }
                    }
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
        writeQuestionInfo( row, question, CORRESPONDENCE_TYPE );

        Cell cell;
        Map<Long, String> codes = new HashMap<>();
        char code = 'A';
        for ( CorrespondenceVariant variant : ((QuestionCorrespondence)question).getCorrespondenceVariants() ) {
            row = sheet.createRow( rowN++ );

            cell = row.createCell( COLUMN_ROW_TYPE );
            cell.setCellValue( CORRESPONDENCE_ROW );

            cell = row.createCell( COLUMN_CODE );
            cell.setCellValue( String.valueOf( code ) );

            cell = row.createCell( COLUMN_TEXT );
            cell.setCellValue( variant.getText() );

            if ( variant.getImage() != null ) {
                cell = row.createCell( COLUMN_IMAGE );
                cell.setCellValue( variant.getImage().getSourceName() );
            }

            for ( AnswerVariant answer : variant.getCorrectAnswers() ) {
                String answerCode = codes.get( answer.getId() );
                if ( answerCode == null ) {
                    answerCode = String.valueOf( code );
                }
                else {
                    answerCode = answerCode + "," + code;
                }
                codes.put( answer.getId(), answerCode );
            }
            code++;
        }

        rowN = writeAnswers( sheet, rowN, ((QuestionCorrespondence)question).getAnswerVariants(), codes );

        return rowN;
    }

}
