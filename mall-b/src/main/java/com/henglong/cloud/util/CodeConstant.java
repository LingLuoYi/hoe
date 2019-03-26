package com.henglong.cloud.util;

public class CodeConstant {
	
	/**
	 * 成功
	 */
	public static final int SUCCESS = 0;
	
	/**
	 * 通用错误,一般可以直接显示出来
	 */
	public static final int ERROR = 1;
	
	/**
	 * 参数错误
	 */
	public static final int ERR_PAR = 2;
	
	/**
	 * 请求没有授权,一般是没有登录或者token过期
	 */
	public static final int ERR_AUTH = 3;
	
	/**
	 * 未找到数据
	 */
	public static final int FIND_ERR = 4;
	
	/**
	 * 更新失败
	 */
	public static final int SET_ERR = 5;
	
	/**
	 * 此用户已被锁定的情况
	 */
	public static final int LOCK = 6;
	
	/**
	 * 匹配错误--主要是密码错误
	 */
	public static final int MAT_ERR = 7;
	
	/**
	 * 无权操作
	 */
	public static final int NO_AUTH = 8;
	
	/**
	 * 系统错误
	 */
	public static final int ERR_SYSTEM = 9;

	/**
	 * token错误
	 */
	public static final int ERR_TOKEN = 10;

	/**
	 * 登录失败，未知原因
	 */
	public static final int LOGIN_NO = 11;

	/**
	 * 登录成功
	 */
	public static final int LOGIN_OK = 12;

	/**
	 * 生成支付订单
	 */
	public static final int PAYMENT = 13;

	/**
	 * 订单初始状态，一般为系统生成订单时的初始状态
	 */
	public static final int INITIAL = 14;

	/**
	 * 订单未发货状态
	 */
	public static final int UNSHIPPED = 15;

	/**
	 * 订单已发货
	 */
	public static final int SHIPMENT = 16;

	/**
	 * 订单已签收
	 */
	public static final int SIGN_FOR = 17;

	/**
	 * 订单已关闭
	 */
	public static final int CLOSE = 18;

	/**
	 * 未通过审核
	 */
	public static final int NOT_PASS = 19;

	/**
	 * 支付中
	 */
	public static final int PAY = 20;

	/**
	 * 优惠劵正常状态Coupon normal
	 */
	public static final int COUPON_NORMAL = 21;

	/**
	 *优惠劵失效Invalid
	 */
	public static final int COUPON_INVALID = 22;

	/**
	 * 优惠劵已使用use
	 */
	public static final int COUPON_USE = 23;
}