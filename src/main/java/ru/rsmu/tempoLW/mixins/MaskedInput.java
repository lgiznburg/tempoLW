package ru.rsmu.tempoLW.mixins;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

/**
 * @author leonid.
 */
public class MaskedInput {

    @Parameter( required = true, defaultPrefix = BindingConstants.LITERAL )
    private String mask;

    @Parameter( required = false, defaultPrefix = BindingConstants.LITERAL )
    private String placeholder;

    @Inject
    private JavaScriptSupport javaScriptSupport;

    @InjectContainer
    private ClientElement attachedTo;


    public void afterRender() {
        // call masked function with given params
        javaScriptSupport.require("maskedinput").with(attachedTo.getClientId(), mask, placeholder != null ? placeholder : "");

    }
}
