package br.com.proway.util;

import java.math.BigInteger;
import java.security.MessageDigest;

public abstract class StringUtil {
    
    public static String toMD5(String value) {        
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(value.getBytes(),0,value.length());
            return new BigInteger(1,m.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
}