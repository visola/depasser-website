package br.com.depasser.content.web.redirect;

import java.util.ArrayList;
import java.util.List;

public class UserAgent {

	private String identifier;
	private List<String> excludeStrings = new ArrayList<String>();

	public List<String> getExcludeStrings() {
		return excludeStrings;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setExcludeStrings(List<String> excludeStrings) {
		this.excludeStrings = excludeStrings;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

}