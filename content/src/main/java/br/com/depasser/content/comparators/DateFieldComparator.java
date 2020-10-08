package br.com.depasser.content.comparators;

import java.util.Calendar;
import java.util.Comparator;

import br.com.depasser.content.Content;
import br.com.depasser.content.Field;
import br.com.depasser.content.field.DateField;

public class DateFieldComparator implements Comparator<Content> {
	
	private final boolean ascending;
	private final String fieldName;
	
	public DateFieldComparator(String fieldName) {
		super();
		this.fieldName = fieldName;
		ascending = true;
	}

	public DateFieldComparator(String fieldName, boolean ascending) {
		super();
		this.fieldName = fieldName;
		this.ascending = ascending;
	}
	
	@Override
	public int compare(Content content1, Content content2) {
		if (content1 == null) {
			throw new NullPointerException("content1 is null.");
		}
		
		Field date1 = content1.getField(fieldName);
		if (date1 == null) {
			throw new NullPointerException("content1 has no " + fieldName + " field.");
		}
		
		if (content2 == null) {
			throw new NullPointerException("content2 is null.");
		}
		
		Field date2 = content2.getField(fieldName);
		if (date2 == null) {
			throw new NullPointerException("content2 has no " + fieldName + " field.");
		}
		
		Calendar c1 = ((DateField) date1).getValue();
		Calendar c2 = ((DateField) date2).getValue();
		if (ascending) {
			return c1.compareTo(c2);
		} else {
			return - c1.compareTo(c2);
		}
	}

}
