package ru.rsmu.rtf.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author leonid.
 */
public class RtfDocument extends RtfGroup {

    private List<RtfElement> fields;

    public RtfDocument( ) {
        this.fields = new ArrayList<>();
    }

    public void addField( RtfElement text ) {
        fields.add( text );
    }

    public List<RtfElement> findField( String key ) {
        List<RtfElement> found = new ArrayList<>();
        for ( RtfElement element : fields ) {
            if ( element instanceof RtfField ) {
                if ( ((RtfField)element).getKey().equals( key ) ) {
                    found.add( element );
                }
            }
        }
        return found;
    }

    @Override
    public void output( OutputStream stream ) throws IOException {
        for ( RtfElement element : getElements() ) {
            element.output( stream );
        }

    }

    public void replaceField( RtfElement field, RtfElement text ) {
        RtfElement parentElement = field.getParent();
        if ( parentElement instanceof RtfGroup ) {
            RtfGroup parent = (RtfGroup) parentElement;

            int pos = parent.getElements().lastIndexOf( field );
            parent.getElements().add( pos, text );
            text.setParent( parent );
            parent.getElements().remove( field );

            fields.remove( field );
        }
    }
}
