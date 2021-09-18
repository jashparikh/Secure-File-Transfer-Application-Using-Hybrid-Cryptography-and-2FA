
package com.example.filetransfer.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CustomEncrypt {

    static PrivateKey privateKey;

    public static void encryptFileWithAES(String keyAES, String keyDES, String fileName, String filePath, String fileExt) throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        // Here you read the cleartext.
        File extStore = Environment.getExternalStorageDirectory();

        FileInputStream fis = new FileInputStream(filePath);
        // This stream write the encrypted text. This stream will be wrapped by
        // another stream.
        FileOutputStream fos = new FileOutputStream(extStore + "/FileTransfer/Sent/output" + fileExt);

        // Length is 16 byte
        SecretKeySpec sks = new SecretKeySpec(keyAES.getBytes(), "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        // Write bytes
        int b;
        byte[] d = new byte[8];
        while ((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        // Flush and close streams.
        cos.flush();
        cos.close();
        fis.close();
        encryptFileWithDES(keyDES, fileName, fileExt);

    }

    public static void encryptFileWithDES(String key, String fileName, String fileExt) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IOException {
        DESKeySpec dks = new DESKeySpec(key.getBytes());
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
        SecretKey desKey = skf.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");

        File extStore = Environment.getExternalStorageDirectory();
        FileInputStream fis = new FileInputStream(extStore + "/FileTransfer/Sent/output" + fileExt);
        FileOutputStream fos = new FileOutputStream(extStore + "/FileTransfer/Sent/" + fileName + fileExt);

        File file = new File(extStore + "/FileTransfer/Sent/output" + fileExt);
        cipher.init(Cipher.ENCRYPT_MODE, desKey);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        doCopy(cis, fos);

        file.delete();
    }

    public static void decryptFileWithAES(String key, String fileName, String filePath, String fileExt) throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {

        File extStore = Environment.getExternalStorageDirectory();

        FileInputStream fis = new FileInputStream(extStore + "/FileTransfer/Received/output" + fileExt);
        FileOutputStream fos = new FileOutputStream(extStore + "/FileTransfer/Received/" + fileName + fileExt);
        SecretKeySpec sks = new SecretKeySpec(key.getBytes(),
                "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int b;
        byte[] d = new byte[8];
        while ((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();
        (new File(extStore + "/FileTransfer/Received/output" + fileExt)).delete();
    }

    public static void decryptFileWithDES(String AESKey, String DESKey, String fileName, String filePath, String fileExt) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IOException {
        DESKeySpec dks = new DESKeySpec(DESKey.getBytes());
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
        SecretKey desKey = skf.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");

        File extStore = Environment.getExternalStorageDirectory();
        FileInputStream fis = new FileInputStream(filePath);
        FileOutputStream fos = new FileOutputStream(extStore + "/FileTransfer/Received/output" + fileExt);

        cipher.init(Cipher.DECRYPT_MODE, desKey);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        doCopy(cis, fos);
        (new File(filePath)).delete();
        decryptFileWithAES(AESKey, fileName, filePath, fileExt);
    }

    public static void doCopy(InputStream is, OutputStream os) throws IOException {
        byte[] bytes = new byte[64];
        int numBytes;
        while ((numBytes = is.read(bytes)) != -1) {
            os.write(bytes, 0, numBytes);
        }
        os.flush();
        os.close();
        is.close();
    }

    public static String encryptWithRSA(String text) throws GeneralSecurityException, IOException {

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair kp = kpg.generateKeyPair();
        PublicKey publicKey = kp.getPublic();
        privateKey = kp.getPrivate();

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(text.getBytes());

        return bytesToString(encryptedBytes);
    }

    public static String getPrivateKey() {
        return bytesToString(privateKey.getEncoded());
    }

    public static String decryptWithRSA(String encrypted, String key) throws GeneralSecurityException, IOException {
        Key privateKey = loadPrivateKey(key);
        Log.d("DEBUG encrypted text", encrypted);
        Cipher cipher1 = Cipher.getInstance("RSA");
        cipher1.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher1.doFinal(stringToBytes(encrypted));
        String decrypted = new String(decryptedBytes);
        Log.d("DEBUG decrypted text", decrypted);
        return decrypted;
    }

    public static Key loadPrivateKey(String stored) throws GeneralSecurityException, IOException {
        byte[] encoded = stringToBytes(stored);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

    }

    public static String bytesToString(byte[] b) {
        byte[] b2 = new byte[b.length + 1];
        b2[0] = 1;
        System.arraycopy(b, 0, b2, 1, b.length);
        return new BigInteger(b2).toString(36);
    }

    public static byte[] stringToBytes(String s) {
        byte[] b2 = new BigInteger(s, 36).toByteArray();
        return Arrays.copyOfRange(b2, 1, b2.length);
    }

    public static String hashWithSHA256(String text) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] encodedHash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedHash);
    }


    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
