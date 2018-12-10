package ru.rsmu.tempoLW.pages;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Value;
import org.apache.tapestry5.services.ExceptionReporter;

/**
 * @author leonid.
 */
public class Login implements ExceptionReporter {
    private Throwable exception;

    @Override
    public void reportException(Throwable exception) {
        this.exception = exception;
    }

    public Throwable getException() {
        return exception;
    }

    public String getMessage() {
        if (exception != null) {
            return exception.getMessage() + " Try login.";
        } else {
            return "";
        }
    }
}
