package br.com.depasser.content.field;

import java.util.ArrayList;
import java.util.List;

import br.com.depasser.content.Field;
import br.com.depasser.content.Option;

public class MultiValueField extends Field {
	
	protected List<Option> values = new ArrayList<Option>();
	
	public void addOption(Option option) {
		if (option == null) {
			throw new NullPointerException("Must not add a null option.");
		}
		values.add(option);
	}
	
	@Override
	public Option getValue() {
		if (!isEmpty()) {
			return values.get(0);
		}
		return null;
	}
	
	public Option[] getValues() {
		return values.toArray(new Option[values.size()]);
	}
	
	public boolean hasOptionNamed(String optionName) {
		for (Option option : values) {
			if (option.getName().equals(optionName))
				return true;
		}
		return false;
	}
	
	public boolean hasOptionValued(String optionValue) {
		for (Option option : values) {
			if (option.getValue().equals(optionValue))
				return true;
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return values == null || values.size() == 0;
	}
	
	public void setValue(Option option) {
		this.values.clear();
		addOption(option);
	}

}