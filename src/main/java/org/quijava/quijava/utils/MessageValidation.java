package org.quijava.quijva.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class MessageValidation{
  public static String showInalidQuizNameMessage(){
    return "Nome de quiz invalido. O nome do quiz deve ter entre 1 a 10 caracters.";
  } 

  public static String showInvalidDescriptionMessage(){
    return "Descrição invalida. A descrição do quiz deve ter entre 1 a 10 caracters.";
  }

  public static void showInvalidCategoriesMessage(){
    return "Categorias invalidas. As categorias do quiz devem ter entre 1 a mais caracters";
  }

  public static void showInvalidUsernameMessage(){
    return "Nome de usuario invalido. O nome de usuario deve ter mais de 5 caracteres.";
  }

  public static void showInvalidPasswordMessage(){
    return "Senha invalida. A senha deve ter mais de 8 caracters";
  }
}