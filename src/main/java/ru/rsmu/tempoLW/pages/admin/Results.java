package ru.rsmu.tempoLW.pages.admin;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import ru.rsmu.tempoLW.consumabales.CrudMode;
import ru.rsmu.tempoLW.dao.TesteeDao;
import ru.rsmu.tempoLW.dao.internal.TesteeDaoImpl;
import ru.rsmu.tempoLW.entities.Testee;

import javax.inject.Inject;

@RequiresRoles(value = {"admin","subject_admin", "teacher"}, logical = Logical.OR )
public class Results {
    @Property
    @ActivationRequestParameter
    private CrudMode mode;

    @Property
    @ActivationRequestParameter
    private Testee testee;

    @Property
    private String casenumber;

    @InjectComponent
    private Form searchResultsForm;

    @InjectComponent
    private Field searchcasenumber;

    @Inject
    TesteeDao testeeDao;

    @org.apache.tapestry5.ioc.annotations.Inject
    private Messages messages;

    public void onValidateFromSearchResultsForm () {
        if ( casenumber != null && !casenumber.isEmpty() ) {
            if ( testeeDao.findByCaseNumber( casenumber ) == null ) {
                searchResultsForm.recordError( messages.get( "no-such-testee" ) );
            }
        } else {
            searchResultsForm.recordError( messages.get( "search-form-empty" ) );
        }
    }

    public void onSuccessFromSearchResultsForm () {
        testee = testeeDao.findByCaseNumber( casenumber );
        mode = CrudMode.REVIEW;
    }

    public boolean isMode( String modeName ) {
        return mode != null && mode.name().equals( modeName );
    }
}
