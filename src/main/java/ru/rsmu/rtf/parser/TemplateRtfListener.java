package ru.rsmu.rtf.parser;

import com.rtfparserkit.parser.IRtfListener;
import com.rtfparserkit.rtf.Command;
import ru.rsmu.rtf.model.*;

/**
 * @author leonid.
 */
public class TemplateRtfListener implements IRtfListener {

    /**
     * Resulting Rtf Template
     */
    private RtfDocument document;

    /**
     * current group
     */
    private RtfGroup currentGroup;
    /**
     *
     */
    private RtfTableRow currentTableRow;


    @Override
    public void processDocumentStart() {
        document = new RtfDocument();
        currentGroup = document;
    }

    @Override
    public void processDocumentEnd() {

    }

    @Override
    public void processGroupStart() {
        // starts new group
        RtfGroup nextGroup = new RtfGroup();
        currentGroup.addElement( nextGroup );
        currentGroup = nextGroup;
    }

    @Override
    public void processGroupEnd() {
        // close current group
        currentGroup = (RtfGroup) currentGroup.getParent();
    }

    @Override
    public void processCharacterBytes( byte[] bytes ) {
        //System.out.println( new String( bytes ) );
    }

    @Override
    public void processBinaryBytes( byte[] bytes ) {
        //System.out.println( new String( bytes ) );
    }

    @Override
    public void processString( String s ) {
        RtfText text = new RtfText( s );
        currentGroup.addElement( text );
    }

    @Override
    public void processCommand( Command command, int parameter, boolean hasParameter, boolean optional ) {
        RtfCommand rtfCommand = new RtfCommand( command, parameter, hasParameter, optional );
        /*if ( command.getCommandType() == CommandType.Destination ) {
            destination = rtfCommand;
        }*/

        switch ( command  ) {
            case field:
                // starts new group - a field. remove just created group from the stack
                RtfField field = new RtfField( currentGroup );
                RtfGroup parentGroup = (RtfGroup) currentGroup.getParent();
                parentGroup.getElements().remove( currentGroup );
                parentGroup.addElement( field );
                if ( currentTableRow != null ) {
                    // if we are in the table row - set table row for make table modification easier
                    field.setTableRow( currentTableRow );
                }

                currentGroup = field;
                document.addField( currentGroup );
                break;
                /*
                MSWord creates table in a weird way. According to standard table row should start from
                "trowd" command but MSWord sometimes creates more "trowd", and sometimes "trowd" is missing.
                This is a try to detect row start by "intbl" command. "ltrrow" does not work because it's
                followed by "}" (group end) sometimes
                 */
            //case ltrrow:
            //case rtlrow:
            case intbl:
            case trowd:
                // starts new table row if we are not in another table
                // NB: we do not support table in table structure !!!
                if ( currentTableRow == null ) {
                    RtfTableRow row = new RtfTableRow();
                    currentGroup.addElement( row );
                    currentGroup = row;
                    currentTableRow = row;
                }
                currentGroup.addElement( rtfCommand );
                break;
            case row:
                // close current table row
                currentGroup.addElement( rtfCommand );
                currentGroup = (RtfGroup) currentGroup.getParent();
                currentTableRow = null;
                break;
            case cell:
                currentTableRow.getCells().add( rtfCommand );
            default:
                currentGroup.addElement( rtfCommand );

        }
    }

    /**
     *
     * @return parsed RTF template
     */
    public RtfDocument getDocument() {
        return document;
    }
}
