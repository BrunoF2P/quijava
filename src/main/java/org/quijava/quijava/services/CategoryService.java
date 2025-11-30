package org.quijava.quijava.services;

import org.quijava.quijava.dao.CategoryDao;
import org.quijava.quijava.models.CategoryModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryDao categoryDao;

    public CategoryService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public List<String> getAllCategoriesDescriptions() {
        return categoryDao.findAll().stream()
                .map(CategoryModel::getDescription)
                .collect(Collectors.toList());
    }

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
        Pageable pageable = PageRequest.of(pageIndex, itemsPerPage);
        return categoryDao.findAll(pageable).getContent();
    }

    public int getNumberOfPages(int itemsPerPage) {
        long totalCategories = categoryDao.count();
        return (int) Math.ceil((double) totalCategories / itemsPerPage);
    }

    public List<CategoryModel> getCategoriesWithQuizzesAndQuestions(int pageIndex, int itemsPerPage) {
        Pageable pageable = PageRequest.of(pageIndex, itemsPerPage);
        return categoryDao.findCategoriesWithQuizzesAndQuestions(pageable).getContent();
    }
}
