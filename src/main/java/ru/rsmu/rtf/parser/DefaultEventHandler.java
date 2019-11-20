package ru.rsmu.rtf.parser;

import com.rtfparserkit.parser.IRtfListener;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Default parser event handler. Passes events to the listener. In this implementation
 * the events are queued to allow later events to modify earlier events before they are
 * passed to the listener. For example, we coalesce consecutive string events together.
 */
public class DefaultEventHandler implements ITemplateEventHandler
{
    private static final int MAX_EVENTS = 5;

    private final IRtfListener listener;
    private final Deque<IParserEvent> events = new ArrayDeque<IParserEvent>();

    /**
     * Constructor.
     */
    public DefaultEventHandler(IRtfListener listener)
    {
        this.listener = listener;
    }

    /**
     * If we've reached the end of the document, flush all queued events to
     * the listener and pass on the document end event.
     * If we have received consecutive string events, coalesce them into
     * a single event in the buffer.
     * If the buffer has reached its maximum size, remove the event from the
     * front of the buffer and pass this to the listener.
     */
    @Override
    public void handleEvent(IParserEvent event)
    {
        if (event.getType() == ParserEventType.DOCUMENT_END_EVENT)
        {
            flushEvents();
            event.fire(listener);
        }
        else
        {
            IParserEvent lastEvent = events.peekLast();
            if (lastEvent != null && lastEvent.getType() == ParserEventType.STRING_EVENT && event.getType() == ParserEventType.STRING_EVENT)
            {
                event = mergeStringEvents((StringEvent) event);
            }

            events.add(event);

            if (events.size() > MAX_EVENTS)
            {
                events.removeFirst().fire(listener);
            }
        }
    }

    /**
     * It's always valid for this handler to continue processing events,
     * so we always return false.
     */
    @Override
    public boolean isComplete()
    {
        return false;
    }

    /**
     * Allows the caller to see the event at the end of the buffer.
     */
    @Override
    public IParserEvent getLastEvent()
    {
        return events.getLast();
    }

    /**
     * Allows the caller to remove the last event from the buffer.
     */
    @Override
    public void removeLastEvent()
    {
        events.removeLast();
    }

    /**
     * Removes the string event from the end of the buffer, merges it with the string
     * event we've just received, and adds the new event to the end of the buffer.
     */
    private IParserEvent mergeStringEvents(StringEvent event)
    {
        StringEvent lastEvent = (StringEvent) events.removeLast();
        StringEvent newEvent = new StringEvent(lastEvent.getString() + event.getString());
        return newEvent;
    }

    /**
     * Passes any remaining events in the buffer to the listener and clears the event buffer,
     */
    private void flushEvents()
    {
        for (IParserEvent event : events)
        {
            event.fire(listener);
        }
        events.clear();
    }

}
