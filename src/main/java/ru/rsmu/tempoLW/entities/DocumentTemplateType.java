package ru.rsmu.tempoLW.entities;

/**
 * @author leonid.
 */
public enum DocumentTemplateType {
    LOGINS("Список логинов и паролей"),
    EXAM_RESULT("Результаты абитуриента"),
    EXAM_STATEMENT("Экзаменационная ведомость");

    private String description;

    DocumentTemplateType( String description ) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
