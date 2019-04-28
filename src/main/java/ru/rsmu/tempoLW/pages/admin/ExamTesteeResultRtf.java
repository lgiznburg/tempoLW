package ru.rsmu.tempoLW.pages.admin;

import com.tutego.jrtf.*;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.func.F;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.consumabales.AttachmentRtf;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.encoders.FileNameTransliterator;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.QuestionResult;
import ru.rsmu.tempoLW.entities.Testee;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * @author leonid.
 */
public class ExamTesteeResultRtf {
    @PageActivationContext(index = 0)
    private ExamSchedule exam;

    @PageActivationContext(index = 1)
    private Testee testee;

    @Inject
    private ExamDao examDao;

    @Inject
    private Locale currentLocale;

    public StreamResponse onActivate() {
        String fileName = "unknown.rtf";
        // prepare all paragraphs before creating RTF section
        List<RtfPara> docContent = new LinkedList<>();
        // document header
        docContent.add(
                RtfPara.p( RtfText.bold(
                        RtfText.textJoinWithSpace(  true,
                                exam.getTestingPlan().getSubject().getTitle(), "(",
                                exam.getTestingPlan().getName(), ")" ) )
                ).alignCentered() );
        docContent.add(
                RtfPara.p( RtfText.bold(
                        RtfText.textJoinWithSpace(  true,
                                testee.getCaseNumber(), "/",
                                testee.getLastName() ) )
                ).alignCentered() );
        docContent.add(
                RtfPara.p( new SimpleDateFormat( "dd MMMM yyyy", currentLocale ).format( exam.getExamDate() ) )
                .alignRight()
        );

        docContent.add( RtfPara.p("") ); //empty line
        docContent.add( RtfPara.row( "Вопрос", "Базовая балл", "Итоговая оценка" )
                .bottomCellBorder()
                .leftCellBorder()
                .topCellBorder()
                .rightCellBorder()
                .cellSpace( 0.4, RtfUnit.CM )
        );


        if ( exam != null && testee != null ) {
            //forming file name with transliteration
            FileNameTransliterator trans = new FileNameTransliterator();
            SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
            String examName = trans.transliterateRuEn(exam.getTestingPlan().getSubject().getTitle());
            String examDate = sdf.format(exam.getExamDate());
            examName = examName.replaceAll("\\s", "_");
            fileName = testee.getCaseNumber() + "_" + examName + "_" + examDate + ".rtf";
            ExamResult result = examDao.findExamResultForTestee( exam, testee );
            int num = 1;
            if ( result != null ) {
                for ( QuestionResult questionResult : result.getQuestionResults() ) {
                    docContent.add( RtfPara.row(
                            RtfText.textJoinWithSpace( true,
                                    String.valueOf( num ++ ), ".", questionResult.getQuestion().getQuestionInfo().getName() ),
                            RtfText.textJoinWithSpace( true,
                                    String.valueOf( questionResult.getScore() ), "( из", String.valueOf( questionResult.getQuestion().getQuestionInfo().getMaxScore() ), ")"),
                            String.valueOf( questionResult.getMark() ) )
                            .bottomCellBorder()
                            .leftCellBorder()
                            .topCellBorder()
                            .rightCellBorder()
                            .cellSpace( 0.4, RtfUnit.CM )
                    );
                }
                docContent.add( RtfPara.row( "Результирующая оценка", "", String.valueOf( result.getMarkTotal() ) )
                        .bottomCellBorder()
                        .leftCellBorder()
                        .topCellBorder()
                        .rightCellBorder()
                        .cellSpace( 0.4, RtfUnit.CM )
                );

            }
        }
        ByteArrayOutputStream document = new ByteArrayOutputStream();

        // create RTF file
        Rtf.rtf()
                .header( RtfHeader.font( RtfHeaderFont.TIMES_ROMAN ).charset( RtfHeaderFont.CharSet.CYRILLIC ).at( 0 ) )
                .section( docContent )
                .out( new OutputStreamWriter( document ) );

        return new AttachmentRtf(  document.toByteArray(), fileName );
    }
}
