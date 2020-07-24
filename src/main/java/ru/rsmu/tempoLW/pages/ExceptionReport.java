package ru.rsmu.tempoLW.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ExceptionReporter;
import org.apache.tapestry5.services.Request;

public class ExceptionReport implements ExceptionReporter {

    @Property
    private String message;

    @Inject
    @Symbol("tapestry.production-mode")
    @Property( write = false )
    private boolean productionMode;

    @Inject
    @Property( write = false )
    protected Request request;

    @Property( write = false )
    private Throwable rootException;

    @Override
    public void reportException( Throwable exception ) {
        message = exception.getMessage();
        rootException = exception;

        if (message == null)
            message = exception.getClass().getName();
    }

}
