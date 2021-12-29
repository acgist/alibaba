package com.acgist.boot.config;

import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.acgist.boot.JSONUtils;
import com.acgist.boot.service.IdService;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.LoggerContext;

/**
 * Boot自动配置
 * 
 * @author acgist
 */
@EnableAsync
@Configuration
@AutoConfigureBefore(value = ServletWebServerFactoryAutoConfiguration.class)
public class BootAutoConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(BootAutoConfiguration.class);

	@Value("${spring.application.name:}")
	private String name;
	@Value("${server.port:0}")
	private int port;
	@Value("${system.thread.min:2}")
	private int min;
	@Value("${system.thread.max:10}")
	private int max;
	@Value("${system.thread.size:1000}")
	private int size;
	@Value("${system.thread.live:30}")
	private int live;
	
	/**
	 * 序列化类型
	 * 
	 * @author acgist
	 */
	public enum SerializerType {

		JDK, JACKSON;

	}

	/**
	 * 系统编号：01~99
	 * 
	 * 可以配置负数：自动生成
	 */
	@Value("${system.sn:-1}")
	private int sn;
	/**
	 * 默认使用JDK序列化
	 * 
	 * Jackson不支持没有默认函数的对象：JWT Token授权信息
	 */
	@Value("${system.serializer.type:jdk}")
	private String serializerType;
	
	@Autowired
	private NacosConfigManager nacosConfigManager;

	@Bean
	@ConditionalOnMissingBean
	public MusesConfig musesConfig() {
		return MusesConfigBuilder.builder()
			.init(this.nacosConfigManager)
			.buildSn(this.sn)
			.buildPid()
			.buildPort(this.port)
			.build(this.nacosConfigManager);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public IdService idService() {
		return new IdService();
	}

	@Bean
	@ConditionalOnMissingBean
	public ObjectMapper objectMapper() {
		return JSONUtils.getMapper();
	}

	@Bean
	@ConditionalOnMissingBean
	public SerializerType serializerType() {
		LOGGER.info("系统序列化类型：{}", this.serializerType);
		if (SerializerType.JACKSON.name().equalsIgnoreCase(this.serializerType)) {
			return SerializerType.JACKSON;
		} else {
			return SerializerType.JDK;
		}
	}

	@Bean
	@ConditionalOnMissingBean
	public TaskExecutor taskExecutor() {
		LOGGER.info("系统线程池：{}-{}-{}-{}", this.min, this.max, this.size, this.live);
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(this.min);
		executor.setMaxPoolSize(this.max);
		executor.setQueueCapacity(this.size);
		executor.setKeepAliveSeconds(this.live);
		executor.setThreadNamePrefix(this.name + "-");
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.setWaitForTasksToCompleteOnShutdown(true);
		return executor;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ShutdownListener shutdownListener() {
		return new ShutdownListener();
	}

	@PreDestroy
	public void destroy() {
		LOGGER.info("系统关闭");
		// 刷出日志缓存
		final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		if (context != null) {
			context.stop();
		}
	}

}