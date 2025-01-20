package org.example.bibliotecaservlets.Modelo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class DAOGenerico<T, ID> {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("biblioteca");
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    private Class <T> clase;
    private Class <ID> id;

    public DAOGenerico(Class <T> clase, Class <ID> id) {
        this.clase = clase;
        this.id = id;
    }

    public void insert(T t){
        tx.begin();
        em.persist(t);
        tx.commit();
    }

    public void update(T t){
        tx.begin();
        em.merge(t);
        tx.commit();
    }

    public void delete(T t){
        tx.begin();
        em.remove(t);
        tx.commit();
    }

    public T getById(ID id){
        return em.find(clase, id);
    }

    public List<T> getAll(){
        return em.createQuery("select c from "+clase.getSimpleName()+" c").getResultList();
    }

    public T findUnique(String columna, String valor) {
        return em.createQuery("SELECT e FROM " + clase.getSimpleName() + " e WHERE " + columna + " =  \"" + valor +"\"", clase).getSingleResult();
    }
    public List<T> findAllWhere(String columna, Integer valor){
        return em.createQuery("SELECT e FROM " +clase.getSimpleName()+" e WHERE "+ columna + " = " + valor + "", clase).getResultList();
    }
}
