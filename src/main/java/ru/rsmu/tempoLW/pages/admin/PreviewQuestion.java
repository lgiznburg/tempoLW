package ru.rsmu.tempoLW.pages.admin;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.Question;
import ru.rsmu.tempoLW.entities.TestSubject;

import java.util.LinkedList;
import java.util.List;


/**
 * @author leonid.
 */
//@Import( stylesheet = {"context:/static/js/KaTeX/katex.css"})
public class PreviewQuestion {

    @Inject
    private QuestionDao questionDao;

    @Property
    private Question question;

    @Property
    private TestSubject subject;

    @Inject
    @Path("context:/static/js/MathJax/MathJax.js")
    private Asset mathJax;

    @Inject
    @Path( "context:/static/js/myMathJaxConfig.js" )
    private Asset mathJaxConfig;

    @Inject
    @Path("context:/static/js/KaTeX/katex.js")
    private Asset KaTeX;

    @Inject
    @Path( "context:/static/js/KaTeX/contrib/auto-render.js" )
    private Asset autoRender;

    @Inject
    @Path( "context:/static/js/KaTeX/katex.css" )
    private Asset katexCss;

    @Inject
    @Path( "context:/static/js/KaTeX/katexRun.js" )
    private Asset katexRun;

    @Environmental
    private JavaScriptSupport javaScriptSupport;

    public void setupRender() {
        javaScriptSupport.importJavaScriptLibrary( mathJaxConfig );
        javaScriptSupport.importJavaScriptLibrary( mathJax );
        //javaScriptSupport.importJavaScriptLibrary( "https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.5/MathJax.js?config=TeX-MML-AM_CHTML" );

        //javaScriptSupport.importJavaScriptLibrary( /*KaTeX*/ "https://cdn.jsdelivr.net/npm/katex@0.10.0/dist/katex.js" );
        //javaScriptSupport.importJavaScriptLibrary( /*autoRender*/ "https://cdn.jsdelivr.net/npm/katex@0.10.0/dist/contrib/auto-render.min.js" );
        //javaScriptSupport.importStylesheet( katexCss );
        //javaScriptSupport.importJavaScriptLibrary( katexRun );
    }

    public Object  onActivate( EventContext context ) {
        if (context.getCount() > 1) {
            long subjectId = context.get(Long.class, 0);

            subject = questionDao.find( TestSubject.class, subjectId );

            if ( subject != null ) {
                long questionId = context.get( Long.class, 1 );
                question = questionDao.findNextQuestion( questionId, subject );
                return null;
            }
        }
        return Subjects.class;
    }

    public List<Long> onPassivate() {
        List<Long> list = new LinkedList<>(  );
        list.add( subject!=null ? subject.getId() : 0 );
        list.add( question!=null ? question.getId() : 0 );
        return list;
    }
}
