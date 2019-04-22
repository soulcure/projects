package com.mykj.comm.util;

/**
 * 数据类型转换工具
 * 
 * @author Administrator
 * 
 */
public final class TByteUtil {

	public static void short2Bytes(byte[] b, short s, int index) {
		b[index] = (byte) (s >> 8);
		b[index + 1] = (byte) (s >> 0);
	}

	public static void short2BytesReverse(byte[] b, short s, int index) {
		b[index] = (byte) (s >> 0);
		b[index + 1] = (byte) (s >> 8);
	}

	public static void short2CharReverse(char[] b, short s, int index) {
		b[index] = (char) (s >> 0);
		b[index + 1] = (char) (s >> 8);
	}

	public static short bytes2Short(byte[] b, int index) {
		return (short) ((b[index] << 8) | b[index + 1] & 0xff);
	}

	public static short bytes2ShortReverse(byte[] b, int index) {
		return (short) ((b[index + 1] << 8) | b[index] & 0xff);
	}

	public static short char2ShortReverse(char[] b, int index) {
		return (short) ((b[index + 1] << 8) | b[index] & 0xff);
	}

	public static void int2Bytes(byte[] bb, int x, int index) {
		bb[index + 0] = (byte) (x >> 24);
		bb[index + 1] = (byte) (x >> 16);
		bb[index + 2] = (byte) (x >> 8);
		bb[index + 3] = (byte) (x >> 0);
	}

	public static void int2BytesReverse(byte[] bb, int x, int index) {
		bb[index + 3] = (byte) (x >> 24);
		bb[index + 2] = (byte) (x >> 16);
		bb[index + 1] = (byte) (x >> 8);
		bb[index + 0] = (byte) (x >> 0);
	}

	public static void int2CharReverse(char[] bb, int x, int index) {
		bb[index + 3] = (char) (x >> 24);
		bb[index + 2] = (char) (x >> 16);
		bb[index + 1] = (char) (x >> 8);
		bb[index + 0] = (char) (x >> 0);
	}

	public static int bytes2Int(byte[] bb, int index) {
		return (int) ((((bb[index + 0] & 0xff) << 24)
				| ((bb[index + 1] & 0xff) << 16)
				| ((bb[index + 2] & 0xff) << 8) | ((bb[index + 3] & 0xff) << 0)));
	}

	public static int bytes2IntReverse(byte[] bb, int index) {
		if (bb.length == 2) {
			return (int) ((((bb[index + 1] & 0xff) << 8) | ((bb[index + 0] & 0xff) << 0)));
		}
		return (int) ((((bb[index + 3] & 0xff) << 24)
				| ((bb[index + 2] & 0xff) << 16)
				| ((bb[index + 1] & 0xff) << 8) | ((bb[index + 0] & 0xff) << 0)));
	}

	public static int char2IntReverse(char[] bb, int index) {
		return (int) ((((bb[index + 3] & 0xff) << 24)
				| ((bb[index + 2] & 0xff) << 16)
				| ((bb[index + 1] & 0xff) << 8) | ((bb[index + 0] & 0xff) << 0)));
	}

	public static int bytes2int(byte[] bb, int index) {
		return (int) ((((bb[index + 0] & 0xff) << 24)
				| ((bb[index + 1] & 0xff) << 16)
				| ((bb[index + 2] & 0xff) << 8) | ((bb[index + 3] & 0xff) << 0)));
	}

	public static int bytes2intReverse(byte[] bb, int index) {
		return (int) ((((bb[index + 3] & 0xff) << 24)
				| ((bb[index + 2] & 0xff) << 16)
				| ((bb[index + 1] & 0xff) << 8) | ((bb[index + 0] & 0xff) << 0)));
	}

	public static int char2intReverse(char[] bb, int index) {
		return (int) ((((bb[index + 3] & 0xff) << 24)
				| ((bb[index + 2] & 0xff) << 16)
				| ((bb[index + 1] & 0xff) << 8) | ((bb[index + 0] & 0xff) << 0)));
	}

	/**
	 * 从数组里，指定位置转换出一个int(包括1~4字节)
	 * 
	 * @param abyte
	 *            byte[]
	 * @param beginPos
	 *            int
	 * @param len
	 *            int
	 * @param LHorder
	 *            boolean 低位在前，高位在后 －－true | 高位在前，低位在后为false
	 * @return int
	 */
	public static int byteArrToInt(byte[] abyte, int beginPos, int len,
			boolean LHorder) {
		if (beginPos > abyte.length - len) {
			return 0;
		}
		int aint = 0;
		int tmpint;
		for (int i = 0; i < len; i++) {
			tmpint = abyte[i + beginPos];
			if (tmpint < 0) {
				tmpint += 256;
			}
			if (LHorder) {
				tmpint = tmpint << (i * 8);
			} else {
				tmpint = tmpint << ((len - i - 1) * 8);
			}
			aint |= tmpint;
		}
		return aint;
	}

	/**
	 * 从数组里，指定位置转换出一个long(包括1~8字节)
	 * 
	 * @param abyte
	 *            byte[]
	 * @param beginPos
	 *            int
	 * @param len
	 *            int
	 * @param LHorder
	 *            boolean 低位在前，高位在后 －－true | 高位在前，低位在后为false
	 * @return int
	 */
	public static long byteArrToLong(byte[] abyte, int beginPos, int len,
			boolean LHorder) {
		if (beginPos >= abyte.length - 1) {
			return 0;
		}
		long aint = 0;
		long tmpint;
		for (int i = 0; i < len; i++) {
			tmpint = abyte[i + beginPos];
			if (tmpint < 0) {
				tmpint += 256;
			}
			if (LHorder) {
				tmpint = tmpint << (i * 8);
			} else {
				tmpint = tmpint << ((len - i - 1) * 8);
			}
			aint |= tmpint;
		}

		return aint;
	}

	public static int ByteArrayToint(byte[] b, int len) {
		int mask = 0xff;
		int temp = 0;
		int res = 0;
		for (int i = len - 1; i >= 0; i--) {
			res <<= 8;
			temp = b[i] & mask;
			res |= temp;
		}
		return res;
	}

}
