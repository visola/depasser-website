package br.com.depasser.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.depasser.util.Timer;

/**
 * Servlet Filter implementation class TimerFilter
 */
public class TimerFilter implements Filter {
	
	private Logger log = LoggerFactory.getLogger(TimerFilter.class);

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		Timer timer = new Timer();
		chain.doFilter(request, response);
		timer.stop();
		
		double result = timer.timeInMicroseconds();
		double millis = timer.timeInMilliseconds();
		
		String uri = ((HttpServletRequest)request).getRequestURI();
		
		log.debug(uri + " processed in " + result + " microseconds (" + millis + " ms)");
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}