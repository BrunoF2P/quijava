package org.quijava.quijava.dao;

import org.jetbrains.annotations.NotNull;
import org.quijava.quijava.models.CategoryModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryDao extends JpaRepository<@NotNull CategoryModel, @NotNull Integer> {


    boolean existsByDescription(String description);

    Optional<CategoryModel> findByDescription(String description);

    List<CategoryModel> findByIdIn(List<Integer> categoryIds);

    @Query("SELECT DISTINCT c FROM CategoryModel c " +
            "JOIN c.quizzes q " +
            "JOIN q.questions qu " +
            "WHERE SIZE(qu) > 0")
    Page<@NotNull CategoryModel> findCategoriesWithQuizzesAndQuestions(Pageable pageable);
}
