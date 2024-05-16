package org.quijava.quijava.repositories;

import org.quijava.quijava.models.CategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryModel, Integer> {

    boolean existsByDescription(String description);

    CategoryModel findByDescription(String description);

    List<CategoryModel> findByIdIn(List<Integer> categoryIds);

}