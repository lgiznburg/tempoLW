package ru.rsmu.tempoLW.components;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author leonid.
 */
@Import(stylesheet = "cross/crossword.css")
public class QuestionCrosswordForm {
    @Property
    private QuestionResult questionResult;

    @Property
    private List<CrosswordElement> crosswordElements;

    @Property
    private CrosswordElement crosswordElement;

    @Property
    private CorrespondenceVariant currentVariant;

    @Property
    private List<List<TableCell>> tableRows;

    @Property
    private List<TableCell> currentRow;

    @Property
    private TableCell tableCell;

    @Property
    private List<ResultTreeOpen> resultTreeOpens;

    @Property
    private ResultTreeOpen resultTreeOpen;

    /**
     * Current exam - stored in the session, used to extract current question from
     */
    @SessionState
    private ExamResult examResult;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private Request request;

    @Environmental
    private JavaScriptSupport javaScriptSupport;

    public void setupRender() {
        javaScriptSupport.require( "jquery.crossword" ).invoke( "startCrossword" );
        prepare();
    }

    public void onPrepareForSubmit() {
        if ( isSessionLost() ) {
            questionResult = new QuestionResult();
            return;
        }
        prepare();
    }

    private void prepare() {
        questionResult = examResult.getCurrentQuestion();
        crosswordElements = new ArrayList<>();
        resultTreeOpens = new ArrayList<>();

        QuestionCrossword question = (QuestionCrossword) questionResult.getQuestion();
        // Lazy init
        questionDao.refresh( question );

        if ( questionResult.getElements() == null ) {
            questionResult.setElements( new ArrayList<>() );
        }

        Pattern crosswordPattern = Pattern.compile( "(.+)%%([xyXY]),\\((\\d+),(\\d+)\\)" );
        question.getCorrespondenceVariants().forEach( variant -> {
            // 1. Populate results if needed
            ResultTreeOpen resultOpen = (ResultTreeOpen) questionResult.getElements().stream().filter( e ->
                    ((ResultTreeOpen)e).getCorrespondenceVariant().getId() == variant.getId()
            ).findFirst().orElse( null );

            if ( resultOpen == null ) {
                resultOpen = new ResultTreeOpen();
                resultOpen.setCorrespondenceVariant( variant );
                resultOpen.setQuestionResult( questionResult );
            }
            resultTreeOpens.add( resultOpen );

            //2. Create elements
            Matcher matcher = crosswordPattern.matcher( variant.getText() );
            if ( matcher.matches() ) {
                String text = matcher.group(1);
                String orientation = matcher.group(2).equalsIgnoreCase( "x" ) ? "across" : "down";
                int x = Integer.parseInt( matcher.group(3) );
                int y = Integer.parseInt( matcher.group(4) );

                String answer = "";
                if ( variant.getCorrectAnswers().get( 0 ).getRegex() == null ) {
                    answer = variant.getCorrectAnswers().get( 0 ).getText();
                    if ( answer.contains( "|" ) ) {
                        answer = answer.split( "\\|" )[0].trim(); // if there is OR condition, use only one part
                    }
                }
                else {
                    answer = variant.getCorrectAnswers().get( 0 ).getReadableText();
                }

                CrosswordElement elem = new CrosswordElement(
                        text,
                        answer.replaceAll( "\\.", "*" ),
                        orientation,
                        resultOpen.getValue() == null ? "" : resultOpen.getValue(),
                        variant.getId(),
                        x, y
                );
                crosswordElements.add( elem );
            }
        } );

        // set numbers according to position in the puzzle
        Collections.sort( crosswordElements );
        AtomicInteger pos = new AtomicInteger( 1 );
        AtomicInteger prevX = new AtomicInteger(0);
        AtomicInteger prevY = new AtomicInteger(0);
        crosswordElements.forEach( e -> {
            if ( prevX.get() != e.startX || prevY.get() != e.startY ) {
                e.setPosition( pos.getAndIncrement() );
            }
            else {
                e.setPosition( pos.get() -1 );
            }
            prevX.set( e.startX );
            prevY.set( e.startY );
        } );

        // define size of crossword table
        int maxX = crosswordElements.stream().mapToInt( ce -> ce.startX + (ce.orientation.equals( "across" ) ? (ce.answer.length() -1) : 0) )
                .max().orElse(0);
        int maxY = crosswordElements.stream().mapToInt( ce -> ce.startY + (ce.orientation.equals( "down" ) ? (ce.answer.length() -1) : 0) )
                .max().orElse( 0 );

        // create and populate crossword table
        tableRows = new ArrayList<>();

        for ( int i = 0; i < maxY; i++ ) {
            List<TableCell> row = new ArrayList<>();
            for ( int j = 0; j < maxX; j++ ) {
                row.add( new TableCell( j, i ) );
            }
            tableRows.add( row );
        }

        // populate crossword table with correct classes and inputs
        for ( int i = 0; i < crosswordElements.size(); i++ ) {
            CrosswordElement element = crosswordElements.get( i );
            int x = element.startX - 1;
            int y = element.startY - 1;

            for ( int k = 0; k < element.answer.length(); k++ ) {
                TableCell cell = element.orientation.equals( "across" ) ? findTableCell( x +k, y ) : findTableCell( x, y+k );
                if ( k == 0 && StringUtils.isBlank( cell.positionMarker ) ) {
                    cell.setPositionMarker( String.format( "<span>%d</span>", element.position ) );
                }
                String givenLetter = element.givenAnswer.length() > k ? String.valueOf( element.givenAnswer.charAt( k ) ) : "";
                if ( StringUtils.isBlank( cell.input ) || givenLetter.length() > 0 ) {
                    cell.setInput( String.format( "<input maxlength=\"1\" value=\"%s\" type=\"text\" tabindex=\"-1\"/>", givenLetter ) );
                }
                cell.setCellClass( "puzzle-cell" );
                cell.setCellClass( String.format( "entry-%d", element.position-1 ) );
                cell.setCellClass( String.format( "position-%d", i ) );
            }
        }

    }

