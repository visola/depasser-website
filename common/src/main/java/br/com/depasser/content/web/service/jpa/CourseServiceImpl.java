package br.com.depasser.content.web.service.jpa;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import br.com.depasser.content.Content;
import br.com.depasser.content.service.ContentException;
import br.com.depasser.content.service.jpa.ContentServiceImpl;
import br.com.depasser.content.web.service.CourseService;

public class CourseServiceImpl extends ContentServiceImpl implements CourseService {

	public CourseServiceImpl(EntityManagerFactory managerFactory) {
		super(managerFactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Content> getNextClasses(String language) throws ContentException {
		EntityManager manager = null;
		try {
			manager = managerFactory.createEntityManager();
			
			StringBuilder sQuery = new StringBuilder();
			sQuery.append("SELECT c FROM Content c");
			sQuery.append(" JOIN c.template t");
			sQuery.append(" WHERE c.language = :language");
			sQuery.append(" AND t.name = 'Class'");
			sQuery.append(" AND c.id IN (");
			sQuery.append(" SELECT df.content.id FROM DateField df");
			sQuery.append(" WHERE df.type.name = 'Start'");
			sQuery.append(" AND df.value >= :today");
			sQuery.append(")");
			
			Query query = manager.createQuery(sQuery.toString());
			query.setParameter("language", language);
			query.setParameter("today", Calendar.getInstance());
			
			return query.getResultList();
		} catch (Exception ex) {
			throw new ContentException("Error while retrieving next classes.", ex);
		} finally {
			if (manager != null) {
				manager.close();
			}
		}
	}

}