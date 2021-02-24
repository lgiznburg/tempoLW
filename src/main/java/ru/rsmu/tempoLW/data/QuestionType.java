package ru.rsmu.tempoLW.data;

/**
 * @author leonid.
 */
public enum QuestionType {
    SIMPLE( "Пр", "(пр.*)|(simple)"),
    OPEN( "Откр", "(откр.*)|(open)"),
    CORRESPONDENCE( "Соот", "(соот.*)|(corr.*)"),
    SIMPLE_ORDER( "Посл", "(посл.*)|(.*поряд.*)|(order)"),
    //COMPLEX,
    //SEQUENCE,
    TREE( "Дер", "(дер.*)|(tree)"),
    //PAZL,
    //MAP
    FREE( "Своб", "(своб.*)|(file)|(скан.*)"),            // olympiad, result should be entered in form of scanned document
    TREE_OPEN( "ДСО", "(дсо)"),
    CROSSWORD( "КВ", "кв"),
    BIG_OPEN( "OПРО", "опро"),
    UNDEFINED("-", "-");

    private final String exportCode;
    private final String regex;

    QuestionType( String exportCode, String regex ) {
        this.exportCode = exportCode;
        this.regex = regex;
    }

    public String getExportCode() {
        return exportCode;
    }

    public static QuestionType findType( String name ) {
        for ( QuestionType type : values() ) {
            if ( name.trim().toLowerCase().matches( type.regex ) ) {
                return type;
            }
        }
        return UNDEFINED;
    }
}
