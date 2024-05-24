package org.quijava.quijava.services;

import org.quijava.quijava.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.quijava.quijava.models.CategoryModel;

@Service
public class CategoryService {


    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void createCategory(String categoryName) {
        validateCategory(categoryName);

        CategoryModel newCategory = new CategoryModel();
        newCategory.setDescription(categoryName);
        categoryRepository.save(newCategory);
    }

    private void validateCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Por favor, insira uma categoria válida.");
        }

        if (categoryRepository.existsByDescription(categoryName)) {
            throw new IllegalArgumentException("Categoria já cadastrada.");
        }

    }
}
