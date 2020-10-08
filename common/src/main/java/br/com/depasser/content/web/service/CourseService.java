package br.com.depasser.content.web.service;

import java.util.List;

import br.com.depasser.content.Content;
import br.com.depasser.content.service.ContentException;
import br.com.depasser.content.service.ContentService;

public interface CourseService extends ContentService {
	
	public List<Content> getNextClasses(String language) throws ContentException;

}