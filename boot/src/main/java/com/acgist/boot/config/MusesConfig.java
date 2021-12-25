package com.acgist.boot.config;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 系统配置
 * 
 * @author acgist
 */
public class MusesConfig {

	/**
	 * 最大机器序号
	 */
	public static final int MAX_SN = 100;
	/**
	 * 超时时间
	 */
	public static final int TIMEOUT = 5 * 1000;
	/**
	 * 默认编码
	 */
	public static final Charset CHARSET = StandardCharsets.UTF_8;
	/**
	 * 默认编码
	 */
	public static final String CHARSET_VALUE = CHARSET.name();
	/**
	 * 系统自动配置
	 */
	public static final String MUSES_CONFIG = "muses.json";
	
	/**
	 * 当前机器序号
	 */
	private Integer sn;

	/**
	 * 初始配置
	 * 
	 * @return 配置
	 */
	public MusesConfig build() {
		if(++this.sn >= MAX_SN) {
			this.sn = 0;
		}
		return this;
	}
	
	public Integer getSn() {
		return sn;
	}

	public void setSn(Integer sn) {
		this.sn = sn;
	}
	
}
