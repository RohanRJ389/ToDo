package com.example.todoappfx;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.text.Font;

public class AlertBox {
    public static void display(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #40e808; -fx-text-fill: #6c2e2e;");

        // Customize font if needed
        Font font = new Font("Arial", 15);
        dialogPane.setStyle("-fx-font-family: " + font.getFamily() + "; -fx-font-size: " + font.getSize() + ";");

        alert.showAndWait();
    }
}

