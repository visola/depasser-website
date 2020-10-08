package br.com.depasser.web.spring;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CustomCalendarEditor extends PropertyEditorSupport {
	
	private final boolean allowEmpty;
	private final SimpleDateFormat formatter;

	public CustomCalendarEditor(SimpleDateFormat dateFormat, boolean allowEmpty) {
		this.allowEmpty = allowEmpty;
		
		if (dateFormat == null) {
			dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		}
		this.formatter = dateFormat;
	}

	@Override
	public String getAsText() {
		if (getValue() != null) { 
			return formatter.format( ( (Calendar) getValue() ).getTime() );
		} else {
			return "";
		}
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (text == null || text.trim().equals("")) {
			if ( allowEmpty ) {
				setValue(null);
			} else {
				throw new IllegalArgumentException("Editor does not suppport empty values");
			}
		} else {
			try {
				Date date = formatter.parse(text);
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				setValue(c);
			} catch (ParseException pe) {
				throw new IllegalArgumentException("Invalid date format: " + text);
			}
		}
	}
	
}