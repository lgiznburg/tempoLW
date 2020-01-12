package ru.rsmu.rtf.model;

import com.rtfparserkit.rtf.Command;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author leonid.
 *
 * Common RTF command
 */
public class RtfCommand extends RtfElement {
    private Command command;
    private int parameter;
    private boolean parameterExist;
    private boolean optional;

    public RtfCommand( Command command, int parameter, boolean hasParameter, boolean optional ) {
        this.command = command;
        this.parameter = parameter;
        this.parameterExist = hasParameter;
        this.optional = optional;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand( Command command ) {
        this.command = command;
    }

    public int getParameter() {
        return parameter;
    }

    public void setParameter( int parameter ) {
        this.parameter = parameter;
    }

    public boolean isParameterExist() {
        return parameterExist;
    }

    public boolean isOptional() {
        return optional;
    }

    @Override
    public void output( OutputStream stream ) throws IOException {
        if ( optional ) {
            stream.write( "\\*".getBytes() );
        }
        stream.write( "\\".getBytes() );
        stream.write( command.getCommandName().getBytes() );
        if ( parameterExist ) {
            stream.write( String.valueOf( parameter ).getBytes() );
        }
        stream.write( " ".getBytes() );
        // this is hack for encoding commands
        if ( command == Command.adeflang ) {
            stream.write( "\\ansi\\ansicpg1251\\uc1".getBytes() );
        }
    }
}
