/**
 * @Title: MSocketCommand.java
 * @Package com.myou.mjava.mNet
 * @Description: socket协议结构
 * @author fwq
 * @date 2011-5-9 下午01:50:06
 * @version V1.0
 * @sine 
 * Copyright 名游网络手机部  Corporation 2011
 * 版权所有
 * 
 */
package com.mingyou.distributor;

import java.io.IOException;

import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataOutputStream;

/**
 * 网络数据封包对象
 * 
 * @ClassName: MSocketCommand
 * @Description: socket协议结构
 * @author fwq
 * @date 2011-5-9 下午01:50:06
 * 
 */
public class NetSocketPak {
	/** 数据包头长度 12 */
	public static final byte HEADSIZE = 12;

	/** 有效回执号的长度 4 */
	public static final byte answerSize = 4;

	/** 版本标示 1个字节 */
	public static final byte version = 65;

	// --------------------------------------------------------
	/** 包数据缓存区 */
	private byte[] bufferByte;

	/** 数据大小 2个字节 */
	private short dataSize;

	/** 校验字段 1个字节 */
	private byte validata;

	/** 主命令码 2个字节 */
	private short mdm_gr;

	/** 子命令码 2个字节 */
	private short sub_gr;

	/** 记录数据 */
	private TDataInputStream dataInputStream;

	/** 记录数据 */
	private TDataOutputStream dataOutputStream;

	/** 记录数据 */
	private byte[] data;

	/** 回执号 4个字节 */
	private int answer = 0;

	/** 无效的回执号 */
	public static final int ANSWER_ERROR = 0;

	public NetSocketPak() {

	}

	/**
	 * 用指定的headByte头信息，和一个输入流构造一个可以发送和由上层处理的网络数据封包
	 * 
	 * @param headByte
	 *            头信息
	 * @param inputStream
	 *            输入流
	 */
	public NetSocketPak(byte[] headByte, TDataInputStream inputStream) {
		setSocketData(headByte, inputStream);
	}

	/**
	 * 设置此对象的headByte头信息，和一个输入流构造一个可以发送和由上层处理的网络数据封包
	 * 
	 * @param headByte
	 *            头信息
	 * @param inputStream
	 *            输入流
	 */
	public void setSocketData(byte[] headByte, TDataInputStream inputStream) {
		if (headByte == null) {
			mdm_gr = sub_gr = -1;
			return;
		}
		TDataInputStream dis = new TDataInputStream(headByte, false);
		dataSize = dis.readShort();
		validata = dis.readByte();
		dis.readByte(); // 版本标识
		dis.readInt(); // 扩展
		mdm_gr = dis.readShort();
		sub_gr = dis.readShort();
		try {
			dis.close();
			dis = null;
		} catch (IOException e) {
		}
		// 记录数据
		dataInputStream = inputStream;
		dataOutputStream = null;
		data = null;
		bufferByte = null;
	}

	/**
	 * 构造一个由主，子命令和一个换存流组装的数据封包，多用来网络协议发送
	 * 
	 * @param _mdm_gr
	 *            主命令码
	 * @param _sub_gr
	 *            子命令码
	 * @param outputStream
	 *            数据
	 */
	public NetSocketPak(short _mdm_gr, short _sub_gr, TDataOutputStream outputStream) {
		mdm_gr = _mdm_gr;
		sub_gr = _sub_gr;
		int datalen = 0;
		if (outputStream != null) {
			datalen = outputStream.toByteArray().length;
		}
		dataOutputStream = outputStream;
		dataInputStream = null;
		data = null;
		// 计算数据大小
		dataSize = (short) (HEADSIZE + datalen);
		bufferByte = null;
		answer = ANSWER_ERROR;
	}

	/**
	 * 构造一个由主，子命令和一个换存字节数组组装的数据封包，多用来网络协议发送
	 * 
	 * @param _mdm_gr
	 *            主命令码
	 * @param _sub_gr
	 *            子命令码
	 * @param _data
	 *            数据
	 */
	public NetSocketPak(short _mdm_gr, short _sub_gr, byte[] _data) {
		mdm_gr = _mdm_gr;
		sub_gr = _sub_gr;
		int datalen = 0;
		if (_data != null) {
			datalen = _data.length;
		}
		data = _data;
		dataOutputStream = null;
		dataInputStream = null;
		// 计算数据大小
		dataSize = (short) (HEADSIZE + datalen);
		bufferByte = null;
		answer = ANSWER_ERROR;
	}

	/**
	 * 此构造用于空协议发送，只有主，子命令码构造的包头信息，没有任何后续数据
	 * 
	 * @param _mdm_gr
	 * @param _sub_gr
	 */
	public NetSocketPak(short _mdm_gr, short _sub_gr) {
		mdm_gr = _mdm_gr;
		sub_gr = _sub_gr;
		int datalen = 0;
		data = null;
		dataOutputStream = null;
		dataInputStream = null;
		// 计算数据大小
		dataSize = (short) (HEADSIZE + datalen);
		bufferByte = null;
		answer = ANSWER_ERROR;
	}

