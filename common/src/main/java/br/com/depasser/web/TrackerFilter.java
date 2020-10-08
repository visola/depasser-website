package br.com.depasser.web;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackerFilter implements Filter {
	
	private Logger logger = LoggerFactory.getLogger(TrackerFilter.class);

	@Override
	public void destroy() {

	}

	@Override
	@SuppressWarnings("rawtypes")
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("User-agent: ");
			message.append(req.getHeader("User-Agent"));
			
			message.append(", time: ");
			message.append(System.currentTimeMillis());
			
			message.append(", requested: ");
			message.append(req.getRequestURI());
			
			Enumeration names = req.getParameterNames();
			if (names.hasMoreElements()) {
				message.append(", parameters: ");
				while (names.hasMoreElements()) {
					String name = (String) names.nextElement();
					message.append(name);
					message.append("=");
					
					String [] params = req.getParameterValues(name);
					if (params.length == 1) {
						message.append("'");
						message.append(params[0]);
						message.append("'");
					} else {
						message.append("[");
						for (int i = 0; i < params.length; i++) {
							message.append("'");
							message.append(params[i]);
							message.append("'");
							if (i != params.length - 1) message.append(",");
						}
						message.append("]");
					}
					message.append(",");
				}
				message.setLength(message.length() - 1);
			}
			
			logger.debug(message.toString());
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {

	}

}