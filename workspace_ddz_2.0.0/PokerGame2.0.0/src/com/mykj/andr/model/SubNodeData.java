package com.mykj.andr.model;

import java.io.Serializable;

import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataInputStream.MDataMark;





public class SubNodeData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int ID; // 节点标识
	private String Name;
	private int Persons;//在线人数
	private String ExName;  //副标题
	
	private String[] SubAreaNodeTips;
	
	
	public SubNodeData(){}

	public SubNodeData(byte[] array){
		this(new TDataInputStream(array));
	}

	public SubNodeData(TDataInputStream dis){
		if (dis == null) {
			return;
		}

		dis.setFront(false);
		final int len=dis.readShort();
		MDataMark mark=dis.markData(len);

		ID = dis.readInt(); // 节点标识
		byte NameLen= dis.readByte();//名称长度
		Name=dis.readUTF(NameLen); //房间名称
		Persons = dis.readInt(); // 游戏人数
		
		byte ExNameLen=dis.readByte();
		ExName=dis.readUTF(ExNameLen);
		

		byte SubAreaNodeTipCount=dis.readByte();  //分区提示个数
		if (SubAreaNodeTipCount > 0) {
			SubAreaNodeTips = new String[SubAreaNodeTipCount];
			for (int i = 0; i < SubAreaNodeTipCount; i++) {
				byte tipLen = dis.readByte();
				SubAreaNodeTips[i] =  dis.readUTF(tipLen);
			}
		}
		
		
		dis.unMark(mark);
	}

	
	
	
	public int getID() {
		return ID;
	}




	public String getName() {
		return Name;
	}



	public int getPersons() {
		return Persons;
	}

	
	public String getExName() {
		return ExName;
	}

	public String[] getSubAreaNodeTips() {
		return SubAreaNodeTips;
	}

	
	
	
	
	
}
