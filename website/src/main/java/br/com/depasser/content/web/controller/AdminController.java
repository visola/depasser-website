package br.com.depasser.content.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import br.com.depasser.content.Content;
import br.com.depasser.content.Field;
import br.com.depasser.content.Template;
import br.com.depasser.content.Type;
import br.com.depasser.content.ValidationException;
import br.com.depasser.content.service.ContentService;
import br.com.depasser.content.service.TemplateService;
import br.com.depasser.content.service.TypeService;
import br.com.depasser.content.web.converter.ConversionException;
import br.com.depasser.content.web.converter.FieldManager;
import br.com.depasser.web.spring.AbstractController;

@Controller
@RequestMapping("/admin")
public class AdminController extends AbstractController {
	
	@Autowired
	private ContentService contentService;
	
	@Autowired
	private TemplateService templateService;
	
	@Autowired
	private TypeService typeService;
	
	@Autowired
	private FieldManager fieldManager;

	@RequestMapping("/edit")
	public ModelAndView edit() throws Exception {
		ModelAndView mv = new ModelAndView();
		return mv;
	}
	
	@RequestMapping("/content")
	public Object getContent(Integer id, String language) throws Exception {
		if (id != null) {
			return contentService.loadContent(id, language);
		} else {
			List<Content> all = contentService.getAll();
			
			ModelAndView mv = new ModelAndView();
			
			List<Map<String, Object>> contentMap = new ArrayList<Map<String, Object>>();
			mv.addObject("contentList", contentMap);
			
			for (Content c : all) {
				Map<String, Object> cMap = new HashMap<String, Object>();
				cMap.put("title", c.getTitle());
				cMap.put("language", c.getLanguage());
				cMap.put("id", c.getId());
				cMap.put("template", c.getTemplate().getName());
				cMap.put("templateId", c.getTemplate().getId());
				
				contentMap.add(cMap);
			}
			
			return mv;
		}
	}
	
	@RequestMapping("/templates")
	public ModelAndView getTemplates() throws Exception {
		List<Template> templates = templateService.getAllTemplates();
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("templates", templates);
		return mv;
	}
	
	@RequestMapping("/save")
	public ModelAndView save(Integer templateId, Integer contentId, String language, String title, int [] typeIds, String [] values) throws Exception {
		if (typeIds.length != values.length) {
			throw new Exception("For each value, a type must be provided.");
		}
		
		Template template = templateService.loadTemplate(templateId);
		
		Content content = new Content();
		content.setLanguage(language);
		content.setTitle(title);
		content.setTemplate(template);
		if (contentId != null) content.setId(contentId);
		
		List<Exception> exceptions = new ArrayList<Exception>();
		for (int i = 0; i < values.length; i++) {
			String value = values[i];
			Type type = typeService.loadType(typeIds[i]);
			
			try {
				Field field = fieldManager.convert(type, value);
				content.addField(field);
				field.validate();
			} catch (ValidationException exception) {
				exceptions.add(exception);
			} catch (ConversionException exception) {
				exceptions.add(exception);
			}
		}
		
		ModelAndView mv = new ModelAndView("redirect:/admin/edit.do");
		
		if (exceptions.size() != 0) {
			mv.setViewName("/error/messages");
			mv.addObject("exceptions", exceptions);
			return mv;
		}
		
		content.validate();
		content = contentService.saveContent(content);
		
		mv.addObject("content", content);
		return mv;
	}
	
}