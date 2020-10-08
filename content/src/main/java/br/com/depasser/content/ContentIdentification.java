package br.com.depasser.content;

public class ContentIdentification {

	protected int id;
	protected String language;

	public ContentIdentification() {
		super();
	}

	public ContentIdentification(int id, String language) {
		super();
		this.id = id;
		this.language = language;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContentIdentification other = (ContentIdentification) obj;
		if (id != other.id)
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		return true;
	}

	public int getId() {
		return id;
	}

	public String getLanguage() {
		return language;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		return result;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
