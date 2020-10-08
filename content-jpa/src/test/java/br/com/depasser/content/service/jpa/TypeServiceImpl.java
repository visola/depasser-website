package br.com.depasser.content.service.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.depasser.content.Type;
import br.com.depasser.content.service.ContentException;
import br.com.depasser.content.service.TypeService;

public class TypeServiceImpl implements TypeService {

	protected EntityManagerFactory managerFactory;

	public TypeServiceImpl () {
		super();
	}

	public TypeServiceImpl(EntityManagerFactory managerFactory) {
		super();
		this.managerFactory = managerFactory;
	}

	@Override
	public void deleteType(int id) throws ContentException {
		if (id <= 0) return;
		EntityManager manager = managerFactory.createEntityManager();
		try {
			try {
				deleteType(manager.find(Type.class, id), manager); 
			} catch (NoResultException nre) {}
		} catch (ContentException ce) {
			throw ce;
		} catch (Exception e) {
			throw new ContentException("Error while deleting type.", e);
		} finally {
			manager.close();
		}
	}

	@Override
	public void deleteType(Type type) throws ContentException {
		if (type == null) return;
		EntityManager manager = managerFactory.createEntityManager();
		try {
			try {
				deleteType(manager.find(Type.class, type.getId()), manager); 
			} catch (NoResultException nre) {}
		} catch (ContentException ce) {
			throw ce;
		} catch (Exception e) {
			throw new ContentException("Error while deleting type.", e);
		} finally {
			manager.close();
		}
	}
	
	private void deleteType(Type type, EntityManager manager) throws ContentException {
		EntityTransaction transaction = null;
		try {
			if (type == null) return;

			transaction = manager.getTransaction();
			transaction.begin();
			if (!manager.contains(type)) {
				manager.refresh(type);
			}
			manager.remove(type);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new ContentException("Error while deleting type.", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Type> getAllTypes() throws ContentException {
		EntityManager manager = managerFactory.createEntityManager();
		try {
			Query query = manager.createQuery("SELECT t FROM Type t");
			return query.getResultList();
		} catch (Exception e) {
			throw new ContentException("Error while retrieving types.", e);
		} finally {
			manager.close();
		}
	}

	@Override
	public Type loadType(int id) throws ContentException {
		EntityManager manager = managerFactory.createEntityManager();
		try {
			return manager.find(Type.class, id);
		} catch (Exception e) {
			throw new ContentException("Error while loading type.", e);
		} finally {
			manager.close();
		}
	}

	@Override
	public Type saveType(Type type) throws ContentException {
		EntityManager manager = managerFactory.createEntityManager();
		EntityTransaction transaction = null;
		try {
			transaction = manager.getTransaction();
			transaction.begin();
			type = manager.merge(type);
			transaction.commit();
			return type;
		} catch (Exception e) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new ContentException("Error while saving type.", e);
		} finally {
			manager.close();
		}
	}

}
