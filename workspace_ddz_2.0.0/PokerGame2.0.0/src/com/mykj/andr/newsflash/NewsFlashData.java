package com.mykj.andr.newsflash;

/**
 * @author wanghj
 *
 */
public class NewsFlashData{
	int type;             //类型，参考NewsFlashManager的operate
	String content;       //内容
	String paramEx;       //附加参数，根据type而类型不同
	String btnText;       //点击文字
	String subTitle;      //每个的标题
	private boolean expanded = false;
	public boolean isExpanded(){
		return expanded;
	}
	
	public void setExpand(boolean isExpand){
		expanded = isExpand;
	}
	
}