package ru.rsmu.tempoLW.utils.builder;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.data.QuestionType;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.utils.ExcelLayout;
import ru.rsmu.tempoLW.utils.ExcelReader;
import ru.rsmu.tempoLW.utils.ImagesExtractor;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author leonid.
 */
public abstract class QuestionBuilder extends ExcelReader implements ExcelLayout {

    protected Question result;

    protected ImagesExtractor imagesExtractor;

    protected QuestionDao questionDao;

    public static QuestionBuilder create( QuestionType questionType ) throws IllegalArgumentException {
        QuestionBuilder builder;
        switch ( questionType ) {
            case SIMPLE:
                builder = new SimpleQuestionBuilder();
                break;
            case OPEN:
                builder = new OpenQuestionBuilder();
                break;
            case CORRESPONDENCE:
                builder = new CorrespondenceQuestionBuilder();
                break;
            case SIMPLE_ORDER:
                builder = new SimpleOrderQuestionBuilder();
                break;
            case TREE:
                builder = new TreeQuestionBuilder();
                break;
            case FREE:
                builder = new FreeQuestionBuilder();
                break;
            case TREE_OPEN:
                builder = new TreeOpenQuestionBuilder();
                break;
            case CROSSWORD:
                builder = new CrosswordQuestionBuilder();
                break;
            case BIG_OPEN:
                builder = new BigOpenQuestionBuilder();
                break;
            default:
                throw new IllegalArgumentException( "Incorrect type of question" );
        }
        return builder;
    }

    /**
     * Read a question from the file
     * @param row row with current question. it could be row with question or answer or correspondence variant
     * @return number of row after reading (next question)
     */
    public abstract int parse( Row row );

    /**
     * Write the question into excel file
     * @param sheet Sheet in excel file
     * @param rowN number of row for current question
     * @param question question to write
     * @return number of row for next question
     */
    public abstract int write( Sheet sheet, int rowN, Question question );

    public Question getResult() {
        return result;
    }

    public ImagesExtractor getImagesExtractor() {
        return imagesExtractor;
    }

    public void setImagesExtractor( ImagesExtractor imagesExtractor ) {
        this.imagesExtractor = imagesExtractor;
    }

    public QuestionDao getQuestionDao() {
        return questionDao;
    }

    public void setQuestionDao( QuestionDao questionDao ) {
        this.questionDao = questionDao;
    }

    UploadedImage checkUploadedImage( Row row ) {
        if ( imagesExtractor != null ) {
            String imageName = getCellValue( row, COLUMN_IMAGE );
            if ( imageName != null && !imageName.isEmpty() ) {
                byte[] picture = imagesExtractor.getPicture( imageName );
                if ( picture != null ) {
                    UploadedImage uploadedImage = new UploadedImage();
                    uploadedImage.setSourceName( imageName );
                    uploadedImage.setPicture( picture );
                    uploadedImage.setContentType( imagesExtractor.getContentType( imageName ) );
                    return uploadedImage;
                }
            }
        }
        return null;
    }

    void writeQuestionInfo( Row row, Question question ) {
        Cell cell;

        cell = row.createCell( COLUMN_QUESTION_TYPE );
        cell.setCellValue( question.getType().getExportCode() );

        cell = row.createCell( COLUMN_TOPIC );
        cell.setCellValue( question.getQuestionInfo().getTopic().getTitle() );

        cell = row.createCell( COLUMN_COMPLEXITY );
        cell.setCellValue( question.getQuestionInfo().getComplexity() );

        cell = row.createCell( COLUMN_MAX_SCORE );
        cell.setCellValue( question.getQuestionInfo().getMaxScore() );

        if ( !question.getQuestionInfo().getName().equals( question.getQuestionInfo().getTopic().getTitle() ) ) {
            cell = row.createCell( COLUMN_NAME );
            cell.setCellValue( question.getQuestionInfo().getName() );
        }
        if ( question.getQuestionInfo().getCode() > 0 ) {
            cell = row.createCell( COLUMN_CODE );
            cell.setCellValue( question.getQuestionInfo().getCode() );
        }

        cell = row.createCell( COLUMN_TEXT );
        cell.setCellValue( question.getText() );

        if ( question.getImage() != null ) {
            cell = row.createCell( COLUMN_IMAGE );
            cell.setCellValue( question.getImage().getSourceName() );
        }

        cell = row.createCell( COLUMN_ID );
        cell.setCellValue( question.getId() );

    }

