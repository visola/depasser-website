package br.com.depasser.content;

public abstract class Field {

	protected int id;
	protected Content content;
	protected Type type;
	
	public boolean acceptType(Type type) {
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Field other = (Field) obj;
		if (id != other.id)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public Content getContent() {
		return content;
	}

	public int getId() {
		return id;
	}

	public Type getType() {
		return type;
	}

	public abstract Object getValue();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	public boolean isEmpty() {
		return getValue() == null;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setType(Type type) {
		if (!acceptType(type)) {
			throw new IllegalArgumentException("Type not supported by this field: " + type.getClass().getName());
		}
		this.type = type;
	}

	@Override
	public String toString() {
		return "Field of type: " + type.toString();
	}

	public void validate() throws ValidationException  {
		if (type.required && isEmpty()) {
			throw new EmptyFieldException("Field is required but is empty: " + type.getName());
		}
	}

}