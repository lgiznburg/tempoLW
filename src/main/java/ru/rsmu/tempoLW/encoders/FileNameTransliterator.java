package ru.rsmu.tempoLW.encoders;

public class FileNameTransliterator {
    public String transliterateRuEn (String source) {
        String output = source;
        output = output.replaceAll("а|А", "a");
        output = output.replaceAll("б|Б", "b");
        output = output.replaceAll("в|В", "v");
        output = output.replaceAll("г|Г", "g");
        output = output.replaceAll("д|Д", "d");
        output = output.replaceAll("е|Е", "e");
        output = output.replaceAll("ё|Ё", "yo");
        output = output.replaceAll("ж|Ж", "zh");
        output = output.replaceAll("з|З", "z");
        output = output.replaceAll("и|И", "i");
        output = output.replaceAll("й|Й", "j");
        output = output.replaceAll("к|К", "k");
        output = output.replaceAll("л|Л", "l");
        output = output.replaceAll("м|М", "m");
        output = output.replaceAll("н|Н", "n");
        output = output.replaceAll("о|О", "o");
        output = output.replaceAll("п|П", "p");
        output = output.replaceAll("р|Р", "r");
        output = output.replaceAll("с|С", "s");
        output = output.replaceAll("т|Т", "t");
        output = output.replaceAll("у|У", "u");
        output = output.replaceAll("ф|Ф", "f");
        output = output.replaceAll("х|Х", "h");
        output = output.replaceAll("ц|Ц", "c");
        output = output.replaceAll("ч|Ч", "ch");
        output = output.replaceAll("ш|Ш", "sh");
        output = output.replaceAll("щ|Щ", "sch");
        output = output.replaceAll("ъ|Ъ", "j");
        output = output.replaceAll("ы|Ы", "y");
        output = output.replaceAll("ь|Ь", "j");
        output = output.replaceAll("э|Э", "a");
        output = output.replaceAll("ю|Ю", "yu");
        output = output.replaceAll("я|Я", "ya");

        return output;
    }
}
