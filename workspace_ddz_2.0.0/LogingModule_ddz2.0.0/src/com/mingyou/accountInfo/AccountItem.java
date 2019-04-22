/**
 * 
 */
package com.mingyou.accountInfo;

import java.io.IOException;

import com.mingyou.accountInfoInterface.AccountInfoInterface;
import com.multilanguage.MultilanguageManager;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataInputStream.MDataMark;
import com.mykj.comm.io.TDataOutputStream;

/**
 * @author JasonWin8
 * 
 */
public class AccountItem implements AccountInfoInterface {

	// 字段
	/** 无效账号 **/
	public static final byte ACC_TYPE_NULL = 0; //

	/** 游客/临时账号 **/
	public static final byte ACC_TYPE_TEMP = 1; //

	// /** 移动账号 **/
	 public static final byte ACC_TYPE_CMCC = 2; //

	/** 普通账号 **/
	public static final byte ACC_TYPE_COMM = 3; //

	/** 第三方帐号 **/
	public static final byte ACC_TYPE_THIRD = 4; //

	/** 移动账号 **/
	// public static final String ACC_CMCC
	// =MultilanguageManager.getInstance().getValuesString("acc_cmcc_label");

	/** 游客/临时账号 **/
	public static final String ACC_TEMP = MultilanguageManager.getInstance()
			.getValuesString("acc_temp_label");

	/** 手机账号 **/
	public static final String ACC_PHONE = MultilanguageManager.getInstance()
			.getValuesString("acc_phone_label");

	/** 无需密码 **/
	public static final String NO_PASS = MultilanguageManager.getInstance()
			.getValuesString("noPwdLable");

	//

	public byte getType() {
		return _type;
	}

	public String getUsername() {
		return _username;
	}

	public String getPassword() {
		return _password;
	}

	public String getToken() {
		return _token;
	}

	public int getUserID() {
		return _accUserID;
	}

	
	
	public void setThirdAccName(String thirdAccName) {
		_thirdAccName = thirdAccName;
	}

	public String getThirdAccName() {
		return _thirdAccName;
	}
	
	
	public void setThirdAccUID(String thirdAccUID) {
		_thirdAccUID = thirdAccUID;
	}

