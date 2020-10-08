package br.com.depasser.content.field;

import br.com.depasser.content.Content;
import br.com.depasser.content.Field;

public class ParentField extends Field {

	protected Content value;

	@Override
	public Content getValue() {
		return value;
	}

	public void setValue(Content value) {
		if (value != null && value.equals(getContent())) {
			throw new IllegalArgumentException("Content can not be parent of itself.");
		}
		this.value = value;
	}

}
