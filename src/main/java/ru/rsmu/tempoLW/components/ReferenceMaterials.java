package ru.rsmu.tempoLW.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.entities.SubjectReferenceMaterial;
import ru.rsmu.tempoLW.pages.QuestionImage;

import java.util.List;

/**
 * @author leonid.
 */
public class ReferenceMaterials {
    @Parameter(required = true)
    @Property
    private ExamSubject subject;

    @Property
    private List<SubjectReferenceMaterial> referenceMaterials;

    /**
     * for interaction through subject.referenceMaterials
     */
    @Property
    private SubjectReferenceMaterial referenceMaterial;

    @Inject
    private LinkSource linkSource;

    @Inject
    private QuestionDao questionDao;

    public String getReferenceImageLink() {
        if ( referenceMaterial != null ) {
            return linkSource.createPageRenderLink( QuestionImage.class.getSimpleName(), false, referenceMaterial.getImage().getId() ).toURI();
        }
        return "";
    }

    public void setupRender() {
        referenceMaterials = questionDao.findReferenceMaterials( subject );
    }

}
