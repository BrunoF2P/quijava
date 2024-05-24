package org.quijava.quijava.services;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class ImageService {

    public byte[] selectImage() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Escolha a imagem");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tipos de arquivos", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            return Files.readAllBytes(selectedFile.toPath());
        }
        return null;
    }
}