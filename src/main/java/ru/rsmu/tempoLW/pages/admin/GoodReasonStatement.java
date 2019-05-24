package ru.rsmu.tempoLW.pages.admin;

import com.tutego.jrtf.*;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.consumabales.AttachmentRtf;
import ru.rsmu.tempoLW.consumabales.FileNameTransliterator;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.Testee;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class GoodReasonStatement {
    @PageActivationContext(index = 0)
    private ExamSchedule exam;

    @PageActivationContext(index = 1)
    private Testee testee;

    @Inject
    private Locale currentLocale;

    public StreamResponse onActivate() {
        String fileName = "unknown.rtf";
        // prepare all paragraphs before creating RTF section
        List<RtfPara> docContent = new LinkedList<>();
        // document header
        docContent.add(
                RtfPara.p(RtfText.text("Ответственному секретарю Приёмной комиссии")).alignRight()
        );
        docContent.add(
                RtfPara.p(RtfText.text("ФГБОУ ВО РНИМУ им. Н.И. Пирогова")).alignRight()
        );
        docContent.add(
                RtfPara.p(RtfText.text("Минздрава России")).alignRight()
        );
        docContent.add(
                RtfPara.p(RtfText.text("Быловой Належде Александровне")).alignRight()
        );
        docContent.add(
                RtfPara.p(RtfText.text("От абитуриента")).alignRight()
        );
        docContent.add(
                RtfPara.p(RtfText.bold(testee.getLastName())).alignRight()
        );
        docContent.add(
                RtfPara.p(RtfText.textJoinWithSpace(true, "Личное дело № ", RtfText.bold(testee.getCaseNumber()))).alignRight()
        );
        docContent.add(RtfPara.p(""));
        docContent.add(RtfPara.p("ЗАЯВЛЕНИЕ").alignCentered());
        docContent.add(RtfPara.p(""));
        docContent.add(RtfPara.p(RtfText.textJoinWithSpace(true, "Уважаемая Надежда Александровна! Довожу до Вашего сведения, что ",
                new SimpleDateFormat("dd.MM.yyyy").format(exam.getExamDate()), " я пропустил(а) экзамен по предмету ",
                exam.getTestingPlan().getSubject().getTitle(),
                " по уважительной причине: ________________________________________ . Прошу допустить к сдаче экзамена по данному предмету в другой день.")).alignJustified());
        docContent.add(RtfPara.p("Копию документа, подтверждающего уважительность причины, прилагаю:"));
        docContent.add(RtfPara.p("________________________________________________________________"));
        docContent.add(RtfPara.p(""));
        Date today = new Date();
        docContent.add(RtfPara.p(new SimpleDateFormat("dd MMMM yyyy", currentLocale)).alignRight());
        docContent.add(RtfPara.p(""));
        docContent.add(RtfPara.p("____________________ / " + testee.getLastName() + " /").alignRight());

        ByteArrayOutputStream document = new ByteArrayOutputStream();

        // create RTF file
        Rtf.rtf()
                .header(RtfHeader.font(RtfHeaderFont.TIMES_ROMAN).charset(RtfHeaderFont.CharSet.CYRILLIC).at(0))
                .section(docContent)
                .out(new OutputStreamWriter(document));

        fileName = testee.getCaseNumber() + "-" + new SimpleDateFormat("dd_MM_yyyy").format(exam.getExamDate()) + "-" + FileNameTransliterator.transliterateRuEn(exam.getName()) + "-goodreason.rtf";

        return new AttachmentRtf(document.toByteArray(), fileName);
    }
}
