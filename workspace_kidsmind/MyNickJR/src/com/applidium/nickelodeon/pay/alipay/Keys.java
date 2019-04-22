/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.applidium.nickelodeon.pay.alipay;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

    // 合作商户ID，用签约支付宝账号登录www.alipay.com后，在商家服务页面中获取。
    public static final String DEFAULT_PARTNER = "2088912678502107";

    // 商户收款的支付宝账号
    public static final String DEFAULT_SELLER = "biz@kidsmind.com";

    // 商户（RSA）私钥
    public static final String PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANPPHjBVSD8qw58BQlZv9CJitJmqP63kX29DPC9Qmbeoo1Z4ql6SMSY2aw26WzGKs2cuObaFt46M2LcoiMT2sNZ/yE9KYAG27yWwpotKmc6fsXhg4m2U7+5z7REqITy6lJcqd8RVZ1XL81KWXBtzIX3apY7fIYwsMATCA+EtzEoLAgMBAAECgYEAk8JeZgtofiCAHXGMbK5nGoHPSY+6Ir45UW4PE1bgFX6Ai/dbquPDg8vcvNPvgE0pQGa//jOkTL63BHczimwqCgQTYjDcC069ogisXiDPfvsOB6hJWMH/K4fDMlKVsBIlkuFSKIiEurswPaqYGN9PXdKXBWjYorMRPLd9IwbSKQECQQD0IJsdhJmXRJUxkEzXSZFTAgTXFxkGRZ0FlaK//l76QzlKwhu7aOXxp7sGTmYBECmEdaO/tOG8uZWBEG8JDpmBAkEA3hwl3VJQrK1GnD8WVc58NioXa55zX+TLpmYczeJeTy75km6HquQ4ElzJ5wZtSgTv6rflE3x7J9GBOrmoLZ1xiwJAaMgJ69x7E+sS4igeYxUnc+pc63j1e7SrVbdJGID0gRQTZhv0TTcT4unsIyPJMlGekETx2Jmpfhhlq/TCw69pgQJBALmwAp/jX3rdajjaLUwTUpjd27KnR4GWAAPfPpmJdJryhMe9DEL8hTwkws8X+mp1cjZjA2qlGTkS191GtaldBT0CQCJU75+emzNAayLkv32iCfD03yMeEYPydJCZmhhNUppd2OyR16x+K6B3QZHPZ/xh6vptg1iHJ45+nwkej0dK4Xs=";


	// 支付宝（RSA）公钥
	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	





    // 安全校验码
    //String key = "ui61u84c1jqurczq0cnb399t7kbyy3k1";



}
