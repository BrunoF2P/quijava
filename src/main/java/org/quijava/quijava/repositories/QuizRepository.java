package org.quijava.quijava.repositories;


import org.quijava.quijava.models.QuizModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<QuizModel, Integer> {

}
