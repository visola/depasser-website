package br.com.depasser.content.service.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.depasser.content.Content;
import br.com.depasser.content.ContentIdentification;
import br.com.depasser.content.Field;
import br.com.depasser.content.Template;
import br.com.depasser.content.ValidationException;
import br.com.depasser.content.service.ContentException;
import br.com.depasser.content.service.ContentService;

public class ContentServiceImpl implements ContentService {

	protected EntityManagerFactory managerFactory;

	public ContentServiceImpl(EntityManagerFactory managerFactory) {
		super();
		this.managerFactory = managerFactory;
	}

	@Override
	public void addToContent(Field f, Content content) throws ContentException, ValidationException {
		content.addField(f);
		saveContent(content);
	}

	@Override
	public void addToContent(Field f, ContentIdentification id) throws ContentException, ValidationException {
		addToContent(f, loadContent(id));
	}

	@Override
	public void addToContent(Field f, int id, String language) throws ContentException, ValidationException {
		addToContent(f, loadContent(id, language));
	}
	
	@Override
	public void deleteContent(Content content) throws ContentException {
		if (content == null) return;
		EntityManager manager = managerFactory.createEntityManager();
		try {
			try {
				deleteContent(manager.find(Content.class, content.getIdentification()), manager);
			} catch (NoResultException nre) {}
		} finally {
			manager.close();
		}
	}

