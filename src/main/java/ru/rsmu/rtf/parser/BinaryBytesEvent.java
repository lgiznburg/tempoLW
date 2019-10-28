package ru.rsmu.rtf.parser;

import com.rtfparserkit.parser.IRtfListener;

/**
 * Represents an event to be sent to the listener.
 */
class BinaryBytesEvent implements IParserEvent
{
    private final byte[] data;

    /**
     * Constructor.
     */
    public BinaryBytesEvent(byte[] data)
    {
        this.data = data;
    }

    /**
     * Retrieve the event type.
     */
    @Override
    public ParserEventType getType()
    {
        return ParserEventType.BINARY_BYTES_EVENT;
    }

    /**
     * Pass the event to the listener.
     */
    @Override
    public void fire( IRtfListener listener)
    {
        listener.processBinaryBytes(data);
    }

    @Override
    public String toString()
    {
        return "[BinaryBytesEvent " + data.length + " bytes]";
    }

}
