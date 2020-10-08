package br.com.depasser.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class OptionType extends Type {

	protected List<Option> options = new ArrayList<Option>();

	public OptionType() {
		super();
	}

	public OptionType(int id, String name, String value, boolean required, int length) {
		super(id, name, value, required, length);
	}

	public OptionType(String name, String value) {
		super(name, value);
	}

	public OptionType(String name, String value, boolean required) {
		super(name, value, required);
	}

	public OptionType(String name, String value, boolean required, int length) {
		super(name, value, required, length);
	}

	public OptionType(String name, String value, Collection<Option> options) {
		super(name, value);
		for (Option option : options) {
			addOption(option);
		}
	}

	public OptionType(String name, String value, int length) {
		super(name, value, length);
	}
	
	public OptionType(String name, String value, Option[] options) {
		super(name, value);
		for (Option option : options) {
			addOption(option);
		}
	}
	
	public void addOption(Option option) {
		if (option != null) {
			options.add(option);
			option.setType(this);
		}
	}

	public Option[] getOptions() {
		return options.toArray(new Option[options.size()]);
	}
	
	public void removeOption(Option option) {
		if (option != null) {
			options.remove(option);
			option.setType(null);
		}
	}

}