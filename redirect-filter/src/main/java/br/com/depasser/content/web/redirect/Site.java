package br.com.depasser.content.web.redirect;

import java.util.ArrayList;
import java.util.List;

public class Site {

	private String context;
	private String compatibilityContext;
	private String action;
	private String compatibilityAction;
	private List<UserAgent> userAgents = new ArrayList<UserAgent>();

	public String getAction() {
		return action;
	}

	public String getCompatibilityAction() {
		return compatibilityAction;
	}

	public String getCompatibilityContext() {
		return compatibilityContext;
	}

	public String getContext() {
		return context;
	}

	public List<UserAgent> getUserAgents() {
		return userAgents;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setCompatibilityAction(String compatibilityAction) {
		this.compatibilityAction = compatibilityAction;
	}

	public void setCompatibilityContext(String compatibilityContext) {
		this.compatibilityContext = compatibilityContext;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public void setUserAgents(List<UserAgent> userAgents) {
		this.userAgents = userAgents;
	}

}