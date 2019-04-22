/**
 * 
 */
package com.mykj.comm.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Jason
 * 
 */
public class SecretCode {
	private static final char[] legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

	// // base64
	/**
	 * Base64 encode the given data
	 * 
	 * @param data
	 * @return
	 */
	public static String base64encode(byte[] data) {
		int start = 0;
		int len = data.length;
		StringBuffer buf = new StringBuffer(data.length * 3 / 2);

		int end = len - 3;
		int i = start;
		int n = 0;

		while (i <= end) {
			int d = ((((int) data[i]) & 0x0ff) << 16) | ((((int) data[i + 1]) & 0x0ff) << 8) | (((int) data[i + 2]) & 0x0ff);
			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append(legalChars[(d >> 6) & 63]);
			buf.append(legalChars[d & 63]);
			i += 3;
			if (n++ >= 14) {
				n = 0;
				buf.append("");
			}
		}

		if (i == start + len - 2) {
			int d = ((((int) data[i]) & 0x0ff) << 16) | ((((int) data[i + 1]) & 255) << 8);
			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append(legalChars[(d >> 6) & 63]);
			buf.append("=");
		} else if (i == start + len - 1) {
			int d = (((int) data[i]) & 0x0ff) << 16;
			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append("==");
		}

		return buf.toString();
	}

	private static int decode(char c) {
		if (c >= 'A' && c <= 'Z')
			return ((int) c) - 65;
		else if (c >= 'a' && c <= 'z')
			return ((int) c) - 97 + 26;
		else if (c >= '0' && c <= '9')
			return ((int) c) - 48 + 26 + 26;
		else
			switch (c) {
			case '+':
				return 62;
			case '/':
				return 63;
			case '=':
				return 0;
			default:
				throw new RuntimeException("unexpected code: " + c);
			}
	}

	/**
	 * Decodes the given Base64 encoded String to a new byte array. The byte
	 * array holding the decoded data is returned.
	 */

	public static byte[] base64decode(String s) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			decode(s, bos);
		} catch (IOException e) {
			throw new RuntimeException();
		}
		byte[] decodedBytes = bos.toByteArray();
		try {
			bos.close();
			bos = null;
		} catch (IOException ex) {
			System.err.println("Error while decoding BASE64: " + ex.toString());
		}
		return decodedBytes;
	}

	private static void decode(String s, OutputStream os) throws IOException {
		int i = 0;

		int len = s.length();

		while (true) {
			while (i < len && s.charAt(i) <= ' ')
				i++;

			if (i == len)
				break;
			int tri = (decode(s.charAt(i)) << 18) + (decode(s.charAt(i + 1)) << 12) + (decode(s.charAt(i + 2)) << 6) + (decode(s.charAt(i + 3)));
			os.write((tri >> 16) & 255);
			if (s.charAt(i + 2) == '=')
				break;
			os.write((tri >> 8) & 255);
			if (s.charAt(i + 3) == '=')
				break;
			os.write(tri & 255);

			i += 4;
		}
	}

	// end base64

	// aes加密
	/**
	 * aes加密
	 * 
	 * @param content
	 *            需要加密的内容
	 * @param password
	 *            加密密码
	 * @return
	 */
	public static byte[] aesEncrypt(String content, String password) {
		try {
			// KeyGenerator kgen = KeyGenerator.getInstance("AES");
			// SecureRandom srRandom = SecureRandom.getInstance("SHA1PRNG");
			// srRandom.setSeed(password.getBytes());
			// kgen.init(128, srRandom);
			// SecretKey secretKey = kgen.generateKey();
			// byte[] enCodeFormat = secretKey.getEncoded();
			// "0102030405060708".getBytes()
			// IvParameterSpec zeroIv = new
			// IvParameterSpec("1234567812345678".getBytes());
			final byte[] enCodeFormat = password.getBytes("UTF-8");
			IvParameterSpec zeroIv = new IvParameterSpec(enCodeFormat);
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			// 此处为了和c++互加密解密，加密模式要用cbc，填充模式要用PKCS5Padding
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");// 创建密码器
			byte[] byteContent = content.getBytes("UTF-8");
			// byte array[] =byteContent;
			byte array[] = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			System.arraycopy(byteContent, 0, array, 0, 6);
			// final
			// final int len=byteContent.length/16;
			// byte[] array=new byte[len];
			// if(byteContent.length>16) {
			//
			// }
			cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);// 初始化
			byte[] result = cipher.doFinal(array);
			return result; // 加密
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * aes 解密
	 * 
	 * @param content
	 *            待解密内容
	 * @param password
	 *            解密密钥
	 * @return
	 */
	public static byte[] aesDecrypt(byte[] content, String password) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom srRandom = SecureRandom.getInstance("SHA1PRNG");
			srRandom.setSeed(password.getBytes());
			kgen.init(128, srRandom);
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			// 此处为了和c++互加密解密，加密模式要用cbc，填充模式要用PKCS5Padding
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(content);
			return result; // 加密
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	// end aes加密

	// md5
	/**
	 * 计算并返回一个32位的MD5码
	 */
	@SuppressWarnings("finally")
	public static String getMD5(String str) {
		StringBuffer strBuf = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] result16 = md.digest();
			char[] digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
			for (int i = 0; i < result16.length; i++) {
				char[] c = new char[2];
				c[0] = digit[result16[i] >>> 4 & 0x0f];
				c[1] = digit[result16[i] & 0x0f];
				strBuf.append(c);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			return strBuf.toString();
		}
	}

	@SuppressWarnings("finally")
	public static String getMD5(final byte array[]) {
		StringBuffer strBuf = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(array);
			byte[] result16 = md.digest();
			char[] digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
			for (int i = 0; i < result16.length; i++) {
				char[] c = new char[2];
				c[0] = digit[result16[i] >>> 4 & 0x0f];
				c[1] = digit[result16[i] & 0x0f];
				strBuf.append(c);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			return strBuf.toString();
		}
	}

    public static String getMD5(final File f) {

        // 缓冲区大小（这个可以抽出一个参数）
        int bufferSize = 256 * 1024;
        FileInputStream fileInputStream = null;
        DigestInputStream digestInputStream = null;
        try {
            // 拿到一个MD5转换器（同样，这里可以换成SHA1）
            MessageDigest messageDigest =MessageDigest.getInstance("MD5");
            // 使用DigestInputStream
            fileInputStream = new FileInputStream(f);
            digestInputStream = new DigestInputStream(fileInputStream,messageDigest);
            // read的过程中进行MD5处理，直到读完文件
            byte[] buffer =new byte[bufferSize];
            while (digestInputStream.read(buffer) > 0);
            // 获取最终的MessageDigest
            messageDigest= digestInputStream.getMessageDigest();
            // 拿到结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 同样，把字节数组转换成字符串
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (IOException e){
            return null;
        } finally {
            try {
                digestInputStream.close();
            } catch (Exception e) {
            }
            try {
                fileInputStream.close();
            } catch (Exception e) {
            }
        }

    }


    //下面这个函数用于将字节数组换成成16进制的字符串
    public static String byteArrayToHex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
            if (n < b.length - 1) {
                hs = hs + "";
            }
        }
        return hs;
    }

	// end md5

}
