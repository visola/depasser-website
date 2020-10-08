package br.com.depasser.content.web.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import br.com.depasser.content.Content;
import br.com.depasser.content.service.ContentService;
import br.com.depasser.web.spring.AbstractController;

@Controller
@RequestMapping("/content")
public class ContentController extends AbstractController {
	
	@Autowired
	private ContentService contentService;
	
	@RequestMapping("/all")
	public ModelAndView all(Integer templateId, String language) throws Exception {
		ModelAndView mv = new ModelAndView();
		if (templateId == null) {
			mv.setViewName("error/message");
			mv.addObject("message", "No template id passed in.");
			return mv;
		}
		
		List<Content> contents = contentService.findContentByType(templateId, language);
		
		Integer [] ids = new Integer[contents.size()];
		String [] titles = new String[contents.size()];
		
		int counter = 0;
		for (Content c : contents) {
			ids[counter] = c.getId();
			titles[counter] = c.getTitle();
			counter++;
		}
		
		mv.addObject("ids", ids);
		mv.addObject("titles", titles);
		return mv;
	}
	
	@RequestMapping("/load")
	public ModelAndView load(Integer contentId, Locale locale) throws Exception {
		Content content = contentService.loadContent(contentId, locale.toString());
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("content", content);
		return mv;
	}
	
}