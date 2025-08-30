package net.paradise_client.chatroom.common.cipher;

import javax.crypto.Cipher;
import java.security.*;

public class RSAEncryption {
  public static byte[] encrypt(byte[] data, PublicKey publicKey) throws Exception {
    Cipher cipher = Cipher.getInstance("RSA");
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    return cipher.doFinal(data);
  }

  public static byte[] decrypt(byte[] data, PrivateKey privateKey) throws Exception {
    Cipher cipher = Cipher.getInstance("RSA");
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    return cipher.doFinal(data);
  }
}
