package com.acrabsoft.web.service.sjt.pc.operation.web.util.kafkaHuawei.util;

import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;



public class ThreeDES {

    // 向量
    private final static String iv = "87654321";
    // 加解密统一使用的编码方式
    private final static String encoding = "UTF-8";
    /**
     * 3DES加密
     * @param plainText 普通文本
     * @param secretKey
     * @return
     * @throws Exception
     */
    public static String encode(String plainText,String secretKey){
        try{
            Key deskey = null;
            DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            deskey = keyfactory.generateSecret(spec);

            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
            byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
            return new String(Base64Utils.encode(encryptData), StandardCharsets.UTF_8);
            //byte[] bb =  Base64Utils.encode(encryptData);

            // return new String(bb,"UTF-8");
        }catch(Exception e){
            e.printStackTrace();
        }
        return plainText;
    }

    /**
     * 3DES解密
     *
     * @param encryptText 加密文本
     * @return
     * @throws Exception
     */
    public static String decode(String encryptText,String secretKey){
        try{
            Key deskey = null;
            DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, deskey, ips);

            //byte[] decryptData = cipher.doFinal(Des3Base64.decode(encryptText));
            byte[] decryptData = cipher.doFinal(Base64Utils.decode(encryptText.getBytes(StandardCharsets.UTF_8)));
            return new String(decryptData, encoding);
        }catch(Exception e){
            e.printStackTrace();
        }
        return encryptText;
    }

//    public static void main(String[] args) {
//        logger.info(encode("dasdsadsadas", "PR3tmPJqcH5RE0iGsW4qiN5VYtjX7sVU"));
//    }
}


