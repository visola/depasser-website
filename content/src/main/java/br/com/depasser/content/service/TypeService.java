package br.com.depasser.content.service;

import java.util.List;

import br.com.depasser.content.Type;

public interface TypeService {

	public void deleteType(int id) throws ContentException;

	public void deleteType(Type type) throws ContentException;
	
	public List<Type> getAllTypes() throws ContentException;
	
	public Type loadType(int id) throws ContentException;
	
	public Type saveType(Type type) throws ContentException;
}
