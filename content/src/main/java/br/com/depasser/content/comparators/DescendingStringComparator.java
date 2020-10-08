package br.com.depasser.content.comparators;

import java.util.Comparator;

public class DescendingStringComparator implements Comparator<String> {

	@Override
	public int compare(String s1, String s2) {
		if (s1 == null) throw new NullPointerException("s1 must not be null.");
		if (s2 == null) throw new NullPointerException("s2 must not be null.");
		return - s1.compareTo(s2);
	}

}