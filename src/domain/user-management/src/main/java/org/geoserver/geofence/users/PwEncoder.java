/*
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved This code is licensed under the
 * GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.users;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author ETj <etj at geo-solutions.it>
 */
class PwEncoder {

    private static byte[] getKey() {
        String strKey = System.getProperty("GEOFENCE_PWENCODER_KEY");
        if (strKey == null || strKey.length() < 16) {
            strKey = "installation dependant key needed";
        }
        return strKey.substring(0, 16).getBytes();
    }

    public static String encode(String msg) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(getKey(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] input = msg.getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = cipher.doFinal(input);
            return Base64.getEncoder().encodeToString(encrypted);
            // return DatatypeConverter.printBase64Binary(encrypted);

        } catch (GeneralSecurityException ex) {
            throw new RuntimeException("Error while encoding", ex);
        }
    }

    public static String decode(String msg) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(getKey(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            // byte[] de64 = DatatypeConverter.parseBase64Binary(msg);
            byte[] de64 = Base64.getDecoder().decode(msg);
            byte[] decrypted = cipher.doFinal(de64);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException ex) {
            throw new RuntimeException("Error while encoding", ex);
        }
    }
}
