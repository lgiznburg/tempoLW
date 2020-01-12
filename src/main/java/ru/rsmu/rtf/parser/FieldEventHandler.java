package ru.rsmu.rtf.parser;

import com.rtfparserkit.rtf.Command;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leonid.
 */
public class FieldEventHandler implements ITemplateEventHandler {

    private final ITemplateEventHandler handler;
    private int groupCount = 1;
    private boolean complete;
    private final List<IParserEvent> events = new ArrayList<IParserEvent>();

    boolean beforeFieldResult = true;
    boolean afterFieldResult = false;
    int fieldResultGroupCount = 0;

    /**
     * Constructor
     * @param handler handler of higher level
     */
    public FieldEventHandler( ITemplateEventHandler handler ) {
        this.handler = handler;
    }

    /**
     * Buffers events until the end of the group containing the field command is reached.
     * Once the end of the group is reached, the buffered events representing the
     * field content is sent to the listener.
     */
    @Override
    public void handleEvent(IParserEvent event)
    {

        /*
         * Before "fldrslt" command we store only one text event.
         * When "fldrslt" appears we store one first group with text event,
         * where field result will be written by template modifiers.
         * After that group finishes we ignore everything till the end of the field
         */
        switch (event.getType())
        {
            case GROUP_START_EVENT:
            {
                ++groupCount;
                if ( !beforeFieldResult && !afterFieldResult ) {
                    events.add( event );  //keep everything before field result group will be finished
                }
                break;
            }

            case GROUP_END_EVENT:
            {
                --groupCount;
                if (groupCount == 0)
                {
                    events.add( event );  // keep final group end
                    complete = true;
                }
                else if ( !beforeFieldResult && !afterFieldResult ) {
                    events.add( event );
                    if ( groupCount == fieldResultGroupCount ) {
                        afterFieldResult = true; // first group after fldrslt command finished
                    }
                }
                break;
            }
            case COMMAND_EVENT:
                CommandEvent commandEvent = (CommandEvent) event;
                if ( commandEvent.getCommand() == Command.field ) {
                    events.add( event ); // main command
                }
                if ( commandEvent.getCommand() == Command.fldrslt && beforeFieldResult ) {
                    beforeFieldResult = false;
                    fieldResultGroupCount = groupCount;
                }
                else if ( !beforeFieldResult && !afterFieldResult ) {
                    events.add( event );  //keep everything before field result group will be finished
                }
                break;

            case STRING_EVENT:
                /*if ( beforeFieldResult ) {
                    events.add( event );  // just keep all string events, they will be merged by default handler
                }*/
                /*
                Keep all events before field result group.
                They are field name and will be merged by default handler.
                Also keep everything before field result group will be finished
                 */
                if ( !afterFieldResult ) {
                    events.add( event );
                }
                break;

            default:
                break;
        }

        if ( complete ) {
            for ( IParserEvent eventFromStack : events ) {
                handler.handleEvent( eventFromStack );
            }
        }

    }

    /**
     * Retrieve the last event seen by the handler.
     */
    @Override
    public IParserEvent getLastEvent()
    {
        return events.get(events.size() - 1);
    }

    /**
     * Assumes the handler is buffering events, and removes the last event from this buffer.
     */
    @Override
    public void removeLastEvent()
    {
        events.remove(events.size() - 1);
    }

    /**
     * Returns true once the end of the group containing the upr command as been reached.
     */
    @Override
    public boolean isComplete()
    {
        return complete;
    }

}
