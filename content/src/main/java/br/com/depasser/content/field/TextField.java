package br.com.depasser.content.field;

import br.com.depasser.content.Field;

public class TextField extends Field {

	protected String value;

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public boolean isEmpty() {
		return value == null || value.trim().equals("");
	}

	public void setValue(String value) {
		this.value = value;
	}

}