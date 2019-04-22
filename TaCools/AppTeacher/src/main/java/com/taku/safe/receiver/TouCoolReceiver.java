package com.taku.safe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import com.taku.safe.login.RegisterFragment;
import com.taku.safe.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TouCoolReceiver extends BroadcastReceiver {
    private static final String TAG = TouCoolReceiver.class.getSimpleName();

    public static final String SMS_RECEIVED_ACTION = "taku.intent.action.SMS_RECEIVED";
    public static final String SMS_CODE = "sms_code";

    private static final int CODE_LEN = 4;   //短信验证码长度


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdu_Objects = (Object[]) bundle.get("pdus");
                if (pdu_Objects != null) {
                    for (Object aObject : pdu_Objects) {
                        SmsMessage currentSMS = getIncomingMessage(aObject, bundle);
                        String senderNo = currentSMS.getDisplayOriginatingAddress();
                        //String senderNo = currentSMS.getOriginatingAddress();

                        String message = currentSMS.getDisplayMessageBody();

                        String valid = patternCode(message);
                        if (!StringUtils.isEmpty(valid)) {
                            broadValid(context, valid);
                            //abortBroadcast();
                            break;
                        }
                    }
                }
            }

        }
    }


    private void broadValid(Context context, String valid) {
        Intent in = new Intent(SMS_RECEIVED_ACTION);
        in.putExtra(SMS_CODE, valid);
        context.sendBroadcast(in);
    }


    /**
     * 匹配短信中间的验证码
     *
     * @param message
     * @return
     */
    private String patternCode(String message) {
        String res = "";
        /* 正则匹配验证码 */
        String patternCoder = "(?<!\\d)\\d{" + CODE_LEN + "}(?!\\d)";
        if (StringUtils.isEmpty(message)) {
            return res;
        }

        Pattern p = Pattern.compile(patternCoder);
        Matcher matcher = p.matcher(message);
        if (matcher.find()) {
            res = matcher.group();
        }
        return res;
    }

    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        //for Build Tool 22,  Build.VERSION_CODES.M build error
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;
    }

}