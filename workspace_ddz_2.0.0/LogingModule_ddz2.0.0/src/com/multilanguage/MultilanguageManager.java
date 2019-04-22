package com.multilanguage;

import java.lang.reflect.Field;

import android.util.Log;

import com.mingyou.community.Community;

public class MultilanguageManager {
	private static final String TAG = "MultianguageManager";

	private static final String language[] = { "", "", "", "", "",
			"lanZH_CN"/* 简体中文 */, "lanZN_TW.lan"/* 繁体中文 */, "lanUS_EN.lan"/* 英文 */,
			"lanDE_DE.lan"/* 德语 */, "lanPT_PT.lan"/* 葡萄牙语 */,
			"lanCA_ES.lan"/* 西班牙语 */, "lanID_ID.lan"/* 印尼语 */,
			"lanTH_TH.lan"/* 泰语 */, "lanVI_VN.lan"/* 越南语 */};

	private static MultilanguageManager _instance = null;

	public static final MultilanguageManager getInstance() {
		if (_instance == null) {
			_instance = new MultilanguageManager();
		}
		return _instance;
	}

	//
	private Class _classObject = null;

	private MultilanguageManager() {
		initValues(language[5]);   //只支持简体中文
//		final int languageType = Community.getCurLanguage();
//		if (languageType >= 0 && languageType < language.length) {
//			initValues(language[languageType]);
//		} else {
//			Log.e(TAG, "languageType=" + languageType
//					+ ",languageFile is no exists");
//		}
	}

	private void initValues(String className) {
		try {
			//com.multilanguage.lanzh_cn
			_classObject = Class.forName("com.multilanguage."+className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String getValuesString(String strkey) {
		if (_classObject == null) {
			return null;
		}
		String value = null;
		try {
			Field field = _classObject.getField(strkey);
			value = (String) field.get(_classObject);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return value.trim();
	}

	// public int getValuesInt(String strkey) {
	// return 0;
	// }
}
