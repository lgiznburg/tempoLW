package ru.rsmu.tempoLW.utils.builder;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.rsmu.tempoLW.entities.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author leonid.
 */
public class CorrespondenceQuestionBuilder extends QuestionBuilder {

    Map<String, CorrespondenceVariant> variantMap = new HashMap<>();

    protected CorrespondenceQuestionBuilder() {
    }

    @Override
    public int parse( Row row ) {
        String type = getCellValue( row, COLUMN_QUESTION_TYPE );
        if ( type != null ) {
            // this is question row. should we check question type again?
            try {
                QuestionCorrespondence question = loadQuestion( row, QuestionCorrespondence.class );
                if ( question != null && question.getAnswerVariants() == null ) {
                    question.setAnswerVariants( new LinkedList<>() );
                }
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
                List<CorrespondenceVariant> variants = ((QuestionCorrespondence)result).getCorrespondenceVariants();
                if ( !variants.contains( variant ) ) {
                    variants.add( variant );
                }
                variantMap.put( code, variant );
            }
            else {
                // answer row
                if ( StringUtils.isNotBlank( text ) ) {
                    AnswerVariant answerVariant = loadAnswer( row );
                    List<AnswerVariant> answers = ((QuestionCorrespondence)result).getAnswerVariants();
                    if ( !answers.contains( answerVariant ) ) {
                        answers.add( answerVariant );
                    }

                    String correct = getCellValue( row, COLUMN_RIGHTNESS );
                    if ( StringUtils.isNotBlank( correct ) ) {
                        String[] codes = correct.split( "," );
                        for ( String answerCode : codes ) {
                            CorrespondenceVariant variant = variantMap.get( answerCode.trim() );
                            if ( variant != null && !variant.getCorrectAnswers().contains( answerVariant ) ) {
                                variant.getCorrectAnswers().add( answerVariant );
                            }
                            /*else {
                                //error ?
                            }*/
                        }
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
        Map<Long, String> codes = new HashMap<>();
        char code = 'A';
        for ( CorrespondenceVariant variant : ((QuestionCorrespondence)question).getCorrespondenceVariants() ) {
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