	/**
	 * @Title: allData
	 * @Description: 获得包括协议头的所有数据
	 * @return
	 * @version: 2011-5-10 下午12:17:42
	 */
	private byte[] getAllData() {
		if (dataSize < HEADSIZE) {
			return new byte[0];
		}
		// 组合数据包
		byte[] bufferByte = new byte[dataSize - HEADSIZE];
		if (dataSize >= HEADSIZE) {
			if (dataOutputStream != null) {
				final byte[] buf = dataOutputStream.toByteArray();
				System.arraycopy(buf, 0, bufferByte, 0, buf.length);
				dataOutputStream.reset();
			} else if (data != null) {
				System.arraycopy(data, 0, bufferByte, 0, bufferByte.length);
			} else if (dataInputStream != null) {
				final byte[] buf = dataInputStream.readBytes();
				System.arraycopy(buf, 0, bufferByte, 0, buf.length);
				dataInputStream.reset();
			}
			// 组装包头
			TDataOutputStream dos = new TDataOutputStream(false); // 还原完整的数据包
			dos.writeShort(dataSize);
			dos.writeByte(validata);
			dos.writeByte(version);
			dos.writeInt(0);
			dos.writeShort(mdm_gr);
			dos.writeShort(sub_gr);
			dos.write(bufferByte, 0, bufferByte.length);
			bufferByte = dos.toByteArray(); // 包头+数据体
			try {
				dos.close();
				dos = null;
			} catch (IOException e) {
			}
		}
		return bufferByte;
	}

	// public NetSocketPak(String url) {
	// if (url == null) {
	// return;
	// }
	// bufferByte = url.getBytes();
	// }

	// get set-----------------

	/**
	 * 获得完整封包数据(包头+数据体)以字节数组的形式
	 * 
	 * @return
	 */
	public byte[] getBufferByte() {
		if (bufferByte == null) {
			bufferByte = getAllData();
		}
		return bufferByte;
	}

	/**
	 * @Title: getDataSize
	 * @Description: 获得数据的总长度--包括协议头
	 * @return
	 * @version: 2011-5-16 下午02:03:56
	 */
	public short getAllDataSize() {
		return dataSize;
	}

	/**
	 * @Title: getDataSize
	 * @Description: 获得总有效数据的长度--不包括协议头
	 * @return
	 * @version: 2011-5-16 下午02:05:31
	 */
	public short getDataSize() {
		return (short) (dataSize - HEADSIZE);
	}

	/**
	 * 获得此数据包的校验字段
	 * 
	 * @return
	 */
	public byte getValidata() {
		return validata;
	}

	/**
	 * 设置此数据包的校验字段
	 * 
	 * @param validata
	 */
	public void setValidata(byte validata) {
		this.validata = validata;
	}

	/**
	 * 获得此数据包的版本号
	 * 
	 * @return
	 */
	public byte getVersion() {
		return version;
	}

	/**
	 * @Title: getMdm_gr
	 * @Description: 获得主命令码
	 * @return
	 * @version: 2011-5-16 下午02:01:47
	 */
	public short getMdm_gr() {
		return mdm_gr;
	}

	/**
	 * @Title: getSub_gr
	 * @Description: 获得子命令码
	 * @return
	 * @version: 2011-5-16 下午02:02:01
	 */
	public short getSub_gr() {
		return sub_gr;
	}

	/**
	 * 获得此数据包的回执号
	 * 
	 * @return
	 */
	public int getAnswer() {
		return answer;
	}

	/**
	 * 获得此数据包的回执号
	 * 
	 * @param answer
	 */
	public void setAnswer(int answer) {
		this.answer = answer;
	}

	/**
	 * 获得输入数据流--多用于接收到网络数据后解析时
	 * 
	 * @return the dataInputStream
	 */
	public TDataInputStream getDataInputStream() {
		return dataInputStream;
	}

	/**
	 * @Title: free
	 * @Description: 清理所有已缓存的数据
	 * @version: 2011-5-10 下午06:12:40
	 */
	public void free() {
		mdm_gr = -1;
		sub_gr = -1;
		bufferByte = null;
		data = null;
		if (dataInputStream != null) {
			try {
				dataInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			dataInputStream = null;
		}
		if (dataOutputStream != null) {
			try {
				dataOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			dataOutputStream = null;
		}
	}

	/**
	 * 已覆盖实现，输出流的包头等关键信息
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("主命令码：").append(mdm_gr);
		sb.append("  子命令码：").append(sub_gr);
		sb.append("  数据长度：").append(dataSize - HEADSIZE);
		return sb.toString();
	}
	
	private int reCutFlag = NetPackConstants.INVALIDRECUTFLAG;
	
	public int getReCutFlag(){
		return reCutFlag;
	}
	
	public void setReCutFlag(int flag){
		reCutFlag = flag;
	}
}
