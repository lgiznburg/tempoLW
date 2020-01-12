package ru.rsmu.tempoLW.entities;

/**
 * @author leonid.
 */
public enum DocumentTemplateType {

    LOGINS("Список логинов и паролей", "Logins and passwords list" ),
    EXAM_RESULT("Результаты абитуриента", "Entrant\'s exam results"),
    EXAM_STATEMENT("Экзаменационная ведомость", "Exam statement");

    private String descriptionRu; //description in Russian locale
    private String descriptionEn; //description in English locale

    DocumentTemplateType( String descriptionRu, String descriptionEn ) {
        this.descriptionRu = descriptionRu;
        this.descriptionEn = descriptionEn;
    }

    //choose which language of description to return ("ru"/"en", case insensitive)
    public String getDescription( String lang ) {
        if ( lang.toLowerCase().equals( "ru" ) ) {
            return descriptionRu;
        } else {
            return descriptionEn;
        }
    }

}
