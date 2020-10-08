package br.com.depasser.content.web.controller;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.depasser.web.spring.AbstractController;

@Controller
public class TrackerController extends AbstractController {
	
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static final String TRACKER_COOKIE_NAME = "trackerCookie";
	private Logger logger = LoggerFactory.getLogger(getClass());

	@RequestMapping("/tracker")
	public void saveTrackObject(String data, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		Cookie [] cookies = request.getCookies();
		Cookie trackerCookie = null;
		if (cookies != null) { 
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(TRACKER_COOKIE_NAME)) {
					trackerCookie = cookie;
					break;
				}
			}
		}
		
		// User's info
		String ipAddress = request.getRemoteHost();
		
		// This user's hash
		String hash = null;
		String lastVisited = null;
		
		// If no cookie found
		if (trackerCookie == null) {
			trackerCookie = new Cookie(TRACKER_COOKIE_NAME, null);
			
			// Generate HASH
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(ipAddress.getBytes());
			digest.update(Long.toString(System.nanoTime()).getBytes());
			digest.update("Depasser".getBytes());
			
			byte[] hashBytes = digest.digest();
			
			StringBuilder hashBuilder = new StringBuilder();
			for (byte b : hashBytes) {
				hashBuilder.append(Integer.toString(Math.abs(b), 16).toUpperCase());
			}
			hash = hashBuilder.toString();
		} else {
			String value = trackerCookie.getValue();
			String [] splitted = value.split(",");
			hash = splitted[0];
			lastVisited = splitted[1];
		}
		
		StringBuilder newValue = new StringBuilder(hash);
		newValue.append(",");
		newValue.append(formatter.format(Calendar.getInstance().getTime()));
		
		trackerCookie.setValue(newValue.toString());
		trackerCookie.setMaxAge(Integer.MAX_VALUE);
		response.addCookie(trackerCookie);
		
		StringBuilder message = new StringBuilder();
		message.append(locale.toString());
		message.append(";");
		message.append(hash);
		message.append(";");
		message.append(ipAddress);
		message.append(";");
		
		if (lastVisited == null) {
			message.append("");
		} else {
			message.append(lastVisited);
		}
		message.append(";");
		message.append(formatter.format(Calendar.getInstance().getTime()));
		message.append(";");
		
		message.append(data);
		
		logger.debug(message.toString());
	}

}