package org.quijava.quijava.repositories;

import org.quijava.quijava.models.OptionsAnswerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionsAnswerRepository extends JpaRepository<OptionsAnswerModel, Integer> {


}