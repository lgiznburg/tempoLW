package ru.rsmu.rtf.model;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * @author leonid.
 */
public class RtfText extends RtfElement {

    private String text;

    public RtfText( String text ) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText( String text ) {
        this.text = text;
    }

    @Override
    public void output( OutputStream stream ) throws IOException {
        byte[] ansi = text.getBytes( Charset.forName( "CP1251" ) );
        for (int i = 0; i < text.length(); ++i) {
            if ( text.codePointAt( i ) > 127 ) {
                stream.write( String.format( "\\'%02x", ansi[i] ).getBytes() );
            }
            else {
                stream.write( text.charAt( i ) );
            }
        }
    }
}
