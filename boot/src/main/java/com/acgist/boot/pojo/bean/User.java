package com.acgist.boot.pojo.bean;

import java.io.Serializable;
import java.util.Set;

import com.acgist.boot.CollectionUtils;

/**
 * 用户
 * 
 * @author acgist
 */
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	private String id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 角色
	 */
	private Set<String> roles;
	/**
	 * 地址
	 */
	private Set<String> paths;

	/**
	 * 判断是否含有角色
	 * 
	 * @param role 角色
	 * 
	 * @return 是否含有角色
	 */
	public boolean hasRole(String role) {
		if (role == null) {
			return false;
		}
		if (CollectionUtils.isEmpty(this.paths)) {
			return false;
		}
		return this.roles.contains(role);
	}

	/**
	 * 判断是否含有权限
	 * 
	 * @param method 请求方法
	 * @param path 请求路径
	 * 
	 * @return 是否含有权限
	 */
	public boolean hasPath(String method, String path) {
		if (method == null || path == null) {
			return false;
		}
		if (CollectionUtils.isEmpty(this.paths)) {
			return false;
		}
		final String value = method + ":" + path;
		return this.paths.stream().anyMatch(pattern -> pattern.matches(value));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public Set<String> getPaths() {
		return paths;
	}

	public void setPaths(Set<String> paths) {
		this.paths = paths;
	}

}