package ru.rsmu.rtf;

import ru.rsmu.rtf.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leonid.
 *
 * Works on template and produces table modification.
 * Modification should be stored in form of String[][].
 *
 */
public class TableModifier {
    private Map<String,List<List<String>>> modifications;

    public TableModifier() {
        modifications = new HashMap<>();
    }

    public void put( String key, List<List<String>> table ) {
        modifications.put( key, table );
    }

    public void modify( RtfDocument document ) {
        for ( String key : modifications.keySet() ) {
            List<List<String>> table = modifications.get( key );
            List<RtfElement> fields = document.findField( key );
            for ( RtfElement fieldElement : fields ) {
                RtfField field = (RtfField)fieldElement;
                RtfTableRow tableRow = field.getTableRow();
                if ( tableRow == null ) {
                    return;  // no table, no modification
                }
                // remove field to work with clear row
                ((RtfGroup)field.getParent()).getElements().remove( field );
                //
                RtfGroup parent = (RtfGroup) tableRow.getParent();
                for( List<String> row : table ) {
                    RtfTableRow rowToInsert = null;
                    if ( table.indexOf( row ) == (table.size() - 1) ) {
                        //last row - modify table row from source
                        rowToInsert = tableRow;
                    }
                    else {
                        // create new row and insert it before existed table row
                        rowToInsert = tableRow.createCopy();
                        parent.getElements().add( parent.getElements().indexOf( tableRow ), rowToInsert );
                    }
                    //insert text into the row
                    int cellSize = Math.min( rowToInsert.getCells().size(), row.size() );
                    for ( int cellIndex = 0; cellIndex < cellSize; cellIndex++ ) {
                        RtfElement cell = rowToInsert.getCells().get( cellIndex );
                        String text = row.get( cellIndex );
                        // replace text in field
                        field.replaceField( text );
                        RtfGroup fieldCopy = field.createCopy();
                        //insert field elements before cell
                        RtfGroup cellParent = (RtfGroup) cell.getParent();
                        int placeToInsert = cellParent.getElements().indexOf( cell );
                        cellParent.getElements().addAll( placeToInsert, fieldCopy.getElements() );
                    }
                }
            }
        }
    }
}
