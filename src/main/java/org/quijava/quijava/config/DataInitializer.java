package org.quijava.quijava.config;

import org.quijava.quijava.dao.CategoryDao;
import org.quijava.quijava.models.CategoryModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    private static final List<String> DEFAULT_CATEGORIES = List.of(
            "Informatica", "Matematica", "Jogos", "Animes", "Historia"
    );

    @Bean
    public CommandLineRunner initializeData(CategoryDao categoryDao) {
        return args -> {
            initializeCategories(categoryDao);
        };
    }

    private void initializeCategories(CategoryDao categoryDao) {
        if (categoryDao.count() == 0) {
            DEFAULT_CATEGORIES.forEach(name -> {
                CategoryModel category = new CategoryModel();
                category.setDescription(name);
                categoryDao.save(category);
            });
        }
    }
}
