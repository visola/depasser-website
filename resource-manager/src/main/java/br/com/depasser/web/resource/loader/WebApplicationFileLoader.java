package br.com.depasser.web.resource.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.depasser.web.resource.Loader;
import br.com.depasser.web.resource.LoaderFactory;
import br.com.depasser.web.resource.ResourceContextListener;

public class WebApplicationFileLoader implements Loader {
	
	public static final String DEFAULT_ENCODING = "UTF-8";

	private static final Logger LOGGER = LoggerFactory.getLogger(WebApplicationFileLoader.class);

	private ServletContext context;
	
	private String encoding = DEFAULT_ENCODING;
	
	public WebApplicationFileLoader() {
		context = ResourceContextListener.getInstance().getServletContext();
		
		String encoding = context.getInitParameter(WebApplicationFileLoader.class.getName().concat(".encoding"));
		if (encoding != null) this.encoding = encoding;
	}
	
	private File getFile(String identifier) {
		String realPath = context.getRealPath(identifier);
		LOGGER.info("Looking for {} at {}", identifier, realPath);
		if (realPath == null) {
			return null;
		}
		return new File(realPath);
	}

	@Override
	public boolean hasResource(String identifier) throws IOException {
		File f = getFile(identifier);
		return f != null && f.exists() && f.isFile();
	}

	@Override
	public String loadResource(String identifier) throws IOException {
		File f = getFile(identifier);
		
		StringBuilder content = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f), encoding));
		
		String line = null;
		while ( (line = in.readLine()) != null) {
			content.append(line);
			content.append("\n");
		}
		
		in.close();
		
		return content.toString();
	}

	@Override
	public long lastUpdated(String identifier) {
		File f = getFile(identifier);
		if (f == null) return 0;
		return f.lastModified();
	}

}