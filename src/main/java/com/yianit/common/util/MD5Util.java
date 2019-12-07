package com.yianit.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.yianit.exception.UtilException;

/**
 * 类名称: MD5Util 类描述: MD5工具类. 创建人: zhangwei 创建时间: 2015年9月9日 下午2:47:06 修改人:
 * zhangwei 修改时间: 2015年9月9日 下午2:47:06 修改备注:
 */

public class MD5Util {

    /**
     * md5Encode:(加密). <br/>
     * 
     * @author zhangwei
     * @param value
     * @param key
     * @return
     * @throws UtilException
     * @since JDK 1.7
     */

    public static final String md5Encode(String value, String key) throws UtilException {
        if (value == null || value.equals("")) {
            throw new UtilException("不能加密空字串！");
        }
        if (key == null || key.equals("")) {
            key = "";
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] value_b = (value + key).getBytes();
            byte[] result = messageDigest.digest(value_b);
            return bytesToHexString(result).toUpperCase();
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilException(e);
        }
    }

    public static final byte[] md5EncodeBt(String value, String key) throws UtilException {
        if (value == null || value.equals("")) {
            throw new UtilException("不能加密空字串！");
        }
        if (key == null || key.equals("")) {
            key = "";
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] value_b = (value + key).getBytes();
            byte[] result = messageDigest.digest(value_b);
            return result;
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilException(e);
        }
    }

    /**
     * bytesToHexString:(转高位处理). <br/>
     * 
     * @author zhangwei
     * @param src
     * @return
     * @since JDK 1.7
     */

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * Convert hex string to byte[]
     * 
     * @param hexString
     * the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     * 
     * @param c
     * char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * main:(这里用一句话描述这个方法的作用). <br/>
     * 
     * @author zhangwei
     * @param args
     * @since JDK 1.7
     */

    public static void main(String[] args) {
        try {
            System.out.println(md5Encode("Cg13550155567", ""));
        }
        catch (UtilException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
