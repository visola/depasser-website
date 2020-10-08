package br.com.depasser.content.web.converter.impl;

import java.util.ArrayList;
import java.util.List;

import br.com.depasser.content.Field;
import br.com.depasser.content.Option;
import br.com.depasser.content.OptionType;
import br.com.depasser.content.Type;
import br.com.depasser.content.field.MultiValueField;
import br.com.depasser.content.web.converter.ConversionException;
import br.com.depasser.content.web.converter.FieldConverter;

public class MultiValueFieldConverter extends FieldConverter {
	
	@SuppressWarnings("unchecked")
	private static Class<? extends Field> [] classes = new Class[] {MultiValueField.class};
	
	/**
	 * List that contains ids for the {@link OptionType OptionTypes} that accept
	 * options that still doesn't exist in the database. These options will be
	 * created when passed in.
	 */
	private List<Integer> createOptionsFor = new ArrayList<Integer>();

	@Override
	public Field convert(Type type, String value) throws ConversionException {
		if ( ! (type instanceof OptionType) ) {
			throw new ConversionException("Invalid type for this converter: " + type.getClass().getName());
		}
		
		OptionType optionType = (OptionType) type;
		MultiValueField field = (MultiValueField) getField(type);
		
		if (value != null && !"".equals(value)) {
			String [] splitted = value.split(",");
			
			for (String s : splitted) {
				s = s.trim();
				if (s.equals("")) continue;
				
				boolean found = false;
				for (Option option : optionType.getOptions()) {
					if (option.getValue().equals(s)) {
						field.addOption(option);
						found = true;
					}
				}
				if (!found) {
					// Should we create options for this type?
					if (createOptionsFor.contains(optionType.getId())) {
						Option option = new Option(s, s);
						option.setType(optionType);
						field.addOption(option);
					} else {
						throw new ConversionException("Invalid option value: '" + s + "' for type: " + optionType.toString());
					}
				}
			}
		}
		
		return field;
	}

	@Override
	public Class<? extends Field>[] getClasses() {
		return classes;
	}

	public List<Integer> getCreateOptionsFor() {
		return createOptionsFor;
	}

	public void setCreateOptionsFor(List<Integer> createOptionsFor) {
		this.createOptionsFor = createOptionsFor;
	}

}