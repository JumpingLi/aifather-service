package com.champion.readingstory.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author jpli3
 */
public class Md5Util {
    /**
     * @return
     */

    public static String md5Array(byte[] bytes){
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.update(bytes);
        String md5Str = bytesToHex1(md5.digest());
        return md5Str;
    }

    private static String bytesToHex1(byte[] md5Array) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < md5Array.length; i++) {
            int temp = 0xff & md5Array[i];
            String hexString = Integer.toHexString(temp);
            if (hexString.length() == 1) {
                strBuilder.append("0").append(hexString);
            } else {
                strBuilder.append(hexString);
            }
        }
        return strBuilder.toString();
    }
    public static String md5Encode(String source) {
        StringBuilder sb = new StringBuilder(32);
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(source.getBytes("UTF-8"));
            byte[] byteArr = md.digest();
            for (byte b : byteArr) {
                int val = ((int) b & 0xff);
                if (val < 16) {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(val));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("ERROR: MD5 encode error! Detail Message: ", e);
        }
    }

    public static void main(String[] args) {
        System.out.println(md5Encode(Md5Util.md5Encode("abc") + 123));
    }
}
