package ru.rsmu.tempoLW.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author leonid.
 */
public class PasswordEncoder {
    public static String encrypt( String password ) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance( "MD5" );
        md.update( password.getBytes() );
        BigInteger hash = new BigInteger( 1, md.digest() );
        String passwordHash = hash.toString( 16 );
        while ( passwordHash.length() < 32 ) passwordHash = "0" + passwordHash;
        return passwordHash;
    }
}
