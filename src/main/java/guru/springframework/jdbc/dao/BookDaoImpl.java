package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookDaoImpl implements BookDao {

    private final EntityManagerFactory emf;

    public BookDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public List<Book> findAll() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Book> query = em.createNamedQuery("find_all_books",Book.class);
            return query.getResultList();
        }finally {
            em.close();
        }
    }

    @Override
    public Book findByIsbn(String isbn) {
        EntityManager entityManager = getEntityManager();
        try {
            TypedQuery<Book> query = entityManager.createQuery("SELECT b FROM Book b WHERE b.isbn = :isbn",Book.class);
            query.setParameter("isbn",isbn);
            Book book = query.getSingleResult();
            return book;
        }finally {
            entityManager.close();
        }
    }

    @Override
    public Book getById(Long id) {
        EntityManager em = getEntityManager();
        Book book =  em.find(Book.class,id);
        em.close();
        return book;
    }

    @Override
    public Book findBookByTitle(String title) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Book> query = em.createNamedQuery("find_by_title", Book.class);
            query.setParameter("title",title);
            return query.getSingleResult();
        }finally {
            em.close();
        }
    }

    @Override
    public Book findBookByTitleNative(String title) {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createNativeQuery("SELECT * FROM book WHERE title= :title", Book.class);
            query.setParameter("title",title);
            return (Book) query.getSingleResult();
        }finally {
            em.close();
        }
    }

    @Override
    public Book saveNewBook(Book book) {
        EntityManager entityManager = getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(book);
        entityManager.flush();
        entityManager.getTransaction().commit();
        entityManager.close();
        return book;
    }

    @Override
    public Book updateBook(Book book) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        em.merge(book);
        em.flush();
        em.clear();
        Book savedBook = em.find(Book.class, book.getId());
        em.getTransaction().commit();
        em.close();
        return savedBook;
    }

    @Override
    public void deleteBookById(Long id) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Book book = em.find(Book.class,id);
        em.remove(book);
        em.flush();
        em.getTransaction().commit();
        em.close();
    }


    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
