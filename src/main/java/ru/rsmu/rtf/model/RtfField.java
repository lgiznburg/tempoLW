package ru.rsmu.rtf.model;

import java.util.List;

/**
 * @author leonid.
 *
 * Represents Field element of RTF. Usualy field contains instruction and destination parts.
 * We extract field name (key) from instruction part and keep only one group from destination
 * as a template to replace with actual value.
 */
public class RtfField extends RtfGroup {
    /**
     * Field name or key of the field
     */
    private String key;

    private RtfTableRow tableRow;

    public RtfField( RtfGroup group ) {
        setElements( group.getElements() );
        setParent( group.getParent() );
    }

    public RtfGroup createCopy() {
        return new RtfGroup( this );
    }

    public void replaceField( String text ) {
        doReplaceText( getElements(), text );
    }

    private void doReplaceText( List<RtfElement> elements, String replace ) {
        for ( RtfElement lookingForText : elements ) {
            if ( lookingForText instanceof RtfText ) {
                ((RtfText)lookingForText).setText( replace );
            }
            else if ( lookingForText instanceof RtfGroup ) {
                doReplaceText( ((RtfGroup)lookingForText).getElements(), replace );
            }
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey( String key ) {
        this.key = key;
    }

    public RtfTableRow getTableRow() {
        return tableRow;
    }

    public void setTableRow( RtfTableRow tableRow ) {
        this.tableRow = tableRow;
    }

    @Override
    public void addElement( RtfElement element ) {
        if ( element instanceof RtfText ) {
            String rawKey = ((RtfText)element).getText();
            String key = rawKey.replaceAll( "(COMMENTS)|(MERGEFORMAT)|(\\\\\\*)", "" ).trim();
            setKey( key );
        } else {
            super.addElement( element );
        }
    }
}
