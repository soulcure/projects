package com.mykj.comm.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

/**
 * 字节输入流，提供各种数据类型的解析 <br>
 * 默认高位在前
 * 
 * @author 赵金元
 * @version 1.0.0
 */
public class TDataInputStream extends ByteArrayInputStream {

	private boolean isFront = true; // 是否高位在前,默认为true

	public static final String normalEnc = "UTF-8"; // 默认的字符串编码

	/**
	 * @Title: setFront
	 * @Description: 设置字节对齐方式
	 * @param _isFront
	 *            true:高位在前 false:低位在前
	 * @version: 2011-5-16 下午02:25:51
	 */
	public void setFront(boolean _isFront) {
		this.isFront = _isFront;
	}

	/**
	 * @return the isFront
	 */
	public boolean isFront() {
		return isFront;
	}

	public TDataInputStream(byte[] buf, boolean bool) {
		super(buf);
		isFront = bool;
	}

	public TDataInputStream(byte[] buf) {
		super(buf);
	}

	public TDataInputStream(InputStream buf) throws Exception {
		super(new byte[0]);
		if (buf == null || buf.available() == 0) {
			throw new Exception("InputStream is null");
		}
		byte[] array = new byte[buf.available()];
		buf.read(array);
		this.buf = array;
		this.count = array.length;
		this.pos = 0;
		// super(buf.);
	}

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param in
	 * @param readlen
	 *            指定长度
	 */
	public TDataInputStream(final InputStream in, final int readlen) throws Exception {
		super(new byte[0]);
		if (in == null || readlen <= 0) {
			return;
		}
		byte[] array = new byte[readlen];
		int re = readDataByLen(in, array, array.length);
		if (re == -1) {
			throw new Exception("Create TDataInputStream readDataByLen return -1");
		}
		this.buf = array;
		this.count = array.length;
		this.pos = 0;
	}

