package ru.rsmu.rtf.parser;

/**
 * Represents a handler which will consume events raised by the parser and handle them
 * appropriately. By default this will typically mean creating objects representing RTF elements,
 * but there may be cases where we may wish to implement something like a state machine
 * to consume a set of related events, then take some action based on the complete set
 * of events read, rather than reacting to events one at a time.
 *
 * This interface allows this functionality to be switched in and out as required.
 *
 * Copied from rtfparserkit library because it isn't public
 *
 * @author leonid.
 */
public interface ITemplateEventHandler {
    /**
     * The parser informs the handler of an event. It could return new event handler
     * which should work with next events. This means old handler should be stored into stack
     * and new one should consume incoming events until completion.
     */
    public void handleEvent( IParserEvent event );

    /**
     * Retrieve the last event seen by the handler.
     */
    public IParserEvent getLastEvent();

    /**
     * Assumes the handler is buffering events, and removes the last event from this buffer.
     */
    public void removeLastEvent();

    /**
     * Returns false if this handler is OK to receive further events, or true
     * if this handler is complete, and the previous handler should be used again.
     * This assumes that the parser is keeping a stack of handlers and popping the
     * last handler from the stack when the current handler has consumed all the events
     * it can.
     */
    public boolean isComplete();

}
