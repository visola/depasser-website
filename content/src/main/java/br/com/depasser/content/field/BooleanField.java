package br.com.depasser.content.field;

import br.com.depasser.content.Field;

public class BooleanField extends Field {

	protected Boolean value;

	@Override
	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}

}