    public void onSuccess() {
        if ( isSessionLost() ) return;
        // find correct current question. NB: should it be checked for type?
        questionResult = examResult.getCurrentQuestion();

        // copy results from
        resultTreeOpens.forEach( res -> {
            if ( StringUtils.isNotBlank( res.getValue() ) && !questionResult.getElements().contains( res ) ) {
                questionResult.getElements().add( res );
            }
        } );
    }

    /**
     * If session expire examResult becomes empty. So we need to show friendly message instead of NPE exception
     * @return true if everything is OK, false if examResult is empty
     */
    private boolean isSessionLost() {
        return request.isXHR() && (examResult == null || examResult.getQuestionResults() == null);
    }

    public QuestionCrossword getQuestion() {
        return (QuestionCrossword) questionResult.getQuestion();
    }

    public TableCell findTableCell( int x, int y ) {
        List<TableCell> row = tableRows.get( y );
        return row != null ? row.get( x ) : null;
    }

    public boolean getElementOrientation( String given ) {
        return crosswordElement.orientation.equals( given );
    }

    public int getElementIndex() {
        return crosswordElements.indexOf( crosswordElement );
    }

    public int getElementIndexForVariant() {
         CrosswordElement lookedFor = crosswordElements.stream().filter( el -> el.inputId == resultTreeOpen.getCorrespondenceVariant().getId() )
                .findFirst().orElse( null );
        return lookedFor != null ? crosswordElements.indexOf( lookedFor ) : -1;
    }

    public static class CrosswordElement implements Comparable<CrosswordElement>{
        private final String clue;
        private final String answer;
        private final String orientation;
        private final String givenAnswer;
        private int position;
        private final long inputId;
        private final int startX;
        private final int startY;

        public CrosswordElement( String clue, String answer, String orientation, String givenAnswer, long inputId, int startX, int startY ) {
            this.clue = clue;
            this.answer = answer;
            this.orientation = orientation;
            this.givenAnswer = givenAnswer;
            this.inputId = inputId;
            this.startX = startX;
            this.startY = startY;
        }

        public String getClue() {
            return clue;
        }

        public String getAnswer() {
            return answer;
        }

        public String getOrientation() {
            return orientation;
        }

        public long getInputId() {
            return inputId;
        }

        public int getStartX() {
            return startX;
        }

        public int getStartY() {
            return startY;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition( int position ) {
            this.position = position;
        }

        public String getGivenAnswer() {
            return givenAnswer;
        }

        @Override
        public int compareTo( CrosswordElement o ) {
            return startY - o.startY == 0 ? startX - o.startX : startY - o.startY;
        }
    }

    public static class TableCell {
        private Set<String> cellClass = new LinkedHashSet<>();
        private String input = "";
        private String positionMarker="";
        private String coords="";

        public TableCell( int x, int y ) {
            coords = String.format( "%d,%d", x+1, y+1 );
        }

        public String getCellClass() {
            return StringUtils.join( cellClass, " " );
        }

        public void setCellClass( String cellClass ) {
            this.cellClass.add( cellClass );
        }

        public String getInput() {
            return input;
        }

        public void setInput( String input ) {
            this.input = input;
        }

        public String getPositionMarker() {
            return positionMarker;
        }

        public void setPositionMarker( String positionMarker ) {
            this.positionMarker = positionMarker;
        }

        public String getCoords() {
            return coords;
        }

        public void setCoords( String coords ) {
            this.coords = coords;
        }
    }
}
