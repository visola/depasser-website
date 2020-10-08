package br.com.depasser.content.web.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Locale;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import br.com.depasser.content.web.HtmlTemplate;
import br.com.depasser.web.spring.AbstractController;

@Controller
@RequestMapping("/template")
public class TemplateController extends AbstractController implements ApplicationContextAware {

	private static final String TEMPLATES_PATH = "/WEB-INF/templates";
	
	private WebApplicationContext context;
	
	@RequestMapping("/html")
	public ModelAndView html(String name, Locale l) throws Exception {
		String sContext = context.getServletContext().getContextPath();
		if (name.indexOf(sContext) == 0) {
			name = name.substring(sContext.length());
		}
		
		if (l == null) {
			l = new Locale("pt", "br");
		}
		String language = l.getLanguage();
		String country = l.getCountry();
		
		HtmlTemplate template = loadTemplate(name, language, country);
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("template", template);
		return mv;
	}
	
	public HtmlTemplate loadTemplate(String name, String language, String country) throws Exception {
		HtmlTemplate template = new HtmlTemplate();
		template.setHtml("<p>Template not found.</p>");
		
		File basePath = new File(context.getServletContext().getRealPath(TEMPLATES_PATH));
		
		// Check in the locale folder
		File templateFile = new File(basePath, language + "/" + country + "/" + name + ".html");
		
		if (! templateFile.getCanonicalPath().startsWith(basePath.getCanonicalPath())) {
			throw new IOException("Invalid path to template file: " + templateFile.getCanonicalPath());
		}
		
		// If not found
		if (!templateFile.exists()) {
			// Check in the language folder
			templateFile = new File(basePath, language + "/" + name + ".html");
			
			// If not found
			if (!templateFile.exists()) {
				// Check in the default folder
				templateFile = new File(basePath, name + ".html");
			}
		}
		
		try {
			// Read template HTML
			StringBuilder html = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(templateFile), "utf-8"));
			String line = null;
			while ( (line = reader.readLine()) != null) {
				html.append(line);
			}
			template.setHtml(html.toString());
			
			// Read template metadata
			File metaDataFile = new File(templateFile.getParentFile(), name + ".json");
			ObjectMapper mapper = new ObjectMapper();
			JsonNode metaData = mapper.readTree(new FileInputStream(metaDataFile));
			
			// Scripts
			for (Iterator<JsonNode> nodes = metaData.get("scripts").getElements(); nodes.hasNext();) {
				template.getScripts().add(nodes.next().getTextValue());
			}
			
			// Styles
			for (Iterator<JsonNode> nodes = metaData.get("styles").getElements(); nodes.hasNext();) {
				template.getStyles().add(nodes.next().getTextValue());
			}
		} catch (FileNotFoundException fnfe) {
			// Do nothing
		} catch (IOException ioe) {
			template.setHtml("<p>Error while reading template file: " + ioe.getMessage() + "</p>");
		}
		
		return template;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = (WebApplicationContext) context;
	}
}