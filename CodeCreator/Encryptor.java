/* Author: Luigi Vincent
* Applying standard AES Encryption / Decryption algorithm
*/

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encryptor {
	private final static String key = "Bar12345Bar12345"; // 128 bit key

	public static String encode(String input) {
		String result = null;
		try {
			Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, aesKey);
			byte[] encrypted = cipher.doFinal(input.getBytes());

			StringBuilder sb = new StringBuilder();
			for (byte b : encrypted) {
				sb.append((char)b);
			} 
			result = sb.toString(); 
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public static String decode(String input) {
		String result = null;
		try {
			Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, aesKey);
			
			byte[] bb = new byte[input.length()];
			for (int i = 0; i < input.length(); i++) {
				bb[i] = (byte) input.charAt(i);
			}
			result = new String(cipher.doFinal(bb));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	// public static void main(String[] args) {
	// 	String test = "Luigi,vincenlu@kean.edu,862-823-1747,Project Fi,External Battery,y";
	// 	String encoded = Encryptor.encode(test);
	// 	System.out.println(encoded);
	// 	System.out.println(Encryptor.decode(encoded));
	// }
}