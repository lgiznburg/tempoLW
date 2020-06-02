package ru.rsmu.tempoLW.entities.system;

/**
 * @author leonid.
 */
public enum StoredPropertyName {
    EMAIL_FROM_ADDRESS("Email","Обратный адрес для email", "prk@rsmu.ru", StoredPropertyType.STRING),
    EMAIL_FROM_SIGNATURE("Email","Название обратного адреса для email", "Приемная комиссия РНИМУ им.Пирогова", StoredPropertyType.STRING),
    PROCTORING_JS_URL("Proctoring", "Адрес JS библиотеки прокторинга", "https://demo.proctoring.online/sdk/supervisor", StoredPropertyType.STRING),
    PROCTORING_SERVER_ADDRESS("Proctoring", "Адрес сервера прокторинга", "https://demo.proctoring.online", StoredPropertyType.STRING),
    PROCTORING_SECRET_KEY("Proctoring", "Секретный ключ доступа", "secret", StoredPropertyType.STRING),
    PROCTORING_PROVIDER("Proctoring", "Способ передачи ключа сессии (JWT или plain)", "JWT", StoredPropertyType.STRING)
            ;

    private String groupName;
    private String title;
    private String defaultValue;
    private StoredPropertyType type;
    private boolean editable = true;

    StoredPropertyName( String groupName, String title, String defaultValue, StoredPropertyType type ) {
        this.groupName = groupName;
        this.title = title;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    StoredPropertyName( String groupName, String title, String defaultValue, StoredPropertyType type, boolean editable ) {
        this.groupName = groupName;
        this.title = title;
        this.defaultValue = defaultValue;
        this.type = type;
        this.editable = editable;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getTitle() {
        return title;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public StoredPropertyType getType() {
        return type;
    }

    public boolean isEditable() {
        return editable;
    }
}