    int writeAnswers( Sheet sheet, int rowN, List<AnswerVariant> answers ) {
        return writeAnswers( sheet, rowN, answers, null );
    }

    int writeAnswers( Sheet sheet, int rowN, List<AnswerVariant> answers, Map<Long, String> codes ) {
        Row row;
        Cell cell;

        for ( AnswerVariant answerVariant : answers ) {
            row = sheet.createRow( rowN++ );

            cell = row.createCell( COLUMN_TEXT );
            cell.setCellType( CellType.STRING );
            cell.setCellValue( answerVariant.getText() );

            if ( codes != null ) {
                String rightness = codes.get( answerVariant.getId() );
                if ( rightness != null ) {
                    cell = row.createCell( COLUMN_RIGHTNESS );
                    cell.setCellValue( rightness );
                }
            }
            else {
                if ( answerVariant.isCorrect() ) {
                    cell = row.createCell( COLUMN_RIGHTNESS );
                    cell.setCellValue( 1 );
                }
            }

            cell = row.createCell( COLUMN_ID );
            cell.setCellValue( answerVariant.getId() );

        }
        return rowN;

    }

    protected <T extends Question> T loadQuestion( Row row, Class<T> questionClass ) throws IllegalAccessException, InstantiationException {
        T question = null;
        Long questionId = getCellNumber( row, COLUMN_ID );
        if ( questionId != null && questionId > 0 ) {
            question = getQuestionDao().find( questionClass, questionId );
        }
        if ( question == null ) {
            question = questionClass.newInstance();
            question.setCreatedDate( new Date() );
        }

        question.setText( getCellValue( row, COLUMN_TEXT ) );

        UploadedImage uploadedImage = checkUploadedImage( row );
        if ( uploadedImage != null ) {
            question.setImage( uploadedImage );
        }
        this.result = question;
        return question;
    }

    protected AnswerVariant loadAnswer( Row row ) {
        Long answerId = getCellNumber( row, COLUMN_ID );
        AnswerVariant answerVariant = null;
        if ( answerId != null && answerId > 0 ) {
            answerVariant = getQuestionDao().find( AnswerVariant.class, answerId );
        }
        if ( answerVariant == null ) {
            answerVariant = new AnswerVariant();
        }
        answerVariant.setText( getCellValue( row, COLUMN_TEXT ) );
        answerVariant.setCorrect( getCellNumber( row, COLUMN_RIGHTNESS ) != null );
        answerVariant.setQuestion( result );
        UploadedImage uploadedImage = checkUploadedImage( row );
        if ( uploadedImage != null ) {
            answerVariant.setImage( uploadedImage );
        }
        return answerVariant;
    }

    protected CorrespondenceVariant loadCorrespondenceVariant( Row row ) {
        Long variantId = getCellNumber( row, COLUMN_ID );

        CorrespondenceVariant variant = null;
        if ( variantId != null && variantId > 0 ) {
            variant = questionDao.find( CorrespondenceVariant.class, variantId );
        }
        if ( variant == null ) {
            variant = new CorrespondenceVariant();
            variant.setCorrectAnswers( new LinkedList<>() );
        }
        variant.setText( getCellValue( row, COLUMN_TEXT ) );
        variant.setQuestion( result );
        UploadedImage uploadedImage = checkUploadedImage( row );
        if ( uploadedImage != null ) {
            variant.setImage( uploadedImage );
        }
        return variant;
    }
}
