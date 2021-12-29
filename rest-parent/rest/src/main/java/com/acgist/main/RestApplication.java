package com.acgist.main;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

import com.acgist.boot.config.PortConfig;

@ComponentScan("com.acgist.rest")
@DubboComponentScan("com.acgist.rest")
@EnableDiscoveryClient
@SpringBootApplication
public class RestApplication {

	public static void main(String[] args) {
		PortConfig.setRestPort();
		SpringApplication.run(RestApplication.class, args);
	}
	
}
