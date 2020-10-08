package br.com.depasser.content.web.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import br.com.depasser.web.spring.AbstractController;

@Controller
public class RootController extends AbstractController {
	
	@RequestMapping("/index")
	public String goToIndex() {
		return "index";
	}
	
	@RequestMapping("/contact") 
	public ModelAndView receiveContact (String name, String email, String message, Locale locale) throws Exception {
		ModelAndView mv = new ModelAndView();
		
		if ("".equals(name.trim()) || "".equals(email.trim()) || "".equals(message.trim())) {
			mv.addObject("message", ResourceBundle.getBundle("keys", locale).getString("contact.message.failed"));
		} else {
			// Save data to a file
			String userFolder = System.getProperty("user.home");
			File f = new File(userFolder, "message/" + System.nanoTime() + ".txt");
			if (!f.getParentFile().exists()) f.getParentFile().mkdirs();
			
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
			
			out.write("Name:");
			out.write(name);
			out.newLine();
			
			out.write("Email:");
			out.write(email);
			out.newLine();
			
			out.write("Message:");
			out.newLine();
			out.write("-------------------");
			out.newLine();
			out.write(message);
			out.newLine();
			out.write("-------------------");
			
			out.flush();
			out.close();
			
			mv.addObject("message", ResourceBundle.getBundle("keys", locale).getString("contact.message.sucess"));
		}
		
		return mv;
	}

}