package com.example.todoappfx;

public class Tasks {
    private String title;
    private String task_description;
    private String date;
    private String time;
    private int taskID;
    private boolean completed;

    public Tasks(String title, String task_description, String date, String time, int taskID, boolean completed) {
        this.title = title;
        this.task_description = task_description;
        this.date = date;
        this.time = time;
        this.taskID = taskID;
        this.completed = completed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTask_description() {
        return task_description;
    }

    public void setTask_description(String task_description) {
        this.task_description = task_description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}