	/**
	 * @Title: readDataByLen
	 * @Description: 读取指定长度len个字节到缓冲区buf中
	 * @param is
	 * @param buf
	 * @param len
	 * @return 读取的字节数，或者-1表示流结束
	 * @version: 2012-9-10 上午11:18:47
	 * @throws Exception
	 */
	public static int readDataByLen(InputStream is, byte[] buf, int len) throws Exception {
		if (buf == null || is == null) {
			throw new NullPointerException();
		}
		if (buf.length < len) {
			throw new ArrayIndexOutOfBoundsException();
		}
		int i = 0;

		try {
			while (i < len) {
				int tmp = is.read(buf, i, len - i);
				if (tmp == -1) {
					return tmp;
				}
				i += tmp;
			}
			/*
			 * for (i = 0; i < len; i++) { int c = 0; if ((c = is.read()) == -1)
			 * { break; } buf[i] = (byte) c; }
			 */
		} catch (SocketTimeoutException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return i;
	}

	/**
	 * 默认高位在前的分布方式读取long
	 * 
	 * @return long
	 */
	public long readLong() {
		return readLong(isFront);
	}

	/**
	 * 默认高位在前的分布方式读取Int
	 * 
	 * @return Int
	 */
	public int readInt() {
		return readInt(isFront);
	}

	/**
	 * 默认高位在前的分布方式读取Short
	 * 
	 * @return Short
	 */
	public short readShort() {
		return readShort(isFront);
	}

	/**
	 * 读取一个byte
	 * 
	 * @return byte
	 */
	public byte readByte() {
		if (!isCanRead(1)) {
			return 0;
		}
		return (byte) this.read();
	}

	/**
	 * 读取BYTE,无符号的，和C++中的byte统一
	 * 
	 * @return
	 */
	public int readByteUnsigned() {
		if (!isCanRead(1)) {
			return 0;
		}
		int data = this.read();
		if (data < 0) {
			data = -data + 128;// ?
		}
		return data;
	}

	/**
	 * 用指定的高低位分布方式读取一个Long,true高位在前，false高位在后
	 * 
	 * @return long
	 */
	public long readLong(boolean bool) {
		if (!isCanRead(8)) {
			return 0;
		}
		byte[] array = new byte[8];
		read(array, 0, array.length);
		return getLongByBytes(array, bool);
	}

	/**
	 * 用指定的高低位分布方式读取一个Int,true高位在前，false高位在后
	 * 
	 * @return Int
	 */
	public int readInt(boolean bool) {
		if (!isCanRead(4)) {
			return 0;
		}
		byte[] array = new byte[4];
		read(array, 0, array.length);
		return getIntByBytes(array, bool);
	}

	/**
	 * 用指定的高低位分布方式读取一个Short,true高位在前，false高位在后
	 * 
	 * @return Short
	 */
	public short readShort(boolean bool) {
		if (!isCanRead(2)) {
			return 0;
		}
		byte[] array = new byte[2];
		read(array, 0, array.length);
		return getShortByBytes(array, bool);
	}

	/**
	 * 读取一个用UTF-8以short为长度单位的字符串
	 */
	public String readUTFShort() {
		if (!isCanRead(2)) {
			return null;
		}
		short len = readShort();
		return readUTFData(len);
	}

	/**
	 * 读取一个用UTF-8以byte为长度单位的字符串
	 */
	public String readUTFByte() {
		if (!isCanRead(1)) {
			return null;
		}
		short len = (short) read();
		return readUTFData(len);
	}

	/**
	 * @Title: readUTFData
	 * @Description: 读取UTF字符串指定个数的字节数，然后转化为正确的DataOutputStream流读取数据
	 * @param len
	 * @return
	 * @version: 2011-9-8 下午03:42:35
	 */
	private String readUTFData(short len) {
		if (!isCanRead(len)) {
			return null;
		}
		if (len <= 0) {
			return null;
		}
		byte bits[] = new byte[len];
		read(bits, 0, bits.length);
		return getUTF8String(bits);
	}

	/**
	 * 将指定数量用UTF-8的字节转化为字符串
	 * 
	 * @param num
	 *            字节数
	 */
	public String readUTF(int len) {
		return readUTFData((short) len);
	}

	/**
	 * 将指定字节数组转化为字符串
	 * 
	 * @param array
	 *            byte[]
	 * @return enc 指定编码
	 */
	public static String getUTF8String(byte[] array) {
		if (array == null || array.length <= 0) {
			return null;
		}
		try {
			int bitLen = -1; // 字符串的实际长度
			for (int i = 0; i < array.length; i++) { // 字符串遇到0将自动结束
				if (array[i] == 0) {
					bitLen = i;
				}
			}
			if (bitLen == 0) {
				return null;
			} else if (bitLen == -1) {
				bitLen = array.length;
			}
			return new String(array, 0, bitLen, normalEnc).trim();
		} catch (Exception e) {
			return new String(array).trim();
		}
	}

	/**
	 * 读取一个byte用来表示boolean 1：true，0：false
	 * 
	 * @return
	 */
	public boolean readBoolean() {
		final byte b = readByte();
		return b == 1;
	}

	/**
	 * 将指定字节数组转化为long
	 * 
	 * @param array
	 *            byte[]
	 * @param bool
	 *            true高位在前，false高位在后
	 * @return long
	 */
	public static long getLongByBytes(byte[] array, boolean bool) {
		if (array == null /* || array.length != 8 */) {
			return -1;
		}
		return getDataByBytes(array, bool);
	}

	/**
	 * 将指定字节数组转化为int
	 * 
	 * @param array
	 *            byte[]
	 * @param bool
	 *            true高位在前，false高位在后
	 * @return int
	 */
	public static int getIntByBytes(byte[] array, boolean bool) {
		if (array == null /* || array.length != 4 */) {
			return -1;
		}
		return (int) getDataByBytes(array, bool);
	}

	/**
	 * 将指定字节数组转化为short
	 * 
	 * @param array
	 *            byte[]
	 * @param bool
	 *            true高位在前，false高位在后
	 * @return short
	 */
	public static short getShortByBytes(byte[] array, boolean bool) {
		if (array == null /* || array.length != 2 */) {
			return -1;
		}
		return (short) getDataByBytes(array, bool);
	}

	/**
	 * 将指定字节数组转化为long
	 * 
	 * @param array
	 *            byte[]
	 * @param bool
	 *            true高位在前，false高位在后
	 * @return long
	 */
	public static long getDataByBytes(byte[] array, boolean bool) {
		if (array == null) {
			return 0;
		}
		long tmp = 0;
		for (int i = 0; i < array.length; i++) {
			//
			tmp = (tmp | ((long) ((array[bool ? array.length - 1 - i : i]) & 0xff) << (i * 8))); // 其他字节转化
		}
		return tmp;
	}

	/**
	 * @Title: readBytes
	 * @Description: 读取此流指定起始位置和长度的字节
	 * @param dataStart
	 * @param dataLen
	 * @deprecated
	 * @return
	 * @version: 2011-5-18 上午09:58:55
	 */
	public byte[] readBytes(int dataStart, int dataLen) {
		if (!isCanRead(dataLen)) {
			return null;
		}
		if (dataLen < 0 || dataLen > buf.length || dataStart >= buf.length || dataStart < 0) {
			return null;
		}
		byte[] array = null;
		try {
			int tmpPos = this.pos;
			array = new byte[dataLen];
			this.pos = dataStart;
			read(array);
			this.pos = tmpPos; // 还原
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return array;
	}

	/**
	 * 当前位置读取指定长度
	 * @param dataLen
	 * @return
	 */
	public byte[] readBytes(int dataLen) {
		if (!isCanRead(dataLen)) {
			return null;
		}
		byte[] array = new byte[dataLen];
		try {
			read(array);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return array;
	}
	
	/**
	 * @Title: readBytes
	 * @Description: 读取流内的所有剩余字节
	 * @return
	 * @version: 2011-5-18 上午09:58:22
	 */
	public byte[] readBytes() {
		if (!isCanRead(1)) {
			return null;
		}
		byte[] array = new byte[available()];
		try {
			read(array);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return array;
	}

	/**
	 * 判断当前是否还可以继续读取
	 * 
	 * @return
	 */
	public boolean isCanRead(int len) {
		if (_curMark == null || !_curMark._isMarkData) { // 如果没有标记就不限制读取
			return true;
		}
		final long count = _curMark._markLen - getMarkBytes(_curMark._pos);
		return count >= len;
	}

	public class MDataMark {
		/** 当前锁定字节数 **/
		long _markLen = 0;

		/** 锁定起始位置 **/
		long _pos = 0;

		/** 上次锁定的剩余数据长度 **/
		long _frontMarkLen = 0;

		/** 是否锁定数据 **/
		boolean _isMarkData = false;

		/**
		 * @param fmark
		 */
		public void copy(MDataMark fmark) {
			if (fmark == null) {
				return;
			}
			_markLen = fmark._markLen;
			_pos = fmark._pos;
			_frontMarkLen = fmark._frontMarkLen;
			_isMarkData = fmark._isMarkData;
		}
	}

	/**
	 * @Title: markLen
	 * @Description: 
	 *               设置标记当前位置，并且限定从当前标记位开始，能够被读取的字节数，如果已经标记过，将会先调用unMark方法，然后重新标记
	 * @param len
	 *            从标记为开始能被读取的字节数
	 * @return
	 * @version: 2012-5-16 上午10:28:31
	 */
	public int markLen(final int len) {
		MDataMark mark = new MDataMark();
		mark._isMarkData = true;
		mark._markLen = len;
		mark._pos = pos;
		isMarkLen = true;
		markLen = len;
		// mark = pos;
		return pos;
	}

	private MDataMark _frontMark = null;

	private MDataMark _curMark = null; // 当前正在锁定的

	/**
	 * 此数据锁定方法目前只支持2层嵌套锁定，谨慎使用
	 * 
	 * @param len
	 * @return
	 */
	public MDataMark markData(final long len) {
		if (len <= 0) {
			return null;
		}
		MDataMark mark = new MDataMark();
		mark._isMarkData = true;
		mark._markLen = len;
		// markLen = len;
		mark._pos = pos;
		if (_frontMark != null && _frontMark._isMarkData) {
			final long skips = _frontMark._markLen - getMarkBytes(_frontMark._pos);
			mark._frontMarkLen = skips; // 上次记录点剩余的字节数
		}
		_curMark = mark;
		if (_frontMark == null) {
			_frontMark = mark;
		}
		return mark;
	}

	public long unMark(final MDataMark mark) {
		if (mark == null) {
			return 0;
		}
		final long skips = mark._markLen - getMarkBytes(mark._pos);
		final long skiped = skip(skips); // 跳过指定字节数
		if (mark._isMarkData) {
			mark._markLen = 0;
			mark._isMarkData = false;
			_curMark = null;
		} else {
			return 0;
		}
		if (_frontMark == mark) { // 相同对象
			_frontMark = null;
		} else {
			if (mark._frontMarkLen > 0) { // 上次有锁定数据
				MDataMark fmark = markData(mark._frontMarkLen - mark._markLen); // 重新锁定上次的剩余数据
				if (_frontMark != null) {
					_frontMark.copy(fmark); // 修改上次锁定数据对象
				}
			}
		}
		return skiped;
	}

	public long getMarkLen() {
		if (!isMarkLen) {
			return Integer.MAX_VALUE;
		}
		return markLen;
	}

	private boolean isMarkLen = false;

	private long markLen = 0;

	/**
	 * @Title: unMark
	 * @Description: 此方法和markLen方法对应使用，解除当前标记的读取字节数限制，如果字节数没有读取完就丢弃
	 * @return 返回实际跳过的字节数
	 * @version: 2012-5-16 上午10:35:15
	 */
	public long unMark(final long len, final long pos) {
		final long skips = len - getMarkBytes(pos);
		if (isMarkLen) {
			markLen = 0;
			isMarkLen = false;
			mark = 0;
		} else {
			return 0;
		}
		return skip(skips);
	}

	/**
	 * @Title: mark
	 * @deprecated 注，此方法可能引起错误
	 * @Description: 标记流的当前位置
	 * @return 流的当前位置
	 * @version: 2011-5-18 上午09:59:28
	 */
	public int mark() {
		mark = pos;
		return mark;
	}

	/**
	 * @Title: getMark
	 * @Description: 获得从标记位置开始到当前位置，已经读取了多少个字节
	 * @return
	 * @version: 2011-5-18 上午10:00:16
	 */
	public long getMarkBytes(final long p) {
		return pos - p;
	}

	/**
	 * @Title: getPos
	 * @Description: 获得流的当前位置索引
	 * @return
	 * @version: 2011-5-26 上午09:36:36
	 */
	public int getPos() {
		return pos;
	}

	/**
	 * @Title: getMark
	 * @Description:获得当前流已经标记的位置索引
	 * @return
	 * @version: 2011-5-26 上午09:36:53
	 */
	public int getMark() {
		return mark;
	}

	// }
}
