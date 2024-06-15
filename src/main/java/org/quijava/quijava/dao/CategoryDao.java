package org.quijava.quijava.dao;

import org.quijava.quijava.models.CategoryModel;

import java.util.List;
import java.util.Optional;

public interface CategoryDao {
    boolean existsByDescription(String description);

    Optional<CategoryModel> findById(Integer id);

    List<CategoryModel> findByIdIn(List<Integer> categoryIds);

    CategoryModel findByDescription(String description);

    void save(CategoryModel category);

    void delete(CategoryModel category);

    List<CategoryModel> findAll();

    List<CategoryModel> findAllLimit(int offset, int limit);

    long count();
}
