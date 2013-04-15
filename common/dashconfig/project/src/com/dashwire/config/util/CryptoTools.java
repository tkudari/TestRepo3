package com.dashwire.config.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoTools {

    public static String sha256( String data ) {
        byte[] digest = sha256Binary( data );
        if ( digest != null ) {
            return hexEncode( digest );
        }
        return "";
    }

    public static String hexEncode( byte[] digest ) {
        StringBuffer hexString = new StringBuffer();
        for ( int i = 0; i < digest.length; i++ ) {
            hexString.append( String.format( "%02x", 0xFF & digest[ i ] ) );
        }
        return hexString.toString();
    }

    public static byte[] sha256Binary( String s ) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance( "SHA-256" );
            digest.update( s.getBytes() );
            return digest.digest();
        } catch ( NoSuchAlgorithmException e ) {
            e.printStackTrace();
        }
        return null;
    }

    public static String md5( String data ) {
        try {
            MessageDigest mDigest;
            
            mDigest = MessageDigest.getInstance( "MD5" );
            mDigest.update( data.getBytes() );

            byte d[] = mDigest.digest();
            StringBuffer hash = new StringBuffer();

            for ( int i = 0; i < d.length; i++ ) {
                hash.append( Integer.toHexString( 0xFF & d[ i ] ) );
            }
            
            return hash.toString();
            
        } catch ( NoSuchAlgorithmException e ) {
            e.printStackTrace();
            return null;
        }
    }
}
