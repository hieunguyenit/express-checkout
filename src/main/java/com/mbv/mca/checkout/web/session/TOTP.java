package com.mbv.mca.checkout.web.session;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;

public class TOTP {

	private TOTP() {
	}

	/**
	 * verify OTP code
	 * @param base32Key
	 * @param code
	 * @param counter
	 * @param window
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static boolean checkOTP(String base32Key, long code, long counter, int window)
			throws NoSuchAlgorithmException, InvalidKeyException {
		Base32 codec = new Base32();
		byte[] decodedKey = codec.decode(base32Key);

		return checkOTP(decodedKey, code, counter, window);
	}
	
	/**
	 * 
	 * @param secret
	 * @param code
	 * @param counter
	 * @param window
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static boolean checkOTP(byte[] secret, long code, long counter, int window)
			throws NoSuchAlgorithmException, InvalidKeyException {
		// Window is used to check codes generated in the near past.
		// You can use this value to tune how far you're willing to go.
		for (int i = -window; i <= window; ++i) {
			long hash = computeOTP(secret, counter + i);

			if (hash == code) {
				return true;
			}
		}

		// The validation code is invalid.
		return false;
	}

	/**
	 * 
	 * @param key
	 * @param t
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static int computeOTP(byte[] key, long t)
			throws NoSuchAlgorithmException, InvalidKeyException {
		byte[] data = new byte[8];
		long value = t;
		for (int i = 8; i-- > 0; value >>>= 8) {
			data[i] = (byte) value;
		}

		SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(signKey);
		byte[] hash = mac.doFinal(data);

		int offset = hash[20 - 1] & 0xF;

		// We're using a long because Java hasn't got unsigned int.
		long truncatedHash = 0;
		for (int i = 0; i < 4; ++i) {
			truncatedHash <<= 8;
			// We are dealing with signed bytes:
			// we just keep the first byte.
			truncatedHash |= (hash[offset + i] & 0xFF);
		}

		truncatedHash &= 0x7FFFFFFF;
		truncatedHash %= 1000000;

		return (int) truncatedHash;
	}

}
