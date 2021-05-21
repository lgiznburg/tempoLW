package ru.rsmu.tempoLW.consumabales.appeal;

import ru.rsmu.tempoLW.data.QuestionType;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.entities.auth.User;
import ru.rsmu.tempoLW.utils.CorrectnessUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
public class QuestionResultFacade {
    private final QuestionResult result;

    public QuestionResultFacade( QuestionResult result ) {
        this.result = result;
    }

    public boolean isSimple() {
        return result.getQuestion().getType().equals( QuestionType.SIMPLE );
    }
    public boolean isSimpleOrder() {
        return result.getQuestion().getType().equals( QuestionType.SIMPLE_ORDER );
    }
    public boolean isCorrespondence() {
        return result.getQuestion().getType().equals( QuestionType.CORRESPONDENCE );
    }
    public boolean isOpen() {
        return result.getQuestion().getType().equals( QuestionType.OPEN );
    }
    public boolean isBigOpen() {
        return result.getQuestion().getType().equals( QuestionType.BIG_OPEN );
    }
    public boolean isCrossword() {
        return result.getQuestion().getType().equals( QuestionType.CROSSWORD );
    }
    public boolean isTree() {
        return result.getQuestion().getType().equals( QuestionType.TREE );
    }
    public boolean isTreeOpen() {
        return result.getQuestion().getType().equals( QuestionType.TREE_OPEN );
    }

    public QuestionResult getResult() {
        return result;
    }

    public String getQuestionBody() {
        return prepareText( result.getQuestion().getText() );
    }

    public String prepareText( String text ) {
        if ( text == null ) return "";
        return text.replace( "\\(", "$" )
                .replace( "\\)", "$" )
                .replaceAll( "\\\\\\[\\\\\\]", "\n" )
                .replace( "\\[", "$$" )
                .replace( "\\]", "$$" )
                .replaceAll( "%%.*", "" )
                //.replaceAll( "\\\\\\((.*[А-Яа-я]+.*?)\\\\\\)", "$1" )
                .replace( "%", "\\%" );
    }

    public List<ResultElement> getVariantAnswers( CorrespondenceVariant variant ) {
        if ( isCrossword() || isTreeOpen() ) {
            return result.getElements().stream()
                    .filter( el -> ((ResultTreeOpen)el).getCorrespondenceVariant().getId() == variant.getId() )
                    .collect( Collectors.toList());
        }
        if ( isCorrespondence() ) {
            return result.getElements().stream()
                    .filter( el -> ((ResultCorrespondence)el).getCorrespondenceVariant().getId() == variant.getId() )
                    .collect( Collectors.toList());
        }
        if ( isTree() ) {
            return result.getElements().stream()
                    .filter( el -> ((ResultTree)el).getCorrespondenceVariant().getId() == variant.getId() )
                    .collect( Collectors.toList());
        }

        return Collections.emptyList();
    }

    public boolean isOpenAnswerCorrect( ResultTreeOpen answer ) {
        return CorrectnessUtils.countErrors( answer.getValue(), answer.getCorrespondenceVariant().getCorrectAnswers() ) == 0;
    }

    public String getBigAnswerBody( String body ) {
        if ( body != null ) {
            return body.replace( "\n", "\n\n" )
                    .replaceAll( "([_%^&$~#{}\\\\])", "\\\\$1" );
        }
        return "";
    }

    public String getExaminerFio( User examiner ) {
        return examiner.getLastName() + " "
                + examiner.getFirstName().charAt( 0 ) + "."
                + examiner.getMiddleName().charAt( 0 ) + ".";
    }

    public List<AnswerVariant> getMissedAnswers() {
        if ( isSimple() ) {
            List<AnswerVariant> correctAnswers = result.getElements().stream()
                    .filter( ResultElement::isCorrect )
                    .map( re -> ((ResultSimple)re).getAnswerVariant() )
                    .collect( Collectors.toList());

            List<AnswerVariant> missed =
                    ((QuestionSimple)result.getQuestion()).getAnswerVariants().stream()
                    .filter( av -> av.isCorrect() && !correctAnswers.contains( av ) )
                    .collect( Collectors.toList());
            return missed;
        }
        return Collections.emptyList();
    }

    public List<AnswerVariant> getMissedAnswers( CorrespondenceVariant variant ) {
        List<ResultElement> elements = getVariantAnswers( variant );
        if ( isCorrespondence() ) {
            List<AnswerVariant> correctAnswers = elements.stream()
                    .filter( ResultElement::isCorrect )
                    .map( re -> ((ResultCorrespondence) re).getAnswerVariant() )
                    .collect( Collectors.toList() );
            List<AnswerVariant> missed = variant.getCorrectAnswers().stream()
                    .filter( av -> !correctAnswers.contains( av ) )
                    .collect( Collectors.toList() );
            return missed;
        }
        if ( isTree() ) {
            List<AnswerVariant> correctAnswers = elements.stream()
                    .filter( ResultElement::isCorrect )
                    .map( re -> ((ResultTree) re).getAnswerVariant() )
                    .collect( Collectors.toList() );
            List<AnswerVariant> missed = variant.getCorrectAnswers().stream()
                    .filter( av -> av.isCorrect() && !correctAnswers.contains( av ) )
                    .collect( Collectors.toList() );
            return missed;
        }
        return Collections.emptyList();
    }

    public int getNumberOfOrderElements() {
        if ( isSimpleOrder() ) {
            List<AnswerVariant> correctAnswers = ((QuestionSimpleOrder)result.getQuestion()).getAnswerVariants().stream()
                    .filter( AnswerVariant::isCorrect )
                    .collect( Collectors.toList());
            int maxEnteredOrder = result.getElements().stream()
                    .mapToInt( el -> ((ResultSimpleOrder)el).getEnteredOrder() )
                    .max().orElse( 0 );
            return Math.max( maxEnteredOrder, correctAnswers.size() );
        }
        return 0;
    }

    public AnswerVariant findCorrectAnswer( int index ) {
        // this index starts from 1
        return ((QuestionSimpleOrder)result.getQuestion()).getAnswerVariants().stream()
                .filter( av -> av.isCorrect() && av.getSequenceOrder() == index  )
                .findFirst().orElse( null );
    }

    public List<AnswerVariant> findAnswersForOrderIndex( int index ) {
        return result.getElements().stream()
                .filter( el -> ((ResultSimpleOrder)el).getEnteredOrder() == index )
                .map( el -> ((ResultSimpleOrder)el).getAnswerVariant() )
                .collect( Collectors.toList());
    }

    public boolean isOrderCorrectAnswerPresent( int index ) {
        return result.getElements().stream()
                .filter( el -> ((ResultSimpleOrder)el).getEnteredOrder() == index && el.isCorrect())
                .findAny().orElse( null ) != null;

    }
}
