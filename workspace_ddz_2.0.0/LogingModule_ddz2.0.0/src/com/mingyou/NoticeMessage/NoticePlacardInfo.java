/**
 * 
 */
package com.mingyou.NoticeMessage;

import java.io.Serializable;

import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataInputStream.MDataMark;
import com.mykj.comm.io.TDataOutputStream;

/**
 * @author Jason
 * 
 */
public class NoticePlacardInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 公告到期时间 **/
	public String limitTime = null;

	/** 消息内容 **/
	public String _msg = null;

	public byte fontSize = 0;

	public int fontColor = 0;

	public NoticePlacardInfo(TDataInputStream dis) {
		if (dis == null) {
			return;
		}
		final int dataLen = dis.readShort(); // 此数据块长度
		MDataMark mark = dis.markData(dataLen);
		limitTime = dis.readUTFByte();
		_msg = dis.readUTFShort();

		fontSize = dis.readByte();
		fontColor = dis.readInt();
		dis.unMark(mark);
	}

	public NoticePlacardInfo(byte[] array) {
		this(new TDataInputStream(array));
	}

	/**
	 * @param msg2
	 */
	public NoticePlacardInfo(String msg) {
		_msg = msg;
	}

	public TDataOutputStream getStream() {
		TDataOutputStream dos = new TDataOutputStream();
		dos.writeUTFByte(limitTime);
		dos.writeUTFShort(_msg);
		return dos;
	}

}
