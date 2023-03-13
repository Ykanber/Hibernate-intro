package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jt on 8/28/21.
 */
@Component
public class AuthorDaoImpl implements AuthorDao {

    private final EntityManagerFactory emf;

    public AuthorDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public List<Author> findALl() {
        EntityManager em =getEntityManager();

        try {
            TypedQuery<Author> typedQuery = em.createNamedQuery("author_find_all", Author.class);
            return typedQuery.getResultList();
        }finally {
            em.close();
        }
    }

    @Override
    public List<Author> listAuthorByLastNameLike(String lastName) {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createQuery("SELECT a FROM Author a WHERE a.lastName like :last_name");
            query.setParameter("last_name",lastName+"%");
            List<Author> authors = query.getResultList();
            return authors;
        }finally {
            em.close();
        }
    }

    @Override
    public Author getById(Long id) {
        EntityManager em = getEntityManager();
        Author author = em.find(Author.class,id);
        em.close();
        return author;
    }

    @Override
    public Author findAuthorByName(String firstName, String lastName) {
        EntityManager em = getEntityManager();
        TypedQuery<Author> query = em.createNamedQuery("find_by_name", Author.class);
        query.setParameter("first_name",firstName);
        query.setParameter("last_name", lastName);
        Author author = query.getSingleResult();
        em.close();
        return author;
    }
    @Override
    public Author findAuthorByNameCriteria(String firstName, String lastName) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Author> criteriaQuery = criteriaBuilder.createQuery(Author.class);

            Root<Author> root = criteriaQuery.from(Author.class);

            ParameterExpression<String> firstNameParam = criteriaBuilder.parameter(String.class);
            ParameterExpression<String> lastNameParam = criteriaBuilder.parameter(String.class);

            Predicate firstNamePred = criteriaBuilder.equal(root.get("firstName"),firstNameParam);
            Predicate lastNamePred = criteriaBuilder.equal(root.get("lastName"),lastNameParam);

            criteriaQuery.select(root).where(criteriaBuilder.and(firstNamePred,lastNamePred));

            TypedQuery<Author> typedQuery = em.createQuery(criteriaQuery);
            typedQuery.setParameter(firstNameParam,firstName);
            typedQuery.setParameter(lastNameParam,lastName);

            return typedQuery.getSingleResult();
        }finally {
            em.close();
        }
    }

    @Override
    public Author findAuthorByNameNative(String firstName, String lastName) {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createNativeQuery("SELECT * FROM author a WHERE a.first_name = ? and a.last_name = ?", Author.class);
            query.setParameter(1,firstName);
            query.setParameter(2,lastName);
            return (Author) query.getSingleResult();
        }finally {
            em.close();
        }
    }

    @Override
    public Author saveNewAuthor(Author author) {
        EntityManager entityManager = getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(author);
        entityManager.flush();
        entityManager.getTransaction().commit();
        entityManager.close();
        return author;
    }

    @Override
    public Author updateAuthor(Author author) {
        EntityManager entityManager = getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(author);
        entityManager.flush();
        entityManager.getTransaction().commit();
        Author author1 = entityManager.find(Author.class,author.getId());
        entityManager.close();
        return author1;
    }

    @Override
    public void deleteAuthorById(Long id) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Author author = em.find(Author.class,id);
        em.remove(author);
        em.flush();
        em.getTransaction().commit();
        em.close();
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
