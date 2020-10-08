package br.com.depasser.content;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Content can contain {@link Field fields}. Each <code>field</code> is responsible to
 * store its data and the only responsability of the content is   
 * </p> 
 * 
 * @author Vinicius Isola
 */
public class Content {

	protected int id;
	protected String language;
	protected String title;
	protected List<Field> fields = new ArrayList<Field>();
	protected Template template;

	public Content() {
		super();
	}

	public Content(int id, String title, String language) {
		super();
		this.id = id;
		this.title = title;
		this.language = language;
	}

	public Content(String title, String language) {
		super();
		this.title = title;
		this.language = language;
	}

	public void addField(Field field) {
		if (field != null) {
			fields.add(field);
			field.setContent(this);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Content other = (Content) obj;
		if (fields == null) {
			if (other.fields != null)
				return false;
		} else if (!fields.equals(other.fields))
			return false;
		if (id != other.id)
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (template == null) {
			if (other.template != null)
				return false;
		} else if (!template.equals(other.template))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	
	public Field getField(String label) {
		for (Field field : fields) {
			if (field.getType() != null && field.getType().getName().equals(label)) {
				return field;
			}
		}
		return null;
	}

	public Field [] getFields() {
		return fields.toArray(new Field[fields.size()]);
	}

	public int getId() {
		return id;
	}

	public ContentIdentification getIdentification() {
		return new ContentIdentification(id, language);
	}

	public String getLanguage() {
		return language;
	}

	public Template getTemplate() {
		return template;
	}


	public String getTitle() {
		return title;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((language == null) ? 0 : language.hashCode());
		result = prime * result
				+ ((template == null) ? 0 : template.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	public void removeField(Field field) {
		if (field != null) {
			fields.remove(field);
			field.setContent(null);
		}
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void validate() throws ValidationException {
		if (template == null) {
			throw new ValidationException("Content has no template, can't validate it.");
		}
		
		TYPE:for (Type type : template.getTypes()) {
			if (type.isRequired()) {
				for (Field f : fields) {
					if (f.getType().equals(type)) {
						f.validate();
						continue TYPE;
					}
				}
				throw new MissingFieldException("Missing required field: " + type.getName());
			}
		}
	}

}