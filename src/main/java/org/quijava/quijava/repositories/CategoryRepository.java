package org.quijava.quijava.repositories;

import org.quijava.quijava.models.CategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryModel, Integer> {

    boolean existsByDescription(String description);

}