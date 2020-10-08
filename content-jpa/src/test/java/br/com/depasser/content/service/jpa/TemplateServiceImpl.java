package br.com.depasser.content.service.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.depasser.content.Template;
import br.com.depasser.content.service.ContentException;
import br.com.depasser.content.service.TemplateService;

public class TemplateServiceImpl implements TemplateService {

	protected EntityManagerFactory managerFactory;

	public TemplateServiceImpl () {
		super();
	}

	public TemplateServiceImpl(EntityManagerFactory managerFactory) {
		super();
		this.managerFactory = managerFactory;
	}

	@Override
	public void deleteTemplate(int id) throws ContentException {
		if (id <= 0) return;
		EntityManager manager = managerFactory.createEntityManager();
		try {
			try {
				deleteTemplate(manager.find(Template.class, id), manager);
			} catch (NoResultException nre) {}
		} finally {
			manager.close();
		}
	}

	@Override
	public void deleteTemplate(Template t) throws ContentException {
		if (t == null) return;
		EntityManager manager = managerFactory.createEntityManager();
		try {
			try {
				deleteTemplate(manager.find(Template.class, t.getId()), manager);
			} catch (NoResultException nre) {}
		} finally {
			manager.close();
		}
	}
	
	public void deleteTemplate(Template t, EntityManager manager) throws ContentException {
		EntityTransaction transaction = null;
		try {
			if (t != null) {
				transaction = manager.getTransaction();
				transaction.begin();
				if (!manager.contains(t)) {
					manager.refresh(t);
				}
				manager.remove(t);
				transaction.commit();
			}
		} catch (Exception e) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new ContentException("Error while deleting template.", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Template> getAllTemplates() throws ContentException {
		EntityManager manager = managerFactory.createEntityManager();
		try {
			Query q = manager.createQuery("Select t From Template t");
			return q.getResultList();
		} catch (Exception e) {
			throw new ContentException("Error while loading templates.", e);
		} finally {
			manager.close();
		}
	}

	@Override
	public Template loadTemplate(int id) throws ContentException {
		EntityManager manager = managerFactory.createEntityManager();
		try {
			return manager.find(Template.class, id);
		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			throw new ContentException("Error while loading template.", e);
		} finally {
			manager.close();
		}
	}

	@Override
	public Template saveTemplate(Template t) throws ContentException {
		EntityManager manager = managerFactory.createEntityManager();
		EntityTransaction transaction = null;
		try {
			transaction = manager.getTransaction();
			transaction.begin();
			t = manager.merge(t);
			transaction.commit();
			return t;
		} catch (Exception e) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new ContentException("Error while saving template.", e);
		} finally {
			manager.close();
		}
	}

	@Override
	public Template loadTemplateByName(String name) throws ContentException {
		EntityManager manager = managerFactory.createEntityManager();
		try {
			Query query = manager.createQuery("select t from Template t where t.name = :name");
			query.setParameter("name", name);
			return (Template) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			throw new ContentException("Error while loading template.", e);
		} finally {
			manager.close();
		}
	}

}