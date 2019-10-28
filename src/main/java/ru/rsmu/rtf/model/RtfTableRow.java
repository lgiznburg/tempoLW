package ru.rsmu.rtf.model;

import com.rtfparserkit.rtf.Command;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author leonid.
 *
 * Represents
 */
public class RtfTableRow extends RtfGroup {

    private boolean lastRow;
    private List<RtfElement> cells = new ArrayList<>();

    public RtfTableRow() {
    }

    public RtfTableRow( RtfTableRow tableRow ) {
        super( tableRow );
        findCells( getElements() );
    }

    private void findCells( List<RtfElement> elements ) {
        for ( RtfElement element : elements ) {
            if ( element instanceof RtfCommand
                    && ((RtfCommand)element).getCommand() == Command.cell ) {
                cells.add( element );
            }
            else if ( element instanceof RtfGroup ) {
                findCells( ((RtfGroup)element).getElements() );
            }
        }
    }

    public boolean isLastRow() {
        return lastRow;
    }

    public void setLastRow( boolean lastRow ) {
        this.lastRow = lastRow;
    }

    public List<RtfElement> getCells() {
        return cells;
    }

    public void setCells( List<RtfElement> cells ) {
        this.cells = cells;
    }

    @Override
    public void output( OutputStream stream ) throws IOException {
        super.output( stream );
    }

    public RtfTableRow createCopy() {
        return new RtfTableRow( this );
    }
}
