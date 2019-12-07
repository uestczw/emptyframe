
/**
 * Project Name:commons
 * File Name:EncrypAES.java
 * Package Name:info.logis60.core.util
 * Date:2016年1月20日下午3:26:41
 * Copyright (c) 2016, chenzhou1025@126.com All Rights Reserved.
 * 
 */

package com.yianit.common.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;

import com.yianit.exception.LogisServiceException;

import hl.king.utils.MD5Util;

/**
 * 类名称: EncrypAES 类描述: 可逆加密. 创建人: zhangwei 创建时间: 2016年1月20日 下午3:26:41 修改人:
 * zhangwei 修改时间: 2016年1月20日 下午3:26:41 修改备注:
 * 
 * @see
 */
public class EncrypAES {
    public static enum ENC_TYPE_ENUM {
        AES("AES", "AES"), DESede("DESede", "DESede");
        public String id;
        public String value;

        private ENC_TYPE_ENUM(String id, String value) {
            this.id = id;
            this.value = value;
        }

        public static String keyOf(String id) {
            for (ENC_TYPE_ENUM e : ENC_TYPE_ENUM.values()) {
                if (e.id.equals(id)) {
                    return e.value;
                }
            }
            return null;
        }
    }

    private static SecretKeyFactory keyFactory = null;

    /**
     * getEnKey:(得到3-DES的密钥匙). 根据接口规范，密钥匙为24个字节，md5加密出来的是16个字节，因此后面补8个字节的0.
     * 
     * @author zhangwei
     * @param spKey
     * @return
     * @since JDK 1.7
     */

    private static byte[] getEnKey(String spKey) {
        byte[] desKey = null;
        try {
            byte[] desKey1 = MD5Util.md5EncodeBt(spKey, "");
            desKey = new byte[24];
            int i = 0;
            while (i < desKey1.length && i < 24) {
                desKey[i] = desKey1[i];
                i++;
            }
            if (i < 24) {
                desKey[i] = 0;
                i++;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return desKey;
    }

    /**
     * Encrytor:(数据加密).
     * 
     * @author zhangwei
     * @param key
     * @param str
     * @return
     * @throws LogisServiceException
     * @since JDK 1.7
     */

    public static String Encrytor(String key, String str, String enc_type) throws LogisServiceException {
        SecretKey deskey;
        byte[] cipherByte;

        try {
            SecretKey skey = null;
            // if (EncrypAES.ENC_TYPE_ENUM.AES.id.equals(enc_type)) {
            // KeyGenerator kgen = KeyGenerator.getInstance(enc_type);
            // SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            // random.setSeed(getEnKey(key));
            // kgen.init(128, random);
            // skey = kgen.generateKey();
            // }
            // else if (EncrypAES.ENC_TYPE_ENUM.DESede.id.equals(enc_type)) {
            // if (null == keyFactory) {
            // keyFactory = SecretKeyFactory.getInstance(enc_type);
            // DESedeKeySpec dks = new DESedeKeySpec(getEnKey(key));
            // skey = keyFactory.generateSecret(dks);
            // }
            // }
            // else {
            // throw new LogisServiceException("只支持AES和DESede加密!");
            // }
            KeyGenerator kgen = KeyGenerator.getInstance(enc_type);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(getEnKey(key));
            if (EncrypAES.ENC_TYPE_ENUM.AES.id.equals(enc_type)) {
                kgen.init(128, random);
            }
            else if (EncrypAES.ENC_TYPE_ENUM.DESede.id.equals(enc_type)) {
                // DESede key长度必须为112或168
                kgen.init(112, random);
            }
            else {
                throw new LogisServiceException("只支持AES和DESede加密!");
            }
            skey = kgen.generateKey();
            Cipher c = Cipher.getInstance(enc_type);
            c.init(Cipher.ENCRYPT_MODE, skey);
            byte[] src = str.getBytes();
            // 加密，结果保存进cipherByte
            cipherByte = c.doFinal(src);
        }
        catch (Exception e) {
            throw new LogisServiceException("加密失败", e);
        }
        // 根据密钥，对Cipher对象进行初始化，ENCRYPT_MODE表示加密模式

        return MD5Util.bytesToHexString(cipherByte);
    }

    /**
     * Decryptor:(数据解密).
     * 
     * @author zhangwei
     * @param key
     * @param signvalue
     * @return
     * @throws LogisServiceException
     * @since JDK 1.7
     */

    public static String Decryptor(String key, String signvalue, String enc_type) throws LogisServiceException {
        // 根据密钥，对Cipher对象进行初始化，DECRYPT_MODE表示加密模式
        SecretKey deskey;
        byte[] cipherByte;
        try {
            SecretKey skey = null;
            // if (EncrypAES.ENC_TYPE_ENUM.AES.id.equals(enc_type)) {
            // KeyGenerator kgen = KeyGenerator.getInstance(enc_type);
            // SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            // random.setSeed(getEnKey(key));
            // kgen.init(128, random);
            // skey = kgen.generateKey();
            // }
            // else if (EncrypAES.ENC_TYPE_ENUM.DESede.id.equals(enc_type)) {
            // if (null == keyFactory) {
            // keyFactory = SecretKeyFactory.getInstance(enc_type);
            // DESedeKeySpec dks = new DESedeKeySpec(getEnKey(key));
            // skey = keyFactory.generateSecret(dks);
            // }
            // }
            // else {
            // throw new LogisServiceException("只支持AES和DESede加密!");
            // }
            KeyGenerator kgen = KeyGenerator.getInstance(enc_type);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(getEnKey(key));
            if (EncrypAES.ENC_TYPE_ENUM.AES.id.equals(enc_type)) {
                kgen.init(128, random);
            }
            else if (EncrypAES.ENC_TYPE_ENUM.DESede.id.equals(enc_type)) {
                // DESede key长度必须为112或168
                kgen.init(112, random);
            }
            else {
                throw new LogisServiceException("只支持AES和DESede加密!");
            }
            skey = kgen.generateKey();
            Cipher c = Cipher.getInstance(enc_type);
            c.init(Cipher.DECRYPT_MODE, skey);
            byte[] src = MD5Util.hexStringToBytes(signvalue);
            // 加密，结果保存进cipherByte
            cipherByte = c.doFinal(src);
        }
        catch (Exception e) {
            throw new LogisServiceException("解密失败", e);
        }
        return new String(cipherByte);
    }

    public static void main(String[] args) throws LogisServiceException {
        EncrypAES ea = new EncrypAES();
        // String t = ea.Encrytor("111111111111111111",
        // "adsasdasdasdasdasdasdasdasdasd",
        // EncrypAES.ENC_TYPE_ENUM.AES.id);
        // System.out.println(t);
        // System.out.println(ea.Decryptor("111111111111111111", t,
        // EncrypAES.ENC_TYPE_ENUM.AES.id));
        //
        System.out.println(
                ea.Encrytor("1", "/admin/save.jsp", EncrypAES.ENC_TYPE_ENUM.AES.id));
        System.out.println(
                ea.Decryptor("1", "3733e2df4f3f6ea0b34404f4fa88cbff", EncrypAES.ENC_TYPE_ENUM.AES.id));
    }
}
