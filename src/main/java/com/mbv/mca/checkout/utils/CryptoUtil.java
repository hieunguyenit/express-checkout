package com.mbv.mca.checkout.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

import org.apache.directory.triplesec.crypto.SHA1Digest;
import org.apache.directory.triplesec.otp.Hotp;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;


public class CryptoUtil {
	private static final String AES_KEY		= "checkout";
	
	public static final String SHA1			= "SHA-1";
	
	public static final String MD5			= "MD5";
	
	private static BlockCipher AESCipher 	= new AESEngine();
	
	private static BlockCipherPadding bcp 	= new PKCS7Padding();
	
	/**
	 * Generate OTP form hex pin
	 * @param hexPin
	 * @param key
	 * @param counter
	 * @return
	 */
	public static String generateOtpFromHexPin(String hexPin) {
		SHA1Digest sha = new SHA1Digest();
		byte[] pinEncodebytes = hexPin.getBytes();
		sha.update(pinEncodebytes, 0, pinEncodebytes.length);
		
		//
		byte[] hashBytes = new byte[sha.getDigestSize()];
		sha.doFinal(hashBytes, 0);
		
		//
		String counter = (new Date().getTime())+"";
		
		return Hotp.generate(hashBytes, Long.parseLong(counter), 9);	
	}
	
	/**
	 * Generate hex string from array of bytes
	 * @param bytes
	 * @return
	 */
	public static String bytes2Hex(byte[] bytes) {
		String HEXES = "0123456789abcdef";
		
		StringBuilder hex = new StringBuilder(2 * bytes.length);
		
		for (byte b : bytes) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
					HEXES.charAt((b & 0x0F)));
		}
		
		return hex.toString();
	}

	/**
	 * Hash the given message and salt in bytes
	 * 
	 * @param msg
	 * @param salt
	 * @return
	 */
	public static byte[] hashMsgWithAlg(String algo, byte[] msg, byte[] salt) {
		byte[] result = null;
		MessageDigest md;

		try {
			md = MessageDigest.getInstance(algo);
			md.update(msg);
			if (salt != null) {
				md.update(salt);
			}
			result = md.digest();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return result;
	}

	/*
     * 
     */
    private static byte[] processing(byte[] input, byte[] key, boolean encrypt) throws DataLengthException, InvalidCipherTextException {
    	// Initialize block cipher with a given key
    	PaddedBufferedBlockCipher pbbc = new PaddedBufferedBlockCipher(AESCipher, bcp);
    	pbbc.init(encrypt, new KeyParameter(key));
    	
    	// Process data bytes
        byte[] output = new byte[pbbc.getOutputSize(input.length)];
        int bytesWrittenOut = pbbc.processBytes(
        						input, 0, input.length, output, 0);
        // Process the last block
        bytesWrittenOut += pbbc.doFinal(output, bytesWrittenOut);
        
        // Remove padding values
        if (!encrypt && (bytesWrittenOut != output.length)){
        	byte[] truncatedOutput = new byte[bytesWrittenOut];
        	
            System.arraycopy(output, 0, truncatedOutput, 0,	bytesWrittenOut);
            
            return truncatedOutput;
        }
        
        return output;
 
    }
    
	/*
	 * Encrypt data
	 */
	public static byte[] encrypt(byte[] input, byte[] key) throws DataLengthException,
											InvalidCipherTextException {
		return processing(input, key, true);
	}

	/*
	 * Decrypt data
	 */
	public static byte[] decrypt(byte[] input, byte[] key) throws DataLengthException,
											InvalidCipherTextException {
		return processing(input, key, false);
	}
	
	/**
	 * Encrypt a message with AES
	 * @param plain
	 * @return
	 * @throws Exception
	 */
	public static String encryptMsgWithAES(String plain) throws Exception{
		byte[] key = hashMsgWithAlg("MD5", AES_KEY.getBytes(), null);
		
		byte[] cipher = encrypt(plain.getBytes(), key);
		
		return Base64Utils.base64Encode(cipher);
	}
	
	/**
	 * Decrypt cipher with AES
	 * @param cipher
	 * @return
	 * @throws Exception
	 */
	public static String decryptMsgWithAES(String cipher) throws Exception{
		byte[] key = hashMsgWithAlg("MD5", AES_KEY.getBytes(), null);
		
		byte[] plain = decrypt(Base64Utils.base64Decode(cipher), key);
		
		return new String(plain);
	}
	
	/**
	 * Encrypt a given number by add 1
	 * @param number
	 * @return
	 */
	public static String encryptNumber(String number){
		String result = "";
		
		Random rd = new Random();
		int tmp = rd.nextInt(9);
		boolean isEven = tmp % 2 == 0 ? true : false;
		
		result += tmp;
		
		for (int i = 0; i < number.length(); i++) {
			result += number.charAt(i);
			if (isEven == (i%2==0)) {
				result += rd.nextInt(9);
			}
		}
				
		return result;
	}
	
	/**
	 * Decrypt a given number
	 * @param cipher
	 * @return
	 */
	public static String decryptNumber(String number){
		String result = "";
		
		int tmp = Character.digit(number.charAt(0), 10);		
		int initial = tmp % 2 == 0 ? 1 : 2;
		number = number.substring(1);
		
		for (int i = 0; i < number.length(); i++) {
			if (i == initial){
				initial += 3;
				continue;
			}
			result += Character.toString(number.charAt(i));
		}
		
		return result;
	}
	
	
	public static void main(String[] args) {
		String test = "1323271090488";
		try {
			String cipher = encryptNumber(test);
			
			System.out.println(cipher);
			
			System.out.println(decryptNumber(cipher));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println(System.getProperty("user.home"));
	}

}
