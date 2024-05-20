package org.quijava.quijava.repositories;

import org.quijava.quijava.models.QuizzesCategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizzesCategoryRepository extends JpaRepository<QuizzesCategoryModel, Integer> {

}
