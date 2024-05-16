package org.quijava.quijava.repositories;

import org.quijava.quijava.models.QuizzesCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizzesCategoryRepository extends JpaRepository<QuizzesCategory, Integer> {

}
