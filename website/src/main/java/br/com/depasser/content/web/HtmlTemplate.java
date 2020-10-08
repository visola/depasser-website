package br.com.depasser.content.web;

import java.util.ArrayList;
import java.util.List;

public class HtmlTemplate {
	
	private String html;
	private List<String> scripts = new ArrayList<String>();
	private List<String> styles = new ArrayList<String>();

	public String getHtml() {
		return html;
	}

	public List<String> getScripts() {
		return scripts;
	}

	public List<String> getStyles() {
		return styles;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public void setScripts(List<String> scripts) {
		this.scripts = scripts;
	}

	public void setStyles(List<String> styles) {
		this.styles = styles;
	}

}