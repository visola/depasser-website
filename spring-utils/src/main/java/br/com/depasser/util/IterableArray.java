package br.com.depasser.util;

import java.util.Iterator;

/**
 * Transform an array into an instance of iterable.
 * 
 * @author Vinicius Isola (viniciusisola@gmail.com)
 */
public class IterableArray implements Iterable<Object> {

	private Object[] array;

	public IterableArray(Object[] array) {
		this.array = array;
	}

	@Override
	public Iterator<Object> iterator() {
		return new Iterator<Object>() {
			int index = 0;

			@Override
			public boolean hasNext() {
				return index < array.length;
			}

			@Override
			public Object next() {
				return array[index++];
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException(
						"Operation not supported: remove");
			}

		};
	}

}