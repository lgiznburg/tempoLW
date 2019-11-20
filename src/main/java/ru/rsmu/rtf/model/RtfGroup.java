package ru.rsmu.rtf.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author leonid.
 *
 * RTF group
 */
public class RtfGroup extends RtfElement {
    private List<RtfElement> elements = new ArrayList<>();;

    public RtfGroup() {}

    public RtfGroup( RtfGroup group ) {
        for ( RtfElement element : group.getElements() ) {
            RtfElement elementCopy = null;
            if ( element instanceof RtfGroup ) {
                elementCopy = new RtfGroup( (RtfGroup) element );
            }
            else if ( element instanceof RtfText ) {
                elementCopy = new RtfText( ((RtfText)element).getText() );
            }
            else if ( element instanceof RtfCommand ) {
                RtfCommand command = (RtfCommand) element;
                elementCopy = new RtfCommand( command.getCommand(), command.getParameter(), command.isParameterExist(), command.isOptional() );
            }

            if ( elementCopy != null ) {
                addElement( elementCopy );
            }
        }

    }

    public List<RtfElement> getElements() {
        return elements;
    }

    public void setElements( List<RtfElement> elements ) {
        this.elements = elements;
    }

    public void addElement( RtfElement element ) {
        element.setParent( this );
        elements.add( element );
    }

    @Override
    public void output( OutputStream stream ) throws IOException {
        stream.write( "{".getBytes() );
        for ( RtfElement element : elements ) {
            element.output( stream );
        }
        stream.write( "}".getBytes() );
    }

    public RtfElement getLastElement() {
        if ( elements.size() > 0 ) {
            return elements.get( elements.size() - 1 );
        }
        return null;
    }
}
