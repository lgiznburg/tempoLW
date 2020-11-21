package ru.rsmu.tempoLW.entities.system;

/**
 * @author leonid.
 */
public enum StoredPropertyName {
    SENDMAIL_HOST("Sendmail","Адрес сервера отправки почты", "127.0.0.1", StoredPropertyType.STRING),
    SENDMAIL_LOGIN("Sendmail","Логин", "login", StoredPropertyType.STRING),
    SENDMAIL_PASSWORD("Sendmail","Пароль", "password", StoredPropertyType.STRING),
    SENDMAIL_PORT("Sendmail","Номер порта (или 0)", "0", StoredPropertyType.INTEGER),
    SENDMAIL_SSL_PORT("Sendmail","Номер SSL порта", "", StoredPropertyType.STRING),
    SENDMAIL_USE_SSL("Sendmail","Использовать SSL", "0", StoredPropertyType.INTEGER),
    SENDMAIL_USE_TLS("Sendmail","Использовать TLS", "0", StoredPropertyType.INTEGER),

    EMAIL_FROM_ADDRESS("Email","Обратный адрес для email", "prk@rsmu.ru", StoredPropertyType.STRING),
    EMAIL_FROM_SIGNATURE("Email","Название обратного адреса для email", "Приемная комиссия РНИМУ им.Пирогова", StoredPropertyType.STRING),

    PROCTORING_JS_URL("Proctoring", "Адрес JS библиотеки прокторинга", "https://demo.proctoring.online/sdk/supervisor", StoredPropertyType.STRING),
    PROCTORING_SERVER_ADDRESS("Proctoring", "Адрес сервера прокторинга", "https://demo.proctoring.online", StoredPropertyType.STRING),
    PROCTORING_SECRET_KEY("Proctoring", "Секретный ключ доступа", "secret", StoredPropertyType.STRING),
    PROCTORING_API_KEY("Proctoring", "API ключ для передачи результатов", "secret", StoredPropertyType.STRING),
    PROCTORING_CALLBACK_ALLOWED("Proctoring","Разрешить передачу результатов сессии", "0", StoredPropertyType.INTEGER),
    PROCTORING_DEBUG_ENVIRONMENT("Proctoring","Режим тестирования (только для разработки)", "0", StoredPropertyType.INTEGER),

    VIEW_ONLY_THIS_YEAR_EXAM("Global", "Показывать экзамены только текущего года", "1", StoredPropertyType.INTEGER ),
    MY_OWN_URI("Global", "Адрес данного сервера", "https://tempolw.rsmu.ru", StoredPropertyType.STRING )
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