	public String getThirdAccUID(){
		return _thirdAccUID;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AccountItem)) {
			return false;
		}
		AccountItem acc = (AccountItem) o;
		if (_type != acc._type) {
			return false;
		}
		if (_accUserID != 0 && _accUserID == acc._accUserID) {// userID相同即为同一个帐号
			return true;
		}
		if (_username != null && _username.equals(acc._username)) {
			return true;
		}
		return false;
	}

	protected boolean _isCopyAcc = false; // 标记是否是拷贝帐号

	protected int _accUserID = 0; // 用户ID，用来标识是否是同一个帐号

	protected byte _type = 0; // 账号类型,0:tat,1:at

	protected String _username; // 用户名64

	protected String _password; // 密码64

	protected String _token; // token串、AT/TAT 256

	protected byte _index = 0; // 该账号的排序号 1

	protected String _thirdAccName = null;
	
	protected String _thirdAccUID=null;

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("_accUserID=").append(_accUserID).append("\n");
		buf.append("_type=").append(_type).append("\n");
		buf.append("_username=").append(_username).append("\n");
		buf.append("_password=").append(_password).append("\n");
		buf.append("_token=").append(_token).append("\n");
		buf.append("_index=").append(_index).append("\n");
		return buf.toString();
	}

	public AccountItem(String name, String pass, String token, byte type,
			int userID) {
		this(name, pass, token, type, userID, null,null);
	}

	public AccountItem(String name, String pass, String token, byte type,
			int userID, String thirdAcc,String thirdUID) {
		_username = name;
		_password = pass;
		_token = token;
		_type = type;
		_accUserID = userID;
		_thirdAccName = thirdAcc;
		_thirdAccUID=thirdUID;
	}

	public AccountItem(TDataInputStream dis) {
		readFromStream(dis);
	}

	/**
	 * @param dis
	 * @param b
	 */
	// public AccountItem(TDataInputStream dis, boolean b) {
	// _type = (byte) dis.readInt(); // 账号类型
	// _username = dis.readUTF(64); // 用户名64
	// _password = dis.readUTF(64); // 密码64
	// if (_username != null) {
	// if (_type == ACC_TYPE_CMCC || _username.equals(ACC_CMCC)) { // 移动帐号
	// _type = ACC_TYPE_CMCC;
	// _password = NO_PASS;
	// } else if (_type == ACC_TYPE_TEMP || _username.equals(ACC_TEMP)) { //
	// 游客帐号
	// _type = ACC_TYPE_TEMP;
	// _password = NO_PASS;
	// } else {
	// _type = ACC_TYPE_COMM;
	// }
	// }
	// _token = dis.readUTF(256); // token串、AT/TAT 256
	// _index = dis.readByte(); // 该账号的排序号 1
	// dis.skip(3);
	// }

	/**
	 * 
	 */
	public AccountItem() {
	}

	/**
	 * @param lastAcc
	 */
	public AccountItem(AccountItem lastAcc) {
		if (lastAcc == null) {
			return;
		}
		_username = lastAcc._username;
		_password = lastAcc._password;
		_token = lastAcc._token;
		_type = lastAcc._type;
		_index = lastAcc._index;
		_accUserID = lastAcc._accUserID;
		_thirdAccName = lastAcc._thirdAccName;
		_thirdAccUID=lastAcc._thirdAccUID;
	}

	/**
	 * 帐号是否有效
	 * 
	 * @return
	 */
	public boolean isValid() {
		if (_type == ACC_TYPE_NULL) {
			return false;
		}
		if (_token != null && _token.length() != 0) {
			return true;
		}
		if ((_username == null || _username.length() == 0)
				|| (_password == null || _password.length() == 0)) {
			return false;
		}
		return true;
	}

	public void writeToStream(TDataOutputStream dos) {
		TDataOutputStream d = new TDataOutputStream();
		d.writeInt(_accUserID);
		d.writeByte(_type);
		d.writeUTFByte(_username);
		d.writeUTFByte(_password);
		d.writeUTFShort(_token);
		d.writeByte(_index);
		d.writeBoolean(_isCopyAcc);
		d.writeUTFByte(_thirdAccName);
		d.writeUTFByte(_thirdAccUID);
		final byte[] array = d.toByteArray();
		dos.writeInt(array.length); // 数据长度
		dos.write(array, 0, array.length);
		try {
			d.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// public void oldwriteToStream(TDataOutputStream dos) {
	// dos.writeInt(_type);// 注原始数据为4个字节
	// dos.writeUTF(_username, 64);
	// dos.writeUTF(_password, 64);
	// dos.writeUTF(_token, 256);
	// dos.writeByte(_index); // 注原始数据为4个字节
	// dos.writeBytes(0, 3);
	// }

	/**
	 * 判断是否有效token
	 * 
	 * @return
	 */
	public boolean isValidToken() {
		return _token != null && _token.length() != 0;
	}

	/**
	 * 判断是否有效帐号密码
	 * 
	 * @return
	 */
	public boolean isValidAccAndPass() {
		if (_type == ACC_TYPE_TEMP /* || _type == ACC_TYPE_CMCC */) {
			return false;
		}
		return _username != null && _username.length() != 0
				&& /* !_username.equals(ACC_CMCC) && */_password != null
				&& _password.length() != 0 && !_password.equals(NO_PASS);
	}

	public void readFromStream(TDataInputStream dis) {
		final int len = dis.readInt();
		// final int mark = dis.getMarkLen();
		// final int pos = dis.markLen(len);
		MDataMark mark = dis.markData(len);
		_accUserID = dis.readInt();
		_type = dis.readByte();
		_username = dis.readUTFByte();
		_password = dis.readUTFByte();
		if (_username != null) {
			/*
			 * if (_username.compareTo(ACC_CMCC) == 0) { // 移动帐号 _type =
			 * ACC_TYPE_CMCC; _password = NO_PASS; } else
			 */if (_username.compareTo(ACC_TEMP) == 0) { // 游客帐号
				_type = ACC_TYPE_TEMP;
				_password = NO_PASS;
			} else {
				_type = ACC_TYPE_COMM;
			}
		}
		_token = dis.readUTFShort();
		_index = dis.readByte();
		_isCopyAcc = dis.readBoolean();
		_thirdAccName = dis.readUTFByte();
		_thirdAccUID=dis.readUTFByte();
		dis.unMark(mark);
		// dis.unMark(len, pos);
		// dis.markLen(mark);
	}
}
