package com.mykj.game;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;

import android.app.Activity;

import com.mykj.andr.pay.payment.UnipayPayment;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.Log;
import com.mykj.game.utils.Util;
import com.skymobi.pay.app.PayApplication;

/***
 * 当前接入斯凯的支付方式，所以继承自PayApplication，否则继承自Application
 * @ClassName: MainApplication
 * @Description: 应用程序全局类，相当于cocos2d-x的AppDelegate,或者IOS的CCApplication
 * @date 2013-4-25 下午03:39:00
 *
 */
public class MainApplication extends PayApplication{

	private static final String TAG="MainApplication";
	private static MainApplication instance;
	public static final String DIR = Util.getSdcardPath() +AppConfig.DOWNLOAD_FOLDER+ "/ExceptionLog/";
	public static final String NAME = getCurrentDateString() + ".txt";

	private static Stack<Activity> mActivityStack;
	
	// 斯凯支付Application
	private PayApplication payApplication = new PayApplication();

	@Override
	public void onCreate() {
		AppConfig.initCmccSwitch(this);

		if(AppConfig.isOpenPayByCmccSdk()){
			System.loadLibrary("megjb");  //移动支付SDK SO
		}
		initCococsLib();
		instance=this;
		super.onCreate();
		
		// 斯凯支付Application初始化
		payApplication.applicationOnCreat(this);
		
		if(!AppConfig.debug){ //非debug模式下把崩溃记录到文件，否则用Logcat看
			Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
		}
		UnipayPayment.getInstance(this).initPayment();
	}


	public static MainApplication sharedApplication(){
		return instance;
	}

	/**
	 * 加载游戏so库
	 */
	private void initCococsLib() {
		try {
			// 下面是加载库文件
			System.loadLibrary("cocosdenshion");
			switch (AppConfig.gameId) {
			case AppConfig.GAMEID_DDZ:
				System.loadLibrary("ddz");
				break;
			case AppConfig.GAMEID_WQ:
				System.loadLibrary("twq");
				break;
			case AppConfig.GAMEID_WZQ:
				System.loadLibrary("twzq");
				break;
			case AppConfig.GAMEID_XQ:
				System.loadLibrary("txq");
				break;
			case AppConfig.GAMEID_GDY:
				System.loadLibrary("tgdy");
				break;
			case AppConfig.GAMEID_GBMJ:
				System.loadLibrary("ddz");
				break;
			}
		} catch (UnsatisfiedLinkError e) {
			Log.e(TAG, "加载库文件失败:");
		}
	}

	/**
	 * add Activity 添加Activity到栈
	 */
	public void addActivity(Activity activity){
		if(mActivityStack ==null){
			mActivityStack =new Stack<Activity>();
		}
		mActivityStack.add(activity);
	}
	/**
	 * get current Activity 获取当前Activity（栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		Activity activity = mActivityStack.lastElement();
		return activity;
	}
	/**
	 * 结束当前Activity（栈中最后一个压入的）
	 */
	public void finishActivity() {
		Activity activity = mActivityStack.lastElement();
		finishActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			mActivityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
		for (Activity activity : mActivityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = mActivityStack.size(); i < size; i++) {
			if (null != mActivityStack.get(i)) {
				mActivityStack.get(i).finish();
			}
		}
		mActivityStack.clear();
	}

	/**
	 * 退出应用程序
	 */
	public void AppExit() {
		try {
			finishAllActivity();
		} catch (Exception e) {
		}
	}

	

	  /**
   * 获取当前日期
   * 
   * @return
   */
  private static String getCurrentDateString() {
      String result = null;
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss",
              Locale.getDefault());
      Date nowDate = new Date();
      result = sdf.format(nowDate);
      return result;
  }

  /**
       * 向文件中写入错误信息
       * 
       * @param info
       */
      protected void writeErrorLog(String info) {
          File dir = new File(DIR);
          if (!dir.exists()) {
              dir.mkdirs();
          }
          File file = new File(dir, NAME);
          try {
              FileOutputStream fileOutputStream = new FileOutputStream(file, true);
              fileOutputStream.write(info.getBytes());
              fileOutputStream.close();
          } catch (FileNotFoundException e) {
              e.printStackTrace();
          } catch (IOException e) {
              e.printStackTrace();
          }
          System.exit(0);

      }
	   /**
	     * 捕获错误信息的handler
	     */
	    private UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {
	 
	        @Override
	        public void uncaughtException(Thread thread, Throwable ex) {
	            String info = null;
	            ByteArrayOutputStream baos = null;
	            PrintStream printStream = null;
	            try {
	                baos = new ByteArrayOutputStream();
	                printStream = new PrintStream(baos);
	                ex.printStackTrace(printStream);
	                byte[] data = baos.toByteArray();
	                info = new String(data);
	                data = null;
	            } catch (Exception e) {
	                e.printStackTrace();
	            } finally {
	                try {
	                    if (printStream != null) {
	                        printStream.close();
	                    }
	                    if (baos != null) {
	                        baos.close();
	                    }
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	            writeErrorLog(info);
	        }
	    };
	
}
