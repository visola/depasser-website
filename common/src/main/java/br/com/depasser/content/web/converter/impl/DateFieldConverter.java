package br.com.depasser.content.web.converter.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.depasser.content.Field;
import br.com.depasser.content.Type;
import br.com.depasser.content.field.DateField;
import br.com.depasser.content.web.converter.ConversionException;
import br.com.depasser.content.web.converter.FieldConverter;

public class DateFieldConverter extends FieldConverter {
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat timestampformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	@SuppressWarnings("unchecked")
	private static Class<? extends Field> [] classes = new Class[] {DateField.class};

	@Override
	public Field convert(Type type, String value) throws ConversionException {		
		DateField dateField = (DateField) getField(type);
		
		if (value == null) dateField.setValue(null);
		
		Calendar calendar = Calendar.getInstance();
		
		try {
			if (value.length() == 10) {
				calendar.setTime(dateFormatter.parse(value));
			} else {
				calendar.setTime(timestampformatter.parse(value));
			}
		} catch (ParseException pe) {
			throw new ConversionException("Error while parsing date: '" + value + "'", pe);
		}
		
		dateField.setValue(calendar);
		return dateField;
	}

	@Override
	public Class<? extends Field>[] getClasses() {
		return classes;
	}

	public SimpleDateFormat getDateFormatter() {
		return dateFormatter;
	}

	public SimpleDateFormat getTimestampformatter() {
		return timestampformatter;
	}

	public void setDateFormatter(SimpleDateFormat dateFormatter) {
		this.dateFormatter = dateFormatter;
	}

	public void setTimestampformatter(SimpleDateFormat timestampformatter) {
		this.timestampformatter = timestampformatter;
	}
	
}