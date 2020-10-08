package br.com.depasser.content.service;

import java.util.List;

import br.com.depasser.content.Content;
import br.com.depasser.content.ContentIdentification;
import br.com.depasser.content.Field;
import br.com.depasser.content.Template;
import br.com.depasser.content.ValidationException;

public interface ContentService {

	public void addToContent(Field f, Content content) throws ContentException, ValidationException;

	public void addToContent(Field f, ContentIdentification id) throws ContentException, ValidationException;

	public void addToContent(Field f, int id, String language) throws ContentException, ValidationException;

	public void deleteContent(Content content) throws ContentException;

	public void deleteContent(ContentIdentification id) throws ContentException;

	public void deleteContent(int id, String language) throws ContentException;

	public List<Content> findContentByTitle(String title) throws ContentException;

	public List<Content> findContentByType(int templateId) throws ContentException;

	public List<Content> findContentByType(Template template) throws ContentException;
	
	public List<Content> findContentByType(int templateId, String language) throws ContentException;

	public List<Content> findContentByType(Template template, String language) throws ContentException;

	public List<String> findTitleByType(int templateId) throws ContentException;

	public List<String> findTitleByType(int templateId, String language) throws ContentException;

	public List<String> findTitleByType(Template template) throws ContentException;

	public List<String> findTitleByType(Template template, String language) throws ContentException;
	
	public List<Content> getAll() throws ContentException;

	public List<String> getAllLanguages() throws ContentException;

	public List<String> getAllTitles() throws ContentException;
	
	public List<Content> getChildContent(ContentIdentification id) throws ContentException;
	
	public List<Content> getChildContent(int id, String language) throws ContentException;
	
	public long getChildContentCount(ContentIdentification id) throws ContentException;
	
	public long getChildContentCount(int id, String language) throws ContentException;

	public List<String> getLanguages(int id) throws ContentException;

	public Content loadContent(ContentIdentification id) throws ContentException;

	public Content loadContent(int id, String language) throws ContentException;

	public Content saveContent(Content c) throws ContentException, ValidationException;

}