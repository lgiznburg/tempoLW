package ru.rsmu.rtf.model;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author leonid.
 *
 * Base class to store RTF file content
 */
public abstract class RtfElement {

    private RtfElement parent;

    public RtfElement getParent() {
        return parent;
    }

    public void setParent( RtfElement parent ) {
        this.parent = parent;
    }

    /**
     * Write content of template into file (stream)
     * @param stream Stream to write content to
     * @throws IOException Exception
     */
    public abstract void output( OutputStream stream ) throws IOException;
}
