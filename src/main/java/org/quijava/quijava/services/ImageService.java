package org.quijava.quijava.services;

import org.springframework.stereotype.Service;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class ImageService {

    public byte[] selectImage() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Escolha a imagem");
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Imagens (PNG, JPG, JPEG)", "png", "jpg", "jpeg"
        );
        fileChooser.setFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(null);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null) {
                return Files.readAllBytes(selectedFile.toPath());
            }
        }
        return null;
    }
}