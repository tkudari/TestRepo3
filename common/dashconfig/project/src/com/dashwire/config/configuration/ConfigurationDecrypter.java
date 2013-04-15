package com.dashwire.config.configuration;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Base64;

import com.dashwire.config.util.CommonUtils;

public final class ConfigurationDecrypter {

	private ConfigurationDecrypter() {} 
	
    public static JSONObject decryptConfiguration( String encryptedConfig, Context context ) {
        try {
            byte[] decodedConfiguration = Base64.decode( encryptedConfig, Base64.DEFAULT );
            byte[] iv = {
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00
            };
            IvParameterSpec ips = new IvParameterSpec( iv );
            String encodedKey = CommonUtils.getKey( context );

            byte[] decodedKey = Base64.decode( encodedKey, Base64.DEFAULT );

            SecretKeySpec skeySpec = new SecretKeySpec( decodedKey, "AES/CBC/PKCS5Padding" );
            Cipher cipher;
            cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding" );
            cipher.init( Cipher.DECRYPT_MODE, skeySpec, ips );
            byte[] decrypted = cipher.doFinal( decodedConfiguration );
            String data = new String( decrypted );
            return new JSONObject( data );
        } catch ( NoSuchAlgorithmException e ) {
            e.printStackTrace();
        } catch ( NoSuchPaddingException e ) {
            e.printStackTrace();
        } catch ( InvalidKeyException e ) {
            e.printStackTrace();
        } catch ( IllegalArgumentException e ) {
            e.printStackTrace();
        } catch ( IllegalBlockSizeException e ) {
            e.printStackTrace();
        } catch ( BadPaddingException e ) {
            e.printStackTrace();
        } catch ( InvalidAlgorithmParameterException e ) {
            e.printStackTrace();
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
        return null;
    }
}
