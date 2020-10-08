package br.com.depasser.content.field;

import java.math.BigDecimal;

import br.com.depasser.content.Field;

public class NumberField extends Field {
	
	protected BigDecimal value = null;

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

}