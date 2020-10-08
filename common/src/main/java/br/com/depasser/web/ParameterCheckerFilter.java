package br.com.depasser.web;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ParameterCheckerFilter implements Filter {

	@Override
	public void destroy() {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		for (Enumeration names = request.getParameterNames(); names.hasMoreElements();) {
			String name = (String) names.nextElement();
			System.out.println("Parameter: " + name);
			for (String value : request.getParameterValues(name)) {
				System.out.println("\t'" + value + "'");
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {

	}

}