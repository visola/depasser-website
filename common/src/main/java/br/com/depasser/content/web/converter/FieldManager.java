package br.com.depasser.content.web.converter;

import java.util.List;

import br.com.depasser.content.Field;
import br.com.depasser.content.Type;

public class FieldManager {
	
	private List<FieldConverter> converters;
	
	public Field convert(Type type, String value) throws ConversionException {
		for (FieldConverter converter : converters) {
			if (converter.canConvert(type)) {
				return converter.convert(type, value);
			}
		}
		throw new ConversionException("Converter not found for type: " + type.toString());
	}

	public List<FieldConverter> getConverters() {
		return converters;
	}

	public void setConverters(List<FieldConverter> converters) {
		this.converters = converters;
	}

}