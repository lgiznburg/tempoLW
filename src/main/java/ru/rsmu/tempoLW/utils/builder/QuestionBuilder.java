package ru.rsmu.tempoLW.utils.builder;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.rsmu.tempoLW.entities.Question;
import ru.rsmu.tempoLW.entities.UploadedImage;
import ru.rsmu.tempoLW.utils.ExcelLayout;
import ru.rsmu.tempoLW.utils.ExcelReader;
import ru.rsmu.tempoLW.utils.ImagesExtractor;

/**
 * @author leonid.
 */
public abstract class QuestionBuilder extends ExcelReader implements ExcelLayout {

    protected Question result;

    protected ImagesExtractor imagesExtractor;

    public static QuestionBuilder create( String questionType ) throws IllegalArgumentException {
        if ( questionType.equalsIgnoreCase( SIMPLE_TYPE ) ) {
            return new SimpleQuestionBuilder();
        }
        else if ( questionType.equalsIgnoreCase( OPEN_TYPE )) {
            return new OpenQuestionBuilder();
        }
        else if ( questionType.equalsIgnoreCase( CORRESPONDENCE_TYPE )) {
            return  new CorrespondenceQuestionBuilder();
        }
        else if ( questionType.equalsIgnoreCase( SIMPLE_ORDER_TYPE )) {
            return  new SimpleOrderQuestionBuilder();
        }
        else if ( questionType.equalsIgnoreCase( TREE_TYPE )) {
            return  new TreeQuestionBuilder();
        }
        throw new IllegalArgumentException( "Incorrect type of question" );
    }

    public abstract int parse( Sheet sheet, int rowN );

    public Question getResult() {
        return result;
    }

    public ImagesExtractor getImagesExtractor() {
        return imagesExtractor;
    }

    public void setImagesExtractor( ImagesExtractor imagesExtractor ) {
        this.imagesExtractor = imagesExtractor;
    }

    public UploadedImage checkUploadedImage( Row row ) {
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
}
