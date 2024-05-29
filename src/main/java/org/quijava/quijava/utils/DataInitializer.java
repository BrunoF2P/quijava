package org.quijava.quijava.utils;


import org.quijava.quijava.models.CategoryModel;
import org.quijava.quijava.repositories.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadData(CategoryRepository categoryRepository) {
        return args -> {
            String[] categories = {"Informatica", "Matematica", "Jogos", "Animes", "Historia", "Outros"};

            // Verifica se o banco de dados está vazio
            if (categoryRepository.findAll().isEmpty()) {
                for (String category : categories) {
                    CategoryModel newCategory = new CategoryModel();
                    newCategory.setDescription(category);
                    categoryRepository.save(newCategory);
                }
            }
        };
    }
}


