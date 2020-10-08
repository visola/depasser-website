package br.com.depasser.web.spring;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

public class CustomWebBindingInitializer implements WebBindingInitializer {

	@Override
	public void initBinder(WebDataBinder binder, WebRequest request) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Calendar.class, new CustomCalendarEditor(formatter, true));
		binder.registerCustomEditor(GregorianCalendar.class, new CustomCalendarEditor(formatter, true));
	}

}
