package org.quijava.quijava.services;

import org.springframework.stereotype.Service;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Service
public class ImageService {

    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024;
    private static final String[] ALLOWED_EXTENSIONS = {"png", "jpg", "jpeg"};

    public Optional<byte[]> selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Escolha a imagem");

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Imagens (PNG, JPG, JPEG)", ALLOWED_EXTENSIONS
        );
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(null);

        if (result != JFileChooser.APPROVE_OPTION) {
            return Optional.empty();
        }

        File selectedFile = fileChooser.getSelectedFile();
        if (selectedFile == null) {
            return Optional.empty();
        }

        return loadImageFile(selectedFile);
    }

    private Optional<byte[]> loadImageFile(File file) {
        try {
            validateImageFile(file);
            return Optional.of(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            showErrorDialog("Erro ao carregar imagem: " + e.getMessage());
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            showErrorDialog(e.getMessage());
            return Optional.empty();
        }
    }

    private void validateImageFile(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("Arquivo não encontrado.");
        }

        if (file.length() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Imagem muito grande. Tamanho máximo: 5MB");
        }

        if (file.length() == 0) {
            throw new IllegalArgumentException("Arquivo vazio.");
        }
    }

    private void showErrorDialog(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(null, message, "Erro", JOptionPane.ERROR_MESSAGE)
        );
    }
}
