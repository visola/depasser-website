package br.com.depasser.content;

import java.util.Calendar;

public class DateType extends Type {
	
	protected Calendar before, after;

	public DateType() {
		super();
	}

	public DateType(int id, String name, String value, boolean required, int length) {
		super(id, name, value, required, length);
	}

	public DateType(String name, String value) {
		super(name, value);
	}

	public DateType(String name, String value, boolean required) {
		super(name, value, required);
	}

	public DateType(String name, String value, boolean required, int length) {
		super(name, value, required, length);
	}

	public DateType(String name, String value, int length) {
		super(name, value, length);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DateType other = (DateType) obj;
		if (after == null) {
			if (other.after != null)
				return false;
		} else if (!after.equals(other.after))
			return false;
		if (before == null) {
			if (other.before != null)
				return false;
		} else if (!before.equals(other.before))
			return false;
		return true;
	}

	public Calendar getAfter() {
		return after;
	}

	public Calendar getBefore() {
		return before;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((after == null) ? 0 : after.hashCode());
		result = prime * result + ((before == null) ? 0 : before.hashCode());
		return result;
	}

	public void setAfter(Calendar after) {
		this.after = after;
	}

	public void setBefore(Calendar before) {
		this.before = before;
	}

}