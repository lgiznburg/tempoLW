package ru.rsmu.tempoLW.mixins;

import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

/**
 * @author leonid.
 *
 * Mixin to use with submit button. To prevent submitting the form twice.
 */
public class ClickOnce {
    @Inject
    private JavaScriptSupport javaScriptSupport;

    @InjectContainer
    private ClientElement attachedTo;


    public void afterRender() {
        // call masked function with given params
        javaScriptSupport.require("clickonce").with(attachedTo.getClientId());

    }
}
