package org.quijava.quijava.config;

import org.quijava.quijava.dao.CategoryDao;
import org.quijava.quijava.dao.UserDao;
import org.quijava.quijava.models.CategoryModel;
import org.quijava.quijava.models.UserModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    private static final int ADMIN_ROLE = 2;

    private static final List<String> DEFAULT_CATEGORIES = List.of(
            "Informatica", "Matematica", "Jogos", "Animes", "Historia"
    );

    @Bean
    public CommandLineRunner initializeData(CategoryDao categoryDao,
                                            UserDao userDao,
                                            PasswordEncoder passwordEncoder) {
        return args -> {
            initializeCategories(categoryDao);
            initializeDefaultAdmin(userDao, passwordEncoder);
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

    private void initializeDefaultAdmin(UserDao userDao, PasswordEncoder passwordEncoder) {
        if (!userDao.existsByUsername("admin")) {
            UserModel admin = new UserModel();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(ADMIN_ROLE);
            userDao.save(admin);
            System.out.println("Admin padrão criado - Username: admin | Senha: admin123");
        }
    }
}
