package br.com.depasser.content.web.service;

import br.com.depasser.content.ContentIdentification;
import br.com.depasser.content.service.ContentException;
import br.com.depasser.content.service.ContentService;

public interface BlogService extends ContentService {

	public long getCommentCount(ContentIdentification identification) throws ContentException;
	
	public long getCommentCount(int parentId, String parentLanguage) throws ContentException;
	
}