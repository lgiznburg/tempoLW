package ru.rsmu.rtf.parser;


/**
 * A simple "struct" (hence the public members) representing the current state of the parser.
 * Copied from rtfparserkit library because it isn't public
 */
class ParserState
{
    public ParserState()
    {

    }

    public ParserState( ParserState state)
    {
        currentFont = state.currentFont;
        currentEncoding = state.currentEncoding;
        currentFontEncoding = state.currentFontEncoding;
        unicodeAlternateSkipCount = state.unicodeAlternateSkipCount;
    }

    public int currentFont;
    public Encoding currentEncoding = Encoding.ANSI_ENCODING;
    public Encoding currentFontEncoding;
    public int unicodeAlternateSkipCount = 1;
}
