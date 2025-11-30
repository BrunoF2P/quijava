package org.quijava.quijava.services;

import org.quijava.quijava.dao.CategoryDao;
import org.quijava.quijava.dao.QuizDao;
import org.quijava.quijava.models.CategoryModel;
import org.quijava.quijava.models.QuizModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryService {


    private final CategoryDao categoryDao;
    private final QuizDao quizDao;


    public CategoryService(CategoryDao categoryDao, QuizDao quizDao) {
        this.categoryDao = categoryDao;
        this.quizDao = quizDao;
    }

    public List<String> getAllCategoriesDescriptions() {
        List<CategoryModel> categories = categoryDao.findAll();
        return categories.stream()
                .map(CategoryModel::getDescription)
                .collect(Collectors.toList());
    }

    /**
     * Converter um conjunto de IDs de categorias
     *
     * @param categoryIds
     * @return Conjunto de objetos do Cateogory Model
     */
    public Set<CategoryModel> findCategoriesByIds(Set<Integer> categoryIds) {
        return new HashSet<>(categoryDao.findByIdIn(new ArrayList<>(categoryIds)));
    }

    public void createCategory(String categoryName) {
        validateCategory(categoryName);

        CategoryModel newCategory = new CategoryModel();
        newCategory.setDescription(categoryName);
        categoryDao.save(newCategory);
    }

    private void validateCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Por favor, insira uma categoria válida.");
        }

        if (categoryDao.existsByDescription(categoryName)) {
            throw new IllegalArgumentException("Categoria já cadastrada.");
        }

    }

    public List<CategoryModel> getCategories(int pageIndex, int itemsPerPage) {
        int offset = pageIndex * itemsPerPage;
        return categoryDao.findAllLimit(offset, itemsPerPage);
    }

    public int getNumberOfPages(int itemsPerPage) {
        long totalCategories = categoryDao.count();
        return (int) Math.ceil((double) totalCategories / itemsPerPage);
    }

    public List<CategoryModel> getCategoriesWithQuizzesAndQuestions(int pageIndex, int itemsPerPage) {

        List<QuizModel> quizzesWithQuestions = quizDao.findQuizzesWithQuestions();

        List<CategoryModel> categoriesWithQuizzesAndQuestions = new ArrayList<>();
        for (QuizModel quiz : quizzesWithQuestions) {
            Set<CategoryModel> categories = quiz.getCategories();
            categoriesWithQuizzesAndQuestions.addAll(categories);
        }
        Set<CategoryModel> uniqueCategories = new HashSet<>(categoriesWithQuizzesAndQuestions);

        List<CategoryModel> result = new ArrayList<>(uniqueCategories);

        int fromIndex = pageIndex * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, result.size());
        return result.subList(fromIndex, toIndex);
    }
}
