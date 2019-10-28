package ru.rsmu.rtf.parser;

import com.rtfparserkit.rtf.Command;

import java.util.ArrayList;
import java.util.List;

/**
 * The upr command is used to wrap two different versions of the same set of
 * formatting commands. The first set of formatting commands uses ANSI encoding,
 * the second set uses Unicode. The upr command is expected to appear
 * in its own group, so this handler can be used to consume all of the RTF events
 * received up to the end of the group It can then pass the Unicode version of
 * the command it wraps to the listener, discarding the ANSI version.
 */
public class UprEventHandler implements ITemplateEventHandler
{
    private final ITemplateEventHandler handler;
    private int groupCount = 1;
    private boolean complete;
    private final List<IParserEvent> events = new ArrayList<IParserEvent>();

    /**
     * Constructor.
     */
    public UprEventHandler(ITemplateEventHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Buffers events until the end of the group containing the upr command is reached.
     * Once the end of the group is reached, the buffered events representing the
     * Unicode content is sent to the listener.
     */
    @Override
    public void handleEvent(IParserEvent event)
    {
        events.add(event);
        switch (event.getType())
        {
            case GROUP_START_EVENT:
            {
                ++groupCount;
                break;
            }

            case GROUP_END_EVENT:
            {
                --groupCount;
                break;
            }

            default:
                break;
        }

        if (groupCount == 0)
        {
            processCommands();
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

    /**
     * Extracts the Unicode version of the commands wrapped by the upr
     * command and passes them to the listener.
     */
    private void processCommands()
    {
        int index = 0;
        while (true)
        {
            if (index == events.size())
            {
                throw new RuntimeException("UPR command: structure not recognised");
            }
            IParserEvent event = events.get(index);
            if (event.getType() == ParserEventType.COMMAND_EVENT)
            {
                CommandEvent command = (CommandEvent) event;
                if (command.getCommand() == Command.ud)
                {
                    break;
                }
            }
            ++index;
        }

        if (index == events.size())
        {
            throw new RuntimeException("UPR command: structure not recognised: unable to locate UD command");
        }

        ++index;
        if (events.get(index).getType() != ParserEventType.GROUP_START_EVENT)
        {
            throw new RuntimeException("UPR command: expecting group start, found: " + events.get(index).getType());
        }

        ++index;
        int endIndex = index;
        int groupCount = 1;
        while (true)
        {
            if (endIndex == events.size())
            {
                break;
            }

            IParserEvent event = events.get(endIndex);
            switch (event.getType())
            {
                case GROUP_START_EVENT:
                {
                    ++groupCount;
                    break;
                }

                case GROUP_END_EVENT:
                {
                    --groupCount;
                    break;
                }

                default:
                    break;
            }

            if (groupCount == 0)
            {
                break;
            }
            ++endIndex;
        }

        if (index == events.size())
        {
            throw new RuntimeException("UPR command: structure not recognised: unable to locate UD group end");
        }

        while (index <= endIndex)
        {
            handler.handleEvent(events.get(index));
            ++index;
        }

        complete = true;
    }

}
