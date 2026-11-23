/*
 * Copyright 2014 Transmetric America Inc. All rights reserved.
 * Transmetric America Inc PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package geocounts.formats.io;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RawTrafficDataCryptographer {
// https://stackoverflow.com/questions/39232799/encrypt-zip-file-in-java-server-and-decrypt-zip-file-in-android
  
  public byte[] encrypt(byte[] skey, byte[] data) throws Exception {
    Cipher cipher = createCipher(skey, Cipher.ENCRYPT_MODE);
    return cipher.doFinal(data);
  }
  
  public byte[] decrypt(byte[] skey, InputStream fis) throws Exception {
    Cipher cipher = createCipher(skey, Cipher.DECRYPT_MODE);
    
    CipherInputStream cis = null;
    try {
      cis = new CipherInputStream(fis, cipher);
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      byte[] data = new byte[2048];
      int numRead;
      while ((numRead = cis.read(data)) != -1) {
        buffer.write(data, 0, numRead);
      }
      buffer.flush();         
      return buffer.toByteArray();     
    } finally {
      cis.close();
    }
  }
  
  private Cipher createCipher(byte[] skey, int mode) throws Exception {
    SecretKeySpec skeySpec = new SecretKeySpec(skey, "AES");
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");    
    cipher.init(mode, skeySpec, new IvParameterSpec(theIV()));
    return cipher;
  }
  
  private byte[] theIV() {
    return new byte[]{'g', 'e', 'o', 'c', 'o', 'u', 'n', 't', 
                      's', '-', 'r', 'o', 'c', 'k', 's', '!'};
  }
}
