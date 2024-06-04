package org.quijava.quijava.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class ValidateInformation{
  public static boolean validateQuizName(String quizName){
    if (quizName == null || quizName.isEmpty() || quizName.length() > 10){
      return false;
    }
    return true;
  }

  public static boolean validateDescription(String description){
    if (description == null || description.isEmpty() || description.lenght() > 10){
      return false;
    }
    return true;
  }

  public static boolean validateCategories(String categories){
    if (categories == null || categories.isEmpty() || categories.lenght() > 10){
      return false;
    }
    return true;
  }

  public static boolean validateUsername(String username){
    if (username == null || username.isEmpty() || username.lenght() > 10){
      return false;
    }
    return true;
  }

  public static boolean validatePassword(String password){
    if (password == null || password.isEmpty() || password.lenght() > 10){
      return false;
    }
    return true;
  }
}