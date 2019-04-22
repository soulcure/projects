package com.applidium.nickelodeon.pay.alipay;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;


public class Result {
	
	private static final Map<String, String> sResultStatus;

	private String mData; // 支付宝对订单请求处理的结果数据
	
	String resultStatus = null;
	String memo = null;
	String result = null;
	boolean isSignOk = false;
	JSONObject resultJson = null;
	
	static {
		sResultStatus = new HashMap<String, String>();
		sResultStatus.put("9000", "支付成功");
		sResultStatus.put("8000", "正在处理中");
		sResultStatus.put("4000", "支付失败");
		sResultStatus.put("4001", "数据格式不正确");
		sResultStatus.put("4003", "该用户绑定的支付宝帐号被冻结或不允许支付");
		sResultStatus.put("4004", "该用户已经解除绑定");
		sResultStatus.put("4005", "绑定失败或没有绑定");
		sResultStatus.put("4006", "订单支付失败");
		sResultStatus.put("4010", "重新绑定帐号");
		sResultStatus.put("6000", "支付服务正在进行升级操作");
		sResultStatus.put("6001", "取消支付操作");
		sResultStatus.put("6002", "网络连接异常");
		sResultStatus.put("7001", "网页支付失败");
	}

	public Result(String data) {
		this.mData = data;
		this.parseResult();
	}

	public static void main(String args[]) {
		String data = "resultStatus={9000};memo={};result={partner=\"2088101568358171\"&seller_id=\"alipay-test09@alipay.com\"&out_trade_no=\"0819145412-6177\"&subject=\"《暗黑破坏神 3: 凯恩之书》 \"&body=\" 暴雪唯一官方授权中文版! 玩家必藏! 附赠暗黑精致手绘地图! 绝不仅仅是 一 本 暗 黑 的 故 事 或 画 册 ， 而 是 一 个 栩 栩 如 生 的 游 戏 再 现 。 是 游 戏 玩 家 珍 藏 的 首 选 。\"&total_fee=\"0.01\"&notify_url=\"http%3A%2F%2Fnotify.msp.hk%2Fnotify.htm\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&show_url=\"m.alipay.com\"&success=\"true\"&sign_type=\"RSA\"&sign=\"hkFZr+zE9499nuqDNLZEF7W75RFFPsly876QuRSeN8WMaUgcdR00IKy5ZyBJ4eldhoJ/2zghqrD4E2G2mNjs3aE+HCLiBXrPDNdLKCZgSOIqmv46TfPTEqopYfhs+o5fZzXxt34fwdrzN4mX6S13cr3UwmEV4L3Ffir/02RBVtU=\"}";
		//data = "resultStatus={6001};memo={操作已经取消。};result={}";
		Result result = new Result(data);
		System.out.println("resultStatus="+ result.getResultStatus());
		System.out.println("resultStatusText="+ result.getResultStatusText());
		System.out.println("memo="+ result.getMemo());
		System.out.println("result="+ result.getResult());
		System.out.println("isSignOk="+ result.isSignOk());
		System.out.println("outTradeNo="+ result.getOutTradeNo());
		System.out.println("totalFee="+ result.getTotalFee());
	}
	
	public String getResultStatus() {
		return resultStatus;
	}

	public String getMemo() {
		return memo;
	}
	
	public String getResult() {
		return result;
	}
	
	public String getResultStatusText() {
		if (sResultStatus.containsKey(resultStatus)) {
			return sResultStatus.get(resultStatus);
		} else {
			return "未知错误"+ "(" + resultStatus + ")";
		}
	}

	public boolean isSignOk() {
		return isSignOk;
	}
	
	public String getOutTradeNo() {
		try {
			return resultJson.getString("out_trade_no");
		} catch (JSONException e) {
			//e.printStackTrace();
		}
		return null;
	}
	
	public Double getTotalFee() {
		try {
			return resultJson.getDouble("total_fee");
		} catch (JSONException e) {
			//e.printStackTrace();
		}
		return null;
	}
	
	public void parseResult() {
		try {
			String src = mData.replace("{", "").replace("}", "");
			
			resultStatus = getContent(src, "resultStatus=", ";memo");
			memo = getContent(src, "memo=", ";result");
			result = getContent(src, "result=", null);
			
			if (result != null && !result.isEmpty())
				isSignOk = checkSign(result);
			// result name/value pair
			resultJson = string2JSON(result.replace("\"", ""), "&");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JSONObject string2JSON(String src, String split) {
		JSONObject json = new JSONObject();
//		resultStatus={6001};memo={操作已经取消。};result={}
//		java.lang.StringIndexOutOfBoundsException: length=0; index=1
//			at java.lang.String.indexAndLength(String.java:579)
//			at java.lang.String.substring(String.java:1438)
//			at com.alipay.android.msp.demo.Result.string2JSON(Result.java:121)
//			at com.alipay.android.msp.demo.Result.checkSign(Result.java:133)
//			at com.alipay.android.msp.demo.Result.parseResult(Result.java:106)
//			at com.alipay.android.msp.demo.Result.<init>(Result.java:41)
		if (src != null && !src.isEmpty()) {
			try {
				String[] arr = src.split(split);
				for (int i = 0; i < arr.length; i++) {
					String[] arrKey = arr[i].split("=");
					json.put(arrKey[0], arr[i].substring(arrKey[0].length() + 1));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return json;
	}

	private boolean checkSign(String result) {
		boolean retVal = false;
		try {
			JSONObject json = string2JSON(result, "&");

			int pos = result.indexOf("&sign_type=");
			String signContent = result.substring(0, pos);

			String signType = json.getString("sign_type");
			signType = signType.replace("\"", "");

			String sign = json.getString("sign");
			sign = sign.replace("\"", "");

			if (signType.equalsIgnoreCase("RSA")) {
				retVal = Rsa.doCheck(signContent, sign, Keys.PUBLIC);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}

	private String getContent(String src, String startTag, String endTag) {
		String content = src;
		int start = src.indexOf(startTag);
		start += startTag.length();

		try {
			if (endTag != null) {
				int end = src.indexOf(endTag);
				content = src.substring(start, end);
			} else {
				content = src.substring(start);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return content;
	}
}
