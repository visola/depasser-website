package br.com.depasser.content.web.redirect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RedirectFilter implements Filter {

	private Logger logger = LoggerFactory.getLogger(RedirectFilter.class);
	private Site managedSite;
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		long id = System.nanoTime();
		
		String userAgent = request.getHeader("User-Agent");
		
		String context = request.getContextPath();
		boolean newBrowser = false;

		if (managedSite != null) {
			// Check if it is a new browser
			for (UserAgent ua : managedSite.getUserAgents()) {
				if (userAgent.indexOf(ua.getIdentifier()) != -1) {
					boolean contains = false;

					// Check for excluding strings
					for (String exclude : ua.getExcludeStrings()) {
						if (userAgent.indexOf(exclude) != -1) {
							contains = true;
							break;
						}
					}

					// If no excluding strings, it is a new browser
					if (!contains) {
						newBrowser = true;
						break;
					}
				}
			}

			// If in normal context
			if (context.equalsIgnoreCase(managedSite.getContext())) {
				if (!newBrowser) {
					logger.debug("Redirecting to compatibility site... User agent: {}, time: {}, requested: " + request.getRequestURI(), userAgent, id);
					redirectOldBrowser(request, response);
					return;
				}

				// If in compatibility context
			} else if (newBrowser) {
				logger.debug("Redirecting to normal site... User agent: {}, time: {}, requested: " + request.getRequestURI(), userAgent, id);
				redirectNewBrowser(request, response);
				return;
			}
		}
		
		// If not redirected, just continue
		logger.debug("User agent: {}, time: {}, requested: " + request.getRequestURI(), userAgent, id);
		chain.doFilter(req, resp);
	}

	@SuppressWarnings("rawtypes")
	private void redirectNewBrowser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuilder newUrl = new StringBuilder();
		newUrl.append(managedSite.getContext());
		newUrl.append("/");
		newUrl.append(managedSite.getAction());
		newUrl.append("#{");
		
		boolean added = false;
		for (Enumeration names = request.getParameterNames(); names.hasMoreElements();) {
			added = true;
			String name = (String) names.nextElement();
			newUrl.append("\"");
			newUrl.append(name);
			newUrl.append("\":\"");
			newUrl.append(request.getParameter(name));
			newUrl.append("\",");
		}
		
		// If any parameter was added, remove trailing comma
		if (added) newUrl.setLength(newUrl.length() - 1);
		
		newUrl.append("}");
		
		response.sendRedirect(newUrl.toString());
	}

	private void redirectOldBrowser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			response.setContentType("text/html");
			response.setCharacterEncoding("utf-8");
			BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("redirect.html"), "utf-8"));
			String line = null;
			Writer out = response.getWriter();
			while ( (line = in.readLine()) != null) {
				if (line.indexOf("${context}") != -1) {
					line = line.replace("${context}", managedSite.getCompatibilityContext() + "/" + managedSite.getCompatibilityAction());
				}
				out.write(line);
				out.write("\n");
			}
		} catch (IOException ioe) {
			logger.error("Error while reading redirect file.", ioe);
			throw ioe;
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		String fileName = config.getInitParameter("configFile");
		if (fileName == null) {
			fileName = System.getProperty("user.home") + "/config/RedirectFilter.xml";
		}
		
		try {
			File f = new File(fileName);
			if (!f.exists()) {
				return;
			}

			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(f));
			
			managedSite = new Site();
			managedSite.setAction(getText("action", doc.getDocumentElement()));
			managedSite.setContext(getText("context", doc.getDocumentElement()));
			managedSite.setCompatibilityAction(getText("compatibility-action", doc.getDocumentElement()));
			managedSite.setCompatibilityContext(getText("compatibility-context", doc.getDocumentElement()));
			
			NodeList userAgentNodes = doc.getElementsByTagName("user-agent");
			for (int i = 0; i < userAgentNodes.getLength(); i++) {
				if (userAgentNodes.item(i) instanceof Element) {
					Element e = (Element) userAgentNodes.item(i);
					UserAgent ua = new UserAgent();
					ua.setIdentifier(getText("identifier", e));
					ua.setExcludeStrings(getTexts("exclude", e));
					managedSite.getUserAgents().add(ua);
				}
			}
			
		} catch (Exception ex) {
			logger.error("Error while loading configuration  file.", ex);
			throw new ServletException("Error while loading configuration file.", ex);
		}
	}
	
	private List<String> getTexts (String tagName, Element element) {
		List<String> result = new ArrayList<String>();
		NodeList items = element.getElementsByTagName(tagName);
		for (int i = 0; i < items.getLength(); i++) {
			Node n = items.item(i);
			if (n instanceof Element) {
				result.add( ( (Element) n ).getTextContent() );
			}
		}
		return result;
	}
	
	private String getText(String tagName, Element element) {
		Node node = element.getElementsByTagName(tagName).item(0);
		return ( (Element)node ).getTextContent();
	}
	
}