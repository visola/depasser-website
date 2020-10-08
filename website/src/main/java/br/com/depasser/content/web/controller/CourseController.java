package br.com.depasser.content.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import br.com.depasser.content.Content;
import br.com.depasser.content.Option;
import br.com.depasser.content.comparators.DateFieldComparator;
import br.com.depasser.content.field.MultiValueField;
import br.com.depasser.content.field.ParentField;
import br.com.depasser.content.web.service.CourseService;
import br.com.depasser.web.spring.AbstractController;

@Controller
@RequestMapping("/course")
public class CourseController extends AbstractController {
	
	@Autowired
	private CourseService courseService;

	@RequestMapping("/all")
	public ModelAndView all(Locale locale) throws Exception {
		ModelAndView mv = new ModelAndView();
		
		// Load courses
		List<Content> courses = courseService.findContentByType(1, locale.toString());
		
		// Store all contents by its categories
		Map<String, List<String>> titlesByCategory = new TreeMap<String, List<String>>();
		
		Integer [] ids = new Integer[courses.size()];
		String [] titles = new String[courses.size()];
		
		int counter = 0;
		for (Content c : courses) {
			ids[counter] = c.getId();
			titles[counter] = c.getTitle();
			
			MultiValueField categories = (MultiValueField) c.getField("Course Categories");
			for (Option option : categories.getValues()) {
				List<String> category = titlesByCategory.get(option.getName());
				if (category == null) {
					category = new ArrayList<String>();
					titlesByCategory.put(option.getName(), category);
				}
				category.add(c.getTitle());
			}
			
			counter++;
		}
		
		mv.addObject("ids", ids);
		mv.addObject("titles", titles);
		mv.addObject("categories", titlesByCategory);
		return mv;
	}
	
	@RequestMapping("/next")
	public ModelAndView getNextClasses(Locale locale) throws Exception {
		List<Content> nextClasses = new ArrayList<Content>(courseService.getNextClasses(locale.toString()));
		Collections.sort(nextClasses, new DateFieldComparator("Start"));
		
		List<Map<String, Object>> next = new ArrayList<Map<String, Object>>();
		
		for (Content clas : nextClasses) {
			Map<String, Object> classInfo = new HashMap<String, Object>();
			next.add(classInfo);
			
			classInfo.put("start", clas.getField("Start").getValue());
			Content course = ( (ParentField) clas.getField("Parent") ).getValue();
			classInfo.put("title", course.getTitle());
			classInfo.put("id", course.getId());
			classInfo.put("language", course.getLanguage());
		}
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("nextClasses", next);
		return mv;
	}
	
	@RequestMapping("/load")
	public ModelAndView load(Integer courseId, Locale locale) throws Exception {
		Content course = courseService.loadContent(courseId, locale.toString());
		
		ModelAndView mv = new ModelAndView();
		
		mv.addObject("title", course.getTitle());
		mv.addObject("description", course.getField("Description").getValue());
		mv.addObject("content", course.getField("Content").getValue());
		mv.addObject("created", course.getField("Created").getValue());
		
		// Categories
		List<String> categories = new ArrayList<String>();
		for (Option category : ( (MultiValueField) course.getField("Course Categories") ).getValues()) {
			categories.add(category.getName());
		}
		mv.addObject("categories", categories);
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		// Classes
		List<Content> classes = courseService.getChildContent(courseId, locale.toString());
		List<Map<String, Object>> courseClasses = new ArrayList<Map<String, Object>>();
		Calendar today = Calendar.getInstance();
		for (Content c : classes) {
			Calendar start = (Calendar) c.getField("Start").getValue();
			if (start.after(today)) {
				Map<String, Object> classMap = new HashMap<String, Object>();
				classMap.put("start", formatter.format(((Calendar) c.getField("Start").getValue()).getTime()));
				classMap.put("end", formatter.format(((Calendar) c.getField("End").getValue()).getTime()));
				classMap.put("schedule", c.getField("Schedule").getValue());
				courseClasses.add(classMap);
			}
		}
		mv.addObject("classes", courseClasses);
		
		return mv;
	}
	
}