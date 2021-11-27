package com.acgist.web.interceptor;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * CSRF拦截器
 * 
 * POST请求验证是否含有TOKEN防止CSRF攻击
 */
@Component
public class CsrfInterceptor implements HandlerInterceptor {

	private static final String SESSION_CSRF_TOKEN = "token";
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		final String method = request.getMethod();
		final HttpSession session = request.getSession();
		final String trueToken = (String) session.getAttribute(SESSION_CSRF_TOKEN);
		if (HttpMethod.POST.name().equalsIgnoreCase(method)) {
			final String token = (String) request.getParameter(SESSION_CSRF_TOKEN);
			if (StringUtils.equals(token, trueToken)) {
				this.buildCsrfToken(session);
				return true;
			} else {
				response.setStatus(HttpStatus.FORBIDDEN.value());
				response.sendRedirect("/error");
				return false;
			}
		}
		if (trueToken == null) {
			this.buildCsrfToken(session);
		}
		return true;
	}

	/**
	 * 生成Token
	 * 
	 * @param session session
	 */
	private void buildCsrfToken(HttpSession session) {
		session.setAttribute(SESSION_CSRF_TOKEN, UUID.randomUUID().toString());
	}

}
