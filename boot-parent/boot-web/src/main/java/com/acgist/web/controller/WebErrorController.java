package com.acgist.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.acgist.boot.StringUtils;

/**
 * 统一错误页面
 * 
 * @author acgist
 */
@Controller
public class WebErrorController implements ErrorController {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebErrorController.class);

	public static final String ERROR_PATH = "/error";

	@ResponseBody
	@RequestMapping(value = ERROR_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> index(String message, HttpServletRequest request, HttpServletResponse response) {
		LOGGER.warn("系统错误：{}-{}", message, response.getStatus());
		final Map<String, Object> map = new HashMap<>();
		if (StringUtils.isEmpty(message)) {
			map.put("message", response.getStatus());
		} else {
			map.put("message", message);
		}
		return map;
	}

	@RequestMapping(value = ERROR_PATH, produces = MediaType.TEXT_HTML_VALUE)
	public String index(String message, ModelMap model, HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.isEmpty(message)) {
			model.put("message", response.getStatus());
		} else {
			model.put("message", message);
		}
		return ERROR_PATH;
	}

	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}
	
}
