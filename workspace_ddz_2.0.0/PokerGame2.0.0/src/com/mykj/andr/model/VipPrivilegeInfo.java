package com.mykj.andr.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mykj.comm.io.TDataInputStream;
/**
 * 我的vip信息
 * @author Administrator
 *
 */
public class VipPrivilegeInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public byte vipLvl;
	public int endTime;
	public String endTimeStr;
	public ArrayList<ArrayList<String>> effList = new ArrayList<ArrayList<String>>();
	public ArrayList<VipInfo> vipList = new ArrayList<VipInfo>();
	synchronized public void init(TDataInputStream tdis){
		effList.clear();
		vipList.clear();
		vipLvl = tdis.readByte();
		
		endTime = tdis.readInt();
		setExpTime(endTime);
		int EffectSum = tdis.readByte();
		ArrayList<String> effInfoList = new ArrayList<String>();
		
		//权限名字
		for(int i = 0; i < EffectSum;i++){
			String Effect = tdis.readUTFByte();
			effInfoList.add(Effect);
		}
		
		int VIPSum = tdis.readByte();
		
		//每个级别的权限配置
		for(int i = 0; i < VIPSum; i++){
			VipInfo vipInfo = new VipInfo();
			vipInfo.init(tdis);
			vipList.add(vipInfo);
		}
		boolean needPer = false;  //是否需要显示%在后面
		for(int i = 0; i < effInfoList.size(); i++){
			ArrayList<String> subList = new ArrayList<String>();
			subList.add(effInfoList.get(i));
			if(effInfoList.get(i).contains("加成")){
				needPer = true;
			}else{
				needPer = false;
			}
			for(int j = 0; j < VIPSum; j++){
				VipInfo vip = vipList.get(j);
				if(i < vip.infoEffList.size()){
					subList.add(vip.infoEffList.get(i)+(needPer?"%":""));
				}else{
					subList.add("—");
				}
			}
			effList.add(subList);
		}
	}
	/**
	 * @param endTime 单位秒
	 */
	public void setExpTime(int endTime){
		try{
			Date d = new Date(endTime*1000l);
			SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
			endTimeStr = sf.format(d);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public class VipInfo{
		public boolean enable;
		public short cost;
		public int propId;
		ArrayList<String> infoEffList = new ArrayList<String>();
		public void init(TDataInputStream tdis){
			byte enble = tdis.readByte();
			enable = (enble == 1);
			String imgName = tdis.readUTFByte();
			int effectSum = tdis.readByte();
			for(int i = 0; i < effectSum;i++){
				int val = tdis.readInt();
				if(val == 0){
					infoEffList.add("—");
				}else if(val == 1){
					infoEffList.add("√");
				}else{
					if(val < 10000){
						infoEffList.add(""+val);
					}else{
						int zheng = val / 10000;
						int fen = val % 10000;
						if(fen / 1000 != 0){
							infoEffList.add(zheng+"."+fen+"万");
						}else{
							infoEffList.add(zheng+"万");
						}
					}
				}
			}
			cost = tdis.readShort();
			propId = tdis.readInt();
		}
	}
	public String getVipName(){
		String vipName = "";
		if(vipLvl > 0){
			switch (vipLvl) {
			case 1:
				vipName = "会员";
				break;
			case 2:
				vipName = "男爵";
				break;
			case 3:
				vipName = "子爵";
				break;
			case 4:
				vipName = "伯爵";
				break;
			case 5:
				vipName = "侯爵";
				break;
			case 6:
				vipName = "公爵";
				break;
			default:
				break;
			}
		}
		return vipName;
	}
}
