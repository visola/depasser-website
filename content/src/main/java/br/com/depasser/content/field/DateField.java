package br.com.depasser.content.field;

import java.text.DateFormat;
import java.util.Calendar;

import br.com.depasser.content.DateType;
import br.com.depasser.content.Field;
import br.com.depasser.content.Type;
import br.com.depasser.content.ValidationException;

public class DateField extends Field {

	protected Calendar value;
	protected DateFormat format = DateFormat.getDateInstance();

	@Override
	public boolean acceptType(Type type) {
		return type instanceof DateType;
	}

	@Override
	public Calendar getValue() {
		return value;
	}

	public void setValue(Calendar value) {
		this.value = value;
	}

	@Override
	public void validate() throws ValidationException {
		super.validate();
		
		DateType dateType = (DateType) type;
		
		// Validate before date
		if (dateType.getBefore() != null) {
			if (dateType.getBefore().after(value)) {
				throw new ValidationException("Invalid date, must be before: " + format.format(dateType.getBefore().getTime()));
			}
		}
		
		// Validate after date
		if (dateType.getAfter() != null) {
			if (dateType.getAfter().before(value)) {
				throw new ValidationException("Invalid date, must be after: " + format.format(dateType.getAfter().getTime()));
			}
		}
	}

}