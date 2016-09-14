/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.watch.customer.alipay;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

	// 合作商户ID，用签约支付宝账号登录www.alipay.com后，在商家服务页面中获取。
	public static final String DEFAULT_PARTNER = "2088411331507115";

	// 商户收款的支付宝账号
	public static final String DEFAULT_SELLER = "xiuhu.f@longerkj.com";

	// 商户（RSA）私钥
	public static final String PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAL9" +
			"LDVC694KUO6O5pXi5TUAdd1oFCkRsuXsB0pq58FK0xs05b6j3C35P+Ht3oWy4cVSAeKty7eZNDIYYkuG" +
			"jk0lj3vTkVNsByVaR+z/W5usP8794HgqhKAQyw6Ymh+F0tj4n2hO8m0XJXLBrsFszEZOytJkUom5J0+n" +
			"O31pYiLhlAgMBAAECgYAESyv2Vuv9S1R3XVBggFBCkya9p4VOed5D64uNm4TQZmxb6OEfusPQRv090Da" +
			"YWR96FRQgTQkX9TzFucF6PRCuOAn2yCuYO+dR+ybYURQo6yrRUX36DHD4/Bz4unD8x6SBntS9yLX/2Lb" +
			"nqQCWmfbnb4dDYzwzX9Fz8PHEe/2a1QJBAPVUp62errFdjLwlfwDofTSwvisOLu20IxQLmnW1R9E092Z" +
			"W7KQSBBsf3P9e8fn4sKoQz69NIFM4MLNJFhsL2ccCQQDHnMe1V0F1kTmnvzjBuSpGgspA1BJGKEaQt40" +
			"ZHerNOQH0cMW70K+32kJMFDiSdhkVDGTknylp2NmVHItJevxzAkBIKFoZSu7+5BEc1bqBPeB1uvZ0G3v" +
			"aFn2qy67mqCczdWy/ARohN9tVTw3lXru1Vlw/6Sns2baEQ6avVPPXiKjJAkARoppKPSE9X802MsCy7Mb" +
			"9X8S6oYHTzO8fDfhbRbde1jCEBgqSI0fC+Hdu/UJaPjDNGUE4qY8hGNVwRQtRPJpdAkEAilm9nXW1Bth" +
			"UWa2bQWsYXq/YjX8tk6UPIYZP6lAAyLWkeZwNqqfj/YqQhOmdS/W9lUswiUQv1gyxItKfOo7+Sw==";

	// 支付宝（RSA）公钥
	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoU" + 
										"h/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/"
										+ "VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpL"
										+ "QIDAQAB";

}
