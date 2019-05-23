package ru.rsmu.tempoLW.consumabales;

public class MonthNameConverter {
    public static String getMonthName (String source) {
        String output = source;
        output = output.replaceAll("01", "января");
        output = output.replaceAll("02", "февраля");
        output = output.replaceAll("03", "марта");
        output = output.replaceAll("04", "апреля");
        output = output.replaceAll("05", "мая");
        output = output.replaceAll("06", "июня");
        output = output.replaceAll("07", "июля");
        output = output.replaceAll("08", "августа");
        output = output.replaceAll("09", "сентября");
        output = output.replaceAll("10", "октября");
        output = output.replaceAll("11", "ноября");
        output = output.replaceAll("12", "декабря");

        return output;
    }
}
