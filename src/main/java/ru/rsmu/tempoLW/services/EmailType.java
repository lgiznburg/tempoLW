package ru.rsmu.tempoLW.services;

/**
 * @author leonid.
 */
public enum EmailType {
    EXAM_PASSWORD_RU("/emails/ExamPasswordRu.vm", "${eventType} в РНИМУ им.Н.И.Пирогова"),
    EXAM_PASSWORD_EN("/emails/ExamPasswordEn.vm", "Pirogov's University ${eventType}"),
    PROCTORING_CHECK_RU("/emails/ExamPreparationPasswordRu.vm", "${eventType} в РНИМУ им.Н.И.Пирогова");

    public static final String EXAM_PASSWORD_SHORT_NAME = "EXAM_PASSWORD_";

    private final String fileName;
    private final String subject;

    EmailType( String fileName, String subject ) {
        this.fileName = fileName;
        this.subject = subject;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSubject() {
        return subject;
    }

    public static EmailType findForLocale( String shortName, String localeName ) {
        return EmailType.valueOf( shortName + localeName.toUpperCase() );
    }
}
