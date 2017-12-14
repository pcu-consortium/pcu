package org.pcu.features.connector;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlUtils {

   private final static Logger log = LoggerFactory.getLogger(CrawlUtils.class);
   
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
   
   /**
    * returns the best MAC address of the computer's network interfaces :
    * non null, if possible non-loopback (which would make it null) 
    * @return
    * @throws IOException
    */
   public static String macAddress() throws IOException {
      byte[] macAddress = null;
      // NOO localhost's network interface is null on Travis (because within Docker ?)
      //macAddress = (NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress());
      
      Collections.list(NetworkInterface.getNetworkInterfaces()).stream()
         .map(itf -> {
            try { return itf + " " + itf.isVirtual() + " " + itf.isLoopback() + " " + itf.getHardwareAddress() + " " + itf.getMTU(); }
            catch (Exception e) {throw new RuntimeException(e); }})
         .forEach(msg -> log.info(msg));
      
      for (NetworkInterface itf : Collections.list(NetworkInterface.getNetworkInterfaces())) {
         //itf.getInterfaceAddresses().get(0).getAddress();
         byte[] curMacAdress = itf.getHardwareAddress();
         if (curMacAdress == null) {
            continue;
         }
         if (!itf.isLoopback()) {
            macAddress = curMacAdress; // found the first non-loopback one
            break;
         }
         if (macAddress == null) {
            macAddress = curMacAdress; // keeping in cas there's no better
         }
      };
      
      if (macAddress == null) {
         throw new IOException("Can't find non-null MAC address"); // happens when ?
      }
      
      return CrawlUtils.base64(macAddress);
   }

   public static String hostName() throws IOException {
      return InetAddress.getLocalHost().getHostName();
   }
   
}
