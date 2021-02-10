package ru.rsmu.tempoLW.utils;

import ru.rsmu.tempoLW.entities.AnswerVariant;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author leonid.
 */
public class CorrectnessUtils {

    public static int countErrors( String answer, List<AnswerVariant> variants ) {
        String result = answer.trim().toLowerCase();
        result = result.replaceAll( "(\\d)[.](\\d)", "$1,$2" );
        int correctCount = 0;
        for ( AnswerVariant variant : variants ) {
            String pattern = variant.getRegex();
            if ( pattern != null ) {
                // use variant text as REGEX pattern
                Pattern p = Pattern.compile( pattern );
                Matcher m = p.matcher( result );
                if ( m.find() ) {
                    correctCount++;
                }
            }
            else {
                // do simple comparison
                String[] parts = variant.getText().split( "\\|" );
                for ( String part : parts ) {
                    String match = part.trim().toLowerCase().replaceAll( "(\\d)[.](\\d)", "$1,$2" );
                    if ( match.matches( "-?\\d+(,\\d+)?" ) ) {
                        //this is numeric (mathematical) comparison. requires full match
                        String mathRegex = "\\D*" + (match.startsWith( "-" ) ? "":"(?<![-])") + match + "\\D*";
                        if ( variants.size() > 1 ) {
                            // more than 1 variant. we should use 'find()' to match all variants
                            Pattern mathPattern = Pattern.compile( mathRegex );
                            Matcher mathMatcher = mathPattern.matcher( result );
                            if ( mathMatcher.find() ) {
                                correctCount++;
                                break;
                            }

                        }
                        else {
                            // only one variant. we could use strict comparison
                            if ( result.matches( mathRegex ) ) {
                                correctCount++;
                                break;
                            }
                        }
                    }
                    else if ( match.length() > 0 && result.contains( match ) ) { // contains is enough
                        correctCount++;
                        break;
                    }
                }
            }

        }
        return Math.abs( correctCount - variants.size() ) ;
    }
}
