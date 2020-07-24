package ru.rsmu.tempoLW.components;

import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

/**
 * @author leonid.
 */
public class Calculator {

    @Environmental
    private JavaScriptSupport javaScriptSupport;

    public void setupRender() {
        javaScriptSupport.require( "calculator" ).invoke( "setAllClicks" );
    }
}
