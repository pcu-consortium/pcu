package org.pcu.features.connector;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CrawlUtils {
   
   private static final String MD5 = "MD5";
   
   public static String md5(String s) {
      try {
         return new BigInteger(1, MessageDigest.getInstance(MD5).digest(s.getBytes())).toString(16);
      } catch (NoSuchAlgorithmException nsaex) {
         throw new RuntimeException("Can't compute md5, error initing hash / digest", nsaex);
      }
   }

   public static String base64(byte[] b) {
      return Base64.getEncoder().encodeToString(b);
   }
   
   public static String macAddress() throws IOException {
      byte[] macAddress = (NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress());
      return CrawlUtils.base64(macAddress);
   }

   public static String hostName() throws IOException {
      return InetAddress.getLocalHost().getHostName();
   }
   
}
