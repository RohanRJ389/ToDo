package com.example.todoappfx;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;  // Import java.sql.Date for compatibility with java.util.Date
import java.time.LocalDate;
//import java.util.Date;



public class TaskApp extends Application {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/to_do_list_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "helloworld";

    private ObservableList<Task> taskList = FXCollections.observableArrayList();
    private ListView<Task> listView = new ListView<>();
    private TextArea taskDetailsTextArea = new TextArea();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("To-Do List App");

        listView.setItems(taskList);
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayTaskDetails(newValue);
            }
        });

        Button addButton = new Button("Add New Task");
        addButton.setOnAction(e -> openAddTaskWindow());

        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.getChildren().addAll(listView, taskDetailsTextArea, addButton);

        Scene scene = new Scene(vBox, 400, 300);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private void openAddTaskWindow() {
        Stage addTaskStage = new Stage();
        addTaskStage.initModality(Modality.APPLICATION_MODAL);
        addTaskStage.setTitle("Add New Task");

        VBox addTaskLayout = new VBox(10);
        addTaskLayout.setPadding(new Insets(10, 10, 10, 10));

        TextField titleField = new TextField();
        TextArea descriptionArea = new TextArea();
        DatePicker datePicker = new DatePicker();

        Button addButton = new Button("Add Task");
        addButton.setOnAction(e -> {
            addTask(titleField.getText(), descriptionArea.getText(), Date.valueOf(datePicker.getValue()));
            addTaskStage.close();
        });

        addTaskLayout.getChildren().addAll(
                new Label("Title:"),
                titleField,
                new Label("Description:"),
                descriptionArea,
                new Label("Due Date:"),
                datePicker,
                addButton
        );

        Scene addTaskScene = new Scene(addTaskLayout, 300, 250);
        addTaskStage.setScene(addTaskScene);
        addTaskStage.showAndWait();
    }

    private void addTask(String title, String description, Date date) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "INSERT INTO task_details (title, description, date, completed) VALUES (?, ?, ?, false)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, description);
                preparedStatement.setDate(3, new java.sql.Date(date.getTime()));
                preparedStatement.executeUpdate();
                refreshTasks();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayTaskDetails(Task task) {
        StringBuilder details = new StringBuilder();
        details.append("Title: ").append(task.getTitle()).append("\n");
        details.append("Description: ").append(task.getDescription()).append("\n");
        details.append("Date: ").append(task.getDate()).append("\n");
        details.append("Completed: ").append(task.isCompleted());

        taskDetailsTextArea.setText(details.toString());
    }

    private void refreshTasks() {
        taskList.clear();
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT * FROM task_details";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String title = resultSet.getString("title");
                    String description = resultSet.getString("description");
                    Date date = resultSet.getDate("date");
                    boolean completed = resultSet.getBoolean("completed");

                    Task task = new Task(id, title, description, date, completed);
                    taskList.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static class Task {
        private int id;
        private String title;
        private String description;
        private Date date;
        private boolean completed;

        public Task(int id, String title, String description, Date date, boolean completed) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.date = date;
            this.completed = completed;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public Date getDate() {
            return date;
        }

        public boolean isCompleted() {
            return completed;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
