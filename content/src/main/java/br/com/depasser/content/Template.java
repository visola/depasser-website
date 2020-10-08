package br.com.depasser.content;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Template {

	protected int id;
	protected String name;
	protected List<Type> types = new ArrayList<Type>();
	
	public Template() {
		super();
	}
	
	public Template(String name) {
		super();
		this.name = name;
	}
	
	public Template(int id, String name) {
		super();
		this.name = name;
		this.id = id;
	}

	public void addType(Type type) {
		if (type != null) {
			types.add(type);
		}
	}

	public Content createEmptyContent() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Content c = new Content();
		c.setTemplate(this);
		for (Type type : types) {
			c.addField(type.createField());
		}
		return c;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Template other = (Template) obj;
		if (id != other.id)
			return false;
		if (types == null) {
			if (other.types != null)
				return false;
		} else if (!types.equals(other.types))
			return false;
		return true;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Type[] getTypes() {
		return types.toArray(new Type[types.size()]);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((types == null) ? 0 : types.hashCode());
		return result;
	}

	public void removeType(String typeName) {
		if (typeName == null) return;
		for (Iterator<Type> i = types.iterator(); i.hasNext();) {
			Type t = i.next();
			if (t.getName().equals(typeName)) {
				i.remove();
			}
		}
	}

	public void removeType(Type type) {
		if (type != null) {
			types.remove(type);
		}
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setTypes(Type[] types) {
		this.types = new ArrayList<Type>();
		for (Type t : types) {
			addType(t);
		}
	}

}