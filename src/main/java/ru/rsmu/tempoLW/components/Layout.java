package ru.rsmu.tempoLW.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import ru.rsmu.tempoLW.pages.Index;

/**
 * @author leonid.
 */
@Import(stylesheet = {"context:/static/css/jquery-ui.min.css", "context:/static/css/tempolw.css"}/*,
library = {"context:/static/js/bootstrap.min.js", "context:/static/js/popper.min.js"}*/)
public class Layout {

    @Property
    @Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
    private String pageTitle;

    @Property
    private String username = "";

    /**
     * Respond to the user clicking on the "Log Out" link
     */
/*
    @Log
    public Object onActionFromLogout()
    {
        return Index.class;
    }
*/

}
