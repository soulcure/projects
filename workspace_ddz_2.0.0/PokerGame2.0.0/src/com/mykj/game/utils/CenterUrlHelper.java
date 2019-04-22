package com.mykj.game.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;

import com.mykj.game.FiexedViewHelper;


public class CenterUrlHelper {

    /** 推广分红apikey */
    public static final String apikey = "2e635029df7f4092a6208b6000f38a7e";
    /** 推广分红secret */
    public static final String secret = "1a697f1983934616a881941be4b338af";
    /** 短信购买校验secret */
    public static final String smsSecret = "MYTL!@#14yhl9tka";
    
    /**上传头像apikey*/
    public static final String API_KEY="vi15xbx2";
    public static final String UP_SECRET="25v3qyb34nlbywpn";
	
    public static String getWapUrl(int urlID) {
		return  AppConfig.HOST+"/redir.php?urlid=" + urlID + "&gameid=" + AppConfig.gameId+"&";
	}

	public static String getUrl(String mainUrl, int userID) {
        String token = FiexedViewHelper.getInstance().getUserToken();
		if (!Util.isEmptyStr(token)) {
			try {
				token= URLEncoder.encode(token,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				token="";
			}
		}
		
		final String key = "87299596b8d9d642922a9a659aa70723";
		long time = System.currentTimeMillis(); // 当前系统时间
		String str = "uid=" + userID + "&ts=" + time + "&key=" + key;
		String url = mainUrl + "uid=" + userID + "&ts=" + time
				+"&cid="+AppConfig.CID+"&token="+token
				+ "&verifystring=" + md5(str);
		return url;
	}

	
	

	
	/**
	 * 获取头像上传参数
	 * @return
	 */
	public static String getUploadParam(){
		String res=null;
		long time = System.currentTimeMillis(); // 当前系统时间
		String sign=getUploadSign(time);
		HashMap<String,String> map=new HashMap<String,String>();
		String token=FiexedViewHelper.getInstance().getUserToken();

		map.put("apikey", API_KEY);
		map.put("op",""+time);
		map.put("sign",sign);
		map.put("tk", token);
		
		String json=Util.getJSONObject(map);
		

		
		try {
			res=URLEncoder.encode(json,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			res="";
		}
		return res;
	}
	
	
	/**
	 * 获取头像上传签名
	 * @return
	 */
	private static String getUploadSign(long time){
		StringBuilder sb=new StringBuilder();
		sb.append("apikey").append('=').append(API_KEY);
		sb.append("gameid").append('=').append(AppConfig.gameId);
		sb.append("op").append('=').append(time);
		sb.append("platid").append('=').append(AppConfig.plat_id);
		
		String token=FiexedViewHelper.getInstance().getUserToken();
		sb.append("tk").append('=').append(token);
		sb.append("secret").append('=').append(UP_SECRET);	
		String noSign=sb.toString();
		
		return md5(noSign);
	}
	
	
    /**
     * 推广分红有使用
     * 采用MD5算法的校验串
     */
	public static String getSign(String params, String secret) {
        // 去除"&"
        String[] s_A = params.split("&");
        // 排序
        Arrays.sort(s_A);
        // 组合
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s_A.length; i++) {
            sb.append(s_A[i]);
        }
        // 加上密钥
        sb.append("secret=").append(secret);
        // 生成参数串
        StringBuffer result_sb = new StringBuffer();
        result_sb.append("&sign=");
        result_sb.append(md5(sb.toString()));
        return result_sb.toString();
    }

	public static String md5(String string) {
		if (string == null || string.trim().length() < 1) {
			return null;
		}
		try {
			return getMD5(string.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private static String getMD5(byte[] source) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			StringBuffer result = new StringBuffer();
			for (byte b : md5.digest(source)) {
				result.append(Integer.toHexString((b & 0xf0) >>> 4));
				result.append(Integer.toHexString(b & 0x0f));
			}
			return result.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
