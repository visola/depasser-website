package br.com.depasser.web.spring.i18n;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class KeysController {
	
	@RequestMapping("/keys")
	public ModelAndView getKeys (Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle("keys", locale);
		
		List<String> keys = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		for (Enumeration<String> ks = bundle.getKeys(); ks.hasMoreElements();) {
			String k = ks.nextElement();
			keys.add(k);
			values.add(bundle.getString(k));
		}
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("keys", keys);
		mv.addObject("values", values);
		return mv;
	}

}