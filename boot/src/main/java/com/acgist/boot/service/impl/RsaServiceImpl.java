package com.acgist.boot.service.impl;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import com.acgist.boot.service.RsaService;
import com.acgist.boot.utils.RsaUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RsaServiceImpl implements RsaService {

	/**
	 * 文本公钥
	 */
	@Value("${system.public.key:}")
	private String publicKeyValue;
	/**
	 * 文本私钥
	 */
	@Value("${system.private.key:}")
	private String privateKeyValue;
	/**
	 * 公钥
	 */
	private PublicKey publicKey;
	/**
	 * 私钥
	 */
	private PrivateKey privateKey;

	public RsaServiceImpl() {
	}

	@PostConstruct
	public void init() {
		if(StringUtils.isEmpty(this.publicKeyValue) || StringUtils.isEmpty(this.privateKeyValue)) {
			final var keys = RsaUtils.buildKey();
			this.publicKeyValue = keys.get(RsaUtils.PUBLIC_KEY);
			this.privateKeyValue = keys.get(RsaUtils.PRIVATE_KEY);
		}
		this.publicKey = RsaUtils.loadPublicKey(this.publicKeyValue);
		log.info("加载RSA公钥：{}", this.publicKey.getAlgorithm());
		this.privateKey = RsaUtils.loadPrivateKey(this.privateKeyValue);
		log.info("加载RSA私钥：{}", this.privateKey.getAlgorithm());
	}

	@Override
	public String encrypt(String content) {
		return RsaUtils.encrypt(content, this.publicKey);
	}
	
	@Override
	public String decrypt(String content) {
		return RsaUtils.decrypt(content, this.privateKey);
	}
	
	@Override
	public String signature(Map<String, Object> map) {
		return RsaUtils.signature(this.join(map), this.privateKey);
	}
	
	@Override
	public boolean verify(Map<String, Object> map, String signature) {
		return RsaUtils.verify(this.join(map), signature, this.publicKey);
	}
	
	/**
	 * 数据拼接
	 * 
	 * @param map 数据
	 * 
	 * @return 拼接结果
	 */
	private String join(Map<String, Object> map) {
		// 排序
		final Map<String, Object> sortMap = new TreeMap<>((source, target) -> source.compareTo(target));
		sortMap.putAll(map);
		// 拼接
		return sortMap.entrySet().stream()
			.filter(entity -> !RsaService.SIGNATURE.equals(entity.getKey()))
			.map(entity -> entity.getKey() + entity.getValue())
			.collect(Collectors.joining());
	}
	
}
