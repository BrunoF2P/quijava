package org.quijava.quijava.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.quijava.quijava.models.CategoryModel;
import org.quijava.quijava.services.CategoryService;
import org.quijava.quijava.utils.ScreenLoader;
import org.quijava.quijava.view.AllCategoriesView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class AllCategoriesController implements Initializable {

    private final ApplicationContext applicationContext;
    private final CategoryService categoryService;
    private final ScreenLoader screenLoader;
    private final AllCategoriesView allCategoriesView;
    @FXML
    private FlowPane categories;
    @FXML
    private Button back;
    @FXML
    private Pagination pagination;

    @Autowired
    public AllCategoriesController(ApplicationContext applicationContext, CategoryService categoryService, ScreenLoader screenLoader, AllCategoriesView allCategoriesView) {
        this.applicationContext = applicationContext;
        this.categoryService = categoryService;
        this.screenLoader = screenLoader;
        this.allCategoriesView = allCategoriesView;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categories.setHgap(15);
        categories.setVgap(15);
        pagination.setPageCount(categoryService.getNumberOfPages(50));
        pagination.setPageFactory(this::createPage);
    }

    private FlowPane createPage(int pageIndex) {
        categories.getChildren().clear();

        List<CategoryModel> categoriesList = categoryService.getCategoriesWithQuizzesAndQuestions(pageIndex, 50);

        for (CategoryModel category : categoriesList) {
            allCategoriesView.createCategoryButton(categories, category);
        }

        return categories;
    }

    @FXML
    public void goBack() {
        screenLoader.loadMenuScreen((Stage) back.getScene().getWindow(), applicationContext);
    }

}
