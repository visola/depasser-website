package br.com.depasser.content.web.service.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import br.com.depasser.content.ContentIdentification;
import br.com.depasser.content.service.ContentException;
import br.com.depasser.content.service.jpa.ContentServiceImpl;
import br.com.depasser.content.web.service.BlogService;

public class BlogServiceImpl extends ContentServiceImpl implements BlogService {

	public BlogServiceImpl(EntityManagerFactory managerFactory) {
		super(managerFactory);
	}

	@Override
	public long getCommentCount(ContentIdentification identification) throws ContentException {
		return getCommentCount(identification.getId(), identification.getLanguage());
	}

	@Override
	public long getCommentCount(int blogPostId, String blogPostLanguage) throws ContentException {
		EntityManager manager = null;
		try {
			StringBuilder sQuery = new StringBuilder();
			sQuery.append("select count(bf) from BooleanField bf");
			sQuery.append(" join bf.content c");
			sQuery.append(" where bf.value = true");
			sQuery.append(" and bf.type.name = 'Active'");
			sQuery.append(" and c.template.name = 'Blog Comment'");
			sQuery.append(" and c.id = (");
				sQuery.append(" select pf.content.id from ParentField pf");
				sQuery.append(" where pf.value.id = :id");
				sQuery.append(" and pf.content.id = c.id");
				if (blogPostLanguage != null) {
					sQuery.append(" and pf.value.language = :language");
				}
			sQuery.append(")");
			
			manager = this.managerFactory.createEntityManager();
			Query q = manager.createQuery(sQuery.toString());
			q.setParameter("id", blogPostId);
			if (blogPostLanguage != null) {
				q.setParameter("language", blogPostLanguage);
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

}