package org.quijava.quijava.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.quijava.quijava.models.CategoryModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class CategoryDaoImpl implements CategoryDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(CategoryModel category) {
        entityManager.persist(category);
    }

    @Override
    public void delete(CategoryModel category) {
        entityManager.remove(category);
    }

    @Override
    public boolean existsByDescription(String description) {
        Long count = entityManager.createQuery("SELECT COUNT(c) FROM CategoryModel c WHERE c.description = :description", Long.class)
                .setParameter("description", description)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public Optional<CategoryModel> findById(Integer id) {
        CategoryModel category = entityManager.find(CategoryModel.class, id);
        return Optional.ofNullable(category);
    }

    @Override
    public List<CategoryModel> findByIdIn(List<Integer> categoryIds) {
        return entityManager.createQuery("SELECT c FROM CategoryModel c WHERE c.id IN :categoryIds", CategoryModel.class)
                .setParameter("categoryIds", categoryIds)
                .getResultList();
    }

    @Override
    public CategoryModel findByDescription(String description) {
        return entityManager.createQuery("SELECT c FROM CategoryModel c WHERE c.description = :description", CategoryModel.class)
                .setParameter("description", description)
                .getSingleResult();
    }

    @Override
    public List<CategoryModel> findAll() {
        return entityManager.createQuery("SELECT c FROM CategoryModel c", CategoryModel.class)
                .getResultList();
    }

    @Override
    @Transactional
    public List<CategoryModel> findAllLimit(int offset, int limit) {
        Query query = entityManager.createQuery("SELECT c FROM CategoryModel c")
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @Transactional
    public long count() {
        Query query = entityManager.createQuery("SELECT COUNT(c) FROM CategoryModel c");
        return (long) query.getSingleResult();
    }
}
