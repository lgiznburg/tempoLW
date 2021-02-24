package ru.rsmu.tempoLW.utils.builder;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.rsmu.tempoLW.entities.*;

import java.util.LinkedList;
import java.util.List;

/**
 * @author leonid.
 */
public class TreeOpenQuestionBuilder extends QuestionBuilder {
    private CorrespondenceVariant currentVariant = null;

    protected TreeOpenQuestionBuilder() {
    }

    @Override
    public int parse( Row row ) {
        String type = getCellValue( row, COLUMN_QUESTION_TYPE );
        if ( type != null ) {
            // this is question row. should we check question type again?
            try {
                QuestionTreeOpen question = loadQuestion( row, QuestionTreeOpen.class );
                if ( question != null && question.getCorrespondenceVariants() == null ) {
                    question.setCorrespondenceVariants( new LinkedList<>() );
                }
            } catch (IllegalAccessException | InstantiationException e) {
                // constructor did not work. log it
            }
        }
        else if ( result != null ){
            String code = getCellValue( row, COLUMN_CODE );
            String text = getCellValue( row, COLUMN_TEXT );

            if ( StringUtils.isNoneBlank( code, text ) ) {
                // correspondence variant row
                CorrespondenceVariant variant = loadCorrespondenceVariant( row );
                List<CorrespondenceVariant> variants = ((QuestionTreeOpen)result).getCorrespondenceVariants();
                if ( !variants.contains( variant ) ) {
                    variants.add( variant );
                }
                currentVariant = variant;
            }
            else {
                // answer row
                if ( StringUtils.isNotBlank( text ) && currentVariant != null ) {
                    AnswerVariant answerVariant = loadAnswer( row );
                    List<AnswerVariant> answers = currentVariant.getCorrectAnswers();
                    if ( !answers.contains( answerVariant ) ) {
                        answers.add( answerVariant );
                    }

                }
            }
        }
        return 0;
    }

    @Override
    public int write( Sheet sheet, int rowN, Question question ) {
        Row row = sheet.createRow( rowN++ );
        writeQuestionInfo( row, question );

        Cell cell;
        char code = 'A';
        for ( CorrespondenceVariant variant : ((QuestionTreeOpen)question).getCorrespondenceVariants() ) {
            row = sheet.createRow( rowN++ );

            cell = row.createCell( COLUMN_CODE );
            cell.setCellValue( String.valueOf( code ) );

            cell = row.createCell( COLUMN_TEXT );
            cell.setCellValue( variant.getText() );

            if ( variant.getImage() != null ) {
                cell = row.createCell( COLUMN_IMAGE );
                cell.setCellValue( variant.getImage().getSourceName() );
            }

            cell = row.createCell( COLUMN_ID );
            cell.setCellValue( variant.getId() );

            rowN = writeAnswers( sheet, rowN, variant.getCorrectAnswers() );
            code++;
        }

        return rowN;
    }
}
