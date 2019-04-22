package com.mykj.andr.model;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataInputStream.MDataMark;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.UtilHelper;

public class ExchangeItem implements Serializable {
	private static final long serialVersionUID = 2052937845161903593L;	
	
	public short index = 0; 		 // 物品序列号索引
	public int itemId;
	public String itemName = null;   // 兑换物品名称
	public String photoName = null;  // 兑换物品图片名称 
	public String itemDesc = null;
	public int itemType = 0;		 // 兑换的物品类型  1=实物 2=话费 3=虚拟货币 4=道具
	public int remainNumber = 0;	 // 当前物品剩余数量, 负数表示当前数量充足
	public int showType ; 			 // 显示类型 1=new (new, hot, 推荐等..此字段只有在客户端版本大于等于2.0.0及视频炸金花才会下发)
	
	

	public static final byte EXCHANGE_GOODS = 1;		// 兑换物品为实物
	public static final byte EXCHANGE_TEL_FEE = 2;		// 兑换物品为话费
	public static final byte EXCHANGE_BEAN = 3;  		// 兑换物品为乐豆	
	public static final byte EXCHANGE_PROP	= 4;		// 兑换物品为道具
	
	public List<MaterialItem> materialItemList; // 兑换材料
	
	public ExchangeItem(TDataInputStream dis){
		if(dis == null){
			return;
		}
		
		materialItemList = new ArrayList<MaterialItem>();
		final int len = dis.readShort();
		MDataMark mark = dis.markData(len);
		String data = dis.readUTF(len);
		
		if (!"".equals(data) && null != data) {
			
			String ix = UtilHelper.parseAttributeByName("ix", data);
			if(UtilHelper.isNumeric(ix)){
				index = Short.parseShort(ix);				
			}
			
			String ai = UtilHelper.parseAttributeByName("ai", data);
			if(UtilHelper.isNumeric(ai)){
				itemId = Integer.parseInt(ai);				
			}

			itemName = UtilHelper.parseAttributeByName("m", data);
			photoName = UtilHelper.parseAttributeByName("l", data);
			itemDesc = UtilHelper.parseAttributeByName("d", data);
			String st = UtilHelper.parseAttributeByName("st", data);
			if(UtilHelper.isNumeric(st)){
				showType = Integer.parseInt(st);				
			}
			String t = UtilHelper.parseAttributeByName("t", data);
			if(UtilHelper.isNumeric(t)){
				itemType = Integer.parseInt(t);
			}
			String c = UtilHelper.parseAttributeByName("c", data);
			if(UtilHelper.isNumeric(c)){
				remainNumber = Integer.parseInt(c);
			}
			parseStatusXml(data, "s", this);
		}
		dis.unMark(mark);		
	}
	
	public ExchangeItem(){
		
	}
	
	public ExchangeItem(byte[] array){
		this(new TDataInputStream(array));
	}
	
	public void addMaterialItem(MaterialItem item) {
		materialItemList.add(item);
	}
	
	public static boolean parseStatusXml(String strXml, String tagName, ExchangeItem item) {
		if(Util.isEmptyStr(strXml)){
			return false;
		}
		// boolean isParseSuccess = false;
		boolean isParseSuccess = false;
		try {
			// 定义工厂
			XmlPullParserFactory f = XmlPullParserFactory.newInstance();
			// 定义解析器
			XmlPullParser p = f.newPullParser();
			// 获取xml输入数据
			p.setInput(new StringReader(strXml));
			// 解析事件
			int eventType = p.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					String tag = p.getName();
					if (tag.equals(tagName)) {
						MaterialItem materialItem = new MaterialItem();
						String i = p.getAttributeValue(null, "i");
						if(UtilHelper.isNumeric(i)){
							materialItem.setMaterialId(Integer.parseInt(i));							
						}
						materialItem.setMaterialName(p.getAttributeValue(null, "m"));
						String t = p.getAttributeValue(null, "t");
						if(UtilHelper.isNumeric(t)){
							materialItem.setMaterialType(Integer.parseInt(t));							
						}
						String n = p.getAttributeValue(null, "n");
						if(UtilHelper.isNumeric(n)){
							materialItem.setNumber(Integer.parseInt(n));						
						}
						String tp = p.getAttributeValue(null, "tp");
						if(UtilHelper.isNumeric(tp)){
							materialItem.setType(Integer.parseInt(tp));							
						}
						String vl = p.getAttributeValue(null, "vl");
						if(UtilHelper.isNumeric(vl)){
							materialItem.setValue(Integer.parseInt(vl));							
						}

						item.addMaterialItem(materialItem);
					}
					isParseSuccess = true;
					break;
				case XmlPullParser.END_TAG:
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				default:
					break;
				}
				// 用next方法处理下一个事件，否则会造成死循环。
				eventType = p.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			isParseSuccess = false;
		}
		return isParseSuccess;
	}
	
}
