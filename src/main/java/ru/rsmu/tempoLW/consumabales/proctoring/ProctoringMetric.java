package ru.rsmu.tempoLW.consumabales.proctoring;

/**
 * @author leonid.
 */
public enum ProctoringMetric {
    b1("Браузер не поддерживается"),
    b2("Переключен фокус на стороннее приложение"),
    b3("Окно браузера не развернуто на весь экран"),
    c1("Камера не работает"),
    c2("Плохо видно лицо перед камерой"),
    c3("Замечен посторонний человек"),
    c4("Лицо перед камерой не соответствует профилю"),
    c5("Обнаружено сходство лица с другим профилем"),
    m1("Микрофон не работает или приглушен"),
    m2("Слышен разговор или шум на фоне"),
    n1("Проблема с сетевым подключением"),
    s1("Нет видео с экрана"),
    s2("Используется дополнительный монитор"),
    unknown("Неизвестная метрика");

    private String description;

    ProctoringMetric( String description ) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

