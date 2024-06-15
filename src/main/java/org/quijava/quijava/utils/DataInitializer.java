package org.quijava.quijava.utils;


import org.quijava.quijava.dao.CategoryDao;
import org.quijava.quijava.dao.UserDao;
import org.quijava.quijava.models.CategoryModel;
import org.quijava.quijava.models.UserModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadCategories(CategoryDao categoryDao) {
        return args -> {
            String[] categories = {"Informatica", "Matematica", "Jogos", "Animes", "Historia", "Outros"};
            // Verifica se o banco de dados está vazio
            if (categoryDao.findAll().isEmpty()) {
                for (String category : categories) {
                    CategoryModel newCategory = new CategoryModel();
                    newCategory.setDescription(category);
                    categoryDao.save(newCategory);
                }
            }
        };
    }

    @Bean
    public CommandLineRunner loadUser(UserDao userDao) {
        return args -> {
            if (!userDao.existsByUsername("Bruno")) {
                UserModel user = new UserModel();
                PasswordEncoder passwordEncoder = new PasswordEncoder();
                user.setUsername("Bruno");
                user.setPassword(passwordEncoder.encodePassword("12345678"));
                user.setRole(2);
                userDao.save(user);
            }
        };
    }
}


