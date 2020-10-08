package br.com.depasser.content;

public class Type {

	protected int id;
	protected String name, value;
	protected boolean required;
	protected int length;

	public Type() {
		super();
	}
	public Type(int id, String name, String value, boolean required, int length) {
		super();
		this.id = id;
		this.name = name;
		this.required = required;
		this.length = length;
		this.value = value;
	}

	public Type(String name, String value) {
		this(0, name, value, false, 0);
	}

	public Type(String name, String value, boolean required) {
		this(0, name, value, required, 0);
	}
	
	public Type(String name, String value, boolean required, int length) {
		this(0, name, value, required, length);
	}

	public Type(String name, String value, int length) {
		this(0, name, value, false, length);
	}

	public Field createField() throws ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> c = Class.forName(value);
		Field f = (Field) c.newInstance();
		f.setType(this);
		return f;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Type other = (Type) obj;
		if (id != other.id)
			return false;
		if (length != other.length)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (required != other.required)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public int getId() {
		return id;
	}

	public int getLength() {
		return length;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + length;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	public boolean isRequired() {
		return required;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return name + (required ? " (required)" : "");
	}

}