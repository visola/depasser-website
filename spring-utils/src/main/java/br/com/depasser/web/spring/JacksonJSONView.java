package br.com.depasser.web.spring;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.View;

/**
 * <p>
 * A JSON generator view that avoid circular references and unexpected
 * exceptions while accessing getters like lazy initialization problems.
 * </p>
 * 
 * <p>
 * Circular references will be replaced by a <code>String</code> that shows the
 * hash of the instance that is being referenced. This is controlled using a
 * stack of hashes from all objects that were added to the result. Classes must
 * implement <code>Object.hashCode()</code> correctly, otherwise an unexisting
 * circular reference may be detected.
 * </p>
 * 
 * <p>
 * Exceptions while getting values are shown in the JSON object as a
 * <code>String</code>s and logged using SLF4J.
 * </p>
 * 
 * @author Vinicius Isola (viniciusisola@gmail.com)
 */
public class JacksonJSONView implements View {
	
	/** Used to generate the JSON object. */
	private ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * Content type is always <code>application/json</code>.
	 */
	@Override
	public String getContentType() {
		return "application/json";
	}

	@Override
	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {		
		Map<String, Object> result = new HashMap<String, Object>();
		
		for (String key : model.keySet()) {
			// Avoid spring framework objects
			if (key.startsWith("org.springframework")) continue;
			
			result.put(key, model.get(key));
		}
		
		// Write result to client
		response.setContentType(getContentType());
		response.setCharacterEncoding("UTF-8");
		
		Writer toClient = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
		objectMapper.writeValue(toClient, new ExtendedObjectMapper().processObject(result));
	}

}