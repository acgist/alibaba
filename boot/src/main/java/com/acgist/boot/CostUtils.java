package com.acgist.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消耗时间工具
 * 
 * @author acgist
 */
public class CostUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(CostUtils.class);

	private CostUtils() {
	}

	/**
	 * 执行消耗
	 * 
	 * @param count 执行次数
	 * @param coster 消耗函数
	 */
	public static final void costed(int count, Coster coster) {
		final long time = System.currentTimeMillis();
		for (int index = 0; index < count; index++) {
			coster.execute();
		}
		final long costed = System.currentTimeMillis() - time;
		LOGGER.info("消耗时间：{}", costed);
	}

	/**
	 * 消耗函数
	 * 
	 * @author acgist
	 */
	public interface Coster {
		/**
		 * 消耗方法
		 */
		void execute();
	}

}