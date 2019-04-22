package com.mykj.andr.model;


import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataInputStream.MDataMark;

public class HPropData {
	/**道具ID*/
	public int PropID;
	/**道具名称*/
	public String PropName;
	/**道具描述*/
	public String PropDesc; 
	/**道具是否可用*/
	public boolean canUse = false;

	public HPropData(TDataInputStream tdis) {
		if (tdis == null) {
			return;
		}
		final int len=tdis.readShort();
		MDataMark mark=tdis.markData(len);
		PropID = tdis.readInt();
		PropName = tdis.readUTFShort();
		PropDesc = tdis.readUTFShort();
		int mCanUse = tdis.readByte();
		tdis.unMark(mark);
		if(mCanUse == 1){
			canUse = true;
		}else{
			canUse = false;
		}
	}
}
