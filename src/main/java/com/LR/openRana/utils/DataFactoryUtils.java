package com.LR.openRana.utils;

import com.LR.openRana.common.LLException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class DataFactoryUtils {

    private static final String ALGORITHM = "AES";
    private static final String KEY_STRING = "Open-Rana-SSO-SecureKey!"; // 密钥长度必须是16字节


    public static String addSalt(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // 将盐值和密码拼接后进行哈希
            String combined = salt + password;
            byte[] hashBytes = md.digest(combined.getBytes(StandardCharsets.UTF_8));

            // 可选：将字节转换为Base64字符串，便于存储和比较
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }


    /**
     * 加密字符串
     *
     * @param data 需要加密的字符串
     * @return 加密后的Base64编码字符串
     * @throws Exception 如果加密过程中出现错误
     */
    public static String encrypt(String data) {
        try {
            Key key = new SecretKeySpec(KEY_STRING.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new LLException("加密失败");
        }
    }


    /**
     * 解密字符串
     *
     * @param encryptedData 需要解密的Base64编码字符串
     * @return 解密后的字符串
     * @throws Exception 如果解密过程中出现错误
     */
    public static String decrypt(String encryptedData) {
        try {
            Key key = new SecretKeySpec(KEY_STRING.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decodedBytes);
            return new String(decrypted);
        } catch (Exception e) {
            throw new LLException("解密失败");
        }

    }


}