	private void deleteContent(Content content, EntityManager manager) throws ContentException {
		EntityTransaction transaction = null;
		try {
			if (content != null) {
				transaction = manager.getTransaction();
				transaction.begin();
				if (!manager.contains(content)) {
					manager.refresh(content);
				}
				manager.remove(content);
				transaction.commit();
			}
		} catch (Exception e) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new ContentException("Error while deleting content.", e);
		}
	}

	@Override
	public void deleteContent(ContentIdentification id) throws ContentException {
		if (id == null) return;
		EntityManager manager = managerFactory.createEntityManager();
		try {
			try {
				deleteContent(manager.find(Content.class, id), manager);
			} catch (NoResultException nre) {}
		} finally {
			manager.close();
		}
	}

	@Override
	public void deleteContent(int id, String language) throws ContentException {
		if (id <= 0 || language == null) return;
		ContentIdentification contentId = new ContentIdentification(id, language);
		EntityManager manager = managerFactory.createEntityManager();
		try {
			try {
				deleteContent(manager.find(Content.class, contentId), manager);
			} catch (NoResultException nre) {}
		} finally {
			manager.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Content> findContentByTitle(String title) throws ContentException {
		EntityManager manager = managerFactory.createEntityManager();
		try {
			Query q = manager.createQuery("SELECT c FROM Content c WHERE c.title = :title");
			q.setParameter("title", title);
			return q.getResultList();
		} catch (Exception e) {
			throw new ContentException("Error while finding languages.", e);
		} finally {
			manager.close();
		}
	}

	@Override
	public List<Content> findContentByType(int templateId) throws ContentException {
		return findContentByType(templateId, null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Content> findContentByType(int templateId, String language) throws ContentException {
		EntityManager manager = managerFactory.createEntityManager();
		try {
			StringBuilder sQuery = new StringBuilder();
			sQuery.append("SELECT c FROM Content c");
			sQuery.append(" WHERE c.template.id = :templateId");
			
			if (language != null && !"".equals(language)) {
				sQuery.append(" AND c.language = :language");
			}
			
			Query q = manager.createQuery(sQuery.toString());
			q.setParameter("templateId", templateId);
			
			if (language != null && !"".equals(language)) {
				q.setParameter("language", language);
			}
			
			return q.getResultList();
		} catch (Exception e) {
			throw new ContentException("Error while finding content by template.", e);
		} finally {
			manager.close();
		}
	}
	
	@Override
	public List<Content> findContentByType(Template template) throws ContentException {
		return findContentByType(template.getId());
	}

	@Override
	public List<Content> findContentByType(Template template, String language) throws ContentException {
		return findContentByType(template.getId(), language);
	}

	@Override
	public List<String> findTitleByType(int templateId) throws ContentException {
		return findTitleByType(templateId, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findTitleByType(int templateId, String language) throws ContentException {
		EntityManager manager = managerFactory.createEntityManager();
		try {
			StringBuilder sQuery = new StringBuilder();
			sQuery.append("SELECT c.title FROM Content c WHERE c.template.id = :templateId");
			
			if (language != null && !"".equals(language)) {
				sQuery.append(" AND c.language = :language");
			}
			
			Query q = manager.createQuery(sQuery.toString());
			q.setParameter("templateId", templateId);
			
			if (language != null && !"".equals(language)) {
				q.setParameter("language", language);
			}
			
			return q.getResultList();
		} catch (Exception e) {
			throw new ContentException("Error while loading titles by type.", e);
		} finally {
			manager.close();
		}
	}

	@Override
	public List<String> findTitleByType(Template template) throws ContentException {
		return findTitleByType(template.getId());
	}

	@Override
	public List<String> findTitleByType(Template template, String language) throws ContentException {
		return findTitleByType(template.getId(), language);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Content> getAll() throws ContentException {
		EntityManager manager = null;
		try {
			manager = managerFactory.createEntityManager();
			Query q = manager.createQuery("select c from Content c");
			return q.getResultList();
		} catch (Exception e) {
			throw new ContentException("Error while retrieving all content.", e);
		} finally {
			if (manager != null) manager.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllLanguages() throws ContentException {
		EntityManager manager = managerFactory.createEntityManager();
		try {
			Query q = manager.createQuery("SELECT DISTINCT c.id.language FROM Content c");
			return q.getResultList();
		} catch (Exception e) {
			throw new ContentException("Error while loading titles.", e);
		} finally {
			manager.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllTitles() throws ContentException {
		EntityManager manager = managerFactory.createEntityManager();
		try {
			Query q = manager.createQuery("SELECT c.title FROM Content c");
			return q.getResultList();
		} catch (Exception e) {
			throw new ContentException("Error while loading titles.", e);
		} finally {
			manager.close();
		}
	}

	@Override
	public List<Content> getChildContent(ContentIdentification parentId) throws ContentException {
		return getChildContent(parentId.getId(), parentId.getLanguage());
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Content> getChildContent(int parentId, String parentLanguage) throws ContentException {
		EntityManager manager = null;
		try {
			StringBuilder sQuery = new StringBuilder();
			sQuery.append("select c from Content c");
			sQuery.append(" where c.id in (");
			sQuery.append(" select pf.content.id from ParentField pf");
			sQuery.append(" where pf.value.id = :id");
			if (parentLanguage != null) {
				sQuery.append(" and pf.value.language = :language");
			}
			sQuery.append(")");
			
			manager = this.managerFactory.createEntityManager();
			Query q = manager.createQuery(sQuery.toString());
			q.setParameter("id", parentId);
			if (parentLanguage != null) {
				q.setParameter("language", parentLanguage);
			}
			
			return q.getResultList();
		} catch (Exception ex) {
			throw new ContentException("Error while retrieving content children.", ex);
		} finally {
			if (manager != null) {
				manager.close();
			}
		}
	}

	@Override
	public long getChildContentCount(ContentIdentification parentId) throws ContentException {
		return getChildContentCount(parentId.getId(), parentId.getLanguage());
	}

	@Override
	public long getChildContentCount(int parentId, String parentLanguage) throws ContentException {
		EntityManager manager = null;
		try {
			StringBuilder sQuery = new StringBuilder();
			sQuery.append("select count(c) from Content c");
			sQuery.append(" where c.id in (");
			sQuery.append(" select pf.content.id from ParentField pf");
			sQuery.append(" where pf.value.id = :id");
			if (parentLanguage != null) {
				sQuery.append(" and pf.value.language = :language");
			}
			sQuery.append(")");
			
			manager = this.managerFactory.createEntityManager();
			Query q = manager.createQuery(sQuery.toString());
			q.setParameter("id", parentId);
			if (parentLanguage != null) {
				q.setParameter("language", parentLanguage);
			}
			
			return (Long) q.getSingleResult();
		} catch (Exception ex) {
			throw new ContentException("Error while retrieving content children.", ex);
		} finally {
			if (manager != null) {
				manager.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getLanguages(int id) throws ContentException {
		EntityManager manager = managerFactory.createEntityManager();
		try {
			Query q = manager.createQuery("SELECT c.id.language FROM Content c WHERE c.id.id = :id");
			q.setParameter("id", id);
			return q.getResultList();
		} catch (Exception e) {
			throw new ContentException("Error while loading languages.", e);
		} finally {
			manager.close();
		}
	}

	@Override
	public Content loadContent(ContentIdentification id) throws ContentException {
		EntityManager manager = null;
		try {
			manager = managerFactory.createEntityManager();
			return manager.find(Content.class, id);
		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			throw new ContentException("Error while loading content.", e);
		} finally {
			if (manager != null) {
				manager.close();
			}
		}
	}

	@Override
	public Content loadContent(int id, String language) throws ContentException {
		ContentIdentification contentId = new ContentIdentification(id, language);
		return loadContent(contentId);
	}

	@Override
	public Content saveContent(Content c) throws ContentException, ValidationException {
		EntityManager manager = managerFactory.createEntityManager();
		EntityTransaction transaction = null;
		try {
			c.validate();
			transaction = manager.getTransaction();
			transaction.begin();
			
			if (c.getId() != 0) {
				Query q = manager.createQuery("delete from Field f where f.content.id = :contentId and f.content.language = :contentLanguage");
				q.setParameter("contentId", c.getId());
				q.setParameter("contentLanguage", c.getLanguage());
				q.executeUpdate();
			}
			
			c = manager.merge(c);
			transaction.commit();
			return c;
		} catch (ValidationException ve) {
			throw ve;
		} catch (Exception e) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new ContentException("Error while savind content.", e);
		} finally {
			manager.close();
		}
	}

}