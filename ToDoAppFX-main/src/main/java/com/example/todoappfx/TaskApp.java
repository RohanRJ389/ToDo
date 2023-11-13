package com.example.todoappfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import java.util.Optional;



import java.sql.*;
import java.time.LocalDate;

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
        listView.setCellFactory(param -> new ListCell<Task>() {
            private final CheckBox checkBox = new CheckBox();
            private final Button deleteButton = new Button("Delete");
            private final Label titleLabel = new Label();
            private final Label descriptionLabel = new Label();

            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item.isCompleted());
                    checkBox.setOnAction(event -> toggleTaskCompletion(item));

                    titleLabel.setText(item.getTitle());
                    titleLabel.setStyle("-fx-font-size: 14pt;");

                    descriptionLabel.setText(item.getDescription() + " - " + item.getDate());
                    descriptionLabel.setStyle("-fx-font-size: 10pt;");

                    // Set text fill color based on completion status
                    if (item.isCompleted()) {
                        titleLabel.setStyle("-fx-text-fill: gray;");
                        descriptionLabel.setStyle("-fx-text-fill: gray;");
                    } else {
                        titleLabel.setStyle("-fx-text-fill: black;");
                        descriptionLabel.setStyle("-fx-text-fill: black;");
                    }

                    Button deleteButton = new Button();
                    deleteButton.getStyleClass().add("delete-button");
                    deleteButton.setOnAction(event -> promptDeleteConfirmation(item));

                    // Assuming you have an image view for the trash can icon
                    ImageView trashCanIcon = new ImageView(
                            new Image(getClass().getResourceAsStream("/com/example/todoappfx/trash-can-icon.png")));
                    trashCanIcon.setFitWidth(16); // Adjust the width of the image as needed
                    trashCanIcon.setFitHeight(16); // Adjust the height of the image as needed

                    deleteButton.setGraphic(trashCanIcon);

                    HBox hbox = new HBox(10, checkBox, titleLabel, descriptionLabel, deleteButton);
                    setGraphic(hbox);
                }
            }
        });




            Button addButton = new Button("Add New Task");
        addButton.setOnAction(e -> openAddTaskWindow());

        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.getChildren().addAll(listView, taskDetailsTextArea, addButton);

        Scene scene = new Scene(vBox, 800, 500);

        // Add the CSS file to the scene
        scene.getStylesheets().add(getClass().getResource("/com/example/todoappfx/styles.css").toExternalForm());

        primaryStage.setScene(scene);


        primaryStage.setOnCloseRequest(event -> {
            // Ensure database connection is closed when the application is closed
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();

        // Refresh tasks after the UI is shown
        refreshTasks();
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
            addTask(titleField.getText(), descriptionArea.getText(), datePicker.getValue());
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

    private void addTask(String title, String description, LocalDate date) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO task_details (title, description, date, completed) VALUES (?, ?, ?, false)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, description);
                preparedStatement.setDate(3, Date.valueOf(date));
                preparedStatement.executeUpdate();
                refreshTasks();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void markTaskCompleted(Task task) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE task_details SET completed = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setBoolean(1, true);
                preparedStatement.setInt(2, task.getId());
                preparedStatement.executeUpdate();
                refreshTasks();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//    private void displayTaskDetails(Task task) {
//        StringBuilder details = new StringBuilder();
//        details.append("Title: ").append(task.getTitle()).append("\n");
//        details.append("Description: ").append(task.getDescription()).append("\n");
//        details.append("Date: ").append(task.getDate()).append("\n");
//        details.append("Completed: ").append(task.isCompleted());
//
//        taskDetailsTextArea.setText(details.toString());
//    }
    private void toggleTaskCompletion(Task task) {
        task.setCompleted(!task.isCompleted());
        // Update the completion status in the database
        updateTaskCompletionStatus(task);
    }

    private void updateTaskCompletionStatus(Task task) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE task_details SET completed = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setBoolean(1, task.isCompleted());
                preparedStatement.setInt(2, task.getId());
                preparedStatement.executeUpdate();
                refreshTasks();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void refreshTasks() {
        taskList.clear();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM task_details ORDER BY completed, date";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String title = resultSet.getString("title");
                    String description = resultSet.getString("description");
                    LocalDate date = resultSet.getDate("date").toLocalDate();
                    boolean completed = resultSet.getBoolean("completed");

                    Task task = new Task(id, title, description, date, completed);
                    taskList.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void promptDeleteConfirmation(Task task) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this task?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteTask(task);
        }
    }

    private void deleteTask(Task task) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "DELETE FROM task_details WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, task.getId());
                preparedStatement.executeUpdate();
                refreshTasks();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





    private static class Task {
        private int id;
        private String title;
        private String description;
        private LocalDate date;
        private boolean completed;

        public Task(int id, String title, String description, LocalDate date, boolean completed) {
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

        public LocalDate getDate() {
            return date;
        }

        public boolean isCompleted() {
            return completed;
        }

        public int getId() {
            return id;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        @Override
        public String toString() {
            return title;
        }
    }

}




