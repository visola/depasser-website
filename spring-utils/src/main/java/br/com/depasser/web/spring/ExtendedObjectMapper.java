package br.com.depasser.web.spring;

import java.beans.PropertyDescriptor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.TypeDescriptor;

import br.com.depasser.util.IterableArray;

public class ExtendedObjectMapper {
	
	private Stack<Integer> references = new Stack<Integer>();
	private Stack<String> properties = new Stack<String>();
	
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private Logger logger = LoggerFactory.getLogger(ExtendedObjectMapper.class);
	
	/**
	 * Process an iterable of objects with the specified number of elements.
	 * 
	 * @param it Iterable to be processed.
	 * @param size	Number of elements to process.
	 * @return An array containing all objects processed.
	 * @see JacksonJSONView#processObject(Object, Stack)
	 */
	private Object[] processList(Iterable<?> it) {
		List<Object> result = new ArrayList<Object>();
		
		// Process each value
		int counter = 0;
		for (Object value : it) {
			properties.push(Integer.toString(counter));
			result.add(processValue(value));
			properties.pop();
			counter++;
		}
		
		return result.toArray(new Object[result.size()]);
	}
	
	/**
	 * <p>
	 * Process an object to avoid circular references. This will process all
	 * properties from the object, recursively, if the object is a complex type.
	 * </p>
	 * 
	 * <p>
	 * If a simple object (strings, numbers, dates, etc.) or null, return it. If
	 * a list or an array, all objects from them will be processed and returned
	 * in a list or an array of processed items. Same for maps, but only maps
	 * with strings as keys are accepted. Other types of objects will have each
	 * of their properties processed separately and will be returned as a map.
	 * </p>
	 * 
	 * <p>
	 * If an exception happens while retrieving any of the properties, it will
	 * be logged and the property will be set to a string containing the message
	 * from the generated exception.
	 * </p>
	 * 
	 * @param o
	 *            The object to be processed.
	 * @return The processed object.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object processObject(Object o) {
		// If null, return it
		if (o == null) return o;
		
		// If date, return as string
		if (o instanceof Date) {
			return formatter.format(o);
		}
		
		// Same for calendar
		if (o instanceof Calendar) {
			return formatter.format( ( (Calendar) o ).getTime() );
		}
		
		// If any other simple value, return it
		if (BeanUtils.isSimpleValueType(o.getClass())) {
			return o;
		}
		
		// If complex object, process it
		Map<Object, Object> result = new TreeMap<Object, Object>();
		
		// Check for arrays, collections, maps
		TypeDescriptor typeDescriptor = TypeDescriptor.forObject(o);
		
		// If collection
		if (typeDescriptor.isCollection()) {
			Collection<?> collection = (Collection<?>) o;
			return processList(collection);
		}
		
		// If array
		if (typeDescriptor.isArray()) {
			// TODO - problem when casting to primitive arrays
			Iterable<Object> array = new IterableArray((Object[])o);
			return processList(array);
		}
		
		// If map
		if (typeDescriptor.isMap()) {
			Map<?, ?> map = (Map<?,?>) o;
			
			// If a TreeMap, get its comparator
			if (map instanceof TreeMap) {
				result = new TreeMap<Object, Object>( ((TreeMap)map).comparator() );
			}
			
			for (Object key : map.keySet()) {
				if ( !(key instanceof String)) throw new ClassCastException("JSON objects only support String as keys.");
				properties.push((String) key);
				result.put((String) key, processValue(map.get(key)));
				properties.pop();
			}
			
			return result;
		}
		
		// Wrap it to be easier to process
		BeanWrapper wrapper = new BeanWrapperImpl(o);
		for (PropertyDescriptor propertyDescriptor : wrapper.getPropertyDescriptors()) {
			String name = propertyDescriptor.getName();
			
			// avoid Object.getClass()
			if (name.equals("class")) continue;
			
			// Get property value and calculate hash
			try {
				properties.push(name);
				result.put(name, processValue(wrapper.getPropertyValue(name)));
				properties.pop();
			} catch (Exception e) {
				logger.error("Error while processing property: " + name, e);
				result.put(name, e.getClass().getName() + ": " + e.getMessage());
			}
		}
		
		return result;
	}
	
	/**
	 * <p>
	 * Process a single value. It will check if it's null or if it is already
	 * referenced in the stack. If not in the stack, will be processed by
	 * <code>processObject</code> and returned.
	 * </p>
	 * 
	 * <p>
	 * If a circular reference is detected, a string containing the hash of the
	 * object will be returned instead.
	 * </p>
	 * 
	 * @param value
	 *            Value to be processed.
	 * @return null if <code>value</code> is null. A string if it is a circular
	 *         reference. The processed object otherwise.
	 * @see #processObject(Object, Stack)
	 */
	private Object processValue(Object value) {
		if (value == null) return null;
		try {
			int hash = value.hashCode();
			if (references.contains(hash)) {
				StringBuilder s = new StringBuilder("[CR:");
				s.append(hash);
				s.append("]");
				return s.toString();
			}
			
			references.push(hash);
			value = processObject(value);
			references.pop();
		} catch (NullPointerException npe) {
			logger.warn("NPE: hashCode() has thrown a NPE", npe);
			return "NPE: hashCode() has thrown a NPE";
		}
		return value;
	}
	
}