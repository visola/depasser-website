package br.com.depasser.content.service;

import java.util.List;

import br.com.depasser.content.Template;

/**
 * <p>
 * Service interface to control {@link Template templates}.
 * </p>
 *
 * @author Vinicius Isola
 */
public interface TemplateService {

	public void deleteTemplate(int id) throws ContentException;

	public void deleteTemplate(Template t) throws ContentException;

	public List<Template> getAllTemplates() throws ContentException;

	public Template loadTemplate(int id) throws ContentException;

	public Template loadTemplateByName(String page) throws ContentException;

	public Template saveTemplate(Template t) throws ContentException;

}